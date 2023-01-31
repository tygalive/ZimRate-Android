package com.tyganeutronics.myratecalculator.activities

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.apollographql.apollo3.api.Optional
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.maltaisn.calcdialog.CalcDialog
import com.maltaisn.calcdialog.CalcNumpadLayout
import com.tyganeutronics.myratecalculator.AppZimrate
import com.tyganeutronics.myratecalculator.Calculator
import com.tyganeutronics.myratecalculator.R
import com.tyganeutronics.myratecalculator.contract.CurrencyContract
import com.tyganeutronics.myratecalculator.database.*
import com.tyganeutronics.myratecalculator.fragments.FragmentCalculator
import com.tyganeutronics.myratecalculator.graphql.FetchRatesQuery
import com.tyganeutronics.myratecalculator.graphql.type.Prefer
import com.tyganeutronics.myratecalculator.utils.BaseUtils
import com.tyganeutronics.myratecalculator.widget.MultipleRateProvider
import com.tyganeutronics.myratecalculator.widget.SingleRateProvider
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity(), TextWatcher, AdapterView.OnItemSelectedListener,
    SwipeRefreshLayout.OnRefreshListener,
    CalcDialog.CalcDialogCallback {

    private val fragmentCalculator: FragmentCalculator = FragmentCalculator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_main)
        setupAd()

        bindViews()
        syncViews()
        textWatchers()

        if (shouldUpdate()) {
            fetchRates()
        }
    }

    private fun textWatchers() {

        listOf(
            R.id.et_usd,
            R.id.et_bond,
            R.id.et_omir,
            R.id.et_rtgs,
            R.id.et_rbz,
            R.id.et_zar,
            R.id.et_amount
        ).forEach { id ->
            findViewById<TextInputEditText>(id).addTextChangedListener(this@MainActivity)
        }
    }

    /**
     * bind layout views
     */
    private fun bindViews() {
        findViewById<SwipeRefreshLayout>(R.id.sr_layout).setOnRefreshListener(this)

        findViewById<AppCompatSpinner>(R.id.s_currency).onItemSelectedListener = this

        //calculator btns
        listOf(
            R.id.et_usd_parent,
            R.id.et_bond_parent,
            R.id.et_omir_parent,
            R.id.et_rtgs_parent,
            R.id.et_rbz_parent,
            R.id.et_zar_parent,
            R.id.et_amount_parent
        ).forEach { id ->

            findViewById<TextInputLayout>(id).run {
                setStartIconOnClickListener {

                    fragmentCalculator.settings.apply {
                        initialValue = this@run.editText?.text.let {
                            val text = if (it.isNullOrBlank()) "0" else it
                            text
                        }.toString().toBigDecimal()
                        requestCode = id
                    }

                    fragmentCalculator.show(supportFragmentManager, "CalcDialog")

                }
            }

        }

        triggerRateDialog()
    }

    /**
     * Sync layout views
     */
    private fun syncViews() {

        findViewById<TextInputEditText>(R.id.et_usd).setText(
            BaseUtils.getPrefs(this).getString(
                getString(R.string.currency_usd),
                "1"
            )
        )
        findViewById<TextInputEditText>(R.id.et_bond).setText(
            BaseUtils.getPrefs(this).getString(
                getString(R.string.currency_bond),
                "1"
            )
        )
        findViewById<TextInputEditText>(R.id.et_omir).setText(
            BaseUtils.getPrefs(this).getString(
                getString(R.string.currency_omir),
                "1"
            )
        )
        findViewById<TextInputEditText>(R.id.et_rtgs).setText(
            BaseUtils.getPrefs(this).getString(
                getString(R.string.currency_rtgs),
                "1"
            )
        )
        findViewById<TextInputEditText>(R.id.et_rbz).setText(
            BaseUtils.getPrefs(this).getString(
                getString(R.string.currency_rbz),
                "1"
            )
        )
        findViewById<TextInputEditText>(R.id.et_zar).setText(
            BaseUtils.getPrefs(this).getString(
                getString(R.string.currency_zar),
                "1"
            )
        )

        findViewById<AppCompatSpinner>(R.id.s_currency).setSelection(
            BaseUtils.getPrefs(this).getInt(CurrencyContract.CURRENCY, 0)
        )

        findViewById<TextInputEditText>(R.id.et_amount).text?.append(
            BaseUtils.getPrefs(this).getString(
                "amount",
                "1"
            )
        )

        fragmentCalculator.settings.apply {
            minValue = BigDecimal("-1e10")
            maxValue = BigDecimal("1e10")
            numpadLayout = CalcNumpadLayout.CALCULATOR
            isExpressionShown = true
            isAnswerBtnShown = true
        }

        findViewById<SwipeRefreshLayout>(R.id.sr_layout).setColorSchemeResources(R.color.colorPrimaryLight)

    }

    override fun onRefresh() {
        findViewById<SwipeRefreshLayout>(R.id.sr_layout).isRefreshing = true

        getFirebaseAnalytics()!!.logEvent("refresh_rates", Bundle())

        fetchRates()
    }

    private fun shouldUpdate(): Boolean {
        var check = BaseUtils.getPrefs(baseContext).getBoolean("check_update", true)

        if (check) {

            val last = BaseUtils.getPrefs(baseContext).getLong(CurrencyContract.LAST_CHECK, 0L)
            val offset = BaseUtils.getPrefs(baseContext).getString("update_interval", "1")

            val hours = TimeUnit.HOURS.toMillis(offset!!.toLong())

            check = check.and(System.currentTimeMillis() > last.plus(hours))
        }

        return check
    }

    /**
     * Do fetch server rates
     */
    private fun fetchRates() {

        lifecycleScope.launch {
            val option = BaseUtils.getPrefs(applicationContext)
                .getString("preferred_currency", getString(R.string.prefer_max))

            val prefer = Optional.presentIfNotNull(Prefer.safeValueOf(option!!.uppercase()))

            try {
                AppZimrate
                    .apolloClient
                    .query(FetchRatesQuery(prefer))
                    .execute()
                    .data
                    ?.rates?.let { rates ->
                        onResponse(rates)
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                findViewById<SwipeRefreshLayout>(R.id.sr_layout).isRefreshing = false
            }
        }
    }

    private fun onResponse(rates: List<FetchRatesQuery.Rate>) {

        if (BaseUtils.getPrefs(baseContext).getBoolean("auto_update", true)) {
            updateCurrencies(rates)
        } else {

            Snackbar.make(
                findViewById<LinearLayoutCompat>(R.id.sr_layout),
                R.string.update_available,
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(
                    R.string.update_apply
                ) {
                    updateCurrencies(rates)
                }.show()
        }

    }

    private fun updateCurrencies(currencies: List<FetchRatesQuery.Rate>) {

        currencies.forEach { currency ->

            val instant = Instant.ofEpochSecond(
                currency.last_updated!!.toLong()
            )
            val format =
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT)
            val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(format)

            val rate = currency.rate.toString()

            when (currency.currency) {
                getString(R.string.currency_bond) -> {
                    findViewById<TextInputEditText>(R.id.et_bond).setText(rate)
                    findViewById<TextInputLayout>(R.id.et_bond_parent).helperText = date
                }
                getString(R.string.currency_omir) -> {
                    findViewById<TextInputEditText>(R.id.et_omir).setText(rate)
                    findViewById<TextInputLayout>(R.id.et_omir_parent).helperText = date
                }
                getString(R.string.currency_rbz) -> {
                    findViewById<TextInputEditText>(R.id.et_rbz).setText(rate)
                    findViewById<TextInputLayout>(R.id.et_rbz_parent).helperText = date
                }
                getString(R.string.currency_rtgs) -> {
                    findViewById<TextInputEditText>(R.id.et_rtgs).setText(rate)
                    findViewById<TextInputLayout>(R.id.et_rtgs_parent).helperText = date
                }
                getString(R.string.currency_zar) -> {
                    findViewById<TextInputEditText>(R.id.et_zar).setText(rate)
                    findViewById<TextInputLayout>(R.id.et_zar_parent).helperText = date
                }
            }
        }

        saveRates()

        BaseUtils.getPrefs(baseContext).edit()
            .putLong(CurrencyContract.LAST_CHECK, System.currentTimeMillis())
            .apply()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        FirebaseAnalytics.getInstance(baseContext).logEvent("change_currency", Bundle())

        calculate()
    }

    override fun afterTextChanged(s: Editable?) {
        calculate()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.calculator, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> {

                val intent = Intent(baseContext, SettingsActivity().javaClass)
                startActivity(intent)

                return true
            }
            R.id.menu_info -> {

                FirebaseAnalytics.getInstance(baseContext).logEvent("view_info_dialog", Bundle())

                val dialog = AlertDialog.Builder(this)
                dialog.setTitle(R.string.menu_info)
                dialog.setIcon(ContextCompat.getDrawable(baseContext, R.drawable.ic_info))
                dialog.setMessage(R.string.info_message)
                dialog.setPositiveButton(R.string.info_dismiss, null)
                dialog.show()

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun saveRates() {
        BaseUtils.getPrefs(this).edit()
            .putString(
                getString(R.string.currency_usd),
                findViewById<TextInputEditText>(R.id.et_usd).text?.toString()
            )
            .apply()
        BaseUtils.getPrefs(this).edit()
            .putString(
                getString(R.string.currency_bond),
                findViewById<TextInputEditText>(R.id.et_bond).text?.toString()
            )
            .apply()
        BaseUtils.getPrefs(this).edit()
            .putString(
                getString(R.string.currency_omir),
                findViewById<TextInputEditText>(R.id.et_omir).text?.toString()
            )
            .apply()
        BaseUtils.getPrefs(this).edit()
            .putString(
                getString(R.string.currency_rtgs),
                findViewById<TextInputEditText>(R.id.et_rtgs).text?.toString()
            )
            .apply()
        BaseUtils.getPrefs(this).edit()
            .putString(
                getString(R.string.currency_rbz),
                findViewById<TextInputEditText>(R.id.et_rbz).text?.toString()
            )
            .apply()
        BaseUtils.getPrefs(this).edit()
            .putString(
                getString(R.string.currency_zar),
                findViewById<TextInputEditText>(R.id.et_zar).text?.toString()
            )
            .apply()
    }

    private fun getCalculator(): Calculator {

        var usdText =
            normaliseInput(findViewById<TextInputEditText>(R.id.et_usd).text?.toString())
        var bondText =
            normaliseInput(findViewById<TextInputEditText>(R.id.et_bond).text?.toString())
        var omirText =
            normaliseInput(findViewById<TextInputEditText>(R.id.et_omir).text?.toString())
        var rtgsText =
            normaliseInput(findViewById<TextInputEditText>(R.id.et_rtgs).text?.toString())
        var rbzText = normaliseInput(findViewById<TextInputEditText>(R.id.et_rbz).text?.toString())
        var zarText = normaliseInput(findViewById<TextInputEditText>(R.id.et_zar).text?.toString())

        if (TextUtils.isEmpty(usdText)) {
            usdText = "1"
        }
        if (TextUtils.isEmpty(bondText)) {
            bondText = "1"
        }
        if (TextUtils.isEmpty(omirText)) {
            omirText = "1"
        }
        if (TextUtils.isEmpty(rtgsText)) {
            rtgsText = "1"
        }
        if (TextUtils.isEmpty(rbzText)) {
            rbzText = "1"
        }
        if (TextUtils.isEmpty(zarText)) {
            zarText = "1"
        }

        saveRates()

        val usd = USD(usdText.toDouble())
        val bond = BOND(bondText.toDouble())
        val omir = OMIR(omirText.toDouble())
        val rtgs = RTGS(rtgsText.toDouble())
        val rbz = RBZ(rbzText.toDouble())
        val zar = ZAR(zarText.toDouble())

        var currency: Currency = usd
        when (findViewById<AppCompatSpinner>(R.id.s_currency).selectedItem) {
            getString(R.string.currency_usd) -> {
                currency = usd
            }
            getString(R.string.currency_omir) -> {
                currency = omir
            }
            getString(R.string.currency_bond) -> {
                currency = bond
            }
            getString(R.string.currency_rtgs) -> {
                currency = rtgs
            }
            getString(R.string.currency_rbz) -> {
                currency = rbz
            }
            getString(R.string.currency_zar) -> {
                currency = zar
            }
        }

        return Calculator(
            usd,
            bond,
            omir,
            rtgs,
            rbz,
            zar,
            currency
        )
    }

    private fun isNumeric(input: String): Boolean {
        return try {
            input.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun normaliseInput(input: String?): String {
        if (input == null || input.isEmpty() || !isNumeric(input)) {
            return "1"
        }
        return input
    }

    private fun calculate() {

        val calculator = getCalculator()

        val amountText =
            normaliseInput(findViewById<TextInputEditText>(R.id.et_amount).text?.toString())

        if (amountText.isNotEmpty()) {

            //save amount entered
            BaseUtils.getPrefs(this).edit().putString("amount", amountText)
                .apply()
            BaseUtils.getPrefs(this).edit().putInt(
                "currency",
                findViewById<AppCompatSpinner>(R.id.s_currency).selectedItemPosition
            )
                .apply()

            calculateCurrencyResult(
                R.id.txt_usd_result,
                calculator.toUSD(amountText.toDouble()),
                calculator.usd
            )

            calculateCurrencyResult(
                R.id.txt_bond_result,
                calculator.toBOND(amountText.toDouble()),
                calculator.bond
            )

            calculateCurrencyResult(
                R.id.txt_omir_result,
                calculator.toOMIR(amountText.toDouble()),
                calculator.omir
            )

            calculateCurrencyResult(
                R.id.txt_rbz_result,
                calculator.toRBZ(amountText.toDouble()),
                calculator.rbz
            )

            calculateCurrencyResult(
                R.id.txt_rtgs_result,
                calculator.toRTGS(amountText.toDouble()),
                calculator.rtgs
            )

            calculateCurrencyResult(
                R.id.txt_zar_result,
                calculator.toZAR(amountText.toDouble()),
                calculator.zar
            )
        }

    }

    private fun calculateCurrencyResult(
        target: Int,
        result: Double,
        currency: Currency
    ) {

        with(findViewById<AppCompatButton>(target)) {
            setOnClickListener {

                this@MainActivity.findViewById<TextInputEditText>(R.id.et_amount).setText(
                    String.format("%10.2f", result).trim()
                )

                val selection = resources.getStringArray(R.array.currencies)
                    .indexOf(getString(currency.getName()))

                FirebaseAnalytics.getInstance(baseContext)
                    .logEvent("copy_result_for_calculation", Bundle())

                this@MainActivity.findViewById<AppCompatSpinner>(R.id.s_currency)
                    .setSelection(selection)

            }

            text = getString(
                R.string.result,
                currency.getSign(),
                result
            )
        }
    }

    override fun onStop() {
        super.onStop()

        updateMultipleWidget()
        updateSingleWidget()
    }

    private fun updateMultipleWidget() {

        val componentName = ComponentName(this, MultipleRateProvider::class.java)

        val appWidgetManager = AppWidgetManager.getInstance(this)

        val intent = Intent(this, MultipleRateProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

        val multipleIds = appWidgetManager.getAppWidgetIds(componentName)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, multipleIds)
        sendBroadcast(intent)
    }

    private fun updateSingleWidget() {

        val componentName = ComponentName(this, SingleRateProvider::class.java)

        val appWidgetManager = AppWidgetManager.getInstance(this)

        val intent = Intent(this, SingleRateProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

        val multipleIds = appWidgetManager.getAppWidgetIds(componentName)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, multipleIds)
        sendBroadcast(intent)
    }

    override fun onValueEntered(requestCode: Int, value: BigDecimal?) {
        when (requestCode) {
            R.id.et_usd_parent -> {
                findViewById<TextInputEditText>(R.id.et_usd).setText(value?.toPlainString() ?: "")
            }
            R.id.et_bond_parent -> {
                findViewById<TextInputEditText>(R.id.et_bond).setText(value?.toPlainString() ?: "")
            }
            R.id.et_omir_parent -> {
                findViewById<TextInputEditText>(R.id.et_omir).setText(value?.toPlainString() ?: "")
            }
            R.id.et_rtgs_parent -> {
                findViewById<TextInputEditText>(R.id.et_rtgs).setText(value?.toPlainString() ?: "")
            }
            R.id.et_rbz_parent -> {
                findViewById<TextInputEditText>(R.id.et_rbz).setText(value?.toPlainString() ?: "")
            }
            R.id.et_zar_parent -> {
                findViewById<TextInputEditText>(R.id.et_zar).setText(value?.toPlainString() ?: "")
            }
            R.id.et_amount_parent -> {
                findViewById<TextInputEditText>(R.id.et_amount).setText(
                    value?.toPlainString() ?: ""
                )
            }
        }
    }

}

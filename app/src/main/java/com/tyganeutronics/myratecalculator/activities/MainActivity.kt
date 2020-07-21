package com.tyganeutronics.myratecalculator.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.snackbar.Snackbar
import com.tyganeutronics.base.BaseUtils
import com.tyganeutronics.myratecalculator.Calculator
import com.tyganeutronics.myratecalculator.MyApplication
import com.tyganeutronics.myratecalculator.R
import com.tyganeutronics.myratecalculator.models.*
import kotlinx.android.synthetic.main.layout_main.*
import kotlinx.android.synthetic.main.layout_rates.*
import kotlinx.android.synthetic.main.layout_result.view.*
import org.json.JSONObject
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import java.util.concurrent.TimeUnit


class MainActivity : BaseActivity(), TextWatcher, AdapterView.OnItemSelectedListener,
    Response.Listener<JSONObject>, Response.ErrorListener, SwipeRefreshLayout.OnRefreshListener,
    View.OnClickListener {

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
        et_bond.addTextChangedListener(this)
        et_omir.addTextChangedListener(this)
        et_rtgs.addTextChangedListener(this)
        et_rbz.addTextChangedListener(this)
        et_rand.addTextChangedListener(this)
        et_amount.addTextChangedListener(this)
    }

    /**
     * bind layout views
     */
    private fun bindViews() {
        sr_layout.setOnRefreshListener(this)

        s_currency.onItemSelectedListener = this

        btn_toggle.setOnClickListener(this)

        triggerRateDialog()
    }

    /**
     * Sync layout views
     */
    private fun syncViews() {

        et_bond.setText(
            BaseUtils.getPrefs(this).getString(
                getString(R.string.currency_bond),
                "1"
            )
        )
        et_omir.setText(
            BaseUtils.getPrefs(this).getString(
                getString(R.string.currency_omir),
                "1"
            )
        )
        et_rtgs.setText(
            BaseUtils.getPrefs(this).getString(
                getString(R.string.currency_rtgs),
                "1"
            )
        )
        et_rbz.setText(
            BaseUtils.getPrefs(this).getString(
                getString(R.string.currency_rbz),
                "1"
            )
        )
        et_rand.setText(
            BaseUtils.getPrefs(this).getString(
                getString(R.string.currency_rand),
                "1"
            )
        )

        s_currency.setSelection(BaseUtils.getPrefs(this).getInt("currency", 0))

        et_amount.text?.append(
            BaseUtils.getPrefs(this).getString(
                "amount",
                "1"
            )
        )

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_toggle -> {

                val attrId: Int
                if (layout_rates.visibility == View.VISIBLE) {
                    layout_rates.visibility = View.GONE

                    attrId = R.attr.ic_show
                } else {
                    layout_rates.visibility = View.VISIBLE

                    attrId = R.attr.ic_hide
                }

                val typedValue = TypedValue()
                theme.resolveAttribute(attrId, typedValue, true)

                btn_toggle.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(baseContext, typedValue.resourceId),
                    null
                )

            }
        }
    }

    override fun onRefresh() {
        sr_layout.isRefreshing = true

        fetchRates()
    }

    private fun shouldUpdate(): Boolean {
        var check = BaseUtils.getPrefs(baseContext).getBoolean("check_update", true)

        if (check) {

            val last = BaseUtils.getPrefs(baseContext).getLong("last_check", 0L)
            val offset =
                BaseUtils.getPrefs(baseContext).getString("update_interval", "0")
            val current = System.currentTimeMillis()

            val hour = TimeUnit.HOURS.toMillis(offset!!.toLong())

            check = check.and(current > last.plus(hour))
        }

        return check
    }

    /**
     * Do fetch server rates
     */
    private fun fetchRates() {

        val prefer = BaseUtils.getPrefs(applicationContext)
            .getString("preferred_currency", getString(R.string.prefer_max))

        val uri = Uri.parse(getString(R.string.rates_url)).buildUpon()
            .appendQueryParameter("prefer", prefer).build()

        val jsonObjectRequest = JsonObjectRequest(uri.toString(), null, this, this)

        jsonObjectRequest.setShouldCache(false)
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(10000, 2, 1.0f)

        MyApplication.getRequestQueue().add(jsonObjectRequest)
    }

    override fun onResponse(response: JSONObject?) {

        sr_layout.isRefreshing = false

        if (BaseUtils.getPrefs(baseContext).getBoolean("auto_update", true)) {
            updateCurrencies(response)
        } else {

            Snackbar.make(
                calc_result_layout,
                R.string.update_available,
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(
                    R.string.update_apply
                ) {
                    updateCurrencies(response)
                }.show()
        }

    }

    private fun updateCurrencies(response: JSONObject?) {
        val currencies = JSONObject(response.toString()).optJSONArray("USD")

        if (currencies != null) {

            for (i in 0 until currencies.length()) {
                val currency = currencies.getJSONObject(i)

                val instant = Instant.ofEpochSecond(currency.getString("last_updated").toLong())
                val format =
                    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT)
                val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(format)

                val rate = currency.getString("rate")

                when (currency.getString("currency")) {
                    getString(R.string.currency_bond) -> {
                        et_bond.setText(rate)
                        et_bond_parent.helperText = date
                    }
                    getString(R.string.currency_omir) -> {
                        et_omir.setText(rate)
                        et_omir_parent.helperText = date
                    }
                    getString(R.string.currency_rbz) -> {
                        et_rbz.setText(rate)
                        et_rbz_parent.helperText = date
                    }
                    getString(R.string.currency_rtgs) -> {
                        et_rtgs.setText(rate)
                        et_rtgs_parent.helperText = date
                    }
                    getString(R.string.currency_rand) -> {
                        et_rand.setText(rate)
                        et_rand_parent.helperText = date
                    }
                }
            }

            saveRates()

            BaseUtils.getPrefs(baseContext).edit().putLong("last_check", System.currentTimeMillis())
                .apply()
        }
    }

    override fun onErrorResponse(error: VolleyError?) {
        error?.printStackTrace()

        sr_layout.isRefreshing = false
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
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

        menuInflater.inflate(R.menu.calculator_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> {

                val intent = Intent(baseContext, SettingsActivity().javaClass)
                startActivity(intent)

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun saveRates() {
        BaseUtils.getPrefs(this).edit()
            .putString(getString(R.string.currency_bond), et_bond.text?.toString())
            .apply()
        BaseUtils.getPrefs(this).edit()
            .putString(getString(R.string.currency_omir), et_omir.text?.toString())
            .apply()
        BaseUtils.getPrefs(this).edit()
            .putString(getString(R.string.currency_rtgs), et_rtgs.text?.toString())
            .apply()
        BaseUtils.getPrefs(this).edit()
            .putString(getString(R.string.currency_rbz), et_rbz.text?.toString())
            .apply()
        BaseUtils.getPrefs(this).edit()
            .putString(getString(R.string.currency_rand), et_rand.text?.toString())
            .apply()
    }

    private fun getCalculator(): Calculator {

        var bondText = et_bond.text?.toString()
        var omirText = et_omir.text?.toString()
        var rtgsText = et_rtgs.text?.toString()
        var rbzText = et_rbz.text?.toString()
        var randText = et_rand.text?.toString()

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
        if (TextUtils.isEmpty(randText)) {
            randText = "1"
        }

        saveRates()

        val usd = USD(1.0)
        val bond = BOND(bondText!!.toDouble())
        val omir = OMIR(omirText!!.toDouble())
        val rtgs = RTGS(rtgsText!!.toDouble())
        val rbz = RBZ(rbzText!!.toDouble())
        val rand = RAND(randText!!.toDouble())

        var currency: Currency = usd
        when (s_currency.selectedItem) {
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
            getString(R.string.currency_rand) -> {
                currency = rand
            }
        }

        return Calculator(
            usd,
            bond,
            omir,
            rtgs,
            rbz,
            rand,
            currency
        )
    }

    private fun calculate() {

        val calculator = getCalculator()

        val amountText = et_amount.text?.toString() ?: "1"

        if (amountText.isNotEmpty()) {

            //save amount entered
            BaseUtils.getPrefs(this).edit().putString("amount", amountText)
                .apply()
            BaseUtils.getPrefs(this).edit().putInt("currency", s_currency.selectedItemPosition)
                .apply()

            calc_result_layout.removeAllViews()

            calculateCurrencyResult(
                amountText.toDouble(),
                calculator.toUSD(amountText.toDouble()),
                calculator.usd
            )

            calculateCurrencyResult(
                amountText.toDouble(),
                calculator.toBOND(amountText.toDouble()),
                calculator.bond
            )

            calculateCurrencyResult(
                amountText.toDouble(),
                calculator.toOMIR(amountText.toDouble()),
                calculator.omir
            )

            calculateCurrencyResult(
                amountText.toDouble(),
                calculator.toRBZ(amountText.toDouble()),
                calculator.rbz
            )

            calculateCurrencyResult(
                amountText.toDouble(),
                calculator.toRTGS(amountText.toDouble()),
                calculator.rtgs
            )

            calculateCurrencyResult(
                amountText.toDouble(),
                calculator.toRAND(amountText.toDouble()),
                calculator.rand
            )
        }

    }

    private fun calculateCurrencyResult(amountText: Double, result: Double, currency: Currency) {

        addResultLayout(
            getResultText(
                amountText,
                currency.getSign(),
                result,
                getString(currency.getName())
            ), result, getString(currency.getName())
        )
    }

    /**
     * get calculated result text
     */
    private fun getResultText(
        amountText: Double,
        sign: String,
        result: Double,
        currency: String
    ): String {
        val calculator = getCalculator()

        return getString(
            R.string.result,
            calculator.currency.getSign(),
            amountText,
            s_currency.selectedItem,
            sign,
            result,
            currency
        )
    }

    /**
     * create result layout
     */
    private fun addResultLayout(result: String, amount: Double, currency: String) {

        val resultLayout: CardView =
            layoutInflater.inflate(
                R.layout.layout_result,
                calc_result_layout,
                false
            ) as CardView

        resultLayout.result_text.text = result
        resultLayout.setOnClickListener {

            et_amount.setText(String.format("%10.2f", amount).trim())

            val selection = resources.getStringArray(R.array.currencies).indexOf(currency)

            s_currency.setSelection(selection)
        }

        calc_result_layout.addView(resultLayout)
    }
}

package com.tyganeutronics.myratecalculator.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
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
import com.tyganeutronics.myratecalculator.database.BOND
import com.tyganeutronics.myratecalculator.database.Currency
import com.tyganeutronics.myratecalculator.database.OMIR
import com.tyganeutronics.myratecalculator.database.RBZ
import com.tyganeutronics.myratecalculator.database.RTGS
import com.tyganeutronics.myratecalculator.database.USD
import com.tyganeutronics.myratecalculator.database.ZAR
import com.tyganeutronics.myratecalculator.database.contract.PurchasesContract
import com.tyganeutronics.myratecalculator.database.models.SpendModel
import com.tyganeutronics.myratecalculator.database.viewmodels.RewardViewModel
import com.tyganeutronics.myratecalculator.fragments.FragmentCalculator
import com.tyganeutronics.myratecalculator.graphql.FetchRatesQuery
import com.tyganeutronics.myratecalculator.graphql.type.Prefer
import com.tyganeutronics.myratecalculator.interfaces.RewardModelInterface
import com.tyganeutronics.myratecalculator.interfaces.RewardsActivity
import com.tyganeutronics.myratecalculator.ui.base.BaseFragment
import com.tyganeutronics.myratecalculator.utils.contracts.CurrencyContract
import com.tyganeutronics.myratecalculator.utils.traits.findViewById
import com.tyganeutronics.myratecalculator.utils.traits.getBooleanPref
import com.tyganeutronics.myratecalculator.utils.traits.getIntPref
import com.tyganeutronics.myratecalculator.utils.traits.getLongPref
import com.tyganeutronics.myratecalculator.utils.traits.getStringPref
import com.tyganeutronics.myratecalculator.utils.traits.invalidateOptionsMenu
import com.tyganeutronics.myratecalculator.utils.traits.putIntPref
import com.tyganeutronics.myratecalculator.utils.traits.putLongPref
import com.tyganeutronics.myratecalculator.utils.traits.putStringPref
import com.tyganeutronics.myratecalculator.utils.traits.requireViewById
import com.tyganeutronics.myratecalculator.utils.traits.setTitle
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.concurrent.TimeUnit

class FragmentCalculator : BaseFragment(), AdapterView.OnItemSelectedListener,
    SwipeRefreshLayout.OnRefreshListener,
    CalcDialog.CalcDialogCallback {

    private lateinit var rewardViewModel: RewardViewModel

    private val fragmentCalculator: FragmentCalculator = FragmentCalculator()

    private var didCalculate = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_calculator, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        rewardViewModel = (requireActivity() as RewardModelInterface).rewardViewModel
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
            requireViewById<TextInputEditText>(id).addTextChangedListener { calculate() }
        }
    }

    /**
     * bind layout views
     */
    override fun bindViews() {
        super.bindViews()

        requireViewById<SwipeRefreshLayout>(R.id.sr_layout).setOnRefreshListener(this)

        requireViewById<AppCompatSpinner>(R.id.s_currency).onItemSelectedListener = this

        //fragment_main_home buttons
        listOf(
            R.id.et_usd_parent,
            R.id.et_bond_parent,
            R.id.et_omir_parent,
            R.id.et_rtgs_parent,
            R.id.et_rbz_parent,
            R.id.et_zar_parent,
            R.id.et_amount_parent
        ).forEach { id ->

            requireViewById<TextInputLayout>(id).run {
                setStartIconOnClickListener {

                    fragmentCalculator.settings.apply {
                        initialValue = this@run.editText?.text.let {
                            val text = if (it.isNullOrBlank()) "0" else it
                            text
                        }.toString().toBigDecimal()
                        requestCode = id
                    }

                    fragmentCalculator.show(childFragmentManager, "CalcDialog")

                }
            }

        }

        requireViewById<TextInputEditText>(R.id.et_amount).setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && canConsumeCoins()) {
                didCalculate = true
            }
        }

    }

    /**
     * Sync layout views
     */
    override fun syncViews() {
        super.syncViews()

        setTitle(R.string.menu_calculator)

        val observer = Observer { _: Long ->
            invalidateOptionsMenu()

            if (shouldUpdate()) {
                fetchRates()
            }
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        rewardViewModel.coins.observe(this, observer)

        requireViewById<TextInputEditText>(R.id.et_usd).setText(
            requireContext().getStringPref(
                getString(R.string.currency_usd),
                "1"
            )
        )
        requireViewById<TextInputEditText>(R.id.et_bond).setText(
            requireActivity().getStringPref(
                getString(R.string.currency_bond),
                "1"
            )
        )
        requireViewById<TextInputEditText>(R.id.et_omir).setText(
            requireContext().getStringPref(
                getString(R.string.currency_omir),
                "1"
            )
        )
        requireViewById<TextInputEditText>(R.id.et_rtgs).setText(
            requireContext().getStringPref(
                getString(R.string.currency_rtgs),
                "1"
            )
        )
        requireViewById<TextInputEditText>(R.id.et_rbz).setText(
            requireContext().getStringPref(
                getString(R.string.currency_rbz),
                "1"
            )
        )
        requireViewById<TextInputEditText>(R.id.et_zar).setText(
            requireContext().getStringPref(
                getString(R.string.currency_zar),
                "1"
            )
        )

        requireViewById<AppCompatSpinner>(R.id.s_currency).setSelection(
            requireContext().getIntPref(CurrencyContract.CURRENCY, 0)
        )

        requireViewById<TextInputEditText>(R.id.et_amount).setText(
            requireContext().getStringPref(
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

        requireViewById<SwipeRefreshLayout>(R.id.sr_layout).setColorSchemeResources(R.color.colorPrimaryLight)

    }

    override fun onStart() {
        super.onStart()

        didCalculate = false

        textWatchers()
    }

    override fun onStop() {
        super.onStop()

        if (didCalculate && canConsumeCoins()) {
            SpendModel.consume(
                requireContext(),
                1,
                PurchasesContract.TYPES.CALCULATION,
                getString(R.string.rewards_spend_calculation)
            )
        }
    }

    override fun onRefresh() {
        requireViewById<SwipeRefreshLayout>(R.id.sr_layout).isRefreshing = true

        firebaseAnalytics.logEvent("refresh_rates", Bundle())

        fetchRates()
    }

    private fun shouldUpdate(): Boolean {
        var check = requireContext().getBooleanPref("check_update", true)

        if (check) {

            val last = requireContext().getLongPref(CurrencyContract.LAST_CHECK, 0L)
            val offset = requireContext().getStringPref("update_interval", "1")

            val hours = TimeUnit.HOURS.toMillis(offset.toLong())

            check = System.currentTimeMillis() > last.plus(hours)
        }

        return check
    }

    /**
     * Do fetch server rates
     */
    private fun fetchRates() {

        lifecycleScope.launch {

            try {
                if (canConsumeCoins()) {

                    val option = requireContext().getStringPref(
                        "preferred_currency",
                        getString(R.string.prefer_max)
                    )

                    val prefer = Optional.presentIfNotNull(Prefer.safeValueOf(option.uppercase()))

                    AppZimrate
                        .apolloClient
                        .query(FetchRatesQuery(prefer))
                        .execute()
                        .data
                        ?.rates!!.let { rates ->
                            onResponse(rates)

                            SpendModel.consume(
                                requireContext(),
                                1,
                                PurchasesContract.TYPES.DATA_FETCH,
                                getString(R.string.rewards_spend_data_fetch)
                            )
                        }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                findViewById<SwipeRefreshLayout>(R.id.sr_layout)?.isRefreshing = false
            }
        }
    }

    private fun canConsumeCoins(): Boolean {
        val hasCoins = (rewardViewModel.coins.value ?: 0) > 0

        if (!hasCoins) {
            (requireActivity() as RewardsActivity).showTopUpDialog()
        }

        return hasCoins
    }

    private fun onResponse(rates: List<FetchRatesQuery.Rate>) {

        if (requireContext().getBooleanPref("auto_update", true)) {
            updateCurrencies(rates)
        } else {

            Snackbar.make(
                requireViewById<LinearLayoutCompat>(R.id.sr_layout),
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
                    requireViewById<TextInputEditText>(R.id.et_bond).setText(rate)
                    requireViewById<TextInputLayout>(R.id.et_bond_parent).helperText = date
                }

                getString(R.string.currency_omir) -> {
                    requireViewById<TextInputEditText>(R.id.et_omir).setText(rate)
                    requireViewById<TextInputLayout>(R.id.et_omir_parent).helperText = date
                }

                getString(R.string.currency_rbz) -> {
                    requireViewById<TextInputEditText>(R.id.et_rbz).setText(rate)
                    requireViewById<TextInputLayout>(R.id.et_rbz_parent).helperText = date
                }

                getString(R.string.currency_rtgs) -> {
                    requireViewById<TextInputEditText>(R.id.et_rtgs).setText(rate)
                    requireViewById<TextInputLayout>(R.id.et_rtgs_parent).helperText = date
                }

                getString(R.string.currency_zar) -> {
                    requireViewById<TextInputEditText>(R.id.et_zar).setText(rate)
                    requireViewById<TextInputLayout>(R.id.et_zar_parent).helperText = date
                }
            }
        }

        saveRates()

        requireContext().putLongPref(CurrencyContract.LAST_CHECK, System.currentTimeMillis())
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        FirebaseAnalytics.getInstance(requireContext()).logEvent("change_currency", Bundle())

        calculate()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.fragment_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.menu_info -> {

                FirebaseAnalytics.getInstance(requireContext()).logEvent("view_info_dialog", null)

                val dialog = AlertDialog.Builder(requireContext())
                dialog.setTitle(R.string.menu_info)
                dialog.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_info_dark))
                dialog.setMessage(R.string.info_message)
                dialog.setPositiveButton(R.string.info_dismiss, null)
                dialog.show()

                return true
            }

            R.id.menu_coins -> {
                (requireActivity() as RewardsActivity).showTopUpDialog()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val coins = rewardViewModel.coins.value

        menu.findItem(R.id.menu_coins).title = getString(R.string.menu_coins_balance, coins ?: 0)

    }

    private fun saveRates() {
        requireContext().putStringPref(
            getString(R.string.currency_usd),
            requireViewById<TextInputEditText>(R.id.et_usd).text!!.toString()
        )
        requireContext().putStringPref(
            getString(R.string.currency_bond),
            requireViewById<TextInputEditText>(R.id.et_bond).text!!.toString()
        )
        requireContext().putStringPref(
            getString(R.string.currency_omir),
            requireViewById<TextInputEditText>(R.id.et_omir).text!!.toString()
        )
        requireContext().putStringPref(
            getString(R.string.currency_rtgs),
            requireViewById<TextInputEditText>(R.id.et_rtgs).text!!.toString()
        )
        requireContext().putStringPref(
            getString(R.string.currency_rbz),
            requireViewById<TextInputEditText>(R.id.et_rbz).text!!.toString()
        )
        requireContext().putStringPref(
            getString(R.string.currency_zar),
            requireViewById<TextInputEditText>(R.id.et_zar).text!!.toString()
        )
    }

    private fun getCalculator(): Calculator {

        val usdText = requireViewById<TextInputEditText>(R.id.et_usd)
            .text.toString()
            .ifEmpty { "1" }
            .toBigDecimalOrNull() ?: BigDecimal(1)

        val bondText = requireViewById<TextInputEditText>(R.id.et_bond)
            .text.toString()
            .ifEmpty { "1" }
            .toBigDecimalOrNull() ?: BigDecimal(1)

        val omirText = requireViewById<TextInputEditText>(R.id.et_omir)
            .text.toString()
            .ifEmpty { "1" }
            .toBigDecimalOrNull() ?: BigDecimal(1)

        val rtgsText = requireViewById<TextInputEditText>(R.id.et_rtgs)
            .text.toString()
            .ifEmpty { "1" }
            .toBigDecimalOrNull() ?: BigDecimal(1)

        val rbzText = requireViewById<TextInputEditText>(R.id.et_rbz)
            .text.toString()
            .ifEmpty { "1" }
            .toBigDecimalOrNull() ?: BigDecimal(1)

        val zarText = requireViewById<TextInputEditText>(R.id.et_zar)
            .text.toString()
            .ifEmpty { "1" }
            .toBigDecimalOrNull() ?: BigDecimal(1)

        saveRates()

        val usd = USD(usdText)
        val bond = BOND(bondText)
        val omir = OMIR(omirText)
        val rtgs = RTGS(rtgsText)
        val rbz = RBZ(rbzText)
        val zar = ZAR(zarText)

        var currency: Currency = usd
        when (requireViewById<AppCompatSpinner>(R.id.s_currency).selectedItem) {
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

    private fun calculate() {

        if (canConsumeCoins()) {

            val calculator = getCalculator()

            val amountText = requireViewById<TextInputEditText>(R.id.et_amount)
                .text.toString()
                .ifEmpty { "1" }
                .toBigDecimalOrNull()?.toPlainString() ?: "1"

            //save amount entered
            requireContext().putStringPref("amount", amountText)
            requireContext().putIntPref(
                "currency",
                requireViewById<AppCompatSpinner>(R.id.s_currency).selectedItemPosition
            )

            calculateCurrencyResult(
                R.id.txt_usd_result,
                calculator.toUSD(amountText.toBigDecimal()),
                calculator.usd
            )

            calculateCurrencyResult(
                R.id.txt_bond_result,
                calculator.toBOND(amountText.toBigDecimal()),
                calculator.bond
            )

            calculateCurrencyResult(
                R.id.txt_omir_result,
                calculator.toOMIR(amountText.toBigDecimal()),
                calculator.omir
            )

            calculateCurrencyResult(
                R.id.txt_rbz_result,
                calculator.toRBZ(amountText.toBigDecimal()),
                calculator.rbz
            )

            calculateCurrencyResult(
                R.id.txt_rtgs_result,
                calculator.toRTGS(amountText.toBigDecimal()),
                calculator.rtgs
            )

            calculateCurrencyResult(
                R.id.txt_zar_result,
                calculator.toZAR(amountText.toBigDecimal()),
                calculator.zar
            )

        }
    }

    private fun calculateCurrencyResult(
        target: Int,
        result: BigDecimal,
        currency: Currency
    ) {

        with(requireViewById<AppCompatButton>(target)) {
            setOnClickListener {

                this@FragmentCalculator.requireViewById<TextInputEditText>(R.id.et_amount).setText(
                    String.format("%10.2f", result).trim()
                )

                val selection = resources.getStringArray(R.array.currencies)
                    .indexOf(getString(currency.getName()))

                FirebaseAnalytics.getInstance(requireContext())
                    .logEvent("copy_result_for_calculation", null)

                this@FragmentCalculator.requireViewById<AppCompatSpinner>(R.id.s_currency)
                    .setSelection(selection)

            }

            text = getString(
                R.string.result,
                currency.getSign(),
                result
            )
        }
    }

    override fun onValueEntered(requestCode: Int, value: BigDecimal?) {
        try {
            when (requestCode) {
                R.id.et_usd_parent -> {
                    requireViewById<TextInputEditText>(R.id.et_usd).setText(
                        value?.toPlainString()
                    )
                }

                R.id.et_bond_parent -> {
                    requireViewById<TextInputEditText>(R.id.et_bond).setText(
                        value?.toPlainString()
                    )
                }

                R.id.et_omir_parent -> {
                    requireViewById<TextInputEditText>(R.id.et_omir).setText(
                        value?.toPlainString()
                    )
                }

                R.id.et_rtgs_parent -> {
                    requireViewById<TextInputEditText>(R.id.et_rtgs).setText(
                        value?.toPlainString()
                    )
                }

                R.id.et_rbz_parent -> {
                    requireViewById<TextInputEditText>(R.id.et_rbz).setText(
                        value?.toPlainString()
                    )
                }

                R.id.et_zar_parent -> {
                    requireViewById<TextInputEditText>(R.id.et_zar).setText(
                        value?.toPlainString()
                    )
                }

                R.id.et_amount_parent -> {
                    requireViewById<TextInputEditText>(R.id.et_amount).setText(
                        value?.toPlainString()
                    )
                }
            }

        } catch (exception: ArithmeticException) {
            Toast.makeText(requireContext(), exception.message, Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        const val TAG = "FragmentCalculator"
    }

}

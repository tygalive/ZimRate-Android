package com.tyganeutronics.myratecalculator

import com.tyganeutronics.myratecalculator.models.*

class Calculator constructor(
    var usd: USD,
    var bond: BOND,
    var omir: OMIR,
    var rtgs: RTGS,
    var rbz: RBZ,
    var zar: ZAR,
    var currency: Currency
) {

    fun toUSD(amount: Double): Double {
        return toCurrency(usd, amount)
    }

    fun toOMIR(amount: Double): Double {
        return toCurrency(omir, amount)
    }

    fun toBOND(amount: Double): Double {
        return toCurrency(bond, amount)
    }

    fun toRTGS(amount: Double): Double {
        return toCurrency(rtgs, amount)
    }

    fun toRBZ(amount: Double): Double {
        return toCurrency(rbz, amount)
    }

    fun toZAR(amount: Double): Double {
        return toCurrency(zar, amount)
    }

    protected fun toCurrency(current: Currency, amount: Double): Double {
        return amount.times(current.rate).div(currency.rate)
    }

}
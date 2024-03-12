package com.tyganeutronics.myratecalculator

import com.tyganeutronics.myratecalculator.database.BOND
import com.tyganeutronics.myratecalculator.database.Currency
import com.tyganeutronics.myratecalculator.database.OMIR
import com.tyganeutronics.myratecalculator.database.RBZ
import com.tyganeutronics.myratecalculator.database.RTGS
import com.tyganeutronics.myratecalculator.database.USD
import com.tyganeutronics.myratecalculator.database.ZAR
import java.math.BigDecimal

open class Calculator constructor(
    var usd: USD,
    var bond: BOND,
    var omir: OMIR,
    var rtgs: RTGS,
    var rbz: RBZ,
    var zar: ZAR,
    var currency: Currency
) {

    fun toUSD(amount: BigDecimal): BigDecimal {
        return toCurrency(usd, amount)
    }

    fun toOMIR(amount: BigDecimal): BigDecimal {
        return toCurrency(omir, amount)
    }

    fun toBOND(amount: BigDecimal): BigDecimal {
        return toCurrency(bond, amount)
    }

    fun toRTGS(amount: BigDecimal): BigDecimal {
        return toCurrency(rtgs, amount)
    }

    fun toRBZ(amount: BigDecimal): BigDecimal {
        return toCurrency(rbz, amount)
    }

    fun toZAR(amount: BigDecimal): BigDecimal {
        return toCurrency(zar, amount)
    }

    private fun toCurrency(current: Currency, amount: BigDecimal): BigDecimal {
        val rate = currency.rate.takeUnless { it == BigDecimal.ZERO } ?: BigDecimal.ONE

        return amount.times(current.rate).div(rate)
    }

}
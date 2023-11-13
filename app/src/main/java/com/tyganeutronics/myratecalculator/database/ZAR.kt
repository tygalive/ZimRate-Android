package com.tyganeutronics.myratecalculator.database

import com.tyganeutronics.myratecalculator.R
import java.math.BigDecimal

class ZAR constructor(id: String, rate: BigDecimal) : Currency(id, rate) {

    constructor(rate: BigDecimal) : this("RAND", rate)

    override fun getSign(): String {
        return "R"
    }

    override fun getName(): Int {
        return R.string.currency_zar
    }
}
package com.tyganeutronics.myratecalculator.database

import com.tyganeutronics.myratecalculator.R
import java.math.BigDecimal

class RTGS constructor(id: String, rate: BigDecimal) : Currency(id, rate) {

    constructor(rate: BigDecimal) : this("RTGS", rate)

    override fun getSign(): String {
        return "$"
    }

    override fun getName(): Int {
        return R.string.currency_rtgs
    }
}
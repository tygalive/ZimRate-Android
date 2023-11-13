package com.tyganeutronics.myratecalculator.database

import com.tyganeutronics.myratecalculator.R
import java.math.BigDecimal

class BOND constructor(id: String, rate: BigDecimal) : Currency(id, rate) {

    constructor(rate: BigDecimal) : this("BOND", rate)

    override fun getSign(): String {
        return "$"
    }

    override fun getName(): Int {
        return R.string.currency_bond
    }
}
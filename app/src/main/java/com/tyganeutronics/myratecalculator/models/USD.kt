package com.tyganeutronics.myratecalculator.models

import com.tyganeutronics.myratecalculator.R

class USD constructor(id: String, rate: Double) : Currency(id, rate) {

    constructor(rate: Double) : this("USD", rate)

    override fun getSign(): String {
        return "$"
    }

    override fun getName(): Int {
        return R.string.currency_usd
    }
}
package com.tyganeutronics.myratecalculator.database

import com.tyganeutronics.myratecalculator.R

class RTGS constructor(id: String, rate: Double) : Currency(id, rate) {

    constructor(rate: Double) : this("RTGS", rate)

    override fun getSign(): String {
        return "$"
    }

    override fun getName(): Int {
        return R.string.currency_rtgs
    }
}
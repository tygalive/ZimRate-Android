package com.tyganeutronics.myratecalculator.models

import com.tyganeutronics.myratecalculator.R

class RAND constructor(id: String, rate: Double) : Currency(id, rate) {

    constructor(rate: Double) : this("RAND", rate)

    override fun getSign(): String {
        return "R"
    }

    override fun getName(): Int {
        return R.string.currency_rand
    }
}
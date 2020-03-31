package com.tyganeutronics.myratecalculator.models

import com.tyganeutronics.myratecalculator.R

class RBZ constructor(id: String, rate: Double) : Currency(id, rate) {

    constructor(rate: Double) : this("RBZ", rate)

    override fun getSign(): String {
        return "$"
    }

    override fun getName(): Int {
        return R.string.currency_rbz
    }
}
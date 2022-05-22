package com.tyganeutronics.myratecalculator.database

abstract class Currency constructor(var id: String, var rate: Double) {

    abstract fun getSign(): String
    abstract fun getName(): Int

    /**
     * add and remove currencies
     *
     * currency code
     * currency name
     *
     * last updated
     *
     * source => manual, auto, max, min, average
     *
     * auto update
     * symbol
     * rate against USD
     *
     *
     */

}
package com.tyganeutronics.myratecalculator.database

import java.math.BigDecimal

abstract class Currency constructor(var id: String, var rate: BigDecimal) {

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
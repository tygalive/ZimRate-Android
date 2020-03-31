package com.tyganeutronics.myratecalculator.models

abstract class Currency constructor(var id: String, var rate: Double) {

    abstract fun getSign(): String
    abstract fun getName(): Int
}
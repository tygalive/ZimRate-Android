package com.tyganeutronics.myratecalculator.models

class USD constructor(id: String, rate: Double) : Currency(id, rate) {

    constructor(rate: Double) : this("USD", rate)
}
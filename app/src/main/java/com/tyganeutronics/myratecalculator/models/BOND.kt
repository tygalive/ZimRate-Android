package com.tyganeutronics.myratecalculator.models

class BOND constructor(id: String, rate: Double) : Currency(id, rate) {

    constructor(rate: Double) : this("BOND", rate)
}
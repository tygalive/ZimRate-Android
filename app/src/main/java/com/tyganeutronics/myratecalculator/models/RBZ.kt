package com.tyganeutronics.myratecalculator.models

class RBZ constructor(id: String, rate: Double) : Currency(id, rate) {

    constructor(rate: Double) : this("RBZ", rate)
}
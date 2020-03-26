package com.tyganeutronics.myratecalculator.models

class RTGS constructor(id: String,  rate: Double) : Currency(id, rate) {

    constructor(   rate: Double) : this ("RTGS", rate)
}
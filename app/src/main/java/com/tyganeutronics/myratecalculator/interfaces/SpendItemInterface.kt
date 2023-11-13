package com.tyganeutronics.myratecalculator.interfaces

import com.tyganeutronics.myratecalculator.database.entities.SpendEntity

interface SpendItemInterface {

    val items: List<SpendEntity>
}
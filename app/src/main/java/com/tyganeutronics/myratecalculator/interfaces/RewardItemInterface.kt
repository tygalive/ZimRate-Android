package com.tyganeutronics.myratecalculator.interfaces

import com.tyganeutronics.myratecalculator.database.entities.RewardEntity

interface RewardItemInterface {

    val items: List<RewardEntity>

}
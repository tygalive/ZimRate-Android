package com.tyganeutronics.myratecalculator.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tyganeutronics.myratecalculator.database.contract.DatabaseContract
import com.tyganeutronics.myratecalculator.database.dao.RatesDao
import com.tyganeutronics.myratecalculator.database.dao.RewardsDao
import com.tyganeutronics.myratecalculator.database.dao.SpendsDao
import com.tyganeutronics.myratecalculator.database.entities.RateEntity
import com.tyganeutronics.myratecalculator.database.entities.RewardEntity
import com.tyganeutronics.myratecalculator.database.entities.SpendEntity

@Database(
    entities = [RateEntity::class, RewardEntity::class, SpendEntity::class],
    version = DatabaseContract.DATABASE_VERSION,
    autoMigrations = [

    ]
)
abstract class Database : RoomDatabase() {
    abstract fun rates(): RatesDao

    abstract fun rewards(): RewardsDao

    abstract fun spends(): SpendsDao
}
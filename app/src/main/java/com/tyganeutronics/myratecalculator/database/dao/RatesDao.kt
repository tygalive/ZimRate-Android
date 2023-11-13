package com.tyganeutronics.myratecalculator.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tyganeutronics.myratecalculator.database.entities.RateEntity

@Dao
interface RatesDao {

    @Query("SELECT * FROM `rates`")
    fun getAll(): List<RateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(rateEntity: RateEntity): Long?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(rateEntity: RateEntity)

    @Delete
    fun delete(rateEntity: RateEntity)
}
package com.tyganeutronics.myratecalculator.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tyganeutronics.myratecalculator.database.entities.SpendEntity

@Dao
interface SpendsDao {

    @Query("SELECT * FROM `purchases` ORDER BY `created_at` DESC")
    fun getAll(): List<SpendEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(spendEntity: SpendEntity): Long?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(spendEntity: SpendEntity)

    @Delete
    fun delete(spendEntity: SpendEntity)

    @Query("DELETE FROM `purchases` WHERE DATETIME(`created_at`, 'unixepoch') < DATETIME('now', '-6 months')")
    fun cleanExpired()

}
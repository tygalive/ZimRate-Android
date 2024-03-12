package com.tyganeutronics.myratecalculator.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tyganeutronics.myratecalculator.database.contract.RewardContract
import com.tyganeutronics.myratecalculator.database.entities.RewardEntity

@Dao
interface RewardsDao {

    @Query("SELECT * FROM `rewards` WHERE DATETIME(`created_at`, 'unixepoch') BETWEEN DATETIME('now', 'start of day', (CAST(:day AS INTEGER) * -1) || ' days') AND DATETIME('now', '+1 day', 'start of day', (CAST(:day AS INTEGER) * -1) || ' days') AND `type` = :type")
    fun getDayRewards(
        day: Int = 0,
        type: String = RewardContract.TYPES.CLOCK_IN
    ): List<RewardEntity>

    @Query("SELECT MAX(`created_at`) FROM (SELECT `created_at` FROM `rates` UNION SELECT `created_at` FROM `rewards` UNION SELECT `created_at` FROM `purchases` UNION SELECT CAST(STRFTIME('%s', DATETIME('now', 'start of day')) AS INTEGER) AS `created_at`)")
    fun getLatestDate(): Long

    @Query("SELECT * FROM `rewards` WHERE DATETIME(`expires_at`, 'unixepoch') >= DATETIME('now') AND `balance` > 0 ORDER BY `expires_at`, `created_at`")
    fun getActive(): List<RewardEntity>

    @Query("SELECT * FROM `rewards` WHERE DATETIME(`expires_at`, 'unixepoch') >= DATETIME('now') AND `type` = :type AND `balance` > 0 ORDER BY `expires_at`, `created_at` DESC")
    fun getType(type: String = RewardContract.TYPES.PURCHASE): List<RewardEntity>

    @Query("SELECT TOTAL(`balance`) FROM `rewards` WHERE DATETIME(`expires_at`, 'unixepoch') >= DATETIME('now')")
    fun liveTokenBalance(): LiveData<Long>

    @Query("SELECT TOTAL(`balance`) FROM `rewards` WHERE DATETIME(`expires_at`, 'unixepoch') >= DATETIME('now') AND `type` = :type")
    fun tokenTypeBalance(type: String = RewardContract.TYPES.PURCHASE): Long

    @Query("SELECT * FROM `rewards` WHERE DATETIME(`expires_at`, 'unixepoch') >= DATETIME('now') AND `balance` > 0 ORDER BY `expires_at` ASC LIMIT 1")
    fun oldestActiveReward(): RewardEntity?

    @Query("SELECT * FROM `rewards` ORDER BY `expires_at` DESC LIMIT 1")
    fun longestLivingReward(): RewardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(rewardEntity: RewardEntity): Long?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(rewardEntity: RewardEntity)

    @Delete
    fun delete(rewardEntity: RewardEntity)

    @Query("DELETE FROM `rewards` WHERE DATETIME(`expires_at`, 'unixepoch') < DATETIME('now', '-6 months')")
    fun cleanExpired()
}
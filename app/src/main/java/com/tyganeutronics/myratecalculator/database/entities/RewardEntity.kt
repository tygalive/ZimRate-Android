package com.tyganeutronics.myratecalculator.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.TypeConverters
import com.tyganeutronics.myratecalculator.database.Database
import com.tyganeutronics.myratecalculator.database.contract.RewardContract
import com.tyganeutronics.myratecalculator.database.converter.InstantConverter
import java.time.Instant

@Entity(tableName = RewardContract.TABLE_NAME)
@TypeConverters(value = [InstantConverter::class])
class RewardEntity : BaseEntity() {

    @ColumnInfo(name = RewardContract.COLUMN_NAME_REMOTE_ID)
    var remoteId: Long = 0

    @ColumnInfo(name = RewardContract.COLUMN_NAME_AMOUNT)
    var amount: Long = 0

    @ColumnInfo(name = RewardContract.COLUMN_NAME_BALANCE)
    var balance: Long = 0

    @ColumnInfo(name = RewardContract.COLUMN_NAME_TYPE)
    var type: String = ""

    @ColumnInfo(name = RewardContract.COLUMN_NAME_DESCRIPTION)
    var description: String = ""

    @ColumnInfo(name = RewardContract.COLUMN_NAME_EXPIRES_AT)
    var expiresAt: Instant = Instant.now()

    @ColumnInfo(name = RewardContract.COLUMN_NAME_DIRTY, defaultValue = "1")
    var dirty: Boolean = true

    override fun doInsert(database: Database) {
        database.rewards().insert(this)
    }

    override fun doUpdate(database: Database) {
        database.rewards().update(this)
    }

    override fun doDelete(database: Database) {
        database.rewards().delete(this)
    }

}
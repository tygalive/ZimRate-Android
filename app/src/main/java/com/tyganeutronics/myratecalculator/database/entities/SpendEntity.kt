package com.tyganeutronics.myratecalculator.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.TypeConverters
import com.tyganeutronics.myratecalculator.database.Database
import com.tyganeutronics.myratecalculator.database.contract.PurchasesContract
import com.tyganeutronics.myratecalculator.database.converter.InstantConverter
import com.tyganeutronics.myratecalculator.database.converter.LongListConverter

@Entity(tableName = PurchasesContract.TABLE_NAME)
@TypeConverters(value = [InstantConverter::class, LongListConverter::class])
class SpendEntity : BaseEntity() {

    @ColumnInfo(name = PurchasesContract.COLUMN_NAME_REMOTE_ID)
    var remoteId: Long = 0

    @ColumnInfo(name = PurchasesContract.COLUMN_NAME_ASSOCIATED_REWARDS)
    var rewards: List<Long> = emptyList()

    @ColumnInfo(name = PurchasesContract.COLUMN_NAME_AMOUNT)
    var amount: Long = 0

    @ColumnInfo(name = PurchasesContract.COLUMN_NAME_TYPE)
    var type: String = ""

    @ColumnInfo(name = PurchasesContract.COLUMN_NAME_DESCRIPTION)
    var description: String = ""

    @ColumnInfo(name = PurchasesContract.COLUMN_NAME_DIRTY)
    var dirty: Boolean = true

    override fun doInsert(database: Database) {
        database.spends().insert(this)
    }

    override fun doUpdate(database: Database) {
        database.spends().update(this)
    }

    override fun doDelete(database: Database) {
        database.spends().delete(this)
    }
}
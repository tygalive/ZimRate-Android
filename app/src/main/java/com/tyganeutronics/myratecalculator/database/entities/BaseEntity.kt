package com.tyganeutronics.myratecalculator.database.entities

import android.provider.BaseColumns
import androidx.room.ColumnInfo
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.tyganeutronics.myratecalculator.AppZimRate
import com.tyganeutronics.myratecalculator.database.Database
import com.tyganeutronics.myratecalculator.database.contract.BaseContract
import com.tyganeutronics.myratecalculator.utils.traits.optInstant
import com.tyganeutronics.myratecalculator.utils.traits.putInstant
import org.json.JSONException
import org.json.JSONObject
import java.time.Instant

abstract class BaseEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = BaseColumns._ID)
    var id: Long = 0

    @ColumnInfo(name = BaseContract.COLUMN_NAME_CREATED_AT)
    var createdAt: Instant = Instant.MIN

    @ColumnInfo(
        name = BaseContract.COLUMN_NAME_UPDATED_AT,
        defaultValue = "0"
    )
    var updatedAt: Instant = Instant.MIN

    protected abstract fun doInsert(database: Database)

    protected abstract fun doUpdate(database: Database)

    protected abstract fun doDelete(database: Database)

    fun insertInstantly() {
        AppZimRate.database.let {
            if (this.createdAt == Instant.MIN) this.createdAt = Instant.now()
            if (this.updatedAt == Instant.MIN) this.updatedAt = Instant.now()
            doInsert(it)
        }
    }

    fun updateInstantly() {
        AppZimRate.database.let {
            if (this.updatedAt == Instant.MIN) this.updatedAt = Instant.now()
            doUpdate(it)
        }
    }

    fun deleteInstantly() {
        AppZimRate.database.let {
            doDelete(it)
        }
    }

    fun insert() {
        AppZimRate.database.let {
            it.transactionExecutor.execute {
                if (this.createdAt == Instant.MIN) this.createdAt = Instant.now()
                if (this.updatedAt == Instant.MIN) this.updatedAt = Instant.now()
                insertInstantly()
            }
        }
    }

    fun update() {
        AppZimRate.database.let {
            it.transactionExecutor.execute {
                if (this.updatedAt == Instant.MIN) this.updatedAt = Instant.now()
                updateInstantly()
            }
        }
    }

    fun delete() {
        AppZimRate.database.let {
            it.transactionExecutor.execute {
                deleteInstantly()
            }
        }
    }

    fun save() {
        if (this.id == 0L) {
            this.insert()
        } else {
            this.update()
        }
    }

    fun saveInstantly() {
        if (this.id == 0L) {
            this.insertInstantly()
        } else {
            this.updateInstantly()
        }
    }

    open fun toJson(): String {
        val jsonObject = JSONObject()
        try {
            //id
            jsonObject.put(BaseColumns._ID, id)

            //dates
            jsonObject.putInstant(BaseContract.COLUMN_NAME_CREATED_AT, createdAt)
            jsonObject.putInstant(BaseContract.COLUMN_NAME_UPDATED_AT, updatedAt)

        } catch (je: JSONException) {
            je.printStackTrace()
        }
        return jsonObject.toString()
    }

    @Ignore
    open fun fromJson(json: String) {
        try {
            val jsonObject = JSONObject(json)

            //id
            id = jsonObject.optLong(BaseColumns._ID, 0)

            //dates
            createdAt = jsonObject
                .optInstant(BaseContract.COLUMN_NAME_CREATED_AT)
                .coerceAtMost(Instant.now())
            updatedAt = jsonObject
                .optInstant(BaseContract.COLUMN_NAME_UPDATED_AT)
                .coerceAtMost(Instant.now())

        } catch (je: JSONException) {
            je.printStackTrace()
        }
    }

}
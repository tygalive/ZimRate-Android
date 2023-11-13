package com.tyganeutronics.myratecalculator.database.contract

object PurchasesContract {

    const val TABLE_NAME = "purchases"

    const val COLUMN_NAME_REMOTE_ID = "remote_id"

    const val COLUMN_NAME_AMOUNT = "amount"

    const val COLUMN_NAME_ASSOCIATED_REWARDS = "rewards"

    const val COLUMN_NAME_TYPE = "type"

    const val COLUMN_NAME_DESCRIPTION = "description"

    const val COLUMN_NAME_DIRTY = "dirty"

    object TYPES {
        const val DATA_FETCH = "data_fetch"
        const val CALCULATION = "calculation"
    }

}
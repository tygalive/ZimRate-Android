package com.tyganeutronics.myratecalculator.database.contract

object RewardContract {

    const val TABLE_NAME = "rewards"

    const val COLUMN_NAME_REMOTE_ID = "remote_id"

    const val COLUMN_NAME_AMOUNT = "amount"

    const val COLUMN_NAME_BALANCE = "balance"

    const val COLUMN_NAME_TYPE = "type"

    const val COLUMN_NAME_DESCRIPTION = "description"

    const val COLUMN_NAME_EXPIRES_AT = "expires_at"

    const val COLUMN_NAME_DIRTY = "dirty"

    object TYPES {

        const val CLOCK_IN = "clock-in"
        const val STARTER_PACK = "starter-pack"
        const val PURCHASE = "purchase"

        const val BANNER_CLICK = "banner-click"
        const val WATCH_ADVERT = "watch-advert"

    }

}
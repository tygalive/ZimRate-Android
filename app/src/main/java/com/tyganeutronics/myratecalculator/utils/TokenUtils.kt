package com.tyganeutronics.myratecalculator.utils

import android.content.Context
import com.tyganeutronics.myratecalculator.AppZimrate
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

object TokenUtils {

    fun canShowAds(context: Context): Boolean {
        return hasNoPaidTokens() && installOlderThan(context)
    }

    fun hasNoPaidTokens(): Boolean {
        return AppZimrate.database.rewards().tokenTypeBalance() <= 0
    }

    fun installOlderThan(context: Context, days: Long = 3): Boolean {
        val installDate = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(
                context.packageManager.getPackageInfo(context.packageName, 0).firstInstallTime
            ), ZoneOffset.UTC
        )

        return LocalDateTime.now()
            .minusDays(days)
            .isAfter(installDate)
    }

    fun hasLowTokenBalance(): Boolean {
        return AppZimrate.database.rewards().tokenBalance() < 5
    }

}
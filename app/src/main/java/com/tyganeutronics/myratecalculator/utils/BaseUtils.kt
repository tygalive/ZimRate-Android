package com.tyganeutronics.myratecalculator.utils

import android.content.Context
import com.tyganeutronics.myratecalculator.AppZimrate
import com.tyganeutronics.myratecalculator.BuildConfig
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

object BaseUtils {

    val isPlayBuild: Boolean
        get() = BuildConfig.FLAVOR == "playstore"

    val isSamsungBuild: Boolean
        get() = BuildConfig.FLAVOR == "samsungstore"

    val isOtherBuild: Boolean
        get() = BuildConfig.FLAVOR == "otherstore"

    val isProductionBuild: Boolean
        get() = !BuildConfig.DEBUG

    fun canShowAds(context: Context): Boolean {
        val installDate = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(
                context.packageManager.getPackageInfo(context.packageName, 0).firstInstallTime
            ), ZoneOffset.UTC
        )

        val hasNoPaidTokens = AppZimrate.database.rewards().tokenTypeBalance() <= 0

        return hasNoPaidTokens && LocalDateTime.now()
            .minusDays(3)
            .isAfter(installDate)
    }
}
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
    
}
package com.tyganeutronics.myratecalculator.utils

import com.tyganeutronics.myratecalculator.BuildConfig

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
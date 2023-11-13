package com.tyganeutronics.myratecalculator.utils

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

object DateUtils {

    fun systemDateTime(): ZonedDateTime {
        return ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES)
            .withZoneSameLocal(ZoneId.systemDefault())
    }
}
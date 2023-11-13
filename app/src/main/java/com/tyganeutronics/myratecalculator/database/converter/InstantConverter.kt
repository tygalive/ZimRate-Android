package com.tyganeutronics.myratecalculator.database.converter

import androidx.room.TypeConverter
import java.time.Instant

class InstantConverter {
    @TypeConverter
    fun secondToDate(seconds: Long): Instant {
        return Instant.ofEpochSecond(seconds)
    }

    @TypeConverter
    fun dateToSecond(instant: Instant): Long {
        return instant.epochSecond
    }
}
package com.tyganeutronics.myratecalculator.database.converter

import androidx.room.TypeConverter

class LongListConverter {
    @TypeConverter
    fun listToString(array: List<Long>): String {
        return array.joinToString(",")
    }

    @TypeConverter
    fun stringToList(string: String): List<Long> {
        return string.split(",").filter { it.isNotBlank() }.map { it.trim().toLong() }.toList()
    }
}
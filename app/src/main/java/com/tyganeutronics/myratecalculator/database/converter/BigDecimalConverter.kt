package com.tyganeutronics.myratecalculator.database.converter

import androidx.room.TypeConverter
import java.math.BigDecimal

class BigDecimalConverter {
    @TypeConverter
    fun bigDecimalToString(bigDecimal: BigDecimal): String {
        return bigDecimal.toPlainString()
    }

    @TypeConverter
    fun stringToBigDecimal(string: String): BigDecimal {
        return BigDecimal(string)
    }
}
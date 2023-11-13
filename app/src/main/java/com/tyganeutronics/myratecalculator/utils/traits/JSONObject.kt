package com.tyganeutronics.myratecalculator.utils.traits

import org.json.JSONObject
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * JSON
 */
fun JSONObject.optInstant(name: String, fallback: Instant = Instant.now()): Instant {
    return Instant.ofEpochSecond(optLong(name, fallback.epochSecond))
}

fun JSONObject.getInstant(name: String): Instant {
    return Instant.ofEpochSecond(getLong(name))
}

fun JSONObject.putInstant(name: String, value: Instant): JSONObject {
    put(name, value.epochSecond)
    return this
}

fun JSONObject.optBigDecimal(name: String, fallback: BigDecimal = BigDecimal(0)): BigDecimal {
    return BigDecimal(optString(name, fallback.toPlainString()))
}

fun JSONObject.getBigDecimal(name: String): BigDecimal {
    return BigDecimal(getString(name))
}

fun JSONObject.putBigDecimal(name: String, value: BigDecimal): JSONObject {
    put(name, value.toPlainString())
    return this
}

fun JSONObject.optZonedDateTime(
    name: String,
    fallback: ZonedDateTime = ZonedDateTime.now()
): ZonedDateTime {
    return ZonedDateTime.parse(
        optString(
            name,
            fallback.format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
        ), DateTimeFormatter.ISO_ZONED_DATE_TIME
    )
}

fun JSONObject.getZonedDateTime(name: String): ZonedDateTime {
    return ZonedDateTime.parse(getString(name), DateTimeFormatter.ISO_ZONED_DATE_TIME)
}

fun JSONObject.putZonedDateTime(name: String, value: ZonedDateTime): JSONObject {
    put(name, value.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
    return this
}

fun JSONObject.optDuration(name: String, fallback: Duration = Duration.ZERO): Duration {
    return Duration.ofSeconds(optLong(name, fallback.seconds))
}

fun JSONObject.getDuration(name: String): Duration {
    return Duration.ofSeconds(getLong(name))
}

fun JSONObject.putDuration(name: String, value: Duration): JSONObject {
    put(name, value.seconds)
    return this
}

fun JSONObject.merge(`object`: JSONObject) {
    val keys = `object`.keys()
    while (keys.hasNext()) {
        keys.next().let { key ->
            put(key, `object`.opt(key))
        }
    }
}
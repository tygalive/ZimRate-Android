package com.tyganeutronics.myratecalculator.utils.traits

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

/**
 * Preferences
 */
fun Context.putStringPref(key: String, value: String) {
    getPrefs().edit().putString(key, value).apply()
}

fun Context.putIntPref(key: String, value: Int) {
    getPrefs().edit().putInt(key, value).apply()
}

fun Context.putLongPref(key: String, value: Long) {
    getPrefs().edit().putLong(key, value).apply()
}

fun Context.putFloatPref(key: String, value: Float) {
    getPrefs().edit().putFloat(key, value).apply()
}

fun Context.putBooleanPref(key: String, value: Boolean) {
    getPrefs().edit().putBoolean(key, value).apply()
}

fun Context.toggleBooleanPref(key: String, defValue: Boolean) {
    getPrefs().edit().putBoolean(key, getBooleanPref(key, defValue).not()).apply()
}

fun Context.removePref(key: String) {
    getPrefs().edit().remove(key).apply()
}

fun Context.getStringPref(key: String, defValue: String = ""): String {
    return getPrefs().getString(key, defValue)!!
}

fun Context.getIntPref(key: String, defValue: Int): Int {
    return getPrefs().getInt(key, defValue)
}

fun Context.getLongPref(key: String, defValue: Long): Long {
    return getPrefs().getLong(key, defValue)
}

fun Context.getFloatPref(key: String, defValue: Float): Float {
    return getPrefs().getFloat(key, defValue)
}

fun Context.getBooleanPref(key: String, defValue: Boolean): Boolean {
    return getPrefs().getBoolean(key, defValue)
}

fun Context.containsPref(key: String): Boolean {
    return getPrefs().contains(key)
}

fun Context.getPrefs(): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(this)
}
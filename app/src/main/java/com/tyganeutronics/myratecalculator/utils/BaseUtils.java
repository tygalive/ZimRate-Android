package com.tyganeutronics.myratecalculator.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.TypedValue;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorRes;
import androidx.preference.PreferenceManager;

import com.tyganeutronics.myratecalculator.BuildConfig;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class BaseUtils {

    public static SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Contract(pure = true)
    public static @NotNull Boolean isPlayBuild() {
        //noinspection ConstantConditions
        return BuildConfig.FLAVOR.equals("playstore");
    }

    @Contract(pure = true)
    public static @NotNull Boolean isSamsungBuild() {
        //noinspection ConstantConditions
        return BuildConfig.FLAVOR.equals("samsungstore");
    }

    @Contract(pure = true)
    public static @NotNull Boolean isOtherBuild() {
        //noinspection ConstantConditions
        return BuildConfig.FLAVOR.equals("otherstore");
    }

}

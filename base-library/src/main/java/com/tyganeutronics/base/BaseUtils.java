package com.tyganeutronics.base;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import java.util.Locale;

public class BaseUtils {

    public static SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * formats any text passed to it in the form ##,###,###
     *
     * @param text Text to format
     */
    @NonNull
    public static String formatAsNumber(@NonNull String text) {

        Integer commas = BaseUtils.floor(text.length() - 1, 3);

        StringBuilder builder = new StringBuilder(text);

        for (int i = 1; i <= commas; i++) {
            builder.insert(text.length() - (i * 3), ",");
        }

        return builder.toString();

    }

    /**
     * formats any long value passed to it in the form ##,###,###
     *
     * @param value Long Text to format
     */
    @NonNull
    public static String formatAsNumber(Long value) {
        return String.format(Locale.getDefault(), "%,d", value);
    }


    /**
     * formats any long value passed to it in the form ##,###,###
     *
     * @param value Long Text to format
     */
    @NonNull
    public static String formatAsNumber(@NonNull Integer value) {
        return BaseUtils.formatAsNumber(value.longValue());
    }

    @NonNull
    private static Integer floor(@NonNull Integer x, @NonNull Integer y) {
        Double result = Math.floor(x.floatValue() / y.floatValue());
        return result.intValue();
    }

    public static String getLineSeparator(){
        return System.getProperty("line.separator");
    }

}

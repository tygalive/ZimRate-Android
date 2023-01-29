package com.tyganeutronics.myratecalculator.contract

import android.content.Context
import android.os.BatteryManager
import android.os.Build
import com.tyganeutronics.myratecalculator.BuildConfig

object ApiContract {

    fun getRatesUrl(context: Context): String{
        return "https://zimrate.tyganeutronics.com/api/graphql"
    }

    private fun getBaseApiUrl(context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && (context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager).isCharging
            && BuildConfig.BUILD_TYPE == "debug"
        ) {
            "http://192.168.81.155/wordpress/api/v1"
        } else {
            "https://zimrate.tyganeutronics.com/api/v1"
            //"http://afrorate.tyganeutronics.com/wp-json/afrorate/v3/rate"
        }
    }

}
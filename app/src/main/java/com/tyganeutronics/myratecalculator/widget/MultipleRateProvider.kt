package com.tyganeutronics.myratecalculator.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import com.google.firebase.analytics.FirebaseAnalytics
import com.tyganeutronics.myratecalculator.R
import com.tyganeutronics.myratecalculator.database.BOND
import com.tyganeutronics.myratecalculator.database.Currency
import com.tyganeutronics.myratecalculator.database.OMIR
import com.tyganeutronics.myratecalculator.database.RBZ
import com.tyganeutronics.myratecalculator.database.RTGS
import com.tyganeutronics.myratecalculator.database.USD
import com.tyganeutronics.myratecalculator.database.ZAR
import com.tyganeutronics.myratecalculator.fragments.main.FragmentCalculator
import com.tyganeutronics.myratecalculator.utils.contracts.CurrencyContract
import com.tyganeutronics.myratecalculator.utils.traits.getLongPref
import com.tyganeutronics.myratecalculator.utils.traits.getStringPref
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class MultipleRateProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        if (appWidgetIds != null) {
            for (appWidgetId in appWidgetIds) {

                if (context != null && appWidgetManager != null) {
                    updateWidget(context, appWidgetManager, appWidgetId)
                }

            }
        }
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)

        if (context != null) {
            FirebaseAnalytics.getInstance(context).logEvent("add_multiple_widget", Bundle())
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if (context != null) {

            val appWidgetManager = AppWidgetManager.getInstance(context)

            val componentName = ComponentName(context, MultipleRateProvider::class.java)

            val appWidgetIds: IntArray? = appWidgetManager.getAppWidgetIds(componentName)

            if (appWidgetIds != null) {

                for (appWidgetId in appWidgetIds) {
                    updateWidget(context, appWidgetManager, appWidgetId)
                }
            }
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {

        val views = RemoteViews(context.packageName, R.layout.widget_multiple)

        //rates
        views.setTextViewText(
            R.id.txt_usd,
            getStoredValue(
                context, USD(BigDecimal(1))
            )
        )
        views.setTextViewText(
            R.id.txt_bond,
            getStoredValue(
                context, BOND(BigDecimal(1))
            )
        )
        views.setTextViewText(
            R.id.txt_omir,
            getStoredValue(
                context, OMIR(BigDecimal(1))
            )
        )
        views.setTextViewText(
            R.id.txt_rtgs,
            getStoredValue(
                context, RTGS(BigDecimal(1))
            )
        )
        views.setTextViewText(
            R.id.txt_rbz,
            getStoredValue(
                context, RBZ(BigDecimal(1))
            )
        )
        views.setTextViewText(
            R.id.txt_zar,
            getStoredValue(
                context, ZAR(BigDecimal(1))
            )
        )

        //date
        val last = context.getLongPref(
            CurrencyContract.LAST_CHECK,
            System.currentTimeMillis()
        )

        val instant = Instant.ofEpochMilli(last)
        val format =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
        val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(format)

        views.setTextViewText(R.id.txt_date_checked, date)

        //pending intent
        val intent = Intent(context, FragmentCalculator::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        views.setOnClickPendingIntent(R.id.widget_main, pendingIntent)

        //update widget
        appWidgetManager.updateAppWidget(appWidgetId, views)

    }

    private fun getStoredValue(context: Context, currency: Currency): String {
        val key = context.getString(currency.getName())

        return context.getString(
            R.string.result,
            currency.getSign(),
            context.getStringPref(key, "1").ifEmpty { "1" }.toBigDecimal()
        )
    }
}
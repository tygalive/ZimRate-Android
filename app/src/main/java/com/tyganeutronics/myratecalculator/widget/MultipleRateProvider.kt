package com.tyganeutronics.myratecalculator.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.tyganeutronics.base.BaseUtils
import com.tyganeutronics.myratecalculator.R
import com.tyganeutronics.myratecalculator.contract.CurrencyContract
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle


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
            R.id.txt_bond,
            getStoredValue(
                context, context.getString(R.string.currency_bond)
            )
        )
        views.setTextViewText(
            R.id.txt_omir,
            getStoredValue(
                context, context.getString(R.string.currency_omir)
            )
        )
        views.setTextViewText(
            R.id.txt_rtgs,
            getStoredValue(
                context, context.getString(R.string.currency_rtgs)
            )
        )
        views.setTextViewText(
            R.id.txt_rbz,
            getStoredValue(
                context, context.getString(R.string.currency_rbz)
            )
        )
        views.setTextViewText(
            R.id.txt_zar,
            getStoredValue(
                context, context.getString(R.string.currency_zar)
            )
        )

        //date
        val last = BaseUtils.getPrefs(context).getLong(
            CurrencyContract.LAST_CHECK,
            System.currentTimeMillis()
        )

        val instant = Instant.ofEpochMilli(last)
        val format =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
        val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(format)

        views.setTextViewText(R.id.txt_date_checked, date)

        //update widget
        appWidgetManager.updateAppWidget(appWidgetId, views)

    }

    private fun getStoredValue(context: Context, key: String): String? {
        return String.format("%10.2f", BaseUtils.getPrefs(context).getString(key, "1")?.toDouble())
    }
}
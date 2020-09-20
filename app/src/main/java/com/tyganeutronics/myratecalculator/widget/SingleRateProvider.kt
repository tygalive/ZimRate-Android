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
import com.tyganeutronics.myratecalculator.activities.MainActivity
import com.tyganeutronics.myratecalculator.utils.BaseUtils


class SingleRateProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        if (appWidgetIds != null) {
            for (appWidgetId in appWidgetIds) {

                if (context != null) {
                    updateWidget(context, appWidgetId)
                }

            }
        }
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)

        if (context != null) {
            FirebaseAnalytics.getInstance(context).logEvent("add_single_widget", Bundle())
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if (context != null) {

            val appWidgetManager = AppWidgetManager.getInstance(context)

            val componentName = ComponentName(context, SingleRateProvider::class.java)

            val appWidgetIds: IntArray? = appWidgetManager.getAppWidgetIds(componentName)

            if (appWidgetIds != null) {

                for (appWidgetId in appWidgetIds) {
                    updateWidget(context, appWidgetId)
                }
            }
        }
    }

    private fun updateWidget(context: Context, appWidgetId: Int) {

        val views = RemoteViews(context.packageName, R.layout.widget_single)

        val currency = BaseUtils.getPrefs(context)
            .getString("widget-$appWidgetId", context.getString(R.string.currency_rbz))

        val value = String.format(
            "%10.2f",
            BaseUtils.getPrefs(context).getString(currency, "1")?.toDouble()
        )

        //set values
        views.setTextViewText(R.id.tv_rate, value)
        views.setTextViewText(R.id.tv_rate_title, currency)

        //pending intent
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        views.setOnClickPendingIntent(R.id.widget_main, pendingIntent)

        getWidgetManager(context).updateAppWidget(appWidgetId, views)

    }

    private fun getWidgetManager(context: Context): AppWidgetManager {
        return AppWidgetManager.getInstance(context)
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)

        if (appWidgetIds != null) {
            for (appWidgetId in appWidgetIds) {
                BaseUtils.getPrefs(context).edit().remove("widget-$appWidgetId").apply()
            }
        }
    }

}
package com.tyganeutronics.myratecalculator.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.tyganeutronics.base.BaseUtils
import com.tyganeutronics.myratecalculator.R


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
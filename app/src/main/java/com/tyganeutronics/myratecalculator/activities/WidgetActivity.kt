package com.tyganeutronics.myratecalculator.activities

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import com.tyganeutronics.base.BaseUtils
import com.tyganeutronics.myratecalculator.R
import kotlinx.android.synthetic.main.widget_configure.*

class WidgetActivity : BaseActivity(), View.OnClickListener {

    var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)
        setContentView(R.layout.widget_configure)

        bindViews()
        syncViews()
    }

    private fun bindViews() {
        mAppWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        RemoteViews(packageName, R.layout.widget_single).also { views ->
            getWidgetManager().updateAppWidget(mAppWidgetId, views)
        }
    }

    private fun syncViews() {
        btn_add.setOnClickListener(this)
    }

    private fun getWidgetManager(): AppWidgetManager {
        return AppWidgetManager.getInstance(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_add -> {

                //save configuration
                var which: String
                when (rg_widget.checkedRadioButtonId) {
                    R.id.rbtn_bond -> {
                        which = getString(R.string.currency_bond)
                    }
                    R.id.rbtn_omir -> {
                        which = getString(R.string.currency_omir)
                    }
                    R.id.rbtn_rtgs -> {
                        which = getString(R.string.currency_rtgs)
                    }
                    R.id.rbtn_zar -> {
                        which = getString(R.string.currency_zar)
                    }
                    else -> {
                        which = getString(R.string.currency_rbz)
                    }
                }

                BaseUtils.getPrefs(baseContext).edit().putString("widget-$mAppWidgetId", which)
                    .apply()

                //prepare response
                val resultValue = Intent().apply {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
                }

                setResult(Activity.RESULT_OK, resultValue)
                finish()
            }
        }
    }
}
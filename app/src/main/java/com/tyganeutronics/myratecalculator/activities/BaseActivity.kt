package com.tyganeutronics.myratecalculator.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import com.tyganeutronics.base.BaseUtils
import kotlinx.android.synthetic.main.ads_view.*

abstract class BaseActivity : AppCompatActivity() {
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    fun getmFirebaseAnalytics(): FirebaseAnalytics? {
        return mFirebaseAnalytics
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val analytics =
            BaseUtils.getPrefs(baseContext).getBoolean("analytics", true)
        getmFirebaseAnalytics()!!.setAnalyticsCollectionEnabled(analytics)
    }

    fun setupAd() {
        MobileAds.initialize(this)

        val builder = AdRequest.Builder()
        val adRequest = builder.build()

        adView.loadAd(adRequest)
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                adView.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                // Code to be executed when an ad request fails.
                if (errorCode == AdRequest.ERROR_CODE_NETWORK_ERROR) {
                    adView.visibility = View.GONE
                }
            }
        }
    }
}
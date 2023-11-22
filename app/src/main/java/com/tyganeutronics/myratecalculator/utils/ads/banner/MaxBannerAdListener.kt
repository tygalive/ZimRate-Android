package com.tyganeutronics.myratecalculator.utils.ads.banner

import android.content.Context
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdRevenueListener
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.tyganeutronics.myratecalculator.database.models.RewardModel
import java.lang.ref.WeakReference

object MaxBannerAdListener : MaxAdViewAdListener, MaxAdRevenueListener {

    lateinit var contextRef: WeakReference<Context>

    override fun onAdLoaded(p0: MaxAd) {

    }

    override fun onAdDisplayed(p0: MaxAd) {

    }

    override fun onAdHidden(p0: MaxAd) {

    }

    override fun onAdClicked(p0: MaxAd) {

        contextRef.get()?.let {
            FirebaseAnalytics.getInstance(it).logEvent("reward_banner_click", null)
        }
    }

    override fun onAdLoadFailed(p0: String, p1: MaxError) {

    }

    override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {

    }

    override fun onAdExpanded(p0: MaxAd) {

    }

    override fun onAdCollapsed(p0: MaxAd) {

    }

    override fun onAdRevenuePaid(impressionData: MaxAd) {
        impressionData.let {

            contextRef.get()?.let { context ->

                if (it.revenue > 0) {

                    FirebaseAnalytics.getInstance(context)
                        .logEvent(FirebaseAnalytics.Event.AD_IMPRESSION) {
                            param(FirebaseAnalytics.Param.AD_PLATFORM, "appLovin")
                            param(FirebaseAnalytics.Param.AD_UNIT_NAME, it.adUnitId)
                            param(FirebaseAnalytics.Param.AD_FORMAT, it.format.label)
                            param(FirebaseAnalytics.Param.AD_SOURCE, it.networkName)
                            param(FirebaseAnalytics.Param.VALUE, it.revenue)
                            // All Applovin revenue is sent in USD
                            param(FirebaseAnalytics.Param.CURRENCY, "USD")
                        }

                    RewardModel.rewardBannerClick(context, it.revenue)
                }
            }
        }
    }
}
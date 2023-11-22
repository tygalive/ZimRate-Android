package com.tyganeutronics.myratecalculator.utils.ads.rewarded

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdRevenueListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.ads.MaxRewardedAd
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.tyganeutronics.myratecalculator.R
import com.tyganeutronics.myratecalculator.database.models.RewardModel
import com.tyganeutronics.myratecalculator.interfaces.AdFragmentSubscriberInterface
import com.tyganeutronics.myratecalculator.utils.ads.banner.MaxBannerAdListener
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit
import kotlin.math.pow

object MaxRewardedAdListener : MaxRewardedAdListener, MaxAdRevenueListener {

    lateinit var adUnit: String
    lateinit var adSubscriberRef: WeakReference<AdFragmentSubscriberInterface>

    lateinit var rewardedAd: MaxRewardedAd

    private var retryAttempt = 0.0

    fun loadAd() {

        adSubscriberRef.get()?.let {

            rewardedAd = MaxRewardedAd.getInstance(
                adUnit,
                it.requireActivity()
            )
            rewardedAd.setListener(MaxRewardedAdListener)
            rewardedAd.setRevenueListener(MaxRewardedAdListener)
            rewardedAd.loadAd()
        }
    }

    override fun onAdLoaded(maxAd: MaxAd) {
        // Rewarded ad is ready to be shown. rewardedAd.isReady() will now return 'true'

        // Reset retry attempt
        retryAttempt = 0.0

        adSubscriberRef.get()?.let {
            it.onAdReady()

            it.onCanReshowAd()
        }
    }

    override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
        // Rewarded ad failed to load
        // We recommend retrying with exponentially higher delays up to a maximum delay (in this case 64 seconds)

        retryAttempt++
        val delayMillis = TimeUnit.SECONDS
            .toMillis(2.0.pow(6.0.coerceAtMost(retryAttempt)).toLong())

        Handler(Looper.getMainLooper()).postDelayed({
            rewardedAd.loadAd()
        }, delayMillis)

        adSubscriberRef.get()?.let {
            it.onCanReshowAd()

            Toast.makeText(
                it.requireContext(),
                R.string.rewards_toast_advert_failed,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
        // Rewarded ad failed to display. We recommend loading the next ad
        rewardedAd.loadAd()
    }

    override fun onAdDisplayed(maxAd: MaxAd) {}

    override fun onAdClicked(maxAd: MaxAd) {
        MaxBannerAdListener.contextRef.get()?.let {
            FirebaseAnalytics.getInstance(it).logEvent("reward_banner_click", null)
        }
    }

    override fun onAdHidden(maxAd: MaxAd) {
        // rewarded ad is hidden. Pre-load the next ad
        rewardedAd.loadAd()
    }

    override fun onRewardedVideoStarted(maxAd: MaxAd) {} // deprecated

    override fun onRewardedVideoCompleted(maxAd: MaxAd) {} // deprecated

    override fun onUserRewarded(maxAd: MaxAd, maxReward: MaxReward) {
        // Rewarded ad was displayed and user should receive the reward

        adSubscriberRef.get()?.let {

            // Reward the user for watching the ad to completion
            FirebaseAnalytics.getInstance(it.requireContext()).logEvent("reward_watch_advert", null)

            Toast.makeText(
                it.requireContext(),
                R.string.rewards_toast_watched_advert,
                Toast.LENGTH_LONG
            ).show()

            it.onCanReshowAd()
        }
    }

    override fun onAdRevenuePaid(impressionData: MaxAd?) {
        impressionData?.let {
            MaxBannerAdListener.contextRef.get()?.let { context ->

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
                }

                RewardModel.rewardWatchAdvert(context, it.revenue)
            }
        }
    }
}
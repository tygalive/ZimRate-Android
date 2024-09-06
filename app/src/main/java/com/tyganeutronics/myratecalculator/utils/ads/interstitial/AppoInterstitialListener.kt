package com.tyganeutronics.myratecalculator.utils.ads.interstitial

import android.content.Context
import android.widget.Toast
import com.appodeal.ads.InterstitialCallbacks
import com.google.firebase.analytics.FirebaseAnalytics
import com.tyganeutronics.myratecalculator.R
import com.tyganeutronics.myratecalculator.database.models.RewardModel
import java.lang.ref.WeakReference

object AppoInterstitialListener : InterstitialCallbacks {

    lateinit var contextRef: WeakReference<Context>

    override fun onInterstitialLoaded(isPrecache: Boolean) {
        // Called when interstitial is loaded
    }

    override fun onInterstitialFailedToLoad() {
        // Called when interstitial failed to load
    }

    override fun onInterstitialShown() {
        // Called when interstitial is shown
        contextRef.get()?.let { context ->

            // Reward the user for watching the ad to completion
            FirebaseAnalytics.getInstance(context)
                .logEvent("reward_watch_interstitial_advert", null)

            Toast.makeText(
                context,
                R.string.rewards_toast_watched_advert,
                Toast.LENGTH_LONG
            ).show()

            RewardModel.rewardWatchAdvert(context, 0.02)
        }
    }

    override fun onInterstitialShowFailed() {
        // Called when interstitial show failed
    }

    override fun onInterstitialClicked() {
        // Called when interstitial is clicked
        contextRef.get()?.let {
            FirebaseAnalytics.getInstance(it).logEvent("reward_interstitial_click", null)

            RewardModel.rewardBannerClick(it, 0.04)
        }
    }

    override fun onInterstitialClosed() {
        // Called when interstitial is closed
    }

    override fun onInterstitialExpired() {
        // Called when interstitial is expired
    }
}
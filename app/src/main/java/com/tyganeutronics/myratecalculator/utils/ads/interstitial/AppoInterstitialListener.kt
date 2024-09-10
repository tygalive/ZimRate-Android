package com.tyganeutronics.myratecalculator.utils.ads.interstitial

import android.content.Context
import android.widget.Toast
import com.appodeal.ads.InterstitialCallbacks
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
            Toast.makeText(
                context,
                R.string.rewards_toast_watched_advert,
                Toast.LENGTH_LONG
            ).show()

            RewardModel.rewardWatchInterstitialAdvert(context, 0.01)
        }
    }

    override fun onInterstitialShowFailed() {
        // Called when interstitial show failed
    }

    override fun onInterstitialClicked() {
        // Called when interstitial is clicked
        contextRef.get()?.let {
            RewardModel.rewardInterstitialClick(it, 0.02)
        }
    }

    override fun onInterstitialClosed() {
        // Called when interstitial is closed
    }

    override fun onInterstitialExpired() {
        // Called when interstitial is expired
    }
}
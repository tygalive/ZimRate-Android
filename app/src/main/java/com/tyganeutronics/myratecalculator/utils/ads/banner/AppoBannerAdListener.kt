package com.tyganeutronics.myratecalculator.utils.ads.banner

import android.content.Context
import com.appodeal.ads.BannerCallbacks
import com.google.firebase.analytics.FirebaseAnalytics
import com.tyganeutronics.myratecalculator.database.models.RewardModel
import java.lang.ref.WeakReference

object AppoBannerAdListener : BannerCallbacks {

    lateinit var contextRef: WeakReference<Context>

    override fun onBannerLoaded(height: Int, isPrecache: Boolean) {
        // Called when banner is loaded
    }

    override fun onBannerFailedToLoad() {
        // Called when banner failed to load
    }

    override fun onBannerShown() {
        // Called when banner is shown
    }

    override fun onBannerShowFailed() {
        // Called when banner show failed
    }

    override fun onBannerClicked() {
        // Called when banner is clicked
        contextRef.get()?.let {
            FirebaseAnalytics.getInstance(it).logEvent("reward_banner_click", null)

            RewardModel.rewardBannerClick(it, 0.01)
        }

    }

    override fun onBannerExpired() {
        // Called when banner is expired
    }
}
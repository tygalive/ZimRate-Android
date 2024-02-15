package com.tyganeutronics.myratecalculator.ui.base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.appodeal.ads.Appodeal
import com.tyganeutronics.myratecalculator.R
import com.tyganeutronics.myratecalculator.utils.BaseUtils
import com.tyganeutronics.myratecalculator.utils.TokenUtils
import com.tyganeutronics.myratecalculator.utils.ads.banner.AppoBannerAdListener
import com.tyganeutronics.myratecalculator.utils.contracts.PreferenceContract
import com.tyganeutronics.myratecalculator.utils.traits.getBooleanPref
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

abstract class BaseAdActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.Main).launch {

            Appodeal.initialize(
                this@BaseAdActivity,
                getString(R.string.ads_appodeal_app_id),
                Appodeal.BANNER or Appodeal.REWARDED_VIDEO
            ) {
                // Appodeal initialization finished
            }

            Appodeal.setTesting(!BaseUtils.isProductionBuild)
        }
    }

    override fun onViewCreated() {
        super.onViewCreated()
        setupAd()
    }

    override fun bindViews() {
        val analytics = getBooleanPref(PreferenceContract.FIREBASE_ANALYTICS, true)
        firebaseAnalytics.setAnalyticsCollectionEnabled(analytics)

    }

    private fun setupAd() {
        findViewById<ViewGroup>(R.id.adView)?.let { adView ->

            adView.post {

                if (TokenUtils.canShowAds(baseContext)) {

                    AppoBannerAdListener.apply {
                        contextRef = WeakReference(baseContext)
                    }

                    Appodeal.setBannerViewId(R.id.adView)
                    Appodeal.setBannerCallbacks(AppoBannerAdListener)

                    val banner: View = Appodeal.getBannerView(this)

                    Appodeal.show(this, Appodeal.BANNER_VIEW)

                    adView.addView(banner)
                    adView.isVisible = true
                } else {
                    adView.isGone = true
                }
            }
        }
    }
}
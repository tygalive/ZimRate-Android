package com.tyganeutronics.myratecalculator.ui.base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.applovin.mediation.ads.MaxAdView
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkConfiguration
import com.tyganeutronics.myratecalculator.AppZimrate
import com.tyganeutronics.myratecalculator.R
import com.tyganeutronics.myratecalculator.utils.ads.banner.MaxBannerAdListener
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
            AppLovinSdk.getInstance(baseContext).apply {
                mediationProvider = "max"
                initializeSdk { configuration: AppLovinSdkConfiguration ->
                    // AppLovin SDK is initialized, start loading ads

                }
            }
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

    fun setupAd() {
        findViewById<ViewGroup>(R.id.adView)?.let { adView ->

            adView.post {

                AppZimrate.database.let {
                    if (it.rewards().tokenTypeBalance() <= 0) {

                        val banner: View

                        banner = MaxAdView(getString(R.string.ads_max_banner_id), this)

                        banner.setListener(MaxBannerAdListener.apply {
                            contextRef = WeakReference(baseContext)
                        })

                        banner.setRevenueListener(MaxBannerAdListener)

                        val width = ViewGroup.LayoutParams.MATCH_PARENT
                        val heightPx = resources
                            .getDimensionPixelSize(R.dimen.ads_banner_height)

                        banner.setLayoutParams(FrameLayout.LayoutParams(width, heightPx))

                        adView.post {
                            banner.loadAd()
                        }

                        adView.addView(banner)
                        adView.isVisible = true
                    } else {
                        adView.isGone = true
                    }
                }
            }
        }

    }


}
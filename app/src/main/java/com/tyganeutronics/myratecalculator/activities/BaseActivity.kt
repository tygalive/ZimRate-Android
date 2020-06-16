package com.tyganeutronics.myratecalculator.activities


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.pixplicity.generate.Rate
import com.tyganeutronics.base.BaseUtils
import com.tyganeutronics.myratecalculator.R
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

    fun triggerRateDialog() {

        val rate: Rate = Rate.Builder(baseContext)
            .setMessage(R.string.rate_app)
            .setPositiveButton(R.string.rate_sure)
            .setCancelButton(R.string.rate_later)
            .setNegativeButton(R.string.rate_feedback)
            .setFeedbackAction {
                val intent = Intent(Intent.ACTION_SEND)

                intent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.author_email))
                intent.putExtra(Intent.EXTRA_SUBJECT, packageName)
                intent.type = "text/html"

                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(
                        Intent.createChooser(
                            intent,
                            getString(R.string.rate_submit_feedback)
                        )
                    )
                } else {
                    Snackbar.make(adView, R.string.rate_email_failed, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.rate_site) {
                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = Uri.parse(getString(R.string.author_url))
                            startActivity(i)
                        }.show()
                }
            }
            .setSnackBarParent(adView)
            .build()

        rate.count()

        rate.showRequest()
    }
}
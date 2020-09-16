package com.tyganeutronics.myratecalculator.activities


import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.codemybrainsout.ratingdialog.RatingDialog
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.tyganeutronics.base.BaseUtils
import com.tyganeutronics.myratecalculator.R
import kotlinx.android.synthetic.main.ads_view.*


abstract class BaseActivity : AppCompatActivity() {
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    private fun getmFirebaseAnalytics(): FirebaseAnalytics? {
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

    private fun isPlayStoreInstall(): Boolean {

        val source: String? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            packageManager.getInstallSourceInfo(packageName).installingPackageName
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstallerPackageName(packageName)
        }

        return source.equals("com.android.vending")
    }

    fun triggerRateDialog() {

        if (isPlayStoreInstall()) {
            val ratingDialog = RatingDialog.Builder(this)
                .icon(ResourcesCompat.getDrawable(resources, R.mipmap.ic_launcher, theme))
                .session(7)
                .threshold(3f)
                .title(getString(R.string.rate_app))
                .positiveButtonText(getString(R.string.rate_later))
                .negativeButtonText(getString(R.string.rate_never))
                .formTitle(getString(R.string.rate_submit_feedbak))
                .formHint(getString(R.string.rate_submit_prompt))
                .formSubmitText(getString(R.string.rate_submit))
                .formCancelText(getString(R.string.rate_cancel))
                .positiveButtonTextColor(R.color.colorAccent)
                .onThresholdCleared { ratingDialog, _, _ -> //do something
                    ratingDialog.dismiss()

                    val rateOnPlayStore: AlertDialog.Builder =
                        AlertDialog.Builder(this@BaseActivity)
                    rateOnPlayStore.setIcon(R.mipmap.ic_launcher)
                    rateOnPlayStore.setTitle(getString(R.string.rate_title))
                    rateOnPlayStore.setMessage(getString(R.string.rate_playstore))
                    rateOnPlayStore.setPositiveButton(R.string.rate_yes) { _, _ ->

                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(getString(R.string.playstore_market, packageName))

                        if (intent.resolveActivity(packageManager) == null) {
                            intent.data =
                                Uri.parse(getString(R.string.playstore_browser, packageName))
                        }
                        startActivity(intent)
                    }
                    rateOnPlayStore.setNegativeButton(R.string.rate_cancel, null)

                    rateOnPlayStore.create().show()
                }
                .onThresholdFailed { ratingDialog, _, _ -> //do something
                    ratingDialog.dismiss()
                }
                .onRatingChanged { _, _ -> }
                .onRatingBarFormSumbit {

                    val intent = Intent(Intent.ACTION_SEND)

                    intent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.author_email))
                    intent.putExtra(Intent.EXTRA_SUBJECT, packageName)
                    intent.type = "text/html"

                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(
                            Intent.createChooser(
                                intent,
                                getString(R.string.rate_submit_feedbak)
                            )
                        )
                    } else {
                        Snackbar.make(
                            adView,
                            R.string.rate_email_failed,
                            Snackbar.LENGTH_INDEFINITE
                        )
                            .setAction(R.string.rate_site) { v1 ->
                                val i = Intent(Intent.ACTION_VIEW)
                                i.data = Uri.parse(getString(R.string.author_url))
                                startActivity(i)
                            }.show()
                    }
                }.build()

            ratingDialog.show()
        }
    }
}
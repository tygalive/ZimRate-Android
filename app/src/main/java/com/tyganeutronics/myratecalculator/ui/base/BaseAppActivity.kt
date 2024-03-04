package com.tyganeutronics.myratecalculator.ui.base

import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.tyganeutronics.myratecalculator.R
import com.tyganeutronics.myratecalculator.interfaces.ReviewableActivity
import com.tyganeutronics.myratecalculator.utils.BaseUtils
import com.tyganeutronics.myratecalculator.utils.traits.getLongPref
import com.tyganeutronics.myratecalculator.utils.traits.putLongPref
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

abstract class BaseAppActivity : BaseAdActivity(), ReviewableActivity {

    private lateinit var reviewManager: ReviewManager
    private lateinit var reviewInfo: ReviewInfo

    private lateinit var appUpdateManager: AppUpdateManager

    private val listener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            Snackbar.make(
                findViewById(R.id.layout_container),
                getString(R.string.app_update_downloaded),
                Snackbar.LENGTH_INDEFINITE
            ).apply {
                setAction(R.string.app_update_update) { appUpdateManager.completeUpdate() }
            }.show()
        }

    }

    override fun syncViews() {
        if (BaseUtils.isPlayBuild || BaseUtils.isOtherBuild) {
            reviewManager = ReviewManagerFactory.create(this)

            reviewManager.requestReviewFlow()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // We got the ReviewInfo object
                        reviewInfo = task.result
                    } else {
                        // There was some problem, log or handle the error code.
                    }
                }
        }
    }

    override fun requestReview() {
        if (this::reviewManager.isInitialized) {
            if (getLongPref("last_rating_requested", 0) < LocalDateTime.now()
                    .minusMonths(2)
                    .toEpochSecond(ZoneOffset.UTC)
            ) {

                val flow = reviewManager.launchReviewFlow(this, reviewInfo)
                flow.addOnCompleteListener { _ ->
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.

                    putLongPref("last_rating_requested", Instant.now().epochSecond)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BaseUtils.isPlayBuild || BaseUtils.isOtherBuild) {
            checkAppHasUpdate()
        }
    }

    override fun onStart() {
        super.onStart()

        if (this::appUpdateManager.isInitialized) {
            appUpdateManager.registerListener(listener)
        }
    }

    override fun onStop() {
        super.onStop()

        if (this::appUpdateManager.isInitialized) {
            appUpdateManager.unregisterListener(listener)
        }
    }

    private fun checkAppHasUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this)

        val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { _: ActivityResult ->
            // handle callback
        }

        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && (appUpdateInfo.clientVersionStalenessDays() ?: -1) >= 7
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                ) {
                    // Request the update.
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activityResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
                    )

                }
            }
    }

}
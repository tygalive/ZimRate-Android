package com.tyganeutronics.myratecalculator.ui.base

import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
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

}
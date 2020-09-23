package com.tyganeutronics.myratecalculator.fragments

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Dialog
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.maltaisn.calcdialog.CalcDialog


class FragmentCalculator : CalcDialog() {

    override fun onStart() {
        super.onStart()

        FirebaseAnalytics.getInstance(requireContext()).logEvent("calculator_dialog", Bundle())

        val view = (dialog as Dialog).window?.decorView?.apply {
            clearAnimation()
            animate().cancel()
        }

        val scaleDown: ObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofInt("top", 0, 0),
            PropertyValuesHolder.ofInt("left", 0, 0),
            PropertyValuesHolder.ofFloat("scaleX", 0.9f, 1.0f),
            PropertyValuesHolder.ofFloat("scaleY", 0.9f, 1.0f),
            PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f)
        )
        scaleDown.duration = 500
        scaleDown.start()
    }

}
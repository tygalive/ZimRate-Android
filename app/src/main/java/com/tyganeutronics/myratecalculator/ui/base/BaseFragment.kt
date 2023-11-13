package com.tyganeutronics.myratecalculator.ui.base

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.analytics.FirebaseAnalytics

abstract class BaseFragment : BottomSheetDialogFragment() {

    val firebaseAnalytics: FirebaseAnalytics
        get() = FirebaseAnalytics.getInstance(requireContext())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
    }

    override fun onStart() {
        super.onStart()
        syncViews()
    }

    protected open fun bindViews() {}
    protected open fun syncViews() {}
}
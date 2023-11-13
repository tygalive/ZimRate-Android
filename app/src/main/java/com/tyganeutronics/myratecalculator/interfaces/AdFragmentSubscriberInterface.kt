package com.tyganeutronics.myratecalculator.interfaces

import android.app.Activity
import android.content.Context

interface AdFragmentSubscriberInterface {

    fun requireContext(): Context

    fun requireActivity(): Activity

    fun onCanReshowAd()

    fun onAdReady()
}
package com.tyganeutronics.myratecalculator.interfaces

import android.app.Activity
import android.content.Context
import android.widget.Button

interface AdFragmentSubscriberInterface {

    fun requireContext(): Context

    fun requireActivity(): Activity

    fun requireShowAdButton(): Button

    fun resetShowAdButtonText()

}
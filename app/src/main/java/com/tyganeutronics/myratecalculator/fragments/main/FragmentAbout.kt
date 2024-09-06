package com.tyganeutronics.myratecalculator.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tyganeutronics.myratecalculator.R
import com.tyganeutronics.myratecalculator.ui.base.BaseFragment
import com.tyganeutronics.myratecalculator.utils.traits.setTitle

class FragmentAbout : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main_about, container, false)
    }

    override fun syncViews() {
        super.syncViews()
        setTitle(R.string.menu_settings)
    }

    companion object {
        const val TAG = "FragmentAbout"
    }
}
package com.tyganeutronics.myratecalculator.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tyganeutronics.myratecalculator.R
import com.tyganeutronics.myratecalculator.ui.base.BaseFragment

class FragmentHome : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_home, container, false)
    }

    companion object {
        const val TAG = "FragmentHome"
    }

}
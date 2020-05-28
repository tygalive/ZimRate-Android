package com.tyganeutronics.myratecalculator.fragments

import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.tyganeutronics.myratecalculator.BuildConfig
import com.tyganeutronics.myratecalculator.R

class FragmentSettings : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findPreference<Preference>(getString(R.string.app_version))?.title = getString(
            R.string.app_version,
            BuildConfig.VERSION_NAME
        )
    }
}
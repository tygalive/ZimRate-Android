package com.tyganeutronics.myratecalculator.fragments.about

import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.tyganeutronics.myratecalculator.BuildConfig
import com.tyganeutronics.myratecalculator.R
import de.psdev.licensesdialog.LicensesDialog


class FragmentSectionSettings : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findPreference<Preference>(getString(R.string.pref_app_version))?.title = getString(
            R.string.pref_app_version,
            BuildConfig.VERSION_NAME
        )

        findPreference<Preference>(getString(R.string.license))?.onPreferenceClickListener = this

    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        when (preference.key) {
            getString(R.string.license) -> {
                LicensesDialog.Builder(activity)
                    .setNotices(R.raw.licenses)
                    .setIncludeOwnLicense(true)
                    .build()
                    .show()
                return true
            }
        }
        return false
    }
}
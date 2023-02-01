package com.tyganeutronics.myratecalculator.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PackageInfoFlags
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.tyganeutronics.myratecalculator.BuildConfig
import com.tyganeutronics.myratecalculator.R
import com.tyganeutronics.myratecalculator.utils.BaseUtils
import de.psdev.licensesdialog.LicensesDialog
import java.net.URI


class FragmentSettings : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findPreference<Preference>(getString(R.string.app_version))?.title = getString(
            R.string.app_version,
            BuildConfig.VERSION_NAME
        )

        findPreference<Preference>(getString(R.string.license))?.onPreferenceClickListener = this

        findPreference<Preference>(getString(R.string.donate))?.isVisible =
            BaseUtils.isPlayBuild().not()

        findPreference<Preference>(getString(R.string.author_email))?.apply {
            isVisible = intent?.let { intent ->
                requireContext().packageManager.run {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        queryIntentActivities(
                            intent,
                            PackageManager.ResolveInfoFlags.of(0L)
                        )
                    } else {
                        queryIntentActivities(intent, 0)
                    }
                }.isNotEmpty()
            } == true
        }
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
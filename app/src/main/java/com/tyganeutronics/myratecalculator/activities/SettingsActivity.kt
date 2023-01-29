package com.tyganeutronics.myratecalculator.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.FragmentTransaction
import com.tyganeutronics.myratecalculator.R
import com.tyganeutronics.myratecalculator.fragments.FragmentSettings

class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_settings)

        title = getString(R.string.menu_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        bindViews()
    }

    private fun bindViews() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.replace(R.id.settings_fragment, FragmentSettings(), "FragmentSettings")
        transaction.commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
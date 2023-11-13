package com.tyganeutronics.myratecalculator.interfaces

import android.os.Bundle

interface RewardsActivity {

    fun showRewardHistory(bundle: Bundle)

    fun showPurchasesHistory(bundle: Bundle)

    fun showTopUpDialog()
}
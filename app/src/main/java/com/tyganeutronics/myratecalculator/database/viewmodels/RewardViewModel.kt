package com.tyganeutronics.myratecalculator.database.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.tyganeutronics.myratecalculator.AppZimRate

class RewardViewModel(application: Application) : AndroidViewModel(application) {
    val coins = AppZimRate.database.rewards().liveTokenBalance()
}
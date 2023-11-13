package com.tyganeutronics.myratecalculator.database.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.tyganeutronics.myratecalculator.AppZimrate

class RewardViewModel(application: Application) : AndroidViewModel(application) {
    val coins = AppZimrate.database.rewards().liveTokenBalance()
}
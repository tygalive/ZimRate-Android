package com.tyganeutronics.myratecalculator

import androidx.multidex.MultiDexApplication
import com.apollographql.apollo3.ApolloClient
import com.google.android.gms.ads.MobileAds
import com.tyganeutronics.myratecalculator.contract.ApiContract

class AppZimrate : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()

        this.setUpApollo()

        MobileAds.initialize(this)
    }

    private fun setUpApollo() {
        apolloClient = ApolloClient.Builder()
            .serverUrl(ApiContract.getRatesUrl(this))
            .build()
    }

    companion object {
        lateinit var apolloClient: ApolloClient
    }
}
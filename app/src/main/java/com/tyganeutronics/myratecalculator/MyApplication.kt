package com.tyganeutronics.myratecalculator

import androidx.multidex.MultiDexApplication
import com.android.volley.Cache
import com.android.volley.Network
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.google.android.gms.ads.MobileAds
import org.jetbrains.annotations.Contract

class MyApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()

        // Instantiate the cache
        val cache: Cache = DiskBasedCache(cacheDir, 1024 * 1024) // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        val network: Network = BasicNetwork(HurlStack())

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = RequestQueue(cache, network)

        // Start the queue
        requestQueue.start()

        MobileAds.initialize(this)
    }

    companion object {
        @get:Contract(pure = true)
        lateinit var requestQueue: RequestQueue
    }
}
package com.tyganeutronics.myratecalculator;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.tyganeutronics.base.BaseApplication;

import org.jetbrains.annotations.Contract;

public final class MyApplication extends BaseApplication {

    private static RequestQueue requestQueue;

    @Contract(pure = true)
    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public static void setRequestQueue(RequestQueue requestQueue) {
        MyApplication.requestQueue = requestQueue;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        setRequestQueue(new RequestQueue(cache, network));

        // Start the queue
        getRequestQueue().start();
    }
}

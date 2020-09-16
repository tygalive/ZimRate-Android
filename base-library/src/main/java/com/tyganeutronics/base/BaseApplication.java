package com.tyganeutronics.base;

import androidx.multidex.MultiDexApplication;

import com.jakewharton.threetenabp.AndroidThreeTen;

public class BaseApplication extends MultiDexApplication {
    private static final String TAG = "BaseApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(getBaseContext());
    }
}

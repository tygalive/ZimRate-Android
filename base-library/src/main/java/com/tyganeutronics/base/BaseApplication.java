package com.tyganeutronics.base;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.jakewharton.threetenabp.AndroidThreeTen;

public class BaseApplication extends Application {
    private static final String TAG = "BaseApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(getBaseContext());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(getBaseContext());
    }
}

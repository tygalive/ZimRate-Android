package com.tyganeutronics.myratecalculator.activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.tyganeutronics.base.BaseUtils;
import com.tyganeutronics.myratecalculator.R;

public abstract class BaseActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    public FirebaseAnalytics getmFirebaseAnalytics() {
        return mFirebaseAnalytics;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MobileAds.initialize(this);
        setupAd();

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        boolean analytics = BaseUtils.getPrefs(getBaseContext()).getBoolean("analytics", true);
        getmFirebaseAnalytics().setAnalyticsCollectionEnabled(analytics);

    }

    private AdView getAdView() {
        return findViewById(R.id.adView);
    }

    public void setupAd() {
        AdView mAdView = getAdView();

        if (mAdView != null) {

            AdRequest.Builder builder = new AdRequest.Builder();

            AdRequest adRequest = builder.build();
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    mAdView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                    if (errorCode == AdRequest.ERROR_CODE_NETWORK_ERROR) {
                        mAdView.setVisibility(View.GONE);
                    }
                }
            });

        }
    }
}

package com.example.bittu.dipin.mainUi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bittu.dipin.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AdFragment extends Fragment {
    @InjectView(R.id.adView)
    AdView mAdView;

    InterstitialAd mInterstitialAd;

    String LOG_TAG = AdFragment.class.getName();

    public static Fragment newInstance() {
        AdFragment fragment = new AdFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ad_fragment, container, false);
        ButterKnife.inject(this, view);


        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        // Load ads into Interstitial Ads
        mAdView.loadAd(adRequest);



        return view;
    }


}

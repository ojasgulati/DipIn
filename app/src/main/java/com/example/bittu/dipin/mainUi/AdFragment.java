package com.example.bittu.dipin.mainUi;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.bittu.dipin.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AdFragment extends Fragment {

    @InjectView(R.id.adLayout)
    FrameLayout adLayout;
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

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int height = (int) convertPixelsToDp(metrics.heightPixels, getActivity());

        AdView adView = new AdView(getActivity());
        Log.i("Width", Integer.toString(height));
        AdSize adSize = new AdSize(-1, height);
        adView.setAdSize(adSize);
        adView.setAdUnitId(getString(R.string.main_ad_unit_id));


    // Initiate a generic request to load it with an ad
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest);
        // Place the ad view.
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        adLayout.addView(adView, params);

//        mAdView.loadAd(adRequest);

        return view;
    }

    public static float convertPixelsToDp(int px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

}

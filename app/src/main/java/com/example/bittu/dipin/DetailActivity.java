package com.example.bittu.dipin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DetailActivity extends AppCompatActivity {
    @InjectView(R.id.webView)
    WebView webView;
    @InjectView(R.id.progressBar1)
    ProgressBar progressBar;
    @InjectView(R.id.news_gif)
    LinearLayout newsGif;

    List<News> news;

    final static String LOG_TAG = "DetailActivity";

    InterstitialAd mInterstitialAd;
    private boolean isAdLoaded = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.inject(this);
        String newsUrl = getIntent().getStringExtra(getString(R.string.intent_position_detail));
        Log.i(LOG_TAG, newsUrl);
        WebSettings webSettings = webView.getSettings();
        webView.setWebViewClient(new WebViewClient());

        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(newsUrl);
        webView.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView webView1, int newProgress) {

                progressBar.setProgress(newProgress);

                if (newProgress >= 25) {
                    newsGif.setVisibility(View.GONE);
                }

                if (newProgress >= 99) {
                    progressBar.setVisibility(View.GONE);
                }

                if(newProgress >= 25){
                    if(!isAdLoaded) {
                        isAdLoaded = true;
                        mInterstitialAd = new InterstitialAd(DetailActivity.this);

                        // set the ad unit ID
                        mInterstitialAd.setAdUnitId(getString(R.string.detail_ad_unit_id));

                        AdRequest adRequest = new AdRequest.Builder()
                                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                                .build();

                        // Load ads into Interstitial Ads
                        mInterstitialAd.loadAd(adRequest);
                        Log.e("Interstiaital ad", "Loading Inter Ad");
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    finish();
                }
            });
        } else {
            super.onBackPressed();
        }
    }
}

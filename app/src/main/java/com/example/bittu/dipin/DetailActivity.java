package com.example.bittu.dipin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.bittu.dipin.service.ApiService;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DetailActivity extends AppCompatActivity {
    @InjectView(R.id.webView)
    WebView webView;
    @InjectView(R.id.progressBar1)
    ProgressBar progressBar;

    List<News> news;

    final static String LOG_TAG = "DetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.inject(this);
        news = ApiService.newsList();
        final News currentNews = news.get(getIntent().getIntExtra(getString(R.string.intent_position_detail), -1));
        Log.i(LOG_TAG,currentNews.getUrl());
        WebSettings webSettings = webView.getSettings();
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient(){

            public void onProgressChanged(WebView webView1, int newProgress){

                progressBar.setProgress(newProgress);

                if(newProgress == 100){
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(currentNews.getUrl());
    }
}

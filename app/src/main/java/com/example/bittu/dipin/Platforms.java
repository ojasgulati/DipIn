package com.example.bittu.dipin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class Platforms extends AppCompatActivity {
    @InjectView(R.id.platform_recycler)
    RecyclerView recyclerView;
    @InjectView(R.id.platform_toolbar)
    Toolbar toolbar;
    PlatformsAdapter platformsAdapter;
    List<Website> websites;
    InterstitialAd mInterstitialAd;
    int showAdCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platforms);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        websites = new ArrayList<Website>();
        setUpList();
        platformsAdapter = new PlatformsAdapter(this, websites);
        recyclerView.setAdapter(platformsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sharedPref = getSharedPreferences("showAd", 0);
        showAdCount = sharedPref.getInt("showAdCount", 1);
        showAdCount = showAdCount + 1;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("showAdCount", showAdCount);
        editor.commit();

        Log.i("SHOWAD", Integer.toString(sharedPref.getInt("showAdCount", 1)));

        if (showAdCount % 10 == 0) {
            mInterstitialAd = new InterstitialAd(this);

            // set the ad unit ID
            mInterstitialAd.setAdUnitId(getString(R.string.fav_ad_unit_id));

            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();

            // Load ads into Interstitial Ads
            mInterstitialAd.loadAd(adRequest);
        }


    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpList() {
        websites.add(new Website(getString(R.string.topic_general), getDrawable(R.drawable.general)));
        websites.add(new Website(getString(R.string.topic_sports), getDrawable(R.drawable.sports)));
        websites.add(new Website(getString(R.string.topic_entertainment), getDrawable(R.drawable.entertainment)));
        websites.add(new Website(getString(R.string.topic_business), getDrawable(R.drawable.business)));
        websites.add(new Website(getString(R.string.topic_technology), getDrawable(R.drawable.technology)));
        websites.add(new Website(getString(R.string.topic_science_nature), getDrawable(R.drawable.science_nature)));
    }


    public class PlatformsAdapter extends RecyclerView.Adapter<PlatformsAdapter.PlatformsViewHolder> {
        Context mContext;
        List<Website> mWebsites;

        public class PlatformsViewHolder extends RecyclerView.ViewHolder {
            @InjectView(R.id.topic_image)
            ImageView imageView;
            @InjectView(R.id.topic_title)
            TextView title;


            public PlatformsViewHolder(View itemView) {
                super(itemView);
                ButterKnife.inject(this, itemView);
            }
        }

        public PlatformsAdapter(Context context, List<Website> websites) {
            mContext = context;
            mWebsites = websites;
        }


        @Override
        public PlatformsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.platforms_item_layout, parent, false);
            return new PlatformsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(PlatformsViewHolder holder, int position) {
            final Website currentTopic = mWebsites.get(position);
            holder.imageView.setBackground(currentTopic.getPlatformImage());
            holder.title.setText(currentTopic.getPlatformTitle());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences sharedPref = getSharedPreferences("sharedPlatform", 0);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.pref_shared_platform), currentTopic.getPlatformTitle());
                    switch (currentTopic.getPlatformTitle()){
                        case "General":
                            editor.putString(getString(R.string.pref_shared_website), "https://newsapi.org/v1/articles?source=al-jazeera-english&apiKey=839b127083e848e188382abfc1e8ee16");
                            break;
                        case "Sports":
                            editor.putString(getString(R.string.pref_shared_website), "https://newsapi.org/v1/articles?source=bbc-sport&apiKey=839b127083e848e188382abfc1e8ee16");
                            break;
                        case "Entertainment":
                            editor.putString(getString(R.string.pref_shared_website), "https://newsapi.org/v1/articles?source=buzzfeed&apiKey=839b127083e848e188382abfc1e8ee16");
                            break;
                        case "Business":
                            editor.putString(getString(R.string.pref_shared_website), "https://newsapi.org/v1/articles?source=bloomberg&apiKey=839b127083e848e188382abfc1e8ee16");
                            break;
                        case "Technology":
                            editor.putString(getString(R.string.pref_shared_website), "https://newsapi.org/v1/articles?source=engadget&apiKey=839b127083e848e188382abfc1e8ee16");
                            break;
                        case "Science and Nature":
                            editor.putString(getString(R.string.pref_shared_website), "https://newsapi.org/v1/articles?source=national-geographic&apiKey=839b127083e848e188382abfc1e8ee16");
                            break;
                        default:
                            editor.putString(getString(R.string.pref_shared_website), "https://newsapi.org/v1/articles?source=al-jazeera-english&apiKey=839b127083e848e188382abfc1e8ee16");
                            break;
                    }
                    editor.commit();
                    Log.i("URL", sharedPref.getString(getString(R.string.pref_shared_platform), "Null"));
                    if (showAdCount % 10 == 0) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                            mInterstitialAd.setAdListener(new AdListener() {
                                @Override
                                public void onAdClosed() {
                                    super.onAdClosed();
                                    finish();
                                }
                            });
                        } else
                            finish();
                    } else {
                        finish();
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return websites.size();
        }
    }
}



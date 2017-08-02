package com.example.bittu.dipin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
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
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

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
        websites.add(new Website(getResources().getString(R.string.Al_Jazeera_English), getDrawable(R.drawable.al_jazeera_english)));
        websites.add(new Website(getResources().getString(R.string.Associated_Press), getDrawable(R.drawable.associated_press)));
        websites.add(new Website(getResources().getString(R.string.BBC_News), getDrawable(R.drawable.bbc_news)));
        websites.add(new Website(getResources().getString(R.string.BBC_Sport), getDrawable(R.drawable.bbc_sport)));
        websites.add(new Website(getResources().getString(R.string.Bloomberg), getDrawable(R.drawable.bloomberg)));
        websites.add(new Website(getResources().getString(R.string.Breitbart_News), getDrawable(R.drawable.breitbart_news)));
        websites.add(new Website(getResources().getString(R.string.Business_Insider), getDrawable(R.drawable.business_insider)));
        websites.add(new Website(getResources().getString(R.string.Business_Insider_UK), getDrawable(R.drawable.business_insider)));
        websites.add(new Website(getResources().getString(R.string.Buzzfeed), getDrawable(R.drawable.buzzfeed)));
        websites.add(new Website(getResources().getString(R.string.CNBC), getDrawable(R.drawable.cnbc)));
        websites.add(new Website(getResources().getString(R.string.CNN), getDrawable(R.drawable.cnn)));
        websites.add(new Website(getResources().getString(R.string.Die_Zeit), getDrawable(R.drawable.die_zeit)));
        websites.add(new Website(getResources().getString(R.string.Engadget), getDrawable(R.drawable.engadget)));
        websites.add(new Website(getResources().getString(R.string.Entertainment_Weekly), getDrawable(R.drawable.entertainment_weekly)));
        websites.add(new Website(getResources().getString(R.string.ESPN), getDrawable(R.drawable.espn)));
        websites.add(new Website(getResources().getString(R.string.ESPN_Cric_Info), getDrawable(R.drawable.espn_cricinfo)));
        websites.add(new Website(getResources().getString(R.string.Financial_Times), getDrawable(R.drawable.financial_times)));
        websites.add(new Website(getResources().getString(R.string.Fortune), getDrawable(R.drawable.fortune)));
        websites.add(new Website(getResources().getString(R.string.FourFourTwo), getDrawable(R.drawable.fourfourtwo)));
        websites.add(new Website(getResources().getString(R.string.Fox_Sports), getDrawable(R.drawable.fox_sports)));
        websites.add(new Website(getResources().getString(R.string.Google_News), getDrawable(R.drawable.google_news)));
        websites.add(new Website(getResources().getString(R.string.Gruenderszene), getDrawable(R.drawable.gruenderszene)));
        websites.add(new Website(getResources().getString(R.string.Hacker_News), getDrawable(R.drawable.hacker_news)));
        websites.add(new Website(getResources().getString(R.string.Independent), getDrawable(R.drawable.independent)));
        websites.add(new Website(getResources().getString(R.string.Mashable), getDrawable(R.drawable.mashableindia)));
        websites.add(new Website(getResources().getString(R.string.Metro), getDrawable(R.drawable.metro)));
        websites.add(new Website(getResources().getString(R.string.Mirror), getDrawable(R.drawable.mirror)));
        websites.add(new Website(getResources().getString(R.string.MTV_News), getDrawable(R.drawable.mtv_news)));
        websites.add(new Website(getResources().getString(R.string.MTV_News_UK), getDrawable(R.drawable.mtv_news)));
        websites.add(new Website(getResources().getString(R.string.National_Geographic), getDrawable(R.drawable.national_geographic)));
        websites.add(new Website(getResources().getString(R.string.New_Scientist), getDrawable(R.drawable.new_scientist)));
        websites.add(new Website(getResources().getString(R.string.Newsweek), getDrawable(R.drawable.newsweek)));
        websites.add(new Website(getResources().getString(R.string.New_York_Magazine), getDrawable(R.drawable.newyork_magazine)));
        websites.add(new Website(getResources().getString(R.string.Polygon), getDrawable(R.drawable.polygon)));
        websites.add(new Website(getResources().getString(R.string.Recode), getDrawable(R.drawable.recode)));
        websites.add(new Website(getResources().getString(R.string.TalkSport), getDrawable(R.drawable.talksport)));
        websites.add(new Website(getResources().getString(R.string.TechCrunch), getDrawable(R.drawable.techcrunch)));
        websites.add(new Website(getResources().getString(R.string.TechRadar), getDrawable(R.drawable.techradar)));
        websites.add(new Website(getResources().getString(R.string.The_Economist), getDrawable(R.drawable.the_economist)));
        websites.add(new Website(getResources().getString(R.string.The_Guardian_AU), getDrawable(R.drawable.the_guardian_au)));
        websites.add(new Website(getResources().getString(R.string.The_Guardian_UK), getDrawable(R.drawable.the_guardian_uk)));
        websites.add(new Website(getResources().getString(R.string.The_Hindu), getDrawable(R.drawable.the_hindu)));
        websites.add(new Website(getResources().getString(R.string.The_Huffington_Post), getDrawable(R.drawable.the_huffington_post)));
        websites.add(new Website(getResources().getString(R.string.The_Lad_Bible), getDrawable(R.drawable.the_lad_bible)));
        websites.add(new Website(getResources().getString(R.string.The_New_York_Times), getDrawable(R.drawable.the_newyork_times)));
        websites.add(new Website(getResources().getString(R.string.The_Next_Web), getDrawable(R.drawable.the_next_web)));
        websites.add(new Website(getResources().getString(R.string.The_Sport_Bible), getDrawable(R.drawable.the_sport_bible)));
        websites.add(new Website(getResources().getString(R.string.The_Telegraph), getDrawable(R.drawable.the_telegraph)));
        websites.add(new Website(getResources().getString(R.string.The_Times_of_India), getDrawable(R.drawable.the_times_of_india)));
        websites.add(new Website(getResources().getString(R.string.The_Verge), getDrawable(R.drawable.the_verge)));
        websites.add(new Website(getResources().getString(R.string.Time), getDrawable(R.drawable.time)));

    }


    public class PlatformsAdapter extends RecyclerView.Adapter<PlatformsAdapter.PlatformsViewHolder> {
        Context mContext;
        List<Website> mWebsites;

        public class PlatformsViewHolder extends RecyclerView.ViewHolder {
            @InjectView(R.id.platform_image)
            ImageView imageView;
            @InjectView(R.id.platform_title)
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
            final Website currentWebsite = mWebsites.get(position);
            holder.imageView.setBackground(currentWebsite.getPlatformImage());
            holder.title.setText(currentWebsite.getPlatformTitle());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String urlString = currentWebsite.getPlatformTitle().toLowerCase().replace(" ", "-");
                    String baseUrl = "https://newsapi.org/v1/articles?source=" + urlString + "&apiKey=839b127083e848e188382abfc1e8ee16";
                    SharedPreferences sharedPref = getSharedPreferences("sharedPlatform", 0);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.pref_shared_platform), baseUrl);
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



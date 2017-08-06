package com.example.bittu.dipin.mainUi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bittu.dipin.R;
import com.example.bittu.dipin.Utils;
import com.example.bittu.dipin.Website;
import com.example.bittu.dipin.drawer.DrawerHeader;
import com.example.bittu.dipin.drawer.DrawerMenuItem;
import com.example.bittu.dipin.service.ApiService;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mindorks.placeholderview.PlaceHolderView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.relex.circleindicator.CircleIndicator;

import static com.example.bittu.dipin.R.id.drawerView;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @InjectView(R.id.swiperefresh)
    SwipeRefreshLayout mSwipeRefresh;
    @InjectView(drawerView)
    PlaceHolderView mDrawerView;
    @InjectView(R.id.drawerLayout)
    DrawerLayout mDrawer;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.vertical_view_pager)
    com.example.bittu.dipin.VerticalViewPager verticalViewPager;
    @InjectView(R.id.news_gif)
    LinearLayout gifLayout;
    @InjectView(R.id.error_emptyView_image)
    ImageView emptyViewImage;
    @InjectView(R.id.error_emptyView_text)
    TextView emptyViewText;
    @InjectView(R.id.indicator)
    CircleIndicator indicator;
    @InjectView(R.id.newsItemRecyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.topic_title_main)
    TextView topicTitle;

    private boolean isDrawerSetup = false;
    private boolean mIsRefreshing = false;

    private NewsPagerAdapter mPagerAdapter;

    final static String ANONYMOUS = "anonymous";
    public static final int RC_SIGN_IN = 1;

    public static String mUserId;
    public static String mUserEmailId;
    public static String mUsername;
    public static String mUserPic;

    boolean networkAvailable;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    FirebaseUser user;
    List<Website> websites;

    BroadcastReceiver viewUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ApiService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                mIsRefreshing = intent.getBooleanExtra(ApiService.EXTRA_REFRESHING, false);
                updateUI();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mUserId = ANONYMOUS;

        removeEmptyViewText();

        mFirebaseAuth = FirebaseAuth.getInstance();

        startService(new Intent(this, ApiService.class));

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                networkAvailable = isNetworkAvailable(MainActivity.this);
                if (!networkAvailable) {
                    setEmptyViewText();
                    mSwipeRefresh.setRefreshing(false);
                }
                removeEmptyViewText();
                startService(new Intent(MainActivity.this, ApiService.class));
            }
        });
        userAuth();
        newsSetupDrawer();
        adLoad();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sp = getSharedPreferences("sharedPlatform", 0);
        sp.unregisterOnSharedPreferenceChangeListener(this);
        ApiService.clearNewsList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(viewUpdate,
                new IntentFilter(ApiService.BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(viewUpdate);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        networkAvailable = isNetworkAvailable(this);
        if (!networkAvailable) {
            setEmptyViewText();
        }

        SharedPreferences sp = getSharedPreferences("sharedPlatform", 0);
        sp.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        mDrawer.closeDrawer(Gravity.START);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                if (emptyViewImage.getVisibility() == View.VISIBLE) {
                    startService(new Intent(MainActivity.this, ApiService.class));
                    removeEmptyViewText();
                }
            } else if (resultCode == RESULT_CANCELED) {
                networkAvailable = isNetworkAvailable(MainActivity.this);
                if (networkAvailable) {
                    Toast.makeText(this, R.string.login_to_continue, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    setEmptyViewText();
                }
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(getString(R.string.pref_shared_platform))) {
            newsSetupDrawer();
        }

        if (s.equals(getString(R.string.pref_shared_website))) {
            networkAvailable = isNetworkAvailable(this);
            if (networkAvailable) {
                gifLayout.setVisibility(View.VISIBLE);
                ApiService.clearNewsList();
                startService(new Intent(this, ApiService.class));
                if (!mIsRefreshing) {
                    mPagerAdapter.notifyDataSetChanged();
                }
                adLoad();
                Log.i("TAG", sharedPreferences.getString(getString(R.string.pref_shared_platform), "URL NULL"));
            } else {
                setEmptyViewText();
            }
        }
    }

    public void updateUI() {
        mSwipeRefresh.setRefreshing(mIsRefreshing);
        if (!mIsRefreshing) {
            gifLayout.setVisibility(View.GONE);
            mPagerAdapter = new NewsPagerAdapter(getSupportFragmentManager());
            verticalViewPager.setAdapter(mPagerAdapter);
            indicator.setViewPager(verticalViewPager);

        } else {
            gifLayout.setVisibility(View.VISIBLE);
            Log.i("mIsRefreshing", "false");
        }
    }

    public void removeEmptyViewText() {
        emptyViewImage.setVisibility(View.GONE);
        emptyViewText.setVisibility(View.GONE);
    }


    public void setEmptyViewText() {
        emptyViewImage.setVisibility(View.VISIBLE);
        emptyViewText.setVisibility(View.VISIBLE);
        emptyViewText.setText(getString(R.string.no_connection));
        gifLayout.setVisibility(View.GONE);
    }


    private void userAuth() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    mUserId = user.getUid().toString();
                    mUserEmailId = user.getEmail().toString();
                    mUsername = user.getDisplayName().toString();
                    if (user.getPhotoUrl() != null)
                        mUserPic = user.getPhotoUrl().toString();
                    if (!isDrawerSetup) {
                        setupDrawer();
                        isDrawerSetup = true;
                    }
                } else {
                    // User is signed out
                    mUserId = ANONYMOUS;

                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build())
                                    )
                                    .setIsSmartLockEnabled(false)
                                    .setLogo(R.mipmap.ic_launcher)
                                    .setTheme(R.style.FullscreenTheme)
                                    .build(),
                            RC_SIGN_IN);

                }

            }
        };


    }


    private void setupDrawer() {
        mDrawerView
                .addView(new DrawerHeader(this))
                .addView(new DrawerMenuItem(this, DrawerMenuItem.DRAWER_MENU_ITEM_HOME))
                .addView(new DrawerMenuItem(this, DrawerMenuItem.DRAWER_MENU_ITEM_PLATFORMS))
                .addView(new DrawerMenuItem(this, DrawerMenuItem.DRAWER_MENU_ITEM_FAVORITES))
                .addView(new DrawerMenuItem(this, DrawerMenuItem.DRAWER_MENU_ITEM_BREAK));
//                .addView(new DrawerMenuItem(this, DrawerMenuItem.DRAWER_MENU_ITEM_RATE_US))
//                .addView(new DrawerMenuItem(this, DrawerMenuItem.DRAWER_MENU_ITEM_FEEDBACK))
//                .addView(new DrawerMenuItem(this, DrawerMenuItem.DRAWER_MENU_ITEM_SHARE_APP))
//                .addView(new DrawerMenuItem(this, DrawerMenuItem.DRAWER_MENU_ITEM_SETTINGS))
//                .addView(new DrawerMenuItem(this, DrawerMenuItem.DRAWER_MENU_ITEM_LOGOUT));

        final ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerOpened(final View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        mDrawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();


    }

    private void newsSetupDrawer() {
        SharedPreferences prefs = getSharedPreferences("sharedPlatform", 0);
        String newsTopic = prefs.getString(getString(R.string.pref_shared_platform), "General");
        websites = new ArrayList<Website>();
        websites.clear();
        switch (newsTopic) {
            case "General":
                websites = Utils.generalWebsiteList(this);
                topicTitle.setText(getString(R.string.topic_general));
                break;
            case "Sports":
                websites = Utils.sportsWebsiteList(this);
                topicTitle.setText(getString(R.string.topic_sports));
                break;
            case "Entertainment":
                websites = Utils.entertainmentWebsiteList(this);
                topicTitle.setText(getString(R.string.topic_entertainment));
                break;
            case "Business":
                websites = Utils.businessWebsiteList(this);
                topicTitle.setText(getString(R.string.topic_business));
                break;
            case "Technology":
                websites = Utils.technologyWebsiteList(this);
                topicTitle.setText(getString(R.string.topic_technology));
                break;
            case "Science and Nature":
                websites = Utils.scienceNatureWebsiteList(this);
                topicTitle.setText(getString(R.string.topic_science_nature));
                break;
            default:
                websites = Utils.generalWebsiteList(this);
                topicTitle.setText(getString(R.string.topic_general));
                break;
        }

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        NewsItemAdapter newsItemAdapter = new NewsItemAdapter(this, websites, mDrawer);
        recyclerView.setAdapter(newsItemAdapter);
    }


    public class NewsPagerAdapter extends FragmentPagerAdapter {


        public NewsPagerAdapter(FragmentManager fm) {

            super(fm);
        }


        @Override
        public int getCount() {
            return (ApiService.newsList().size() != 0) ? ApiService.newsList().size() : 0;
        }

        @Override
        public Fragment getItem(int position) {
            if (ApiService.newsList().get(position).getHeadline() == null) {
                return AdFragment.newInstance();
            }
            return ItemFragment.newInstance(position);

        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

    }


    static public boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public void adLoad() {

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int height = (int) AdFragment.convertPixelsToDp(metrics.heightPixels, this);

        AdView adView = new AdView(this);
        Log.i("Width", Integer.toString(height));
        AdSize adSize = new AdSize(-1, height);
        adView.setAdSize(adSize);
        adView.setAdUnitId(getString(R.string.main_ad_unit_id));


        // Initiate a generic request to load it with an ad
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest);

        Log.e("Loading Ads", "Ads");
    }
}

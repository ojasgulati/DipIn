package com.example.bittu.dipin.mainUi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.bittu.dipin.R;
import com.example.bittu.dipin.drawer.DrawerHeader;
import com.example.bittu.dipin.drawer.DrawerMenuItem;
import com.example.bittu.dipin.service.ApiService;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mindorks.placeholderview.PlaceHolderView;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends AppCompatActivity  implements SharedPreferences.OnSharedPreferenceChangeListener {

    @InjectView(R.id.swiperefresh)
    SwipeRefreshLayout mSwipeRefresh;
    @InjectView(R.id.drawerView)
    PlaceHolderView mDrawerView;
    @InjectView(R.id.drawerLayout)
    DrawerLayout mDrawer;
    @InjectView(R.id.drawerViewLayout)
    FrameLayout mDrawerViewLayout;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.vertical_view_pager)
    com.example.bittu.dipin.VerticalViewPager verticalViewPager;

    private boolean doNotifyDataSetChangedOnce = false;
    private boolean mIsRefreshing = false;

    private NewsPagerAdapter mPagerAdapter;

    final static String ANONYMOUS = "anonymous";
    public static final int RC_SIGN_IN = 1;

    public static String mUserId;
    public static String mUserEmailId;
    public static String mUsername;
    public static String mUserPic;


    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    FirebaseUser user;

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

        mFirebaseAuth = FirebaseAuth.getInstance();

        startService(new Intent(this, ApiService.class));

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startService(new Intent(MainActivity.this, ApiService.class));
            }
        });

        userAuth();
        setupDrawer();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sp = getSharedPreferences("sharedPlatform", 0);
        sp.unregisterOnSharedPreferenceChangeListener(this);
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

        SharedPreferences sp = getSharedPreferences("sharedPlatform", 0);
        sp.registerOnSharedPreferenceChangeListener(this);

        if(doNotifyDataSetChangedOnce) {
            mPagerAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    public void updateUI() {
        mSwipeRefresh.setRefreshing(mIsRefreshing);
        if (!mIsRefreshing) {
            mPagerAdapter = new NewsPagerAdapter(getSupportFragmentManager());
            verticalViewPager.setAdapter(mPagerAdapter);
            doNotifyDataSetChangedOnce = true;
        } else {
            Log.i("mIsRefreshing", "false");
        }

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
                } else {
                    // User is signed out
                    mUserId = ANONYMOUS;
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
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
                .addView(new DrawerMenuItem(this, DrawerMenuItem.DRAWER_MENU_ITEM_BREAK))
                .addView(new DrawerMenuItem(this, DrawerMenuItem.DRAWER_MENU_ITEM_RATE_US))
                .addView(new DrawerMenuItem(this, DrawerMenuItem.DRAWER_MENU_ITEM_FEEDBACK))
                .addView(new DrawerMenuItem(this, DrawerMenuItem.DRAWER_MENU_ITEM_SHARE_APP))
                .addView(new DrawerMenuItem(this, DrawerMenuItem.DRAWER_MENU_ITEM_SETTINGS))
                .addView(new DrawerMenuItem(this, DrawerMenuItem.DRAWER_MENU_ITEM_LOGOUT));

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, R.string.login_to_continue, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(getString(R.string.pref_shared_platform))) {
            mSwipeRefresh.setRefreshing(true);
            startService(new Intent(this, ApiService.class));
            Log.i("TAG", sharedPreferences.getString(getString(R.string.pref_shared_platform), "URL NULL"));
        }
    }

    public class NewsPagerAdapter extends FragmentStatePagerAdapter {


        public NewsPagerAdapter(FragmentManager fm) {

            super(fm);
        }


        @Override
        public int getCount() {
            return (ApiService.newsList().size() != 0) ? ApiService.newsList().size() : 0;
        }

        @Override
        public Fragment getItem(int position) {
            return ItemFragment.newInstance(position);
        }
        @Override
        public int getItemPosition(Object object){
            return PagerAdapter.POSITION_NONE;
        }

    }
}

package com.example.bittu.dipin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.transition.TransitionManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.bittu.dipin.drawer.DrawerHeader;
import com.example.bittu.dipin.drawer.DrawerMenuItem;
import com.example.bittu.dipin.service.ApiService;
import com.firebase.ui.auth.AuthUI;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.IntentPickerSheetView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.PlaceHolderView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.example.bittu.dipin.R.id.drawerView;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.bottomsheet)
    BottomSheetLayout bottomSheet;
    @InjectView(R.id.swiperefresh)
    SwipeRefreshLayout mSwipeRefresh;
    @InjectView(drawerView)
    PlaceHolderView mDrawerView;
    @InjectView(R.id.drawerLayout)
    DrawerLayout mDrawer;
    @InjectView(R.id.drawerViewLayout)
    FrameLayout mDrawerViewLayout;


    private boolean mIsRefreshing = false;

    NewsAdapter newsAdapter;
    LinearLayoutManager mLayoutManager;

    final static String ANONYMOUS = "anonymous";
    public static final int RC_SIGN_IN = 1;

    private Animation animationUp, animationDown;
    public static String mUserId;
    public static String mUserEmailId;
    public static String mUsername;
    public static String mUserPic;

    FirebaseDatabase mDatabase;
    DatabaseReference mDatabaseReference;
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
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        mUserId = ANONYMOUS;

        mDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        startService(new Intent(this, ApiService.class));

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startService(new Intent(MainActivity.this, ApiService.class));
            }
        });

        animationUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        animationDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        userAuth();
        setupDrawer();

    }

    @Override
    protected void onDestroy() {
        SharedPreferences sp = getSharedPreferences("sharedPlatform", 0);
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
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
        //TODO: notify after returning from favorites if data changed
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
        newsAdapter = new NewsAdapter(this, ApiService.newsList(), animationUp, animationDown);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(newsAdapter);

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
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_HOME))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_PLATFORMS))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_FAVORITES))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_BREAK))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_RATE_US))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_FEEDBACK))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_SHARE_APP))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_SETTINGS))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_LOGOUT));

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawer, R.string.open_drawer, R.string.close_drawer) {
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

                //TODO: set username at new installation
                Snackbar.make(mDrawer, "Welcome " + mUsername, Snackbar.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Please login to continue", Toast.LENGTH_LONG).show();
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


    public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
        private Context mContext;
        private List<News> mNews;
        private AnimatedVectorDrawable emptyHeart;
        private AnimatedVectorDrawable fillHeart;

        private Animation animationUp, animationDown;

        private final int COUNTDOWN_RUNNING_TIME = 500;

        ValueEventListener valueEventListener;


        public NewsAdapter(Context context, List<News> news, Animation animationUp, Animation animationDown) {
            mContext = context;
            mNews = news;
            this.animationDown = animationDown;
            this.animationUp = animationUp;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @InjectView(R.id.title)
            TextView title;
            @InjectView(R.id.date)
            TextView date;
            @InjectView(R.id.image)
            ImageView image;
            @InjectView(R.id.list_item_layout)
            FrameLayout listItemLayout;
            @InjectView(R.id.text_layout)
            LinearLayout textLayout;
            @InjectView(R.id.detail)
            LinearLayout detail;
            @InjectView(R.id.bookmark)
            ImageView bookmark;
            @InjectView(R.id.url_news)
            ImageView url;
            @InjectView(R.id.share_news)
            ImageView share;


            public ViewHolder(View view) {
                super(view);
                ButterKnife.inject(this, view);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder,int position) {

            final News currentNews = mNews.get(position);
            if (currentNews.getHeadline() != "null")
                holder.title.setText(currentNews.getHeadline());
            if (currentNews.getDate() != "null")
                holder.date.setText(currentNews.getDate());
            if (currentNews.getImgUrl() != "null") {
                setUpImage(currentNews, holder);
            } else
                holder.image.setImageDrawable(getDrawable(R.drawable.not_available));

            holder.detail.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.detail.isShown()) {
                        holder.detail.startAnimation(animationUp);
                        CountDownTimer countDownTimerStatic = new CountDownTimer(COUNTDOWN_RUNNING_TIME, 16) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                            }

                            @Override
                            public void onFinish() {
                                holder.detail.setVisibility(View.GONE);
                                TransitionManager.beginDelayedTransition(mRecyclerView);
                            }
                        };
                        countDownTimerStatic.start();
                    } else {
                        holder.detail.setVisibility(View.VISIBLE);
                        holder.detail.startAnimation(animationDown);
                        TransitionManager.beginDelayedTransition(mRecyclerView);
                    }
                }
            });

            emptyHeart = (AnimatedVectorDrawable) getDrawable(R.drawable.avd_heart_empty);
            fillHeart = (AnimatedVectorDrawable) getDrawable(R.drawable.avd_heart_fill);
            final String formattedHeadline = currentNews.getHeadline().replace("$", "").replace(".", "").replace("#", "").replace("[", "").replace("]", "");
            mDatabaseReference = mDatabase.getReference().child(mUserId).child(formattedHeadline);

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        AnimatedVectorDrawable drawable = fillHeart;
                        holder.bookmark.setImageDrawable(drawable);
                        drawable.start();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mDatabaseReference.addListenerForSingleValueEvent(valueEventListener);
            mDatabaseReference.removeEventListener(valueEventListener);
            holder.bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDatabaseReference = mDatabase.getReference().child(mUserId).child(formattedHeadline);
                    valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {
                                AnimatedVectorDrawable drawable = emptyHeart;
                                holder.bookmark.setImageDrawable(drawable);
                                drawable.start();
                                mDatabaseReference.removeValue();
                            } else {
                                News bookmarkNews = new
                                        News(currentNews.getHeadline(), currentNews.getDate(), currentNews.getImgUrl(), currentNews.getDescription(), currentNews.getAuthor(), currentNews.getUrl());
                                AnimatedVectorDrawable drawable = fillHeart;
                                holder.bookmark.setImageDrawable(drawable);
                                drawable.start();
                                mDatabaseReference.push().setValue(bookmarkNews);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    mDatabaseReference.addListenerForSingleValueEvent(valueEventListener);
                    mDatabaseReference.removeEventListener(valueEventListener);
                }
            });

            holder.url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra(mContext.getString(R.string.intent_position_detail), holder.getAdapterPosition());
                    mContext.startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
                }
            });


            holder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri bmpUri = getLocalBitmapUri(holder.image);
                    final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.share_news, currentNews.getHeadline(), currentNews.getUrl()));
                    shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                    shareIntent.setType("image/*");

                    bottomSheet.showWithSheetView(new IntentPickerSheetView(mContext, shareIntent, "Share with...", new IntentPickerSheetView.OnIntentPickedListener() {
                        @Override
                        public void onIntentPicked(IntentPickerSheetView.ActivityInfo activityInfo) {
                            bottomSheet.dismissSheet();
                            startActivity(activityInfo.getConcreteIntent(shareIntent));
                        }
                    }));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mNews.size();
        }

        private void setUpImage(News currentNews, final ViewHolder holder) {
            Glide.with(mContext)
                    .load(currentNews.getImgUrl().toString())
                    .fitCenter()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            Bitmap bitmap = ((GlideBitmapDrawable) resource.getCurrent()).getBitmap();
                            Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                                public void onGenerated(Palette palette) {
                                    int defaultColor = 0xFF333333;
                                    int darkMutedColor = palette.getDarkMutedColor(defaultColor);
                                    holder.title.setBackgroundColor(darkMutedColor);
                                    holder.listItemLayout.setBackgroundColor(darkMutedColor);
                                    holder.textLayout.setBackgroundColor(darkMutedColor);
                                    holder.date.setBackgroundColor(darkMutedColor);


                                }
                            });

                            return false;
                        }
                    })
                    .into(holder.image);
            holder.image.setAdjustViewBounds(true);

        }

        public Uri getLocalBitmapUri(ImageView imageView) {
            // Extract Bitmap from ImageView drawable
            Drawable drawable = imageView.getDrawable();
            Bitmap bmp = null;
            if (drawable instanceof BitmapDrawable) {
                bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            } else {
                return null;
            }
            // Store image to default external storage directory
            Uri bmpUri = null;
            try {
                // Use methods on Context to access package-specific directories on external storage.
                // This way, you don't need to request external read/write permission.
                // See https://youtu.be/5xVh-7ywKpE?t=25m25s
                File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.close();
                // **Warning:** This will fail for API >= 24, use a FileProvider as shown below instead.
                bmpUri = Uri.fromFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmpUri;
        }

    }


}

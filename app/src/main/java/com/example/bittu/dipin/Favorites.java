package com.example.bittu.dipin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.IntentPickerSheetView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.example.bittu.dipin.mainUi.MainActivity.mUserId;


public class Favorites extends AppCompatActivity {
    @InjectView(R.id.fav_recycler)
    RecyclerView favRecyclerView;
    @InjectView(R.id.fav_bottomsheet)
    BottomSheetLayout bottomSheet;
    @InjectView(R.id.favorites_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.fav_error_emptyView_image)
    ImageView emptyViewImage;
    @InjectView(R.id.fav_error_emptyView_text)
    TextView emptyViewText;
    private Animation animationUp, animationDown;

    public static boolean NOTIFY;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    ChildEventListener mEventListener;
    List<News> databaseNews = new ArrayList<News>();
    FavAdapter favAdapter;

    private static final String LOG_TAG = "FAVORITES";

    boolean networkAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_favorites);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NOTIFY = false;
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference().child(mUserId);
        animationUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        animationDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        networkAvailable = isNetworkAvailable(this);
        if (!networkAvailable) {
            emptyViewImage.setVisibility(View.VISIBLE);
            emptyViewText.setVisibility(View.VISIBLE);
            emptyViewText.setText(getString(R.string.no_connection));

        } else {
            emptyViewImage.setVisibility(View.GONE);
            emptyViewText.setVisibility(View.GONE);
            favRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }


    }



    static public boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    @Override
    protected void onResume() {
        if (networkAvailable) {
            attachDatabaseListener();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (networkAvailable) {
            mReference.removeEventListener(mEventListener);
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


    public void attachDatabaseListener() {
        mEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i(LOG_TAG, dataSnapshot.getChildren().toString());

                News news = null;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    news = postSnapshot.getValue(News.class);
                    Log.i(LOG_TAG, news.getImgUrl());
                }
                databaseNews.add(news);
                favAdapter = new FavAdapter(Favorites.this, databaseNews, animationUp, animationDown);
                favRecyclerView.setAdapter(favAdapter);
                favAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mReference.addChildEventListener(mEventListener);
    }


    public class FavAdapter extends RecyclerView.Adapter<FavAdapter.FavViewHolder> {
        Context mContext;
        List<News> mNews;

        private AnimatedVectorDrawable emptyHeart;
        private AnimatedVectorDrawable fillHeart;

        private Animation animationUp, animationDown;

        private final int COUNTDOWN_RUNNING_TIME = 500;


        public class FavViewHolder extends RecyclerView.ViewHolder {
            @InjectView(R.id.fav_title)
            TextView title;
            @InjectView(R.id.fav_date)
            TextView date;
            @InjectView(R.id.fav_image)
            ImageView image;
            @InjectView(R.id.fav_list_item_layout)
            FrameLayout listItemLayout;
            @InjectView(R.id.fav_text_layout)
            LinearLayout textLayout;
            @InjectView(R.id.fav_detail)
            LinearLayout detail;
            @InjectView(R.id.fav_bookmark)
            ImageView bookmark;
            @InjectView(R.id.fav_url_news)
            ImageView url;
            @InjectView(R.id.fav_share_news)
            ImageView share;

            public FavViewHolder(View itemView) {
                super(itemView);
                ButterKnife.inject(this, itemView);
            }
        }

        public FavAdapter(Context context, List<News> news, Animation animationUp, Animation animationDown) {
            mContext = context;
            mNews = news;
            this.animationDown = animationDown;
            this.animationUp = animationUp;
        }

        @Override
        public FavViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.fav_layout_item, parent, false);
            return new FavViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final FavViewHolder holder, int position) {
            final News currentNews = mNews.get(position);
            holder.title.setText(currentNews.getHeadline());
            if (!currentNews.getDate().equals("null")) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                try {
                    Date dtIn = simpleDateFormat.parse(currentNews.getDate());
                    Log.i("Time", Long.toString(dtIn.getTime()));

                    String dateFormated = Utils.printDifference(Favorites.this,dtIn);
                    if (dateFormated == null) {
                        holder.date.setText(currentNews.getAuthor());
                    } else {
                        holder.date.setText(dateFormated + " - " + currentNews.getAuthor());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            } else if (!(currentNews.getAuthor().equals("null") || currentNews.getAuthor().equals(""))) {
                holder.date.setText(currentNews.getAuthor());
            } else {
                holder.date.setVisibility(View.GONE);
            }
            setUpImage(currentNews, holder);

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
                                holder.image.setVisibility(View.GONE);
                                TransitionManager.beginDelayedTransition(favRecyclerView);
                            }
                        };
                        countDownTimerStatic.start();
                    } else {
                        holder.detail.setVisibility(View.VISIBLE);
                        holder.image.setVisibility(View.VISIBLE);
                        holder.detail.startAnimation(animationDown);
                        TransitionManager.beginDelayedTransition(favRecyclerView);
                    }
                }
            });

            emptyHeart = (AnimatedVectorDrawable) getDrawable(R.drawable.avd_heart_empty);
            fillHeart = (AnimatedVectorDrawable) getDrawable(R.drawable.avd_heart_fill);

            AnimatedVectorDrawable drawable = fillHeart;
            holder.bookmark.setImageDrawable(drawable);
            drawable.start();

            holder.bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int newPosition = holder.getAdapterPosition();
                    String formatedHeadline = currentNews.getHeadline().replace("$", "").replace(".", "").replace("#", "").replace("[", "").replace("]", "");
                    mReference = mDatabase.getReference().child(mUserId).child(formatedHeadline);
                    mReference.removeValue();
                    mNews.remove(newPosition);
                    AnimatedVectorDrawable drawable = emptyHeart;
                    holder.bookmark.setImageDrawable(drawable);
                    drawable.start();

                    notifyItemRemoved(newPosition);
                    notifyItemRangeChanged(newPosition, mNews.size());
                    NOTIFY = true;

                }
            });

            holder.url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra(mContext.getString(R.string.intent_position_detail), currentNews.getUrl());
                    mContext.startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
                }
            });


            holder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.share_news, currentNews.getHeadline(), currentNews.getUrl()));
                    shareIntent.setType("image/*");

                    bottomSheet.showWithSheetView(new IntentPickerSheetView(mContext, shareIntent, getString(R.string.article_share), new IntentPickerSheetView.OnIntentPickedListener() {
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

        private void setUpImage(News currentNews, final FavViewHolder holder) {
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
                                    holder.image.setVisibility(View.GONE);


                                }
                            });

                            return false;
                        }
                    })
                    .into(holder.image);
            holder.image.setAdjustViewBounds(true);

        }


    }
}

package com.example.bittu.dipin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
    private Animation animationUp, animationDown;

    public static boolean NOTIFY;
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    ChildEventListener mEventListener;
    List<News> databaseNews = new ArrayList<News>();
    FavAdapter favAdapter;

    private static final String LOG_TAG = "FAVORITES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NOTIFY = false;
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference().child(mUserId);
        animationUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        animationDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        favRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        attachDatabaseListener();


    }


    @Override
    protected void onPause() {
        super.onPause();
        mReference.removeEventListener(mEventListener);
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
            LinearLayout listItemLayout;
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
        public void onBindViewHolder(final FavViewHolder holder,int position) {
            final News currentNews = mNews.get(position);
            holder.title.setText(currentNews.getHeadline());
            holder.date.setText(currentNews.getDate());
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
                                TransitionManager.beginDelayedTransition(favRecyclerView);
                            }
                        };
                        countDownTimerStatic.start();
                    } else {
                        holder.detail.setVisibility(View.VISIBLE);
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
                    String formatedHeadline = currentNews.getHeadline().replace("$","").replace(".","").replace("#","").replace("[","").replace("]","");
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

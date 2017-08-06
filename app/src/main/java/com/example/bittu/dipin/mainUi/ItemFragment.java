package com.example.bittu.dipin.mainUi;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.bittu.dipin.DetailActivity;
import com.example.bittu.dipin.News;
import com.example.bittu.dipin.R;
import com.example.bittu.dipin.Utils;
import com.example.bittu.dipin.service.ApiService;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.IntentPickerSheetView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.example.bittu.dipin.mainUi.MainActivity.mUserId;

public class ItemFragment extends Fragment {
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.date)
    TextView date;
    @InjectView(R.id.image)
    ImageView image;
    @InjectView(R.id.image_blurred)
    ImageView imageBlurred;
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
    @InjectView(R.id.bottomsheet)
    BottomSheetLayout bottomSheet;
    @InjectView(R.id.image_loading)
    ProgressBar imageProgress;
    @InjectView(R.id.content_layout)
    LinearLayout contentLayout;

    private Animation animationUp, animationDown;
    DatabaseReference mDatabaseReference;

    private AnimatedVectorDrawable emptyHeart;
    private AnimatedVectorDrawable fillHeart;

    private final int COUNTDOWN_RUNNING_TIME = 500;

    ValueEventListener valueEventListener;

    public static String mPosition = "position";
    private int mCurrentPosition;


    public static Fragment newInstance(int position) {
        int positionNo = position;
        Bundle arguments = new Bundle();
        ItemFragment fragment = new ItemFragment();
        arguments.putInt(mPosition, positionNo);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCurrentPosition = getArguments().getInt(mPosition);
        Log.e("PositionFragment", Integer.toString(mCurrentPosition));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_item, container, false);
        ButterKnife.inject(this, view);

        List<News> mNews = ApiService.newsList();

        if (mNews.get(mCurrentPosition) != null) {
            News currentNews = mNews.get(mCurrentPosition);
            setupNews(currentNews);
            return view;
        }

        return null;

    }

    private void setupNews(final News currentNews){
        Log.i("Author", currentNews.getHeadline());
        if (!currentNews.getHeadline().equals("null"))
            title.setText(currentNews.getHeadline());
        if (!currentNews.getDate().equals("null")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            try {
                Date dtIn = simpleDateFormat.parse(currentNews.getDate());
                Log.i("Time", Long.toString(dtIn.getTime()));

                String dateFormated = Utils.printDifference(getActivity(), dtIn);
                if (dateFormated == null) {
                    date.setText(currentNews.getAuthor());
                } else {
                    date.setText(dateFormated + " - " + currentNews.getAuthor());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


        } else if (!(currentNews.getAuthor().equals("null") || currentNews.getAuthor().equals(""))) {
            date.setText(currentNews.getAuthor());
        } else {
            date.setVisibility(View.GONE);
        }


        if (!currentNews.getImgUrl().equals("null")) {
            setUpImage(currentNews);
        } else {
            image.setImageDrawable(getActivity().getDrawable(R.drawable.not_available));
            imageProgress.setVisibility(View.GONE);
            contentLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        animationUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        animationDown = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);

        detail.setVisibility(View.GONE);
        textLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (detail.isShown()) {
                    detail.startAnimation(animationUp);
                    CountDownTimer countDownTimerStatic = new CountDownTimer(COUNTDOWN_RUNNING_TIME, 16) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            detail.setVisibility(View.GONE);
                            TransitionManager.beginDelayedTransition(listItemLayout);
                        }
                    };
                    countDownTimerStatic.start();
                } else {
                    detail.setVisibility(View.VISIBLE);
                    detail.startAnimation(animationDown);
                    TransitionManager.beginDelayedTransition(listItemLayout);
                }
            }
        });

        emptyHeart = (AnimatedVectorDrawable) getActivity().getDrawable(R.drawable.avd_heart_empty);
        fillHeart = (AnimatedVectorDrawable) getActivity().getDrawable(R.drawable.avd_heart_fill);
        final String formattedHeadline = currentNews.getHeadline().replace("$", "").replace(".", "").replace("#", "").replace("[", "").replace("]", "");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(mUserId).child(formattedHeadline);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    AnimatedVectorDrawable drawable = fillHeart;
                    bookmark.setImageDrawable(drawable);
                    drawable.start();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addListenerForSingleValueEvent(valueEventListener);
        mDatabaseReference.removeEventListener(valueEventListener);
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(mUserId).child(formattedHeadline);
                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            AnimatedVectorDrawable drawable = emptyHeart;
                            bookmark.setImageDrawable(drawable);
                            drawable.start();
                            mDatabaseReference.removeValue();
                        } else {
                            News bookmarkNews = new
                                    News(currentNews.getHeadline(), currentNews.getDate(), currentNews.getImgUrl(), currentNews.getDescription(), currentNews.getAuthor(), currentNews.getUrl());
                            AnimatedVectorDrawable drawable = fillHeart;
                            bookmark.setImageDrawable(drawable);
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

        url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(getActivity().getString(R.string.intent_position_detail), currentNews.getUrl());
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
            }
        });


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_news, currentNews.getHeadline(), currentNews.getUrl()));
                shareIntent.setType("text/plain");

                bottomSheet.showWithSheetView(new IntentPickerSheetView(getActivity(), shareIntent, R.string.article_share, new IntentPickerSheetView.OnIntentPickedListener() {
                    @Override
                    public void onIntentPicked(IntentPickerSheetView.ActivityInfo activityInfo) {
                        bottomSheet.dismissSheet();
                        startActivity(activityInfo.getConcreteIntent(shareIntent));
                    }
                }));


            }
        });


    }


    private void setUpImage(News currentNews) {

        Glide.with(getActivity())
                .load(currentNews.getImgUrl().toString())
                .dontAnimate()
                .override(300, 300)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(final GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        imageProgress.setVisibility(View.GONE);

                        if (resource instanceof GlideBitmapDrawable) {
                            final Bitmap bitmap = ((GlideBitmapDrawable) resource.getCurrent()).getBitmap();


                            Glide.with(getActivity())
                                    .load(Utils.bitmapToByte(bitmap))
                                    .bitmapTransform(new jp.wasabeef.glide.transformations.BlurTransformation(getActivity(), 50))
                                    .dontAnimate()
                                    .into(imageBlurred);

                            Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                                public void onGenerated(Palette palette) {
                                    int defaultColor = 0xFF333333;
                                    int darkMutedColor = palette.getDarkMutedColor(defaultColor);
                                    contentLayout.setBackgroundColor(darkMutedColor);

                                }
                            });
                        } else {

                            int defaultColor = getResources().getColor(R.color.colorPrimaryDark);
                            title.setBackgroundColor(defaultColor);
                            listItemLayout.setBackgroundColor(defaultColor);
                            textLayout.setBackgroundColor(defaultColor);
                            date.setBackgroundColor(defaultColor);


                        }

                        return false;
                    }

                })
                .into(image);
    }

}

package com.example.bittu.dipin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.bittu.dipin.service.ApiService;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private static List<News> mNews;
//    public static void stackRemoteViews(List<News> news){
//        mNews = news;
//    }

    public StackRemoteViewsFactory(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        mNews = ApiService.newsList();

    }

    @Override
    public void onDestroy() {
        mNews.clear();

    }

    @Override
    public int getCount() {
        if (mNews == null) return 0;
        return mNews.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mNews == null) return null;
        News currentNews = mNews.get(position);
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.news_widget);
        views.setTextViewText(R.id.appwidget_title, currentNews.getHeadline());
        views.setTextViewText(R.id.appwidget_date,currentNews.getDate());


        try {
            Bitmap bitmap = Glide.with(mContext)
                    .load(currentNews.getImgUrl())
                    .asBitmap()
                    .into(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL)
                    .get();
            views.setImageViewBitmap(R.id.appwidget_image,bitmap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Bundle extras = new Bundle();
        extras.putLong(mContext.getString(R.string.intent_position_detail), position);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.appwidget_read_more, fillInIntent);
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

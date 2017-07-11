package com.example.bittu.dipin.service;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.bittu.dipin.News;
import com.example.bittu.dipin.NewsWidget;
import com.example.bittu.dipin.R;
import com.example.bittu.dipin.StackRemoteViewsFactory;
import com.example.bittu.dipin.StackRemoteViewsService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.action;


public class ApiService extends IntentService {
    private final static String LOG_TAG = "ApiService";
    public static final String BROADCAST_ACTION_STATE_CHANGE
            = "com.example.bittu.dipin.intent.action.STATE_CHANGE";
    public static final String EXTRA_REFRESHING
            = "com.example.bittu.dipin.intent.extra.REFRESHING";
    public static boolean ACTION_UPDATE_WIDGET = false;

    private static List<News> mNews;


    public ApiService() {
        super("ApiService");
    }



    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            Log.w(LOG_TAG, "Not online, not refreshing.");
            return;
        }
        sendBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, true));
        mNews = new ArrayList<News>();

        try {
            JSONArray array = QueryUtil.fetchJsonArray(getApplicationContext());
            if (array == null) {
                throw new JSONException("Invalid parsed item array");
            }

            for (int i = 0; i < array.length(); i++) {
                JSONObject article = array.getJSONObject(i);
                String headline = article.getString("title");
                String date = article.getString("publishedAt");
                String imgUrl = article.getString("urlToImage");
                String author = article.getString("author");
                String description = article.getString("description");
                String url = article.getString("url");
                mNews.add(new News(headline, date, imgUrl, description, author, url));
                Log.i(LOG_TAG, headline);
            }


        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error updating content.", e);
        }

        sendBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, false));

        updateWidget();

        ACTION_UPDATE_WIDGET = true;



    }

    private void updateWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, NewsWidget.class));
        //Now update all widgets
        NewsWidget.updateNewsWidget(this, appWidgetManager, mNews, appWidgetIds);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_stack_view);
    }

    public static List<News> newsList() {
        return mNews;
    }

}

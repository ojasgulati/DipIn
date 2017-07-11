package com.example.bittu.dipin;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.bittu.dipin.service.ApiService;

import java.util.List;
import java.util.Locale;

/**
 * Implementation of App Widget functionality.
 */
public class NewsWidget extends AppWidgetProvider {
    List<News> mNews;
    boolean mIsRefreshing;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,List<News> news,
                                int appWidgetId) {

        // Construct the RemoteViews object
        Log.i("Size",Integer.toString(news.size()));
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_stack_view);
        // Set the StackWidgetService intent to act as the adapter for the StackView
        Intent intent = new Intent(context, StackRemoteViewsService.class);
        views.setRemoteAdapter(R.id.widget_stack_view, intent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

        Intent appIntent = new Intent(context, DetailActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_stack_view, appPendingIntent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        //TODO: OPTIMISE calling 2nd time after being called by main activity
        //TODO: FIX Showing content whether user is is signed in or not
       if(ApiService.ACTION_UPDATE_WIDGET)
        context.startService(new Intent(context, ApiService.class));
    }

    public static void updateNewsWidget(Context context, AppWidgetManager appWidgetManager,List<News> news,
                                        int[] appWidgetIds){
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, news, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}


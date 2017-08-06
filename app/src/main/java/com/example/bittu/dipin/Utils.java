package com.example.bittu.dipin;


import android.content.Context;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Utils {

    private static List<Website> websiteList;

    public static String printDifference(Context context, Date startDate) {
        //milliseconds
        Calendar c = Calendar.getInstance();
        int current_date = c.get(Calendar.HOUR_OF_DAY);

        long different = -current_date + startDate.getTime();


        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        String dateFormated;
        if (elapsedHours > 0) {
            dateFormated = context.getString(R.string.time_hours, Long.toString(elapsedHours));
        } else if (elapsedMinutes > 0) {
            dateFormated = context.getString(R.string.time_minutes, Long.toString(elapsedMinutes));
        } else {
            dateFormated = null;
        }
        return dateFormated;
    }

    public static byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public static List<Website> generalWebsiteList(Context context) {

        websiteList = new ArrayList<Website>();

        websiteList.add(new Website(context.getResources().getString(R.string.Al_Jazeera_English), context.getDrawable(R.drawable.al_jazeera_english)));
        websiteList.add(new Website(context.getResources().getString(R.string.Associated_Press), context.getDrawable(R.drawable.associated_press)));
        websiteList.add(new Website(context.getResources().getString(R.string.BBC_News), context.getDrawable(R.drawable.bbc_news)));
        websiteList.add(new Website(context.getResources().getString(R.string.Breitbart_News), context.getDrawable(R.drawable.breitbart_news)));
        websiteList.add(new Website(context.getResources().getString(R.string.CNN), context.getDrawable(R.drawable.cnn)));
        websiteList.add(new Website(context.getResources().getString(R.string.Google_News), context.getDrawable(R.drawable.google_news)));
        websiteList.add(new Website(context.getResources().getString(R.string.Independent), context.getDrawable(R.drawable.independent)));
        websiteList.add(new Website(context.getResources().getString(R.string.Metro), context.getDrawable(R.drawable.metro)));
        websiteList.add(new Website(context.getResources().getString(R.string.Mirror), context.getDrawable(R.drawable.mirror)));
        websiteList.add(new Website(context.getResources().getString(R.string.Newsweek), context.getDrawable(R.drawable.newsweek)));
        websiteList.add(new Website(context.getResources().getString(R.string.New_York_Magazine), context.getDrawable(R.drawable.newyork_magazine)));
        websiteList.add(new Website(context.getResources().getString(R.string.The_Guardian_AU), context.getDrawable(R.drawable.the_guardian_au)));
        websiteList.add(new Website(context.getResources().getString(R.string.The_Guardian_UK), context.getDrawable(R.drawable.the_guardian_uk)));
        websiteList.add(new Website(context.getResources().getString(R.string.The_Hindu), context.getDrawable(R.drawable.the_hindu)));
        websiteList.add(new Website(context.getResources().getString(R.string.The_Huffington_Post), context.getDrawable(R.drawable.the_huffington_post)));
        websiteList.add(new Website(context.getResources().getString(R.string.The_New_York_Times), context.getDrawable(R.drawable.the_newyork_times)));
        websiteList.add(new Website(context.getResources().getString(R.string.The_Telegraph), context.getDrawable(R.drawable.the_telegraph)));
        websiteList.add(new Website(context.getResources().getString(R.string.The_Times_of_India), context.getDrawable(R.drawable.the_times_of_india)));
        websiteList.add(new Website(context.getResources().getString(R.string.Time), context.getDrawable(R.drawable.time)));

        return websiteList;
    }


    public static List<Website> sportsWebsiteList(Context context) {

        websiteList = new ArrayList<Website>();

        websiteList.add(new Website(context.getResources().getString(R.string.BBC_Sport), context.getDrawable(R.drawable.bbc_sport)));
        websiteList.add(new Website(context.getResources().getString(R.string.ESPN), context.getDrawable(R.drawable.espn)));
        websiteList.add(new Website(context.getResources().getString(R.string.ESPN_Cric_Info), context.getDrawable(R.drawable.espn_cricinfo)));
        websiteList.add(new Website(context.getResources().getString(R.string.Fox_Sports), context.getDrawable(R.drawable.fox_sports)));
        websiteList.add(new Website(context.getResources().getString(R.string.FourFourTwo), context.getDrawable(R.drawable.fourfourtwo)));
        websiteList.add(new Website(context.getResources().getString(R.string.TalkSport), context.getDrawable(R.drawable.talksport)));
        websiteList.add(new Website(context.getResources().getString(R.string.The_Sport_Bible), context.getDrawable(R.drawable.the_sport_bible)));

        return websiteList;
    }


    public static List<Website> entertainmentWebsiteList(Context context) {

        websiteList = new ArrayList<Website>();

        websiteList.add(new Website(context.getResources().getString(R.string.Buzzfeed), context.getDrawable(R.drawable.buzzfeed)));
        websiteList.add(new Website(context.getResources().getString(R.string.Entertainment_Weekly), context.getDrawable(R.drawable.entertainment_weekly)));
        websiteList.add(new Website(context.getResources().getString(R.string.Mashable), context.getDrawable(R.drawable.mashableindia)));
        websiteList.add(new Website(context.getResources().getString(R.string.The_Lad_Bible), context.getDrawable(R.drawable.the_lad_bible)));
        websiteList.add(new Website(context.getResources().getString(R.string.MTV_News), context.getDrawable(R.drawable.mtv_news)));
        websiteList.add(new Website(context.getResources().getString(R.string.MTV_News_UK), context.getDrawable(R.drawable.mtv_news)));
        websiteList.add(new Website(context.getResources().getString(R.string.Polygon), context.getDrawable(R.drawable.polygon)));

        return websiteList;
    }


    public static List<Website> businessWebsiteList(Context context) {

        websiteList = new ArrayList<Website>();

        websiteList.add(new Website(context.getResources().getString(R.string.Bloomberg), context.getDrawable(R.drawable.bloomberg)));
        websiteList.add(new Website(context.getResources().getString(R.string.Business_Insider), context.getDrawable(R.drawable.business_insider)));
        websiteList.add(new Website(context.getResources().getString(R.string.Business_Insider_UK), context.getDrawable(R.drawable.business_insider)));
        websiteList.add(new Website(context.getResources().getString(R.string.CNBC), context.getDrawable(R.drawable.cnbc)));
        websiteList.add(new Website(context.getResources().getString(R.string.Die_Zeit), context.getDrawable(R.drawable.die_zeit)));
        websiteList.add(new Website(context.getResources().getString(R.string.Financial_Times), context.getDrawable(R.drawable.financial_times)));
        websiteList.add(new Website(context.getResources().getString(R.string.Fortune), context.getDrawable(R.drawable.fortune)));
        websiteList.add(new Website(context.getResources().getString(R.string.The_Economist), context.getDrawable(R.drawable.the_economist)));

        return websiteList;
    }


    public static List<Website> technologyWebsiteList(Context context) {

        websiteList = new ArrayList<Website>();

        websiteList.add(new Website(context.getResources().getString(R.string.Engadget), context.getDrawable(R.drawable.engadget)));
        websiteList.add(new Website(context.getResources().getString(R.string.Gruenderszene), context.getDrawable(R.drawable.gruenderszene)));
        websiteList.add(new Website(context.getResources().getString(R.string.Hacker_News), context.getDrawable(R.drawable.hacker_news)));
        websiteList.add(new Website(context.getResources().getString(R.string.Recode), context.getDrawable(R.drawable.recode)));
        websiteList.add(new Website(context.getResources().getString(R.string.TechCrunch), context.getDrawable(R.drawable.techcrunch)));
        websiteList.add(new Website(context.getResources().getString(R.string.TechRadar), context.getDrawable(R.drawable.techradar)));
        websiteList.add(new Website(context.getResources().getString(R.string.The_Next_Web), context.getDrawable(R.drawable.the_next_web)));
        websiteList.add(new Website(context.getResources().getString(R.string.The_Verge), context.getDrawable(R.drawable.the_verge)));

        return websiteList;
    }

    public static List<Website> scienceNatureWebsiteList(Context context) {

        websiteList = new ArrayList<Website>();

        websiteList.add(new Website(context.getResources().getString(R.string.National_Geographic), context.getDrawable(R.drawable.national_geographic)));
        websiteList.add(new Website(context.getResources().getString(R.string.New_Scientist), context.getDrawable(R.drawable.new_scientist)));

        return websiteList;
    }


}

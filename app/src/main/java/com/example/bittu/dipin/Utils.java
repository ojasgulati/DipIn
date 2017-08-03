package com.example.bittu.dipin;


import android.content.Context;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;

public class Utils {

    public static String printDifference(Context context,Date startDate) {
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

    public static byte[] bitmapToByte(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
}

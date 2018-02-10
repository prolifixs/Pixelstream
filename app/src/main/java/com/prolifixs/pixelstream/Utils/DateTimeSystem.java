package com.prolifixs.pixelstream.Utils;

import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Prolifixs on 2/8/2018.
 */

public class DateTimeSystem {
    private static final String TAG = "DateTimeSystem";

    //-------------TimeZone and location is hard coded...need to get user information in future.
    public String getHourTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Turkey"));
        return simpleDateFormat.format(new Date());
    }

    public String getTwentyFourHours(){
        Log.d(TAG, "getTwentyFourHours: getting all 24 hours");
        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("Turkey"));
        Date today = c.getTime();
        sdf.format(today);

        return sdf.format(new Date());
    }



    //Get timeStamp-------move to utils when working
    public String getTimeStamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("Turkey"));
        return sdf.format(new Date());
    }








}

package com.sports.unity.util.commons;

import android.content.Context;

import com.sports.unity.R;
import com.sports.unity.common.model.Match;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by madmachines on 29/2/16.
 */
public class DateUtil {
    private static final SimpleDateFormat formatter =  new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DATE_FORMAT =  new SimpleDateFormat("dd MMMM yyyy");
    private static final SimpleDateFormat TIME_FORMAT =  new SimpleDateFormat("HH:mm");
    public  static String getDaysDiffrence(String oldDate,Context context){
      String days;
        Calendar with = getCalendar(oldDate);
        Calendar to = Calendar.getInstance();
        to.set(Calendar.YEAR, with.get(Calendar.YEAR));
        int withDAY = with.get(Calendar.DAY_OF_YEAR);
        int toDAY = to.get(Calendar.DAY_OF_YEAR);

        Integer diffDay =  withDAY-toDAY   ;
        if(diffDay == 0){
            days = context.getString(R.string.today);
        } else if (diffDay == 1)
        {
            days = context.getString(R.string.tomorrow);
        }else {
            days = String.format(context.getString(R.string. ucoming_match_day_format),String.valueOf(diffDay));

        }

        return days;
    }


     public static Calendar getCalendar(String date){

         try {
             Date aDate = formatter.parse(date);
             Calendar with = Calendar.getInstance();
             with.setTime(aDate);
             return  with;
         } catch (ParseException e) {
             e.printStackTrace();
         }
         return Calendar.getInstance();

     }

    public static String getFormattedDate(String oldDate){

       String date = "";
        try
        {
            Date date1 = formatter.parse(oldDate);
            date = DATE_FORMAT.format(date1);

        }
        catch (Exception e)
        {
            date = oldDate;
        }
        return  date;
    }


    public static String getMatchDays(long l,Context context) {
        String days = "";

        Calendar with = Calendar.getInstance();
        with.setTimeInMillis(l);
        Calendar to = Calendar.getInstance();
        to.set(Calendar.YEAR, with.get(Calendar.YEAR));
        int withDAY = with.get(Calendar.DAY_OF_YEAR);
        int toDAY = to.get(Calendar.DAY_OF_YEAR);

        int diffDay =  withDAY-toDAY   ;
        if(diffDay == 0){
            days = context.getString(R.string.today);
        } else if (diffDay == 1)
        {
            days = context.getString(R.string.tomorrow);
        }else {
            days = String.format(context.getString(R.string.days),String.valueOf(diffDay));
        }

        return days;
    }

    public static String getMatchTime(long l) {

        Calendar with = Calendar.getInstance();
        with.setTimeInMillis(l);
        return TIME_FORMAT.format(with.getTime());
    }


    public static String getDayFromEpochTime(long l,Context context) {
        String days = "";

        Calendar with = Calendar.getInstance();
        with.setTimeInMillis(l);
        Calendar to = Calendar.getInstance();
        to.set(Calendar.YEAR, with.get(Calendar.YEAR));
        int withDAY = with.get(Calendar.DAY_OF_YEAR);
        int toDAY = to.get(Calendar.DAY_OF_YEAR);

        int diffDay =  withDAY-toDAY   ;
        if(diffDay == 0){
            days = context.getString(R.string.today);
        } else if (diffDay == 1)
        {
            days = context.getString(R.string.tomorrow);
        } else {
               if(diffDay<0){
                   days=   String.format(context.getString(R.string.daysago),String.valueOf(Math.abs(diffDay)));
               }else{
                   days=   String.format(context.getString(R.string.days),String.valueOf(diffDay));
               }


        }

        return days;
    }


}
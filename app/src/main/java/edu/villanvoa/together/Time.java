package edu.villanvoa.together;

import android.util.Log;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created by aodell on 4/12/15.
 */
public class Time {
    public int timeToInt(String time) {
        StringTokenizer st = new StringTokenizer(time,": ");
        int hour = Integer.valueOf(st.nextElement().toString()) % 12;
        int min = Integer.valueOf(st.nextElement().toString());
        boolean isam = st.nextElement().toString().equals("AM");

        if(!isam) {
            hour += 12;
        }

        int t = hour * 60;
        t += min;

        return t;
    }

    public String findMidTime(String startTime, String endTime) {
        int start = timeToInt(startTime);
        int end = timeToInt(endTime);
        int mid;

        Log.i("debug","start Time: " + start);
        Log.i("debug","end Time: " + end);

        if(end > start) {
            mid = (end + start ) / 2;
            Log.i("debug","mid Time: " + mid);
        } else {
            int maxtime = timeToInt("11:59 PM");
            mid = (end + maxtime + start) / 2;
            if(mid > maxtime) {
                mid -= maxtime;
            }
            Log.i("debug","mid (special) Time: " + mid);
        }

        return intToTime(mid);
    }

    public String intToTime(int time) {
        if(time < 0) {
            time = timeToInt("11:59 PM") + time;
        }

        int hour = time / 60;
        int minute = time % 60;
        boolean ispm = hour > 12;
        String ampm = "AM";

        if(ispm) {
            hour -= 12;
            ampm = "PM";
        }

        String smin = String.valueOf(minute);

        if(minute < 10) {
            smin = "0" + minute;
        }

        String t = hour + ":" + smin + " " + ampm;

        Log.i("debug","time: " + time + ", hour: " + hour + ", minute: " + minute + ", isam: " + ispm);
        return t;
    }
}

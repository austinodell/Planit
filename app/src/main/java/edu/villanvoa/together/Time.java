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

        Log.i("debug","hour = " + hour + " min = " + min + " isam = " + isam + " t = " + t);

        return t;
    }
}

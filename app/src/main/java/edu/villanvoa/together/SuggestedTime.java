package edu.villanvoa.together;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by wildcat on 4/12/2015.
 */
public class SuggestedTime {
    private String startTime, endTime;
    private List<TimeAvailable> userAvailableTimes;

    public SuggestedTime(List<TimeAvailable> userAvailableTimes) {
        this.userAvailableTimes = userAvailableTimes;
        this.startTime = suggestedStartTime(userAvailableTimes);
        this.endTime = suggestedEndTime(userAvailableTimes);
    }

    private String suggestedStartTime(List<TimeAvailable> userAvailableTimes) {
        ArrayList<Date> userStartTimes = new ArrayList<>();
        Date startTime;
        int i;
        for (TimeAvailable timeAvailable : userAvailableTimes) {
            try {
                userStartTimes.add(new SimpleDateFormat("hh:mm aa").parse(timeAvailable.getStartTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(userStartTimes);
        if (userStartTimes.size() >= 3) {
            i = (((userStartTimes.size() / 3) * 2) - 1);
        }
        else {
            i = 0;
        }
        //Log.d("Debugging", "i: " + Integer.toString(i));
        //Log.d("Debugging", userStartTimes.toString());
        startTime = userStartTimes.get(i);

        return new SimpleDateFormat("h:mm aa").format(startTime);
    }

    private String suggestedEndTime(List<TimeAvailable> userAvailableTimes) {
        ArrayList<Date> userEndTimes = new ArrayList<>();
        Date endTime;
        int i;
        for (TimeAvailable timeAvailable : userAvailableTimes) {
            try {
                userEndTimes.add(new SimpleDateFormat("hh:mm aa").parse(timeAvailable.getEndTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(userEndTimes);
        if (userEndTimes.size() >= 3) {
            i = (((userEndTimes.size() / 3) * 2) - 1);
        }
        else {
            i = 0;
        }
        //Log.d("Debugging", "i: " + Integer.toString(i));
        //Log.d("Debugging", userStartTimes.toString());
        endTime = userEndTimes.get(i);

        return new SimpleDateFormat("h:mm aa").format(endTime);
    }

    private ArrayList<Date> timeSort(ArrayList<Date> userTimes) {
        ArrayList<Date> sortedTimes = new ArrayList<>();
        Date first;
        for (Date date : userTimes) {
            for (int i = 0; i < sortedTimes.size(); i++) {
                if (date.before(sortedTimes.get(i))) {
                }
            }
        }
        return userTimes;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<TimeAvailable> getUserAvailableTimes() {
        return userAvailableTimes;
    }

    public void setUserAvailableTimes(List<TimeAvailable> userAvailableTimes) {
        this.userAvailableTimes = userAvailableTimes;
    }

    @Override
    public String toString() {
        return startTime + " - " + endTime;
    }
}

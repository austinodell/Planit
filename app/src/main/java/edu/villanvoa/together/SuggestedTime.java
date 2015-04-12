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
    String startTime, endTime;
    List<TimeAvailable> userAvailableTimes;

    public SuggestedTime(List<TimeAvailable> userAvailableTimes) {
        this.userAvailableTimes = userAvailableTimes;
    }

    private String suggestedStartTime(List<TimeAvailable> userAvailableTimes) {
        ArrayList<Date> userStartTimes = new ArrayList<>();
        for (TimeAvailable timeAvailable : userAvailableTimes) {
            try {
                userStartTimes.add(new SimpleDateFormat("HH:mm").parse(timeAvailable.getStartTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(userStartTimes);

        return null;
    }

    private ArrayList<Date> timeSort(ArrayList<Date> userTimes) {
        ArrayList<Date> sortedTimes = new ArrayList<>();
        Date first;
        for (Date date : userTimes) {
            for (int i=0; i<sortedTimes.size(); i++) {
                if (date.before(sortedTimes.get(i))) {
                }
            }
        }
        return userTimes;
    }
}

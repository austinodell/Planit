package edu.villanvoa.together;

import android.text.format.Time;

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
        List<Time> userStartTimes;
        for (TimeAvailable timeAvailable : userAvailableTimes) {

        }
        return null;
    }
}

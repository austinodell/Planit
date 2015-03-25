package edu.villanvoa.together;

/**
 * Created by wildcat on 3/24/2015.
 */
public class TimeAvailable {
    String startTime, endTime, userFbId;

    public TimeAvailable(String userFbId, String startTime, String endTime){
        this.endTime = endTime;
        this.startTime = startTime;
        this.userFbId = userFbId;
    }

    @Override
    public String toString() {
        return "TimeAvailable{" +
                "startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", userFbId='" + userFbId + '\'' +
                '}';
    }
}

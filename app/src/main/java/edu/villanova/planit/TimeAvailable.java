package edu.villanova.planit;

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

    public String getUserFbId() {
        return userFbId;
    }

    public void setUserFbId(String userFbId) {
        this.userFbId = userFbId;
    }
}

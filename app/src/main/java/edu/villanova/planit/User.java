package edu.villanova.planit;

/**
 * Created by aodell on 3/16/15.
 */
public class User {
    private String name;
    private String imageURL = null;
    private String id;
    private String startTime, endTime;

    public User(String id, String name, String imageURL, String startTime, String endTime) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getName() {
        return this.name;
    }

    public boolean imgIsURL() {
        return this.imageURL != null;
    }

    public String getImageURL() {
        return this.imageURL;
    }

    public String getId() {
        return this.id;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public String getFirstName() {
        int spaceIndex = name.indexOf(' ');
        return name.substring(0, spaceIndex);
    }
}

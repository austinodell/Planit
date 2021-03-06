package edu.villanova.planit;

/**
 * Created by aodell on 3/16/15.
 */
public class Event {
    private String name;
    private int drawableId;
    private String imageURL = null;
    private String id;

    public Event(String id, String name, int drawableId) {
        this.id = id;
        this.name = name;
        this.drawableId = drawableId;
    }

    public Event(String id, String name, String imageURL) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageResource(int drawableId) {
        this.drawableId = drawableId;
        this.imageURL = null;
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

    public int getImageResource() {
        return this.drawableId;
    }

    public String getId() {
        return this.id;
    }
}

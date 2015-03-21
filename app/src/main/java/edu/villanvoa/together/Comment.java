package edu.villanvoa.together;

/**
 * Created by Matt_Wiedmeier on 3/15/15.
 */

public class Comment {

    private String name;
    private String comment;
    private String userID;
    private String timeStamp;

    public Comment(String name,String comment, String userID, String timeStamp){

        this.name = name;
        this.comment = comment;
        this.userID = userID;
        this.timeStamp = timeStamp;

    }

    public String getName(){
        return name;
    }

    public String getComment(){
        return comment;
    }

    public String getTimeStamp() { return timeStamp; }

    public String getUserID() { return userID; }

    public void setComment(String comment){
        this.comment = comment;
    }

}

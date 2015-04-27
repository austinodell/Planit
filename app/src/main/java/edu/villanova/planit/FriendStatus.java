package edu.villanova.planit;

/**
 * Created by wildcat on 4/8/2015.
 */
public class FriendStatus {
    private String friendName, friendStatus, friendId;

    public FriendStatus(String friendName, String friendStatus, String friendId) {
        this.friendName = friendName;
        this.friendStatus = friendStatus;
        this.friendId = friendId;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getFriendStatus() {
        return friendStatus;
    }

    public void setFriendStatus(String friendStatus) {
        this.friendStatus = friendStatus;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }
}

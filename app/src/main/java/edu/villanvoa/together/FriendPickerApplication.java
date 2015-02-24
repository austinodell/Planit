package edu.villanvoa.together;

import android.app.Application;

import com.facebook.model.GraphUser;

import java.util.List;

/**
 * Created by wildcat on 2/22/2015.
 */
public class FriendPickerApplication extends Application {
    private List<GraphUser> selectedUsers;

    public List<GraphUser> getSelectedUsers() {
        return selectedUsers;
    }

    public void setSelectedUsers(List<GraphUser> selectedUsers) {
        this.selectedUsers = selectedUsers;
    }
}

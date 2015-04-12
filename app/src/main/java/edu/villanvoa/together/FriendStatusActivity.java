package edu.villanvoa.together;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import junit.framework.Test;

import java.util.ArrayList;


public class FriendStatusActivity extends ActionBarActivity {
    final static String TAG = "Debugging";
    ArrayList<String> friendsNames, friendsIds;
    ArrayList<FriendStatus> friendStatusList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_status);

        friendsNames = new ArrayList<String>();
        friendsIds = new ArrayList<String>();
        friendStatusList = getFriendStatuses();

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.friend_status, friendsNames);
        ImageLib imgLib = new ImageLib(this);
        FriendStatusAdapter adapter = new FriendStatusAdapter(this, imgLib, friendStatusList);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        //Log.d(TAG, "Count: " + adapter.getCount());
    }

    private ArrayList<FriendStatus> getFriendStatuses() {
        ArrayList<FriendStatus> friendStatusList = new ArrayList<>();
        Intent callingIntent = getIntent();
        friendsNames = callingIntent.getStringArrayListExtra("friendsNames");
        friendsIds = callingIntent.getStringArrayListExtra("friendsIds");
        ParseQuery<ParseObject> queryAll = ParseQuery.getQuery("User");
        ParseObject parseObject;
        for (int i=0; i < friendsNames.size(); i++) {
            try {
                parseObject = queryAll.whereEqualTo("FacebookID", friendsIds.get(i)).getFirst();
                friendStatusList.add(new FriendStatus(friendsNames.get(i), parseObject.getString("UserStatus"), friendsIds.get(i)));
            } catch (ParseException e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            }
            Log.d(TAG, "Friend: " + friendsNames.get(i));
        }
        return friendStatusList;
    }
}

package edu.villanvoa.together;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aodell on 4/12/15.
 */
public class ViewTime extends ToolbarActivity {
    private ArrayList<User> userList;
    private ImageLib imgLib;
    private TimeGridAdapter timeAdapter;
    private Intent callingIntent;
    private String startTime;
    private String endTime, eventObjectId, userFbId;
    private List<TimeAvailable> userTimesList = new ArrayList<TimeAvailable>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_times);

        callingIntent = getIntent();
        startTime = callingIntent.getStringExtra("StartTime");
        endTime = callingIntent.getStringExtra("EndTime");
        eventObjectId = getIntent().getStringExtra("EventObjId");

        SharedPreferences sharedPreferences;
        sharedPreferences = this.getSharedPreferences("UserDetails", MainActivity.MODE_PRIVATE);
        userFbId = sharedPreferences.getString("UserFbId", null);

        // Initializes Universal Image Loader Library to load images given URLs
        imgLib = new ImageLib(this);

        // Used to store all events to pass to HomeGridAdapter
        userList = new ArrayList<User>();
        getUserTimes();

        Time timeObj = new Time();
        timeObj.timeToInt("12:00 AM");
        timeObj.timeToInt("11:59 PM");
        timeObj.timeToInt("9:00 PM");
        timeObj.timeToInt("11:20 PM");

        // Setup Adapter
        GridView gridView = (GridView) findViewById(R.id.container);
        timeAdapter = new TimeGridAdapter(this, userList, imgLib, startTime, endTime);
        gridView.setAdapter(timeAdapter);
    }

    // Add user with Image URL (downloaded)
    private void addUser(String id, String name, String startTime, String endTime) {
        String img = "http://graph.facebook.com/" + id + "/picture?size=large";
        userList.add(new User(id, name, img, startTime, endTime));
    }

    private void getUserTimes() {
        List<ParseObject> parseObjects;
        ParseObject parseObject;
        ParseQuery<ParseObject> userToEventQuery = ParseQuery.getQuery("UserToEvent");
        userToEventQuery.whereEqualTo("EventId", eventObjectId);
        try {
            parseObjects = userToEventQuery.find();
            for (int i = 0; i < parseObjects.size(); i++) {
                parseObject = parseObjects.get(i);
                if (parseObject.getString("StartTime") != null) {
                    addUser(parseObject.getString("UserFbId"), parseObject.getString("UserName"), parseObject.getString("StartTime"), parseObject.getString("EndTime"));
                }
            }
        } catch (
                ParseException e
                )

        {
            e.printStackTrace();
            Log.d("debug", e.toString());
        }
    }
}

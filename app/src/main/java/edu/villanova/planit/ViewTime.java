package edu.villanova.planit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.TextView;

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

        setupToolbar(startTime + " - " + endTime);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Setup Adapter
        GridView gridView = (GridView) findViewById(R.id.container);
        timeAdapter = new TimeGridAdapter(this, userList, imgLib, startTime, endTime);
        gridView.setAdapter(timeAdapter);

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        View span1_1 = (View) findViewById(R.id.span1_1);
        View span1_2 = (View) findViewById(R.id.span1_2);
        View span2_1 = (View) findViewById(R.id.span2_1);
        View span2_2 = (View) findViewById(R.id.span2_2);
        int spanWidth = (width - 86) / 4;
        Log.i("debug","SpanWidth: " + spanWidth + ", ScreenWidth: " + width);

        span1_1.getLayoutParams().width = spanWidth;
        span1_2.getLayoutParams().width = spanWidth;
        span2_1.getLayoutParams().width = spanWidth;
        span2_2.getLayoutParams().width = spanWidth;

        TextView time1 = (TextView) findViewById(R.id.tv_time_1);
        TextView time2 = (TextView) findViewById(R.id.tv_time_2);
        TextView time3 = (TextView) findViewById(R.id.tv_time_3);

        Time timeObj = new Time();
        int sTimeI = timeObj.timeToInt(startTime) - 60;
        String sTime = timeObj.intToTime(sTimeI);
        String mTime = timeObj.findMidTime(startTime,endTime);
        int eTimeI = timeObj.timeToInt(endTime) + 60;
        String eTime = timeObj.intToTime(eTimeI);

        time1.setText(sTime);
        time2.setText(mTime);
        time3.setText(eTime);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

package edu.villanvoa.together;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class Home extends ToolbarActivity {

    private static final String TAG = "Debugging";

    private ArrayList<Event> eventsList;
    private Context mContext;

    private String userFbId;
    private Intent viewEventIntent;

    private ImageLib imgLib;

    private int event_id = 0; // temporary - to be populated by Parse

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Parse.initialize(this, "YMPhMAAd5vjkITGtdjD2pNsLmfAIhYZ5u3gXFteJ", "5w3m3Zex78Knrz69foyli8FKAv96PEzNlhBNJL3l");

        mContext = this;

        imgLib = new ImageLib(this);

        setupToolbar(R.string.title_activity_home);

        //Get userId from shared preferences
        SharedPreferences sharedPreferences;
        sharedPreferences = this.getSharedPreferences("UserDetails", MainActivity.MODE_PRIVATE);
        userFbId = sharedPreferences.getString("UserFbId", null);

        // Initializes Universal Image Loader Library to load images given URLs
        ImageLib imgLib = new ImageLib(mContext);

        // Used to store all events to pass to HomeGridAdapter
        eventsList = new ArrayList<Event>();

        // Populate Events List
        addEventsFromParse(userFbId);

        // Setup Adapter
        GridView gridView = (GridView) findViewById(R.id.container);
        gridView.setAdapter(new HomeGridAdapter(this, eventsList, imgLib));

        // Add New Event Button Setup
        Button new_event_btn = (Button) findViewById(R.id.new_event_btn);
        new_event_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, AddFriends.class));
            }
        });
    }

    //Add users events from the parse database
    private void addEventsFromParse(String userFbId) {
        ParseObject parseObject, eventObject;
        List<ParseObject> parseObjects;

        if (userFbId != null) {
            ParseQuery<ParseObject> queryAll = ParseQuery.getQuery("UserToEvent");
            queryAll.whereEqualTo("UserFbId", userFbId);
            try {
                parseObjects = queryAll.find();
                Log.i(TAG,"query # events = " + parseObjects.size());
                for (int i = 0; i < parseObjects.size(); i++) {
                    parseObject = parseObjects.get(i);
                    ParseQuery<ParseObject> eventQuery = ParseQuery.getQuery("Event");
                    eventQuery.whereEqualTo("objectId",parseObject.getString("EventId"));
                    eventObject = eventQuery.find().get(0);

                    String imgLocalString = eventObject.getString("ImageType");
                    if(imgLocalString == null) {
                        imgLocalString = "local";
                    }
                    if(imgLocalString.equals("local")) {
                        if(eventObject.getString("ImageResID") != null && !eventObject.getString("ImageResID").equals("")) {
                            addEvent(parseObject.getString("EventId"), eventObject.getString("Title"), true, eventObject.getString("ImageResID"));
                        } else {
                            addEvent(parseObject.getString("EventId"), eventObject.getString("Title"));
                        }
                    } else {
                        addEvent(parseObject.getString("EventId"), eventObject.getString("Title"), false, "http://planit.austinodell.com/img/" + eventObject.getString("ImageURL"));
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            }
        } else {
            Log.d(TAG, "Userid null");
        }
    }

    // Add event with Image Drawable Resource (preloaded) or URL (downloaded)
    private void addEvent(String id, String name, boolean imgLocal, String img) {
        Event event;
        if(imgLocal) {
            int res_id = imgLib.getResId(img);
            event = new Event(id, name, res_id);
        } else {
            event = new Event(id, name, img);
        }
        eventsList.add(event);
    }

    // Add event with no predefined image
    private void addEvent(String id, String name) {
        Event event = new Event(id, name, R.drawable.picnic);
        eventsList.add(event);
    }
}

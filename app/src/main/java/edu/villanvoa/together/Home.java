package edu.villanvoa.together;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.facebook.Session;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class Home extends ToolbarActivity implements GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "Debugging";

    GoogleApiClient apiClient;
    private PendingIntent mActivityRecognitionPendingIntent;

    private ArrayList<Event> eventsList;
    private Context mContext;
    private SwipeRefreshLayout swipeEventLayout;

    private String userFbId;
    private Intent viewEventIntent;

    private ImageLib imgLib;

    private HomeGridAdapter eventAdapter;
    GridView gridView;
    LinearLayout sad_layout;

    private int event_id = 0; // temporary - to be populated by Parse

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        apiClient = null;
        apiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .build();
        apiClient.connect();
        mActivityRecognitionPendingIntent = null;

        Parse.initialize(this, "YMPhMAAd5vjkITGtdjD2pNsLmfAIhYZ5u3gXFteJ", "5w3m3Zex78Knrz69foyli8FKAv96PEzNlhBNJL3l");

        mContext = this;

        imgLib = new ImageLib(this);

        setupToolbar(R.string.title_activity_home);

        //Get userId from shared preferences
        SharedPreferences sharedPreferences;
        sharedPreferences = this.getSharedPreferences("UserDetails", MainActivity.MODE_PRIVATE);
        userFbId = sharedPreferences.getString("UserFbId", null);

        // Initializes Universal Image Loader Library to load images given URLs
        final ImageLib imgLib = new ImageLib(mContext);

        // Used to store all events to pass to HomeGridAdapter
        eventsList = new ArrayList<Event>();

        // Populate Events List
        addEventsFromParse(userFbId);

        // Setup Adapter
        gridView = (GridView) findViewById(R.id.container);
        sad_layout = (LinearLayout) findViewById(R.id.sad_layout);

        if(eventsList.size() == 0) { // Check to see if user has events
            gridView.setVisibility(View.INVISIBLE);
            sad_layout.setVisibility(View.VISIBLE); // Display message
        } else {
            gridView.setVisibility(View.VISIBLE);
            sad_layout.setVisibility(View.INVISIBLE); // Hide message

            eventAdapter = new HomeGridAdapter(this, eventsList, imgLib);
            gridView.setAdapter(eventAdapter);
        }

        // Add New Event Button Setup
        Button new_event_btn = (Button) findViewById(R.id.new_event_btn);
        new_event_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, AddFriends.class));
            }
        });

        setAlarm();

        swipeEventLayout = (SwipeRefreshLayout) findViewById(R.id.swipeEventContainer);
        swipeEventLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("Scroll Container", "Refreshing");

                // Used to store all events to pass to HomeGridAdapter

                eventsList = new ArrayList<Event>();

                // Populate Events List
                addEventsFromParse(userFbId);

                if(eventsList.size() > 0 && eventAdapter == null){
                    gridView.setVisibility(View.VISIBLE);
                    sad_layout.setVisibility(View.INVISIBLE); // Hide message
                    eventAdapter = new HomeGridAdapter(getApplicationContext(), eventsList, imgLib);
                    gridView.setAdapter(eventAdapter);
                }
                else if(eventsList.size()==0 && eventAdapter != null){

                    gridView.setVisibility(View.INVISIBLE);
                    sad_layout.setVisibility(View.VISIBLE); // Display message
                    eventAdapter = null;

                }

                if(eventAdapter != null) {
                    eventAdapter.addItems(eventsList);
                    eventAdapter.notifyDataSetChanged();
                }
                // Stop the refresh once you have the data
                swipeEventLayout.setRefreshing(false);

            }
        });

        //Set the refresh circle color cycle
        swipeEventLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

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
                Log.i(TAG, "query # events = " + parseObjects.size());
                for (int i = 0; i < parseObjects.size(); i++) {
                    parseObject = parseObjects.get(i);
                    ParseQuery<ParseObject> eventQuery = ParseQuery.getQuery("Event");
                    eventQuery.whereEqualTo("objectId", parseObject.getString("EventId"));
                    eventObject = eventQuery.find().get(0);

                    String imgLocalString = eventObject.getString("ImageType");
                    if (imgLocalString == null) {
                        imgLocalString = "local";
                    }
                    if (imgLocalString.equals("local")) {
                        if (eventObject.getString("ImageResID") != null && !eventObject.getString("ImageResID").equals("")) {
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
            catch (IndexOutOfBoundsException e){
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
        if (imgLocal) {
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


    public void setAlarm() {
        //Log.d(TAG, "setAlarm");
        //Set alarm to run MinuteReceiver every minute
        //Check to see if alarm is already running
        Context context = this;

        boolean alarmUp = (PendingIntent.getBroadcast(context, 0,
                new Intent(context, AlarmReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);
        if (alarmUp) {
            Log.d(TAG, "Alarm already set.");
        } else {
            Intent myIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Calendar calendar = Calendar.getInstance();
            long frequency = 60 * 1000; // in ms
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), frequency, pendingIntent);
        }
    }

    public void requestActivityUpdates() {
        /*
         * Request updates, using the default detection interval.
         * The PendingIntent sends updates to ActivityRecognitionIntentService
         */
//        getActivityRecognitionClient().requestActivityUpdates(
//                60000,
//                createRequestPendingIntent());
        if (apiClient.isConnected()) {
            Log.d(TAG, "Requesting activity updates");
            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(getActivityRecognitionClient(), 60000, createRequestPendingIntent());
            // Disconnect the client
        }
        else {
            Log.d(TAG, "Not connected");
        }
        requestDisconnection();
    }

    /**
     * Get the current activity recognition client, or create a new one if necessary.
     * This method facilitates multiple requests for a client, even if a previous
     * request wasn't finished. Since only one client object exists while a connection
     * is underway, no memory leaks occur.
     *
     * @return An ActivityRecognitionClient object
     */
    private GoogleApiClient getActivityRecognitionClient() {
        if (apiClient == null) {

//            apiClient = new GoogleApiClient.Builder(this)
//                    .addApi(ActivityRecognition.API)
//                    .build();
//            apiClient.connect();
        }
        return apiClient;
    }

    private PendingIntent createRequestPendingIntent() {

        // If the PendingIntent already exists
        if (null != getRequestPendingIntent()) {

            // Return the existing intent
            return mActivityRecognitionPendingIntent;

            // If no PendingIntent exists
        } else {
            // Create an Intent pointing to the IntentService
            Intent intent = new Intent(mContext, ActivityRecognitionIntentService.class);

            /*
             * Return a PendingIntent to start the IntentService.
             * Always create a PendingIntent sent to Location Services
             * with FLAG_UPDATE_CURRENT, so that sending the PendingIntent
             * again updates the original. Otherwise, Location Services
             * can't match the PendingIntent to requests made with it.
             */
            PendingIntent pendingIntent = PendingIntent.getService(mContext, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            setRequestPendingIntent(pendingIntent);
            return pendingIntent;
        }

    }

    /**
     * Returns the current PendingIntent to the caller.
     *
     * @return The PendingIntent used to request activity recognition updates
     */
    public PendingIntent getRequestPendingIntent() {
        return mActivityRecognitionPendingIntent;
    }

    /**
     * Sets the PendingIntent used to make activity recognition update requests
     *
     * @param intent The PendingIntent
     */
    public void setRequestPendingIntent(PendingIntent intent) {
        mActivityRecognitionPendingIntent = intent;
    }

    /**
     * Get the current activity recognition client and disconnect from Location Services
     */
    private void requestDisconnection() {
        getActivityRecognitionClient().disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");
        requestActivityUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_home, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_logout:
                Session session = Session.getActiveSession();
                session.closeAndClearTokenInformation();
                Session.setActiveSession(null);
                mContext.startActivity(new Intent(mContext, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

package edu.villanova.planit;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.austinodell.common.ToolbarActivity;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class PickDateActivity extends ToolbarActivity {

    private static final String TAG = "Debugging";

    ParseObject newParseObject; //Object to hold new parse object

    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog1, timePickerDialog2;

    Button dateButton;
    Button timeButton1;
    Button timeButton2;
    ImageButton nextButton;
    TextView dateTextView, timeTextView1, timeTextView2;

    String eventDate, startTime, endTime, eventTitle, eventDetails, objectId, creatorId, creatorFirstName, creatorName, eventImgUrl = "", eventImgResource = "";
    boolean eventImgLocal;
    GraphUser user;
    ArrayList<String> friendsIds = new ArrayList<>();
    ArrayList<String> friendsNames = new ArrayList<>();

    Intent callingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_date);

        setupToolbar(R.string.title_activity_pick_date);

        callingIntent = getIntent();

        final DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        final DateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
        Date date = new Date();

        eventDate = dateFormat.format(date);
        startTime = timeFormat.format(date);
        endTime = null;
        eventTitle = String.valueOf(callingIntent.getCharSequenceExtra("EventTitle"));
        eventDetails = String.valueOf(callingIntent.getCharSequenceExtra("EventDetails"));
        friendsNames = callingIntent.getStringArrayListExtra("FriendsNames");
        friendsIds = callingIntent.getStringArrayListExtra("FriendsIds");
        eventImgLocal = callingIntent.getBooleanExtra("EventImageLocal", true);
        eventImgResource = callingIntent.getStringExtra("EventImageResource");
        eventImgUrl = callingIntent.getStringExtra("EventImageUrl");

        if (eventImgUrl == null) {
            eventImgUrl = "";
        }

        SharedPreferences sharedPreferences;
        sharedPreferences = this.getSharedPreferences("UserDetails", MainActivity.MODE_PRIVATE);
        creatorFirstName = sharedPreferences.getString("UserFirstName", null);

        Log.i(TAG, "EventTitle (rec): " + eventTitle);
        Log.i(TAG, "EventDetails (rec): " + eventDetails);
        Log.i(TAG, "FriendsNames (rec): " + friendsNames);
        Log.i(TAG, "FriendsIds (rec): " + friendsIds);

        // Get facebook id for user currently logged in
        // Request user data and show the results
        final Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            // If the session is open, make an API call to get user data
            // and define a new callback to handle the response
            Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    // If the response is successful
                    if (session == Session.getActiveSession()) {
                        if (user != null) {
                            creatorId = user.getId();//user id
                            creatorName = user.getName();

                        }
                    }
                }
            });
            Request.executeBatchAsync(request);
        }

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String oldDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                Date date1 = null;
                try {
                    date1 = new SimpleDateFormat("yyyy-MM-dd").parse(oldDate);
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
                eventDate = dateFormat.format(date1);
                dateButton.setText(eventDate);
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        timePickerDialog1 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String oldTime = hourOfDay + ":" + minute;
                Date date1 = null;
                try {
                    date1 = new SimpleDateFormat("HH:mm").parse(oldTime);
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
                startTime = timeFormat.format(date1);
                timeButton1.setText(startTime);
            }
        }, Calendar.getInstance().get(Calendar.HOUR), Calendar.getInstance().get(Calendar.MINUTE), false);

        timePickerDialog2 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String oldTime = hourOfDay + ":" + minute;
                Date date1 = null;
                try {
                    date1 = new SimpleDateFormat("HH:mm").parse(oldTime);
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
                endTime = timeFormat.format(date1);
                timeButton2.setText(endTime);
            }
        }, Calendar.getInstance().get(Calendar.HOUR), Calendar.getInstance().get(Calendar.MINUTE), false);

        dateButton = (Button) findViewById(R.id.dateButton);
        dateButton.setText(eventDate);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        timeButton1 = (Button) findViewById(R.id.timeButton1);
        timeButton1.setText(startTime);
        timeButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog1.show();
            }
        });

        timeButton2 = (Button) findViewById(R.id.timeButton2);
        timeButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog2.show();
            }
        });


        nextButton = (ImageButton) findViewById(R.id.pickDateNextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((eventDate != null) && (startTime != null && (endTime != null))) {
                    objectId = createEvent(eventTitle, eventDetails, eventDate, startTime, endTime, creatorId, eventImgLocal, eventImgUrl, eventImgResource);
                    if (objectId != null) {
                        addUsersToEvent(creatorId, creatorName, friendsIds, objectId, eventTitle, friendsNames);
                        inviteFriends(friendsIds, creatorFirstName, eventTitle);

                        Intent viewEventIntent = new Intent(getApplicationContext(), ViewEvent.class);
                        viewEventIntent.putExtra("EventObjectId", objectId);
                        viewEventIntent.setFlags(viewEventIntent.FLAG_ACTIVITY_NEW_TASK | viewEventIntent.FLAG_ACTIVITY_TASK_ON_HOME);
                        startActivity(viewEventIntent);
                    } else {
                        Log.d(TAG, "objectId is null");
                    }
                }
            }
        });

    }

    //Create event on parse
    private String createEvent(final String eventTitle, final String eventDetails,
                               final String eventDate, final String startTime, final String endTime,
                               final String creatorId, final boolean eventImgLocal,
                               final String eventImgUrl, final String eventImgResource) {

        ParseApplication.init(this);

        newParseObject = new ParseObject("Event");
        newParseObject.put("Title", eventTitle);
        newParseObject.put("Details", eventDetails);
        newParseObject.put("Date", eventDate);
        newParseObject.put("StartTime", startTime);
        newParseObject.put("EndTime", endTime);
        newParseObject.put("CreatorId", creatorId);
        newParseObject.put("ImageType", eventImgLocal ? "local" : "remote");
        newParseObject.put("ImageURL", eventImgUrl);
        newParseObject.put("ImageResID", eventImgResource);
        newParseObject.put("IdeaSelected", false);

        // saves it to parse.com
        try {
            newParseObject.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newParseObject.getObjectId();
    }

    //Add each user to the UserToEvent table on parse
    private void addUsersToEvent(String creatorId, String creatorName, ArrayList<String> friendsIds, String eventId, String eventTitle, ArrayList<String> friendsNames) {
        //Add creator id
        newParseObject = new ParseObject("UserToEvent");
        newParseObject.put("UserFbId", creatorId);
        newParseObject.put("EventId", eventId);
        newParseObject.put("UserName", creatorName);
        newParseObject.put("EventTitle", eventTitle);
        newParseObject.put("StartTime", startTime);
        newParseObject.put("EndTime", endTime);
        newParseObject.saveInBackground();

        //Add all the invitees Ids
        for (int i = 0; i < friendsIds.size(); i++) {
            newParseObject = new ParseObject("UserToEvent");
            newParseObject.put("UserFbId", friendsIds.get(i));
            newParseObject.put("EventId", eventId);
            newParseObject.put("UserName", friendsNames.get(i));
            newParseObject.put("EventTitle", eventTitle);
            newParseObject.saveInBackground();
        }
    }

    //Update friends' parse to notify them
    private void inviteFriends(ArrayList<String> friendsIds, String creatorName, String eventTitle) {
        ParseObject userObject;
        ParseQuery<ParseObject> queryUser;
        ParseQuery<ParseObject> queryAll = ParseQuery.getQuery("User");
        for (String fbId : friendsIds) {
            queryUser = queryAll.whereEqualTo("FacebookID", fbId);
            try {
                userObject = queryUser.getFirst();
                userObject.put("Notification", true);
                userObject.put("InviteFrom", creatorName);
                userObject.put("InviteToTitle", eventTitle);
                userObject.put("InviteToId", objectId);
                userObject.save();
            } catch (ParseException e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            }
        }
    }
}

package edu.villanvoa.together;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Calendar;


public class PickDateActivity extends ActionBarActivity {

    private static final String TAG = "Debugging";

    ParseObject newParseObject; //Object to hold new parse object

    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog1, timePickerDialog2;

    Button dateButton, timeButton1, timeButton2, nextButton;
    TextView dateTextView, timeTextView1, timeTextView2;

    String eventDate, startTime, endTime, eventTitle, eventDetails, objectId, creatorId;
    GraphUser user;
    ArrayList<String> friendsIds = new ArrayList<>();

    Intent callingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_date);

        callingIntent = getIntent();

        eventDate = null;
        startTime = null;
        endTime = null;
        eventTitle = callingIntent.getStringExtra("EventTitle");
        eventDetails = callingIntent.getStringExtra("EventDetails");
        friendsIds = callingIntent.getStringArrayListExtra("FriendsIds");
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
                        }
                    }
                }
            });
            Request.executeBatchAsync(request);
        }

        dateTextView = (TextView) findViewById(R.id.dateTextView);
        timeTextView1 = (TextView) findViewById(R.id.timeTextView1);
        timeTextView2 = (TextView) findViewById(R.id.timeTextView2);

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateButton.setVisibility(View.INVISIBLE);
                eventDate = ((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                dateTextView.setText(eventDate);
                dateTextView.setVisibility(View.VISIBLE);
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        timePickerDialog1 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeButton1.setVisibility(View.INVISIBLE);
                startTime = (hourOfDay + ":" + minute);
                timeTextView1.setText(startTime);
                timeTextView1.setVisibility(View.VISIBLE);
            }
        }, Calendar.getInstance().get(Calendar.HOUR), Calendar.getInstance().get(Calendar.MINUTE), false);

        timePickerDialog2 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeButton2.setVisibility(View.INVISIBLE);
                endTime = (hourOfDay + ":" + minute);
                timeTextView2.setText(endTime);
                timeTextView2.setVisibility(View.VISIBLE);
            }
        }, Calendar.getInstance().get(Calendar.HOUR), Calendar.getInstance().get(Calendar.MINUTE), false);

        dateButton = (Button) findViewById(R.id.dateButton);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        timeButton1 = (Button) findViewById(R.id.timeButton1);
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


        nextButton = (Button) findViewById(R.id.pickDateNextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((eventDate != null) && (startTime != null && (endTime != null))) {
                    objectId = createEvent(eventTitle, eventDetails, eventDate, startTime, endTime, creatorId);
                    if (objectId != null) {
                        addUsersToEvent(creatorId, friendsIds, objectId);
                    } else {
                        Log.d(TAG, "objectId is null");
                    }
                }
            }
        });

    }

    //Create event on parse
    private String createEvent(final String eventTitle, final String eventDetails,
                               final String eventDate, final String startTime, final String endTime, final String creatorId) {

        newParseObject = new ParseObject("Event");
        newParseObject.put("Title", eventTitle);
        newParseObject.put("Details", eventDetails);
        newParseObject.put("Date", eventDate);
        newParseObject.put("StartTime", startTime);
        newParseObject.put("EndTime", endTime);
        newParseObject.put("CreatorId", creatorId);

        // saves it to parse.com
        try {
            newParseObject.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newParseObject.getObjectId();
    }

    //Add each user to the UserToEvent table on parse
    private void addUsersToEvent(String creatorId, ArrayList<String> friendsIds, String eventId) {
        //Add creator id
        newParseObject = new ParseObject("UserToEvent");
        newParseObject.put("UserFbId", creatorId);
        newParseObject.put("EventId", eventId);
        newParseObject.saveInBackground();

        //Add all the invitees Ids
        for (String fbId : friendsIds) {
            newParseObject = new ParseObject("UserToEvent");
            newParseObject.put("UserFbId", fbId);
            newParseObject.put("EventId", eventId);
            newParseObject.saveInBackground();
        }
    }
}

package edu.villanova.planit;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TimePicker;

import com.austinodell.common.ToolbarActivity;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class PickTimeActivity extends ToolbarActivity {
    private static final String TAG = "Debugging";

    TimePickerDialog timePickerDialog1, timePickerDialog2;

    Button timeButton1, timeButton2;
    ImageButton nextButton;
    String startTime, endTime, userFbId, eventId, eventDate;
    Intent callingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_time);

        callingIntent = getIntent();
        userFbId = callingIntent.getStringExtra("UserFbId");
        eventId = callingIntent.getStringExtra("EventId");
        eventDate = callingIntent.getStringExtra("EventDate");
        setupToolbar(eventDate);

        final DateFormat timeFormat = new SimpleDateFormat("hh:mm aa");

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

        nextButton = (ImageButton) findViewById(R.id.pickTimeNextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((startTime != null) && (endTime != null)) {
                    ParseObject parseObject;
                    ParseQuery<ParseObject> queryAll = ParseQuery.getQuery("UserToEvent");
                    queryAll.whereEqualTo("EventId", eventId);
                    queryAll.whereEqualTo("UserFbId", userFbId);
                    try {
                        parseObject = queryAll.getFirst();
                        parseObject.put("StartTime", startTime);
                        parseObject.put("EndTime", endTime);
                        parseObject.save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Log.d(TAG, e.toString());
                    }
                    Intent intent = new Intent();
                    intent.putExtra("startTime", startTime);
                    intent.putExtra("endTime", endTime);
                    intent.putExtra("userFbId", userFbId);
                    setResult(0, intent);
                    finish();
                }
            }
        });
    }
}

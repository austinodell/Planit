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

import java.util.Calendar;


public class PickDateActivity extends ActionBarActivity {

    private static final String TAG = "MainFragment";

    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog1, timePickerDialog2;

    Button dateButton, timeButton1, timeButton2;
    TextView dateTextView, timeTextView1, timeTextView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_date);

        dateTextView = (TextView) findViewById(R.id.dateTextView);
        timeTextView1 = (TextView) findViewById(R.id.timeTextView1);
        timeTextView2 = (TextView) findViewById(R.id.timeTextView2);

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateButton.setVisibility(View.INVISIBLE);
                dateTextView.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                dateTextView.setVisibility(View.VISIBLE);
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        timePickerDialog1 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeTextView1.setText(hourOfDay + ":" + minute);
                timeButton1.setVisibility(View.INVISIBLE);
                timeTextView1.setVisibility(View.VISIBLE);
            }
        }, Calendar.getInstance().get(Calendar.HOUR), Calendar.getInstance().get(Calendar.MINUTE), false);

        timePickerDialog2 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeTextView2.setText(hourOfDay + ":" + minute);
                timeButton2.setVisibility(View.INVISIBLE);
                timeTextView2.setVisibility(View.VISIBLE);
            }
        }, Calendar.getInstance().get(Calendar.HOUR), Calendar.getInstance().get(Calendar.MINUTE), false);

        dateButton = (Button) findViewById(R.id.dateButton);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivityForResult(datePickerIntent, 1);
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


    }
}

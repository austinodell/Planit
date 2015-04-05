package edu.villanvoa.together;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.parse.Parse;

import java.util.Calendar;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "Debugging";

    private LoginFragment loginFragment;

    private Button login_btn;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Parse.initialize(this, "YMPhMAAd5vjkITGtdjD2pNsLmfAIhYZ5u3gXFteJ", "5w3m3Zex78Knrz69foyli8FKAv96PEzNlhBNJL3l");

        mContext = this;

        if (savedInstanceState == null) {
            // Add the fragment button on initial activity setup
            loginFragment = new LoginFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, loginFragment)
                    .commit();
        } else {
            // Or set the fragment button from restored state info
            loginFragment = (LoginFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.container);
        }

        login_btn = (Button) findViewById(R.id.login_btn);

        /* If user clicks image, open up recipe details activity */
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, Home.class));
            }
        });

       // setAlarm();
    }
//
//    public void setAlarm() {
//        //Log.d(TAG, "setAlarm");
//        //Set alarm to run MinuteReceiver every minute
//        //Check to see if alarm is already running
//        Context context = this;
//
//        boolean alarmUp = (PendingIntent.getBroadcast(context, 0,
//                new Intent(context, AlarmReceiver.class),
//                PendingIntent.FLAG_NO_CREATE) != null);
//        if (alarmUp) {
//            Log.d(TAG, "Alarm already set.");
//        } else {
//            Intent myIntent = new Intent(context, AlarmReceiver.class);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);
//
//            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//            Calendar calendar = Calendar.getInstance();
//            long frequency = 60 * 1000; // in ms
//            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), frequency, pendingIntent);
//        }
//
//
//    }
}

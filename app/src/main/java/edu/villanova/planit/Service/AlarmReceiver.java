package edu.villanova.planit.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import edu.villanova.planit.CheckNotificationService;

/**
 * Created by wildcat on 3/11/2015.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "Debugging";

    private CheckNotificationService svc;
    boolean isBound = false;

    //Alarm Broadcast Receiver to go off every 1 minute and call the CheckNotificationService
    @Override
    public void onReceive(Context context, Intent intent) {
       //Log.d(TAG, "Alarm onReceive");
        final Intent checkNotificationIntent = new Intent(context, CheckNotificationService.class);
        context.startService(checkNotificationIntent);
    }
}

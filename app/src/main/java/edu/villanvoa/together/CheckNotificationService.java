package edu.villanvoa.together;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by wildcat on 3/11/2015.
 */
public class CheckNotificationService extends IntentService {

    private static final String TAG = "Debugging";
    private final static String svcName = "CheckNotificationService";

    ParseObject userObject;
    String userId, creatorName, eventTitle;
    boolean notification;

    public CheckNotificationService() {
        super(svcName);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "YMPhMAAd5vjkITGtdjD2pNsLmfAIhYZ5u3gXFteJ", "5w3m3Zex78Knrz69foyli8FKAv96PEzNlhBNJL3l");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Log.d(TAG, "onHandleIntent");

        userObject = getUserObject();
        creatorName = userObject.getString("InviteFrom");
        eventTitle = userObject.getString("InviteToTitle");
        if (userObject != null) {
            notification = checkNotification(userObject);
            if (notification) {
                //Log.d(TAG, "Notification available");
                notifyUser();
                //Change notification boolean to false
                userObject.put("Notification", false);

                try {
                    userObject.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                //Log.d(TAG, "Notification not available");
            }
        }
        else {
            Log.d(TAG, "userObject null");
        }
    }

    private ParseObject getUserObject() {
        //Log.d(TAG, "getUserObject()");

        //Get userId from shared preferences
        SharedPreferences sharedPreferences;
        sharedPreferences = this.getSharedPreferences("UserDetails", MainActivity.MODE_PRIVATE);
        userId = sharedPreferences.getString("UserFbId", null);
        if (userId != null) {
            //Log.d(TAG, "userId: " + userId);
            ParseQuery<ParseObject> queryAll = ParseQuery.getQuery("User");
            queryAll.whereEqualTo("FacebookID", userId);
            try {
                userObject = queryAll.getFirst();
            } catch (ParseException e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
            }
            return userObject;
        } else {
            Log.d(TAG, "Userid null");
            return null;
        }
    }

    private boolean checkNotification(ParseObject userObject) {
        //Log.d(TAG, "checkNotification()");
        return userObject.getBoolean("Notification");
    }

    private void notifyUser(){
        //Log.d(TAG, "Notifying User");
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(this)
                .setContentTitle("New Planit from " + creatorName)
                .setContentText(eventTitle)
                .setSmallIcon(R.drawable.club)
                .setSound(uri)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .build();
        notificationManager.notify(0, notification);
    }
}

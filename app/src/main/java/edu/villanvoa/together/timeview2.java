package edu.villanvoa.together;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class timeview2 extends ActionBarActivity {
    private String TAG = "edu.villanvoa.together.ViewEvent";

    private String eventObjectId, eventTitle, eventDetails, eventImgUrl, userFbId, eventDate, creatorId;
    private ArrayList<String> friendsNames, friendsIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeview2);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeview2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private List<TimeAvailable> getUserTimes() {
        List<TimeAvailable> userTimesList = new ArrayList<TimeAvailable>();

        List<ParseObject> parseObjects;
        ParseObject parseObject;
        ParseQuery<ParseObject> userToEventQuery = ParseQuery.getQuery("UserToEvent");
        userToEventQuery.whereEqualTo("EventId", eventObjectId);
        try {
            parseObjects = userToEventQuery.find();
            for (int i = 0; i < parseObjects.size(); i++) {
                parseObject = parseObjects.get(i);
                if (parseObject.getString("StartTime") == null) {
                    if (parseObject.getString("UserFbId").equals(userFbId)) {
                        Log.d(TAG, "Start time null");
                        Intent pickTimeIntent = new Intent(this, PickTimeActivity.class);
                        pickTimeIntent.putExtra("UserFbId", parseObject.getString("UserFbId"));
                        pickTimeIntent.putExtra("EventId", parseObject.getString("EventId"));
                        pickTimeIntent.putExtra("EventDate", eventDate);
                        startActivityForResult(pickTimeIntent, 0);
                    }
                } else {
                    userTimesList.add(new TimeAvailable(parseObject.getString("UserFbId"), parseObject.getString("StartTime"), parseObject.getString("EndTime")));
                }
            }
        } catch (
                ParseException e
                ) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }

        return userTimesList;
    }


    public TimeView extends PickTimeActivity

    {
        final List getUserTimes = (List) findViewById(R.id.listview);
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < getUserTimes.length; ++i) {
            list.add(getUserTimes[i]);
        }
        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        getUserTimes().setAdapter(adapter);
    }



}

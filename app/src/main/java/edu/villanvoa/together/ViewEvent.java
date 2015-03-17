package edu.villanvoa.together;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class ViewEvent extends ToolbarActivity {

    private String TAG = "edu.villanvoa.together.ViewEvent";

    private String eventObjectId, eventTitle, eventDetails, eventImgUrl;
    private boolean eventImgLocal;
    private int eventImgResource;

    private ImageView eventImg;
    private TextView eventDesc;
    private TextView eventTime;
    private FullGridView eventFriendsGV;
    private FullGridView eventIdeasTable;
    private Button addIdeaButton;

    private ArrayList<Friend> friendsList;
    private FriendsGridAdapter friendsGridAdapter;

    private ArrayList<Idea> ideasList;
    private IdeaListAdapter ideaListAdapter;

    /* UniversalImageLoader library variables */
    static public ImageLoader imageLoader;
    static public DisplayImageOptions imageOptions;
    static public ImageLoaderConfiguration imageConfig;

    private Intent addIdeaIntent;
    private Intent homeIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        Log.i(TAG, "Started ViewEvent");
        Parse.initialize(this, "YMPhMAAd5vjkITGtdjD2pNsLmfAIhYZ5u3gXFteJ", "5w3m3Zex78Knrz69foyli8FKAv96PEzNlhBNJL3l");

        //Get eventObjectId from calling activity
        eventObjectId = getIntent().getStringExtra("EventObjectId");

        ParseQuery eventQuery = ParseQuery.getQuery("Event");
        eventQuery.whereEqualTo("objectId", eventObjectId);
        try {
            ParseObject eventObject = eventQuery.getFirst();
            eventDetails = eventObject.getString("Details");
            eventTitle = eventObject.getString("Title");
            setupToolbar(eventTitle);
            eventImgLocal = eventObject.getBoolean("ImageType");
            if(eventImgLocal) {
                eventImgResource = eventObject.getInt("ImageResID");
            } else {
                eventImgUrl = eventObject.getString("ImageURL");
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }

        ImageLib imgLib = new ImageLib(this);

        Log.i(TAG,"ViewEvent ImageLoader initialized");

        friendsList = new ArrayList<Friend>();
        if (eventObjectId != null) {
            addFriendsFromParse(eventObjectId);
        }
        else {
            Log.d(TAG, "ObjectId is null");
        }

        Log.i(TAG,"ViewEvent Friends Added");

        eventImg = (ImageView) findViewById(R.id.view_event_img);
        eventDesc = (TextView) findViewById(R.id.view_event_description);
        eventTime = (TextView) findViewById(R.id.view_event_time);
        eventFriendsGV = (FullGridView) findViewById(R.id.view_event_friends_container);
        eventIdeasTable = (FullGridView) findViewById(R.id.view_event_ideas_container);

        //EventPictureSelector pictureSelector = new EventPictureSelector();
        //eventImg.setImageResource(pictureSelector.getImgResource());

        if(eventImgLocal) {
            eventImg.setImageResource(eventImgResource);
        } else {
            if(eventImgUrl == null) {
                eventImg.setImageResource(R.drawable.bar);
            } else {
                String img_url = "http://planit.austinodell.com/img/" + eventImgUrl;
                imgLib.imageLoader.displayImage(img_url, eventImg, imgLib.imageOptions); // Display Image
            }
        }

        eventDesc.setText(eventDetails);
        eventTime.setText("Time: 9:00pm - 1:00am");

        friendsGridAdapter = new FriendsGridAdapter(this,friendsList,eventFriendsGV,imgLib);
        eventFriendsGV.setAdapter(friendsGridAdapter);

        ideasList = new ArrayList<Idea>();
        addIdeasFromParse(eventObjectId);

        ideaListAdapter = new IdeaListAdapter(this,ideasList);
        eventIdeasTable.setAdapter(ideaListAdapter);

        addIdeaButton = (Button) findViewById(R.id.addIdeaButton);
        addIdeaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIdeaIntent = new Intent(getApplicationContext(),NewIdea.class);
                addIdeaIntent.putExtra("EventObjectId", eventObjectId);
                startActivity(addIdeaIntent);
            }
        });
    }

    private void addFriendsFromParse(String eventObjectId){
        List<ParseObject> parseObjects;
        ParseObject parseObject;
        ParseQuery<ParseObject> userToEventQuery = ParseQuery.getQuery("UserToEvent");
        userToEventQuery.whereEqualTo("EventId", eventObjectId);
        try {
            parseObjects = userToEventQuery.find();
            for (int i=0; i<parseObjects.size(); i++) {
                parseObject = parseObjects.get(i);
                addFriendToGrid(parseObject.getString("UserFbId"), parseObject.getString("UserName"), true);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }
    }

    private void addFriendToGrid(String id, String name, boolean isReal) {
        Friend friend = new Friend(id, name);
        friendsList.add(friend);

        Log.i(TAG,"ViewEvent Friend ("+friend.id+") Added");
    }

    private void addIdeasFromParse(String eventObjectId){
        List<ParseObject> parseObjects;
        ParseObject parseObject;
        ParseQuery<ParseObject> userToEventQuery = ParseQuery.getQuery("Idea");
        userToEventQuery.whereEqualTo("EventId", eventObjectId);
        try {
            parseObjects = userToEventQuery.find();
            for (int i=0; i<parseObjects.size(); i++) {
                parseObject = parseObjects.get(i);
                addIdeaToList(parseObject.getString("Title"), parseObject.getString("Details"));
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }
    }

    private void addIdeaToList(String name, String description) {
        Idea idea = new Idea(name);
        idea.setDesc(description);
        ideasList.add(idea);

        Log.i(TAG, "ViewEvent Idea (" + idea.getId() + ") Added");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        homeIntent = new Intent(this, Home.class);
        homeIntent.setFlags(homeIntent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}

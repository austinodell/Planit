package edu.villanvoa.together;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class
        ViewEvent extends ActionBarActivity {

    private String TAG = "edu.villanvoa.together.ViewEvent";

    private String eventObjectId, eventTitle, eventDetails;

    private ImageView eventImg;
    private TextView eventDesc;
    private TextView eventTime;
    private FullGridView eventFriendsGV;
    private FullGridView eventIdeasTable;

    private ArrayList<Friend> friendsList;
    private FriendsGridAdapter friendsGridAdapter;

    private ArrayList<Idea> ideasList;
    private IdeaListAdapter ideaListAdapter;

    /* UniversalImageLoader library variables */
    static public ImageLoader imageLoader;
    static public DisplayImageOptions imageOptions;
    static public ImageLoaderConfiguration imageConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        Log.i(TAG,"Started ViewEvent");

        //Get eventObjectId from calling activity
        eventObjectId = getIntent().getStringExtra("EventObjectId");

        /* Set up toolbar to replace Actionbar */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ParseQuery eventQuery = ParseQuery.getQuery("Event");
        eventQuery.whereEqualTo("objectId", eventObjectId);
        try {
            ParseObject eventObject = eventQuery.getFirst();
            eventDetails = eventObject.getString("Details");
            eventTitle = eventObject.getString("Title");
            toolbar.setTitle(eventTitle);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }

        setSupportActionBar(toolbar);
        ActionBar mSupportActionBar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);



        /* Set up UniversalImageLoader library variables */
        imageConfig = new ImageLoaderConfiguration.Builder(this)
                .build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(imageConfig);

        imageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_launcher)
                .showImageForEmptyUri(R.drawable.ic_launcher)
                .showImageOnFail(R.drawable.ic_launcher)
                .cacheOnDisk(true)
                .build();

        Log.i(TAG,"ViewEvent ImageLoader initialized");

        friendsList = new ArrayList<Friend>();
        if (eventObjectId != null) {
            addFriendsFromParse(eventObjectId);
        }
        else {
            Log.d(TAG, "ObjectId is null");
        }
//        addFriendToGrid("10206106201596623","Andrew Walters",true);
//        addFriendToGrid("10153062089109882","Andy Rinaldi",true);
//        addFriendToGrid("10204703776492383","Carlos Alejandro Gallardo",true);
//        addFriendToGrid("1020156028013792","Matt Wiedmeier",true);
//        addFriendToGrid("10206307009142454","Melissa Sustaita",true);
//        addFriendToGrid("10202752169485847","Olivia Greene",true);
//        addFriendToGrid("10206202607453094","Ricky Baum",true);
//        addFriendToGrid("10153204634714903","Stephanie Molina",true);
//        addFriendToGrid("10206106201596623","Andrew Walters",true);
//        addFriendToGrid("10153062089109882","Andy Rinaldi",true);
//        addFriendToGrid("10204703776492383","Carlos Alejandro Gallardo",true);
//        addFriendToGrid("1020156028013792","Matt Wiedmeier",true);
//        addFriendToGrid("10206307009142454","Melissa Sustaita",true);
//        addFriendToGrid("10202752169485846","Olivia Greene",true);
//        addFriendToGrid("10206202607453094","Ricky Baum",true);
//        addFriendToGrid("10153204634714903","Stephanie Molina",true);
//        addFriendToGrid("10206106201596623","Andrew Walters",true);
//        addFriendToGrid("10153062089109882","Andy Rinaldi",true);
//        addFriendToGrid("10204703776492383","Carlos Alejandro Gallardo",true);
//        addFriendToGrid("1020156028013792","Matt Wiedmeier",true);
//        addFriendToGrid("10206307009142454","Melissa Sustaita",true);

        Log.i(TAG,"ViewEvent Friends Added");

        eventImg = (ImageView) findViewById(R.id.view_event_img);
        eventDesc = (TextView) findViewById(R.id.view_event_description);
        eventTime = (TextView) findViewById(R.id.view_event_time);
        eventFriendsGV = (FullGridView) findViewById(R.id.view_event_friends_container);
        eventIdeasTable = (FullGridView) findViewById(R.id.view_event_ideas_container);

        eventDesc.setText(eventDetails);
        eventTime.setText("Time: 9:00pm - 1:00am");

        friendsGridAdapter = new FriendsGridAdapter(this,friendsList,eventFriendsGV);
        eventFriendsGV.setAdapter(friendsGridAdapter);

        ideasList = new ArrayList<Idea>();
        addIdeasFromParse(eventObjectId);
//        addIdeaToList("Idea Name","Idea Description");
//        addIdeaToList("Idea Name","Idea Description");
//        addIdeaToList("Idea Name","Idea Description");
//        addIdeaToList("Idea Name","Idea Description");
//        addIdeaToList("Idea Name","Idea Description");
//        addIdeaToList("Idea Name","Idea Description");
//        addIdeaToList("Idea Name","Idea Description");
//        addIdeaToList("Idea Name","Idea Description");
//        addIdeaToList("Idea Name","Idea Description");
//        addIdeaToList("Idea Name","Idea Description");
//        addIdeaToList("Idea Name","Idea Description");
//        addIdeaToList("Idea Name","Idea Description");

        ideaListAdapter = new IdeaListAdapter(this,ideasList);
        eventIdeasTable.setAdapter(ideaListAdapter);
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

        Log.i(TAG,"ViewEvent Idea ("+idea.getId()+") Added");
    }
}

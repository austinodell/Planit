package edu.villanvoa.together;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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

    private String eventObjectId, eventTitle, eventDetails, eventImgUrl, userFbId, eventDate, creatorId;
    private ArrayList<String> friendsNames, friendsIds;
    private boolean eventImgLocal, ideaSelected;
    private int eventImgResource;

    private ImageLib imgLib;

    private ImageView eventImg;
    private TextView eventDesc;
    private TextView eventTime;
    private FullGridView eventFriendsGV;
    private FullGridView eventIdeasTable;
    private Button addIdeaButton;
    private SwipeRefreshLayout ideaRefreshLayout;

    private ArrayList<Friend> friendsList;
    private FriendsGridAdapter friendsGridAdapter;

    private ArrayList<Idea> ideasList;
    private IdeaListAdapter ideaListAdapter;

    /* UniversalImageLoader library variables */
    static public ImageLoader imageLoader;
    static public DisplayImageOptions imageOptions;
    static public ImageLoaderConfiguration imageConfig;

    private Intent homeIntent;
    private Intent addIdeaIntent, ideaDiscussionIntent;

    List<TimeAvailable> userTimesList = new ArrayList<TimeAvailable>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        Log.i(TAG, "Started ViewEvent");
        Parse.initialize(this, "YMPhMAAd5vjkITGtdjD2pNsLmfAIhYZ5u3gXFteJ", "5w3m3Zex78Knrz69foyli8FKAv96PEzNlhBNJL3l");

        friendsIds = new ArrayList<String>();
        friendsNames = new ArrayList<String>();

        imgLib = new ImageLib(this);

        //Get eventObjectId from calling activity
        eventObjectId = getIntent().getStringExtra("EventObjectId");
        SharedPreferences sharedPreferences;
        sharedPreferences = this.getSharedPreferences("UserDetails", MainActivity.MODE_PRIVATE);
        userFbId = sharedPreferences.getString("UserFbId", null);
        //userTimesList = getUserTimes();

        ParseObject eventObject;
        ParseQuery<ParseObject> eventQuery = ParseQuery.getQuery("Event");
        eventQuery.whereEqualTo("objectId", eventObjectId);

        Log.i(TAG, "EventObjectId = " + eventObjectId);
        try {
            if (eventQuery.find().size() > 0) {
                try {
                    eventObject = eventQuery.find().get(0);
                    eventDetails = eventObject.getString("Details");
                    eventTitle = eventObject.getString("Title");
                    eventDate = eventObject.getString("Date");
                    creatorId = eventObject.getString("CreatorId");
                    ideaSelected = eventObject.getBoolean("IdeaSelected");
                    setupToolbar(eventTitle);
                    eventImgLocal = eventObject.getString("ImageType").equals("local") ? true : false;
                    if (eventImgLocal) {
                        String temp_resid = eventObject.getString("ImageResID");
                        if (temp_resid != null && !temp_resid.equals("")) {
                            eventImgResource = imgLib.getResId(temp_resid);
                        } else {
                            eventImgResource = R.drawable.bowl;
                        }
                    } else {
                        eventImgUrl = eventObject.getString("ImageURL");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.d(TAG, e.toString());
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        userTimesList = getUserTimes();
        SuggestedTime suggestedTime = new SuggestedTime(userTimesList);
        Log.d(TAG, "Suggested startTime: " + suggestedTime.getStartTime());
        Log.d(TAG, "Suggested endTime: " + suggestedTime.getEndTime());

        ImageLib imgLib = new ImageLib(this);

        Log.i(TAG, "ViewEvent ImageLoader initialized");

        friendsList = new ArrayList<Friend>();
        if (eventObjectId != null) {
            addFriendsFromParse(eventObjectId);
        } else {
            Log.d(TAG, "ObjectId is null");
        }

        Log.i(TAG, "ViewEvent Friends Added");

        eventImg = (ImageView) findViewById(R.id.view_event_img);
        eventDesc = (TextView) findViewById(R.id.view_event_description);
        eventTime = (TextView) findViewById(R.id.view_event_time);
        eventFriendsGV = (FullGridView) findViewById(R.id.view_event_friends_container);
        eventIdeasTable = (FullGridView) findViewById(R.id.view_event_ideas_container);

        ideaRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeIdeaContainer);
        ideaRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("Scroll Container", "Refreshing");

                //Populate ideasList with all the Event's ideas from parse
                ideasList = new ArrayList<Idea>();
                addIdeasFromParse(eventObjectId);

                //Replace the adapaters current list with the new updated list
                ideaListAdapter.addItems(ideasList);

                //Request the adapter update its view
                ideaListAdapter.notifyDataSetChanged();

                // Stop the refresh once you have the data
               ideaRefreshLayout.setRefreshing(false);

            }
        });

        //Set the refresh circle color cycle
        ideaRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //EventPictureSelector pictureSelector = new EventPictureSelector();
        //eventImg.setImageResource(pictureSelector.getImgResource());

        if (eventImgLocal) {
            eventImg.setImageResource(eventImgResource);
        } else {
            if (eventImgUrl == null) {
                eventImg.setImageResource(R.drawable.bar);
            } else {
                String img_url = "http://planit.austinodell.com/img/" + eventImgUrl;
                imgLib.imageLoader.displayImage(img_url, eventImg, imgLib.imageOptions); // Display Image
            }
        }

        eventDesc.setText(eventDetails);
        eventTime.setText("Time: " + suggestedTime.toString());

        friendsGridAdapter = new FriendsGridAdapter(this, friendsList, eventFriendsGV, imgLib);
        eventFriendsGV.setAdapter(friendsGridAdapter);
        eventFriendsGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent friendStatusIntent = new Intent(getApplicationContext(), FriendStatusActivity.class);
                friendStatusIntent.putExtra("friendsIds", friendsIds);
                friendStatusIntent.putExtra("friendsNames", friendsNames);
                startActivity(friendStatusIntent);
            }
        });

        ideasList = new ArrayList<Idea>();
        addIdeasFromParse(eventObjectId);

        ideaListAdapter = new IdeaListAdapter(this, ideasList);
        eventIdeasTable.setAdapter(ideaListAdapter);

        eventIdeasTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Idea selectedIdea = ideasList.get(position);
                ideaDiscussionIntent = new Intent(getApplicationContext(), IdeaDiscussion.class);
                ideaDiscussionIntent.putExtra("ideaID", selectedIdea.getParseID());
                ideaDiscussionIntent.putExtra("eventId", eventObjectId);
                if (creatorId.equals(userFbId)) {
                    ideaDiscussionIntent.putExtra("isCreator", true);
                } else {
                    ideaDiscussionIntent.putExtra("isCreator", false);
                }

                startActivity(ideaDiscussionIntent);
            }
        });


        addIdeaButton = (Button) findViewById(R.id.addIdeaButton);
        addIdeaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIdeaIntent = new Intent(getApplicationContext(), NewIdea.class);
                addIdeaIntent.putExtra("EventObjectId", eventObjectId);
                startActivity(addIdeaIntent);
            }
        });

        Log.d(TAG, userTimesList.toString());

        //If idea has been selected, remove the add button and the edit text
        if (ideaSelected) {
            addIdeaButton.setVisibility(View.INVISIBLE);

        }
    }

    private void addFriendsFromParse(String eventObjectId) {
        List<ParseObject> parseObjects;
        ParseObject parseObject;
        ParseQuery<ParseObject> userToEventQuery = ParseQuery.getQuery("UserToEvent");
        userToEventQuery.whereEqualTo("EventId", eventObjectId);
        try {
            parseObjects = userToEventQuery.find();
            for (int i = 0; i < parseObjects.size(); i++) {
                parseObject = parseObjects.get(i);
                friendsIds.add(parseObject.getString("UserFbId"));
                friendsNames.add(parseObject.getString("UserName"));
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

        Log.i(TAG, "ViewEvent Friend (" + friend.id + ") Added");
    }

    private void addIdeasFromParse(String eventObjectId) {
        List<ParseObject> parseObjects;
        ParseObject parseObject;
        ParseQuery<ParseObject> userToEventQuery = ParseQuery.getQuery("Idea");
        userToEventQuery.whereEqualTo("EventId", eventObjectId);
        try {
            parseObjects = userToEventQuery.find();
            for (int i = 0; i < parseObjects.size(); i++) {
                parseObject = parseObjects.get(i);
                addIdeaToList(parseObject.getString("Title"), parseObject.getString("Details"), parseObject.getString("Location"), parseObject.getObjectId());
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }
    }

    private void addIdeaToList(String name, String description, String location, String objectID) {
        Idea idea = new Idea(name, objectID);
        idea.setDesc(description);
        idea.setLoc(location);
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
                )

        {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }

        return userTimesList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            String endTime = data.getStringExtra("endTime");
            String startTime = data.getStringExtra("startTime");
            String userFbId = data.getStringExtra("userFbId");
            userTimesList.add(new TimeAvailable(userFbId, startTime, endTime));
            Log.d(TAG, userTimesList.toString());
        }
    }

}

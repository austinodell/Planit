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

import java.util.ArrayList;


public class ViewEvent extends ActionBarActivity {

    private String TAG = "edu.villanvoa.together.ViewEvent";

    private ImageView eventImg;
    private TextView eventDesc;
    private TextView eventTime;
    private GridView eventFriendsGV;
    private GridView eventIdeasTable;

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

        /* Set up toolbar to replace Actionbar */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar mSupportActionBar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setTitle(R.string.title_activity_view_event);

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
        addFriendToGrid("1020156028013792","Matt Wiedmeier",true);
        addFriendToGrid("1020156028013792","Matt Wiedmeier",true);
        addFriendToGrid("1020156028013792", "Matt Wiedmeier", true);

        Log.i(TAG,"ViewEvent Friends Added");

        eventImg = (ImageView) findViewById(R.id.view_event_img);
        eventDesc = (TextView) findViewById(R.id.view_event_description);
        eventTime = (TextView) findViewById(R.id.view_event_time);
        eventFriendsGV = (GridView) findViewById(R.id.view_event_friends_container);
        eventIdeasTable = (GridView) findViewById(R.id.view_event_ideas_container);

        friendsGridAdapter = new FriendsGridAdapter(this,friendsList,eventFriendsGV);
        eventFriendsGV.setAdapter(friendsGridAdapter);

        ideasList = new ArrayList<Idea>();
        addIdeaToList("Idea Name","Idea Description");
        addIdeaToList("Idea Name","Idea Description");
        addIdeaToList("Idea Name","Idea Description");

        ideaListAdapter = new IdeaListAdapter(this,ideasList);
        eventIdeasTable.setAdapter(ideaListAdapter);
    }

    private void addFriendToGrid(String id, String name, boolean isReal) {
        Friend friend = new Friend(id, name);
        friendsList.add(friend);

        Log.i(TAG,"ViewEvent Friend ("+friend.id+") Added");
    }

    private void addIdeaToList(String name, String description) {
        Idea idea = new Idea(name);
        idea.setDesc(description);
        ideasList.add(idea);

        Log.i(TAG,"ViewEvent Idea ("+idea.getId()+") Added");
    }
}

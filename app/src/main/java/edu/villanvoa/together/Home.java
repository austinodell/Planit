package edu.villanvoa.together;

import android.content.Context;
import android.content.Intent;
import android.graphics.Outline;
import android.graphics.Point;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;

import java.util.ArrayList;


public class Home extends ActionBarActivity {

    private Display display;
    private Point size;
    public static int screenWidth;
    public int screenHeight;
    private ArrayList<Event> eventsList;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mContext = this;

        /* Set up toolbar to replace Actionbar */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar mSupportActionBar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setTitle(R.string.title_activity_home);

        eventsList = new ArrayList<Event>();

        addEvent("Movie Night",R.drawable.movie);
        addEvent("Bowling",R.drawable.bowl);
        addEvent("Passion Pit Show",R.drawable.contert_big);
        addEvent("Restaurant Week",R.drawable.dinner);
        addEvent("Bar Hop",R.drawable.bar);
        addEvent("Club Shampoo",R.drawable.club);
        addEvent("KOP",R.drawable.shop);
        addEvent("S'mores",R.drawable.smores);
        addEvent("Picnic on Sheehan Beach",R.drawable.picnic);
        addEvent("Game at Wells Fargo",R.drawable.basketball);
        addEvent("Blue Mountain",R.drawable.ski);
        addEvent("Road Trip to Boston",R.drawable.roadtrip);

        GridView gridView = (GridView) findViewById(R.id.container);
        gridView.setAdapter(new HomeGridAdapter(this,eventsList));

        Button new_event_btn = (Button) findViewById(R.id.new_event_btn);
        new_event_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext,AddFriends.class));
            }
        });
    }

    private void addEvent(String name, int img_resid) {
        Event event = new Event(name,img_resid);
        eventsList.add(event);
    }

    public static class Event {
        public final String name;
        public final int drawableId;

        Event(String name, int drawableId) {
            this.name = name;
            this.drawableId = drawableId;
        }
    }
}

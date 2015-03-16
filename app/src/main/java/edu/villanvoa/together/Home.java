package edu.villanvoa.together;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import java.util.ArrayList;


public class Home extends ToolbarActivity {

    private ArrayList<Event> eventsList;
    private Context mContext;

    private int event_id = 0; // temporary - to be populated by Parse

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mContext = this;

        setupToolbar(R.string.title_activity_home);

        // Initializes Universal Image Loader Library to load images given URLs
        ImageLib imgLib = new ImageLib(mContext);

        // Used to store all events to pass to HomeGridAdapter
        eventsList = new ArrayList<Event>();

        addEvent(this.event_id++,"Movie Night","http://images.clipartpanda.com/movie-night-clipart-9cp4q9xcE.jpeg"); // Example with image to fetch
        addEvent(this.event_id++,"Bowling",R.drawable.bowl); // Examples with image to use drawable ids
        addEvent(this.event_id++,"Passion Pit Show",R.drawable.contert_big);
        addEvent(this.event_id++,"Restaurant Week",R.drawable.dinner);
        addEvent(this.event_id++,"Bar Hop",R.drawable.bar);
        addEvent(this.event_id++,"Club Shampoo",R.drawable.club);
        addEvent(this.event_id++,"KOP",R.drawable.shop);
        addEvent(this.event_id++,"S'mores",R.drawable.smores);
        addEvent(this.event_id++,"Picnic on Sheehan Beach",R.drawable.picnic);
        addEvent(this.event_id++,"Game at Wells Fargo",R.drawable.basketball);
        addEvent(this.event_id++,"Blue Mountain",R.drawable.ski);
        addEvent(this.event_id++,"Road Trip to Boston",R.drawable.roadtrip);

        // Setup Adapter
        GridView gridView = (GridView) findViewById(R.id.container);
        gridView.setAdapter(new HomeGridAdapter(this,eventsList,imgLib));

        // Add New Event Button Setup
        Button new_event_btn = (Button) findViewById(R.id.new_event_btn);
        new_event_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext,AddFriends.class));
            }
        });
    }

    // Add event with Image URL
    private void addEvent(int id, String name, String img_url) {
        Event event = new Event(id,name,img_url);
        eventsList.add(event);
    }

    // Add event with Image Drawable Resource (preloaded)
    private void addEvent(int id, String name, int img_resid) {
        Event event = new Event(id,name,img_resid);
        eventsList.add(event);
    }
}

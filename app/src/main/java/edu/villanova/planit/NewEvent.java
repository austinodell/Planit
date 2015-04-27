package edu.villanova.planit;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class NewEvent extends ActionBarActivity {

    private static final String TAG = "MainFragment";

    Button nextButton;
    EditText titleText, detailsText;
    String title, details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        /* Set up toolbar to replace Actionbar */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar mSupportActionBar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setTitle(R.string.title_activity_new_event);

        final Intent addFriendsIntent = new Intent(this, AddFriends.class);

        titleText = (EditText) findViewById(R.id.new_event_name_et);
        detailsText = (EditText) findViewById(R.id.new_event_details_et);

        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = titleText.getText().toString();
                details = detailsText.getText().toString();
                //Check if user entered text into event title field
                if (title.matches("")) {
                    Toast.makeText(getBaseContext(), "Must enter event title", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d(TAG, title);
                    addFriendsIntent.putExtra("EventTitle", title);
                    addFriendsIntent.putExtra("EventDetails", details);
                    startActivity(addFriendsIntent);
                }
            }
        });
    }
}

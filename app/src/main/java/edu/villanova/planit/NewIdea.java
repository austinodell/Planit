package edu.villanova.planit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class NewIdea extends ActionBarActivity {

    //View Variables
    EditText titleET, descriptionET;
    AutoCompleteTextView locACV;
    Button submitButton;

    //Intent Variables
    Intent callingIntent, returnIdea;

    ParseObject ideaObject;
    String eventObjectId;

    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_idea);

        geocoder = new Geocoder(this, Locale.getDefault());
        //Receive the intent that started this activity
        callingIntent = getIntent();
        eventObjectId = callingIntent.getStringExtra("EventObjectId");

        //Instantiate the EditText Views
        titleET = (EditText)findViewById(R.id.new_idea_title_et);
        descriptionET = (EditText)findViewById(R.id.new_idea_description_et);

        //Instantiate the submit button
        submitButton = (Button)findViewById(R.id.submitButton);

        //Instantiate the auto complete textview
        locACV = (AutoCompleteTextView) findViewById(R.id.new_idea_loc_et);

        //Set the view adapater the the Google autocomplete adapter
        locACV.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_idea, menu);
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

    public void submitClicked(View view) {

        SharedPreferences sharedPreferences;
        sharedPreferences = this.getSharedPreferences("UserDetails", MainActivity.MODE_PRIVATE);
        String userId = sharedPreferences.getString("UserFbId", null);

        String ideaTitle, ideaLoc, ideaDesc;
        ideaTitle = titleET.getText().toString();
        ideaLoc = locACV.getText().toString();
        ideaDesc = descriptionET.getText().toString();


        try {
            List<Address> locResults = new ArrayList<Address>();
            String temp = ideaLoc.replaceAll("&","%26");
            locResults = geocoder.getFromLocationName(temp, 5);

            for(int  i = 0; i < locResults.size(); i++){

                Log.d("Addresses: " ,locResults.get(i).toString());


            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(ideaTitle.contentEquals("")){
            Toast.makeText(getApplicationContext(), "Empty Idea", Toast.LENGTH_SHORT).show();
        }
        else if((ideaLoc).contentEquals("")){
            Toast.makeText(getApplicationContext(), "Enter Location", Toast.LENGTH_SHORT).show();

        }
        else {

            //Create new intent to bring idea data back to event screen
            returnIdea = new Intent(this, ViewEvent.class);

            //Store Idea data inside the intent
            //        returnIdea.putExtra("ideaTitle", titleET.getText().toString());
            //        returnIdea.putExtra("ideaLoc", locACV.getText().toString());
            //        returnIdea.putExtra("ideaDescription", descriptionET.getText().toString());
            returnIdea.putExtra("EventObjectId", eventObjectId);

            //Save the idea to parse
            ideaObject = new ParseObject("Idea");
            ideaObject.put("Title", ideaTitle);
            ideaObject.put("Location", ideaLoc);
            ideaObject.put("Details", ideaDesc);
            ideaObject.put("EventId", eventObjectId);
            ideaObject.put("Upvotes", 0);
            ideaObject.put("Downvotes", 0);
            if (userId != null) {
                ideaObject.put("CreatorId", userId);
            }

            // saves it to parse.com
            try {
                ideaObject.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }


            //Launch the intent
            startActivity(returnIdea);
        }
    }
}

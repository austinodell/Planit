package edu.villanvoa.together;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;


public class NewIdea extends ActionBarActivity {

    //View Variables
    EditText titleET, descriptionET;
    AutoCompleteTextView locACV;
    Button submitButton;

    //Intent Variables
    Intent callingIntent, returnIdea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_idea);

        //Receive the intent that started this activity
        callingIntent = getIntent();

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

        //Create new intent to bring idea data back to event screen
        returnIdea = new Intent(this, Home.class);

        //Store Idea data inside the intent
        returnIdea.putExtra("ideaTitle", titleET.getText());
        returnIdea.putExtra("ideaLoc", locACV.getText());
        returnIdea.putExtra("ideaDescription", descriptionET.getText());


        //Launch the intent
        //startActivity(returnIdea);

    }
}

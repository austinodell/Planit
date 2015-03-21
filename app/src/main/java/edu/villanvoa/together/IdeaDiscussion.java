package edu.villanvoa.together;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class IdeaDiscussion extends ToolbarActivity {


    private TextView locationTV,descriptionTV,votesTV;
    private Button plusButton;
    private ToggleButton upvoteButton,downvoteButton;
    private EditText commentET;
    private ListView commentsLV;
    private SwipeRefreshLayout commentLVContainer;

    private Intent callingIntent;
    private ArrayList<Comment> commentsList;
    private CommentListAdapter adapter;
    private ParseObject eventObject;
    private SimpleDateFormat dateTimeStamp;

    private String creatorId,creatorName, commentTimeStamp;
    private String ideaTitle,ideaLoc,ideaDesc, ideaObjectID;
    private int ideaVotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea_discussion);

        Parse.initialize(this, "YMPhMAAd5vjkITGtdjD2pNsLmfAIhYZ5u3gXFteJ", "5w3m3Zex78Knrz69foyli8FKAv96PEzNlhBNJL3l");

        //Get calling intent information
        callingIntent = getIntent();

        //Get the selected idea's information
        ideaObjectID = callingIntent.getStringExtra("ideaID");
        ParseQuery eventQuery = ParseQuery.getQuery("Idea");
        eventQuery.whereEqualTo("objectId", ideaObjectID);
        try {
            eventObject = eventQuery.getFirst();
            ideaTitle = eventObject.getString("Title");
            ideaLoc = eventObject.getString("Location");
            ideaDesc = eventObject.getString("Details");
            ideaVotes = eventObject.getInt("Upvotes")- eventObject.getInt("Downvotes");
            setupToolbar(ideaTitle);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Map the Text Views for the idea information
        locationTV = (TextView)findViewById(R.id.idea_location_tv);
        descriptionTV = (TextView)findViewById(R.id.idea_description_tv);
        votesTV = (TextView) findViewById(R.id.idea_votes_tv);

        //Set the idea information text views
        locationTV.setText(ideaLoc);
        descriptionTV.setText(ideaDesc);
        votesTV.setText(Integer.toString(ideaVotes));

        //Set up the buttons
        plusButton = (Button)findViewById(R.id.addCommentButton);
        upvoteButton = (ToggleButton)findViewById(R.id.upvote_button);
        downvoteButton = (ToggleButton)findViewById(R.id.downvote_button);

        //Set the state of the upvote/downvote buttons <--Need Parse Data


        commentET = (EditText)findViewById(R.id.new_comment_et);
        commentsLV = (ListView)findViewById(R.id.comment_lv);
        commentsList = new ArrayList<Comment>();
        //Populate comments List with data from parse!!!

        adapter = new CommentListAdapter(this,commentsList);
        commentsLV.setAdapter(adapter);


        commentLVContainer = (SwipeRefreshLayout)findViewById(R.id.swipeCommentContainer);
        // Setup refresh listener which triggers new data loading
        commentLVContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("Scroll Container","Refreshing");

                //Refresh the commentsList with the data from parse

                //Notify the adapter that the data set has changed
                adapter.notifyDataSetChanged();

                // Stop the refresh once you have the data
                commentLVContainer.setRefreshing(false);

            }
        });

        //Set the refresh circle color cycle
       commentLVContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Get facebook id for user currently logged in
        // Request user data and show the results
        final Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            // If the session is open, make an API call to get user data
            // and define a new callback to handle the response
            Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    // If the response is successful
                    if (session == Session.getActiveSession()) {
                        if (user != null) {
                            creatorId = user.getId();//user id
                            creatorName = user.getName();
                        }
                    }
                }
            });
            Request.executeBatchAsync(request);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_idea_discussion, menu);
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

    public void addNewCommentClicked(View view) {

        String comment;

        //Get the contents of the edit text field
        comment = commentET.getText().toString();

        //Notify users if the typed nothing in
        if(comment.contentEquals("")){
            Toast.makeText(getApplicationContext(),"Empty Comment",Toast.LENGTH_SHORT).show();
        }else{

            //Add comment to Parse database
            dateTimeStamp = new SimpleDateFormat("MM-dd-yy     hh:mm a");
            commentTimeStamp = dateTimeStamp.format(new java.util.Date());

            Comment comm = new Comment(creatorName,comment,creatorId,commentTimeStamp);
            commentsList.add(comm);
            adapter.notifyDataSetChanged();
            commentET.setText("");
        }

    }

    public void upvoteClicked(View view){

        //Check the toggle state
        boolean on = ((ToggleButton) view).isChecked();

        if(on){
            //Enabled
            if(downvoteButton.isChecked()){
                downvoteButton.setChecked(false);
                //Take away downvote and add an upvote here
                updateVoteView();

            }else{

                //Add an upvote here
                updateVoteView();
            }

        }else{
           //Disabled

            //Take away an upvote here
            updateVoteView();
        }
    }

    public void downvoteClicked(View view){
        //Check the toggle state
        boolean on = ((ToggleButton) view).isChecked();

        if(on){
            //Enabled
            if(upvoteButton.isChecked()){
                upvoteButton.setChecked(false);

                //Take away upvote and add a downvote
                updateVoteView();
            }else{

                //Add a downvote here
                updateVoteView();
            }


        }else{
            //Disabled

            //Take away a downvote here
            updateVoteView();
        }
    }

    public void updateVoteView(){

        if(eventObject!=null){
            ideaVotes = eventObject.getInt("Upvotes")- eventObject.getInt("Downvotes");
            votesTV.setText(Integer.toString(ideaVotes));
        }
    }

}

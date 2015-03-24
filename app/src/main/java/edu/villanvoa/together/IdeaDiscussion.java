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
import com.google.android.gms.internal.ge;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class IdeaDiscussion extends ToolbarActivity {

    private static final String TAG = "Debugging";

    private TextView locationTV, descriptionTV, votesTV;
    private Button plusButton;
    private ToggleButton upvoteButton, downvoteButton;
    private EditText commentET;
    private ListView commentsLV;
    private SwipeRefreshLayout commentLVContainer;

    private Intent callingIntent;
    private ArrayList<Comment> commentsList;
    private CommentListAdapter adapter;
    private ParseObject ideaObject;
    private ParseObject userVoteObject;
    private ParseQuery<ParseObject> ideaQuery;
    private ParseQuery<ParseObject> userVoteQuery;
    private SimpleDateFormat dateTimeStamp;

    private String userFbId, userName, commentTimeStamp;
    private String ideaTitle, ideaLoc, ideaDesc, ideaObjectID;
    private int ideaVotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea_discussion);

        //Set up the buttons
        plusButton = (Button) findViewById(R.id.addCommentButton);
        upvoteButton = (ToggleButton) findViewById(R.id.upvote_button);
        downvoteButton = (ToggleButton) findViewById(R.id.downvote_button);

        Parse.initialize(this, "YMPhMAAd5vjkITGtdjD2pNsLmfAIhYZ5u3gXFteJ", "5w3m3Zex78Knrz69foyli8FKAv96PEzNlhBNJL3l");

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
                            userFbId = user.getId();//user id
                            userName = user.getName();
                            //Get user vote object from parse
                            if (userFbId != null) {
                                userVoteObject = new ParseObject("");
                                userVoteQuery = ParseQuery.getQuery("UserVotes");
                                userVoteQuery.whereEqualTo("UserFbId", userFbId);
                                userVoteQuery.whereEqualTo("IdeaId", ideaObjectID);
                                try {
                                    userVoteObject = userVoteQuery.getFirst();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    Log.d(TAG, e.toString());
                                }
                                //Check to see if this user has a vote tuple of parse for this idea
                                if ((userVoteObject.getString("IdeaId") == null)) {
                                    userVoteObject = new ParseObject("UserVotes");
                                    userVoteObject.put("UserFbId", userFbId);
                                    userVoteObject.put("IdeaId", ideaObjectID);
                                }
                                else {
                                    if (userVoteObject.getBoolean("UpVoted")) {
                                        upvoteButton.setChecked(true);
                                    }
                                    else if(userVoteObject.getBoolean("DownVoted")) {
                                        downvoteButton.setChecked(true);
                                    }
                                }
                            }
                            else {
                                Log.d(TAG, "UserFbIs is null");
                            }
                        }
                    }
                }
            });
            Request.executeBatchAsync(request);
        }

        //Get calling intent information
        callingIntent = getIntent();

        //Get the selected idea's information
        ideaObjectID = callingIntent.getStringExtra("ideaID");
        ParseQuery eventQuery = ParseQuery.getQuery("Idea");
        eventQuery.whereEqualTo("objectId", ideaObjectID);
        try {
            ideaObject = eventQuery.getFirst();
            ideaTitle = ideaObject.getString("Title");
            ideaLoc = ideaObject.getString("Location");
            ideaDesc = ideaObject.getString("Details");
            ideaVotes = ideaObject.getInt("Upvotes") - ideaObject.getInt("Downvotes");
            setupToolbar(ideaTitle);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Map the Text Views for the idea information
        locationTV = (TextView) findViewById(R.id.idea_location_tv);
        descriptionTV = (TextView) findViewById(R.id.idea_description_tv);
        votesTV = (TextView) findViewById(R.id.idea_votes_tv);

        //Set the idea information text views
        locationTV.setText(ideaLoc);
        descriptionTV.setText(ideaDesc);
        votesTV.setText(Integer.toString(ideaVotes));



        //Set the state of the upvote/downvote buttons <--Need Parse Data


        commentET = (EditText) findViewById(R.id.new_comment_et);
        commentsLV = (ListView) findViewById(R.id.comment_lv);
        commentsList = new ArrayList<Comment>();
        //Populate comments List with data from parse!!!
        commentsList = getParseComments();
        adapter = new CommentListAdapter(this, commentsList);
        commentsLV.setAdapter(adapter);


        commentLVContainer = (SwipeRefreshLayout) findViewById(R.id.swipeCommentContainer);
        // Setup refresh listener which triggers new data loading
        commentLVContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("Scroll Container", "Refreshing");

                //Refresh the commentsList with the data from parse
                commentsList = getParseComments();

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
    }

    @Override
    protected void onStop() {
        super.onStop();
        // saves it to parse.com
        try {
            userVoteObject.save();
            if (ideaObject != null) {
                ideaObject.save();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public void addNewCommentClicked(View view) {

        String comment;

        //Get the contents of the edit text field
        comment = commentET.getText().toString();

        //Notify users if the typed nothing in
        if (comment.contentEquals("")) {
            Toast.makeText(getApplicationContext(), "Empty Comment", Toast.LENGTH_SHORT).show();
        } else {

            //Add comment to Parse database
            dateTimeStamp = new SimpleDateFormat("MM-dd-yy     hh:mm a");
            commentTimeStamp = dateTimeStamp.format(new java.util.Date());

            ParseObject newParseObject;
            newParseObject = new ParseObject("Comment");
            newParseObject.put("CommentText", comment);
            newParseObject.put("IdeaId", ideaObjectID);
            newParseObject.put("UserFbId", userFbId);
            newParseObject.put("UserName", userName);
            newParseObject.put("TimeStamp", commentTimeStamp);

            // saves it to parse.com
            try {
                newParseObject.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Comment comm = new Comment(userName, comment, userFbId, commentTimeStamp);
            commentsList.add(comm);
            adapter.notifyDataSetChanged();
            commentET.setText("");
        }
    }


    //Function to retrieve comments from parse
    private ArrayList<Comment> getParseComments() {
        ArrayList<Comment> commentsList = new ArrayList<Comment>();
        List<ParseObject> parseObjects;
        ParseObject parseObject;
        String comment, creatorName, creatorId, commentTimeStamp;
        ParseQuery<ParseObject> queryAll = ParseQuery.getQuery("Comment");
        queryAll.whereEqualTo("IdeaId", ideaObjectID);
        try {
            parseObjects = queryAll.find();
            for (int i = 0; i < parseObjects.size(); i++) {
                parseObject = parseObjects.get(i);
                creatorName = parseObject.getString("UserName");
                creatorId = parseObject.getString("UserFbId");
                comment = parseObject.getString(("CommentText"));
                Log.d(TAG, comment);
                commentTimeStamp = parseObject.getString("TimeStamp");
                Comment comm = new Comment(creatorName, comment, creatorId, commentTimeStamp);
                commentsList.add(comm);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }
        return commentsList;
    }

    public void upvoteClicked(View view) {
        //Check the toggle state
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            //Enabled
            if (downvoteButton.isChecked()) {
                downvoteButton.setChecked(false);
                //Take away downvote and add an upvote here
                userVoteObject.put("UpVoted", true);
                userVoteObject.put("DownVoted", false);
                if (ideaObject != null) {
                    ideaObject.put("Upvotes", ideaObject.getInt("Upvotes") + 1);
                    ideaObject.put("Downvotes", ideaObject.getInt("Downvotes") - 1);
                }
                updateVoteView();

            } else {

                //Add an upvote here
                if (userVoteObject != null) {
                    userVoteObject.put("UpVoted", true);
                    userVoteObject.put("DownVoted", false);
                }
                if (ideaObject != null) {
                    ideaObject.put("Upvotes", ideaObject.getInt("Upvotes") + 1);
                }
                else {
                    Log.d(TAG, "idea object null");
                }
                updateVoteView();

            }

        } else {
            //Disabled

            //Take away an upvote here
            userVoteObject.put("UpVoted", false);
            userVoteObject.put("DownVoted", false);
            if (ideaObject != null) {
                ideaObject.put("Upvotes", ideaObject.getInt("Upvotes") - 1);
            }
            updateVoteView();
        }
    }

    public void downvoteClicked(View view) {
        //Check the toggle state
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            //Enabled
            if (upvoteButton.isChecked()) {
                upvoteButton.setChecked(false);

                //Take away upvote and add a downvote
                userVoteObject.put("UpVoted", false);
                userVoteObject.put("DownVoted", true);
                if (ideaObject != null) {
                    ideaObject.put("Upvotes", ideaObject.getInt("Upvotes") - 1);
                    ideaObject.put("Downvotes", ideaObject.getInt("Downvotes") + 1);
                }
                updateVoteView();
            } else {

                //Add a downvote here
                userVoteObject.put("UpVoted", false);
                userVoteObject.put("DownVoted", true);
                if (ideaObject != null) {
                    ideaObject.put("Downvotes", ideaObject.getInt("Downvotes") + 1);
                }
                updateVoteView();
            }


        } else {
            //Disabled

            //Take away a downvote here
            userVoteObject.put("UpVoted", false);
            userVoteObject.put("DownVoted", false);
            if (ideaObject != null) {
                ideaObject.put("Downvotes", ideaObject.getInt("Downvotes") - 1);
            }
            updateVoteView();
        }
    }

    public void updateVoteView() {

        if (ideaObject != null) {
            ideaVotes = ideaObject.getInt("Upvotes") - ideaObject.getInt("Downvotes");
            votesTV.setText(Integer.toString(ideaVotes));
        }
    }

}

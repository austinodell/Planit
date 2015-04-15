package edu.villanvoa.together;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class IdeaDiscussion extends ToolbarActivity {

    private static final String TAG = "Debugging";

    private TextView locationTV, descriptionTV, votesTV;
    private Button plusButton, pickIdeaButton;
    private ToggleButton upvoteButton, downvoteButton;
    private EditText commentET;
    private ListView commentsLV;
    private SwipeRefreshLayout commentLVContainer;

    private ToolbarActivity toolbar;

    private Intent callingIntent;
    private ArrayList<Comment> commentsList;
    private CommentListAdapter adapter;
    private ParseObject ideaObject;
    private ParseObject userVoteObject;
    private ParseObject userObject;
    private ParseQuery<ParseObject> ideaQuery;
    private ParseQuery<ParseObject> userVoteQuery;
    private ParseQuery<ParseObject> userQuery;
    private SimpleDateFormat dateTimeStamp;

    private String userFbId, userName, commentTimeStamp, eventId, mapsURI;
    private String ideaUserId, ideaTitle, ideaLoc, ideaDesc, ideaObjectID;
    private int ideaVotes;
    private boolean isCreator;

    private ImageLib imgLib = null;
    private String response_val = null;
    private URL url;
    private String host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea_discussion);

        //Set up the buttons
        plusButton = (Button) findViewById(R.id.addCommentButton);
        upvoteButton = (ToggleButton) findViewById(R.id.upvote_button);
        downvoteButton = (ToggleButton) findViewById(R.id.downvote_button);

        Parse.initialize(this, "YMPhMAAd5vjkITGtdjD2pNsLmfAIhYZ5u3gXFteJ", "5w3m3Zex78Knrz69foyli8FKAv96PEzNlhBNJL3l");
        imgLib = new ImageLib(this);

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
                                } else {
                                    if (userVoteObject.getBoolean("UpVoted")) {
                                        upvoteButton.setChecked(true);
                                    } else if (userVoteObject.getBoolean("DownVoted")) {
                                        downvoteButton.setChecked(true);
                                    }
                                }
                            } else {
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
        isCreator = callingIntent.getBooleanExtra("isCreator", false);

        eventId = callingIntent.getStringExtra("eventId");
        //Get the selected idea's information
        ideaObjectID = callingIntent.getStringExtra("ideaID");

        ParseQuery eventQuery = ParseQuery.getQuery("Idea");
        eventQuery.whereEqualTo("objectId", ideaObjectID);
        try {
            ideaObject = eventQuery.getFirst();
            ideaUserId = ideaObject.getString("CreatorId");
            ideaTitle = ideaObject.getString("Title");
            ideaLoc = ideaObject.getString("Location");
            ideaDesc = ideaObject.getString("Details");
            ideaVotes = ideaObject.getInt("Upvotes") - ideaObject.getInt("Downvotes");
            setupToolbar(ideaTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SquareImage userImg = (SquareImage) findViewById(R.id.user_img);
        final TextView userName = (TextView) findViewById(R.id.user_name);

        String img_url = "http://graph.facebook.com/" + ideaUserId + "/picture?type=large";
        imgLib.imageLoader.displayImage(img_url, userImg, imgLib.imageOptions); // Display Image


        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link");

        new Request(
                session,
                "/" + ideaUserId,
                parameters,
                HttpMethod.GET,
                new Request.Callback() {
                    public void onCompleted(Response response) {
                        try {
                            Log.i(TAG,"Name: " + response.getGraphObject().getInnerJSONObject().getString("name"));
                            userName.setText(response.getGraphObject().getInnerJSONObject().getString("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();

        //Map the Text Views for the idea information
        locationTV = (TextView) findViewById(R.id.idea_location_tv);
        descriptionTV = (TextView) findViewById(R.id.idea_description_tv);
        votesTV = (TextView) findViewById(R.id.idea_votes_tv);

        //Set the idea information text views
        if(ideaLoc.equals("")) {
            locationTV.setVisibility(View.INVISIBLE);
            locationTV.setHeight(0);
        } else {
            locationTV.setText(ideaLoc);
        }

        if(ideaDesc.equals("")) {
            descriptionTV.setVisibility(View.INVISIBLE);
            descriptionTV.setHeight(0);
        } else {
            descriptionTV.setText(ideaDesc);
        }
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
                //commentsList = getParseComments();
                adapter.addItems(getParseComments());

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isCreator) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.menu_idea_discussion, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_pick_idea:
                onPickIdeaClicked();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
            //adapter.addItems(commentsList);
            adapter.notifyDataSetChanged();
            commentET.setText("");
        }
    }

    public void locationClicked(View view){

        ideaLoc = ideaLoc.replaceAll("&","%26");
        mapsURI = "http://maps.google.co.in/maps?q=" + ideaLoc;
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(mapsURI));
        startActivity(i);

    }

    //Function to retrieve comments from parse
    private ArrayList<Comment> getParseComments() {
        ArrayList<Comment> commentsList = new ArrayList<Comment>();
        List<ParseObject> parseObjects;
        ParseObject parseObject;
        String comment, creatorName, creatorId, commentTimeStamp;
        ParseQuery<ParseObject> queryAll = ParseQuery.getQuery("Comment");
        queryAll.whereEqualTo("IdeaId", ideaObjectID);
        queryAll.orderByAscending("createdAt");
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
                } else {
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

    public void onPickIdeaClicked() {

        //Delete all other ideas from Parse
        List<ParseObject> parseObjects;
        ParseObject parseObject;
        ParseQuery<ParseObject> queryAll = ParseQuery.getQuery("Idea");
        queryAll.whereEqualTo("EventId", eventId);
        try {
            parseObjects = queryAll.find();
            for (int i = 0; i < parseObjects.size(); i++) {
                parseObject = parseObjects.get(i);
                Log.d(TAG, parseObject.getString("Title"));
                if (parseObject.getObjectId() != null) {
                    Log.d(TAG, parseObject.getObjectId());
                    if (!parseObject.getObjectId().equals(ideaObjectID)) {
                        Log.d(TAG, parseObject.get("Title") + " deleted");
                        parseObject.delete();
                    }
                } else {
                    Log.d(TAG, "objectId is null");
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }

        //Set IdeaSelected to true for the event
        queryAll = ParseQuery.getQuery("Event");
        queryAll.whereEqualTo("objectId", eventId);
        try {
            parseObject = queryAll.getFirst();
            parseObject.put("IdeaSelected", true);
            parseObject.save();
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }

        //Return to event screen
        Intent eventIntent;
        eventIntent = new Intent(this, ViewEvent.class);
        eventIntent.putExtra("EventObjectId", eventId);
        eventIntent.setFlags(eventIntent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(eventIntent);
    }

    @Override
    public void onBackPressed() {
        // set idea object vote count here (to do)
        super.onBackPressed();
        Intent eventIntent;
        eventIntent = new Intent(this, ViewEvent.class);
        eventIntent.putExtra("EventObjectId", eventId);
        eventIntent.setFlags(eventIntent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(eventIntent);
    }

    class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();
                } else{
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Do anything with response..
        }
    }

}

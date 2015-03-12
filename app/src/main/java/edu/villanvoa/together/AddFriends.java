package edu.villanvoa.together;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class AddFriends extends ActionBarActivity {

    /* UniversalImageLoader library variables */
    static public ImageLoader imageLoader;
    static public DisplayImageOptions imageOptions;
    static public ImageLoaderConfiguration imageConfig;

    private static final List<String> PERMISSIONS = new ArrayList<String>() {
        {
            add("user_friends");
            add("public_profile");
        }
    };

    private static final String TAG = "Debugging";

    private EditText event_name;
    private EditText event_details;
    private TextView addUserHint;

    private String hint = "Click button to add friends.";

    private Friend hintFriend;

    private GridView gridView;
    private View btnView;
    private FriendsGridAdapter friendsGridAdapter;

    private SquareImageButton addUserBtn;

    private ArrayList<Friend> friendsList;

    private static final int PICK_FRIENDS_ACTIVITY = 1;
    private UiLifecycleHelper lifecycleHelper;
    boolean pickFriendsWhenSessionOpened;
    ArrayList<String> friendsNames = new ArrayList<String>();
    ArrayList<String> friendsIds = new ArrayList<>();
    Intent callingIntent, pickDateIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        /* Set up toolbar to replace Actionbar */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar mSupportActionBar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setTitle(R.string.title_activity_add_friends);

        callingIntent = getIntent();
        pickDateIntent = new Intent(this, PickDateActivity.class);

        lifecycleHelper = new UiLifecycleHelper(this, new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                onSessionStateChanged(session, state, exception);
            }
        });
        lifecycleHelper.onCreate(savedInstanceState);

        ensureOpenSession();

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

        event_name = (EditText) findViewById(R.id.event_name_et);
        event_details = (EditText) findViewById(R.id.event_details_et);
        addUserHint = (TextView) findViewById(R.id.add_user_hint_tv);

        addUserHint.setText(hint);

        friendsList = new ArrayList<Friend>();
        friendsList.add(hintFriend);

        gridView = (GridView) findViewById(R.id.container);

        ViewGroup vg = (ViewGroup) findViewById(R.id.container);

        LayoutInflater mInflater = LayoutInflater.from(this);
        btnView = mInflater.inflate(R.layout.fragment_grid_user_first,vg,false);
        addUserBtn = (SquareImageButton) btnView.findViewById(R.id.add_user_btn);

        addUserBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onClickPickFriends();
            }
        });

        friendsGridAdapter = new FriendsGridAdapter(this,friendsList,btnView,gridView);
        gridView.setAdapter(friendsGridAdapter);

        final ImageButton nextBtn = (ImageButton) findViewById(R.id.next_page_btn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextStep();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.menu_add_friends, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_add_friends_next:
                //pickDateIntent.putExtra("EventTitle", callingIntent.getStringExtra("EventTitle"));
                //pickDateIntent.putExtra("EventDetails", callingIntent.getStringExtra("EventDetails"));
                pickDateIntent.putExtra("EventTitle",event_name.getText());
                pickDateIntent.putExtra("EventDetails",event_details.getText());
                pickDateIntent.putExtra("FriendsNames", friendsNames);
                pickDateIntent.putExtra("FriendsIds", friendsIds);

                Log.i(TAG,"EventTitle: " + event_name.getText());
                Log.i(TAG,"EventDetails: " + event_details.getText());
                Log.i(TAG,"FriendsNames: " + friendsNames);
                Log.i(TAG,"FriendsIds: " + friendsIds);

                startActivity(pickDateIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void nextStep() {
        pickDateIntent.putExtra("EventTitle",event_name.getText());
        pickDateIntent.putExtra("EventDetails",event_details.getText());
        pickDateIntent.putExtra("FriendsNames", friendsNames);
        pickDateIntent.putExtra("FriendsIds", friendsIds);

        Log.i(TAG,"EventTitle: " + event_name.getText());
        Log.i(TAG,"EventDetails: " + event_details.getText());
        Log.i(TAG,"FriendsNames: " + friendsNames);
        Log.i(TAG,"FriendsIds: " + friendsIds);

        startActivity(pickDateIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Update the display every time we are started.
        displaySelectedFriends(RESULT_OK);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Call the 'activateApp' method to log an app event for use in analytics and advertising reporting.  Do so in
        // the onResume methods of the primary Activities that an app may be launched into.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Call the 'deactivateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onPause methods of the primary Activities that an app may be launched into.
        AppEventsLogger.deactivateApp(this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_FRIENDS_ACTIVITY:
                displaySelectedFriends(resultCode);
                break;
            default:
                Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
                break;
        }
    }

    private boolean ensureOpenSession() {
        if (Session.getActiveSession() == null ||
                !Session.getActiveSession().isOpened()) {
            Session.openActiveSession(
                    this,
                    true,
                    PERMISSIONS,
                    new Session.StatusCallback() {
                        @Override
                        public void call(Session session, SessionState state, Exception exception) {
                            onSessionStateChanged(session, state, exception);
                        }
                    });
            return false;
        }
        return true;
    }

    private boolean sessionHasNecessaryPerms(Session session) {
        if (session != null && session.getPermissions() != null) {
            for (String requestedPerm : PERMISSIONS) {
                if (!session.getPermissions().contains(requestedPerm)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private List<String> getMissingPermissions(Session session) {
        List<String> missingPerms = new ArrayList<String>(PERMISSIONS);
        if (session != null && session.getPermissions() != null) {
            for (String requestedPerm : PERMISSIONS) {
                if (session.getPermissions().contains(requestedPerm)) {
                    missingPerms.remove(requestedPerm);
                }
            }
        }
        return missingPerms;
    }

    private void onSessionStateChanged(final Session session, SessionState state, Exception exception) {
        if (state.isOpened() && !sessionHasNecessaryPerms(session)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.need_perms_alert_text);
            builder.setPositiveButton(
                    R.string.need_perms_alert_button_ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            session.requestNewReadPermissions(
                                    new Session.NewPermissionsRequest(
                                            AddFriends.this,
                                            getMissingPermissions(session)));
                        }
                    }).setNegativeButton(
                    R.string.need_perms_alert_button_quit,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            builder.show();
        } else if (pickFriendsWhenSessionOpened && state.isOpened()) {
            pickFriendsWhenSessionOpened = false;

            startPickFriendsActivity();
        }
    }

    private void displaySelectedFriends(int resultCode) {
        //String results = "";
        friendsList.clear();
        FriendPickerApplication application = (FriendPickerApplication) getApplication();

        Collection<GraphUser> selection = application.getSelectedUsers();
        if (selection != null && selection.size() > 0) {
            for (GraphUser user : selection) {
                addFriendToGrid(user.getId(),user.getName(),true);
                friendsNames.add(user.getName());
                if (!friendsIds.contains(user.getId())) {
                    friendsIds.add(user.getId());
                }
            }

            addUserHint.setText("Friends");
            //results = TextUtils.join(", ", friendsNames);
        } else {
            addUserHint.setText(hint);
            //friendsList.add(hintFriend);
            //results = "<No friends selected>";
        }

        gridView.setAdapter(friendsGridAdapter);
        //resultsTextView.setText(results);
    }

    public void onClickPickFriends() {

        startPickFriendsActivity();
    }

    private void startPickFriendsActivity() {
        if (ensureOpenSession()) {
            Intent intent = new Intent(this, PickFriendsActivity.class);
            // Note: The following line is optional, as multi-select behavior is the default for
            // FriendPickerFragment. It is here to demonstrate how parameters could be passed to the
            // friend picker if single-select functionality was desired, or if a different user ID was
            // desired (for instance, to see friends of a friend).
            PickFriendsActivity.populateParameters(intent, null, true, true);
            startActivityForResult(intent, PICK_FRIENDS_ACTIVITY);
        } else {
            pickFriendsWhenSessionOpened = true;
        }
    }

    private void addFriendToGrid(String id, String name, boolean isReal) {
        Friend friend = new Friend(id, name);
        friendsList.add(friend);
    }

    public static class Friend {
        public final String id;
        public final String name;
        public final boolean isReal;

        Friend(String id, String name) {
            this.id = id;
            this.name = name;
            isReal = true;
        }

        Friend(String text) {
            id = "0";
            name = text;
            isReal = false;
        }
    }

}

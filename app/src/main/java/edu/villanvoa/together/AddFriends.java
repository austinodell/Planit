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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class AddFriends extends ActionBarActivity {

    /* UniversalImageLoader library variables */
    protected ImageLoader imageLoader;
    protected DisplayImageOptions imageOptions;
    protected ImageLoaderConfiguration imageConfig;

    private static final List<String> PERMISSIONS = new ArrayList<String>() {
        {
            add("user_friends");
            add("public_profile");
        }
    };

    private static final String TAG = "Debugging";

    private static final int PICK_FRIENDS_ACTIVITY = 1;
    private Button pickFriendsButton;
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

        pickFriendsButton = (Button) findViewById(R.id.pickFriendsButton);
        pickFriendsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onClickPickFriends();
            }
        });

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

        /* test */
        onClickPickFriends();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_friends, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_add_friends_next:
                pickDateIntent.putExtra("EventTitle", callingIntent.getStringExtra("EventTitle"));
                pickDateIntent.putExtra("EventDetails", callingIntent.getStringExtra("EventDetails"));
                pickDateIntent.putExtra("FriendsNames", friendsNames);
                pickDateIntent.putExtra("FriendsIds", friendsIds);
                startActivity(pickDateIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Update the display every time we are started.
        try {
            displaySelectedFriends(RESULT_OK);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                try {
                    displaySelectedFriends(resultCode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    private void displaySelectedFriends(int resultCode) throws IOException {
        String results = "";
        FriendPickerApplication application = (FriendPickerApplication) getApplication();

        Collection<GraphUser> selection = application.getSelectedUsers();
        if (selection != null && selection.size() > 0) {
            resetFriendsList();

            for (GraphUser user : selection) {
                addFriendToList(user.getId(),user.getName());
                /*friendsNames.add(user.getName());
                if (!friendsIds.contains(user.getId())) {
                    friendsIds.add(user.getId());
                }*/
            }

            //results = TextUtils.join(", ", friendsNames);
        } else {
            resetFriendsList();
            //results = "<No friends selected>";
        }

        //resultsTextView.setText(results);
    }

    private void onClickPickFriends() {

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

    /* Remove all friends from ViewGroup */
    protected void resetFriendsList() {
        ViewGroup vg = (ViewGroup) findViewById(R.id.friends_placeholder);
        vg.removeAllViews();
    }

    /* Add card for given recipe name, details, starred status, and custom status */
    protected void addFriendToList(String id, String name) throws IOException {
        Log.i(TAG,"Displaying user ("+id+") "+name);
        final LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.fragment_user,null);

        SquareImage user_picture = (SquareImage) layout.findViewById(R.id.user_iv);
        String img_url = "http://graph.facebook.com/"+id+"/picture?type=large";
        imageLoader.displayImage(img_url,user_picture,imageOptions); // Display Image
        //Bitmap mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
        //user_picture.setImageBitmap(mIcon1);

        TextView user_name = (TextView) layout.findViewById(R.id.user_tv);
        user_name.setText(name);

        /* Add Layout */
        ViewGroup vg = (ViewGroup) findViewById(R.id.friends_placeholder);
        vg.addView(layout);
    }

}

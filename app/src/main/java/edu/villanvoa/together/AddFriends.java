package edu.villanvoa.together;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class AddFriends extends FragmentActivity {

    private static final List<String> PERMISSIONS = new ArrayList<String>() {
        {
            add("user_friends");
            add("public_profile");
        }
    };

    private static final String TAG = "Debugging";

    private static final int PICK_FRIENDS_ACTIVITY = 1;
    private Button pickFriendsButton, addFriendsNextButton;
    private TextView resultsTextView;
    private UiLifecycleHelper lifecycleHelper;
    boolean pickFriendsWhenSessionOpened;
    ArrayList<String> friendsNames = new ArrayList<String>();
    ArrayList<String> friendsIds = new ArrayList<>();
    Intent callingIntent, pickDateIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        callingIntent = getIntent();
        pickDateIntent = new Intent(this, PickDateActivity.class);

        resultsTextView = (TextView) findViewById(R.id.resultsTextView);
        pickFriendsButton = (Button) findViewById(R.id.pickFriendsButton);
        pickFriendsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onClickPickFriends();
            }
        });

        addFriendsNextButton = (Button) findViewById(R.id.addFriendsNextButton);
        addFriendsNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDateIntent.putExtra("EventTitle", callingIntent.getStringExtra("EventTitle"));
                pickDateIntent.putExtra("EventDetails", callingIntent.getStringExtra("EventDetails"));
                pickDateIntent.putExtra("FriendsNames", friendsNames);
                pickDateIntent.putExtra("FriendsIds", friendsIds);
                startActivity(pickDateIntent);
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
        String results = "";
        FriendPickerApplication application = (FriendPickerApplication) getApplication();

        Collection<GraphUser> selection = application.getSelectedUsers();
        if (selection != null && selection.size() > 0) {

            for (GraphUser user : selection) {
                friendsNames.add(user.getName());
                if (!friendsIds.contains(user.getId())) {
                    friendsIds.add(user.getId());
                }
            }
            results = TextUtils.join(", ", friendsNames);
        } else {
            results = "<No friends selected>";
        }

        resultsTextView.setText(results);
    }

    private void onClickPickFriends() {

        startPickFriendsActivity();
        addFriendsNextButton.setVisibility(View.VISIBLE);
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

}

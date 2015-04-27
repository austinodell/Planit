package edu.villanova.planit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Arrays;
import java.util.List;


public class LoginFragment extends Fragment {

    private static final String TAG = "MainFragment";
    private UiLifecycleHelper uiHelper;
    public GraphUser user;
    List<ParseObject> userList; //Holds all users
    ParseObject newEventObject; //Object to hold new event
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);

       context = getActivity();

    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        final LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));
        authButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser graphUser) {
                LoginFragment.this.user = graphUser;
                if (user != null) {
                    if (user.asMap().get("email") != null) {
                        Log.d(TAG, user.asMap().get("email").toString());
                    }
                    addUserToParse(user.getId());
                    //Add user Facebook id to shared preferences for CheckNotificationService
                    SharedPreferences sharedPreferences;
                    sharedPreferences = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("UserFbId", user.getId());
                    editor.putString("UserFirstName", user.getFirstName());
                    editor.apply();
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }


    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
            Intent homeIntent = new Intent(getActivity(), Home.class);
            startActivity(homeIntent);
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };


    private void getAllUsers() {
        ParseQuery<ParseObject> queryAll = ParseQuery.getQuery("Users");
        try {
            userList = queryAll.find();
        } catch (ParseException e) {
            Log.d(TAG, e.toString());
        }
    }

    private boolean hasAccount(String facebookID) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getString("FacebookID") == facebookID) {
                return true;
            }
        }
        return false;
    }

    private void addUserToParse(final String facebookID) {
        // checks to see if object exists in database
        ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("User");
        // where clause to see if it exists
        userQuery.whereEqualTo("FacebookID", facebookID);
        userQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject event, ParseException e) {
                // retrieves object
                if (event == null) {
                    // if event hasn't been created, create new one
                    Log.d(TAG, "Adding new user");
                    newEventObject = new ParseObject("User");
                    newEventObject.put("FacebookID", facebookID);
                    // saves it to parse.com
                    newEventObject.saveInBackground();
                }
            }
        });
    }
}

package edu.villanvoa.together;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.Inflater;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.entity.mime.MultipartEntity;
import com.parse.entity.mime.content.ContentBody;
import com.parse.entity.mime.content.FileBody;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

public class AddFriends extends ToolbarActivity {

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

    private String imgNameOnServer = null;
    private String imgResource = null;
    private boolean imgLocal = true;
    private LayoutInflater inflator;
    private View lastSpinnerItemView;
    private ImageLib imgLib;

    private String hint = "Click button to add friends.";

    private Friend hintFriend;

    private Context mContext;

    private GridView gridView;
    private View btnView;
    private FriendsGridAdapter friendsGridAdapter;

    private Spinner imageSpinner;
    private ImageView eventImage;

    private SquareImageButton addUserBtn;

    private ArrayList<Friend> friendsList;

    private static final int RESULT_LOAD_IMAGE = 2;
    private static final int REQUEST_CAMERA = 3;
    private static final int SELECT_FILE = 4;
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

        setupToolbar(R.string.title_activity_add_friends);

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

        mContext = this;

        imgLib = new ImageLib(this);

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

        friendsGridAdapter = new FriendsGridAdapter(this,friendsList,btnView,gridView,imgLib);
        gridView.setAdapter(friendsGridAdapter);

        final Button nextBtn = (Button) findViewById(R.id.next_page_btn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextStep();
            }
        });

        imageSpinner = (Spinner) findViewById(R.id.event_image_spinner);

        inflator = LayoutInflater.from(this);

        //ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,R.array.new_event_spinner,android.R.layout.simple_spinner_dropdown_item);
        final ArrayList<String> spinnerOptions = new ArrayList<String>();
        spinnerOptions.add("Bar");
        spinnerOptions.add("Basketball");
        spinnerOptions.add("Bowl");
        spinnerOptions.add("Club");
        spinnerOptions.add("Concert");
        spinnerOptions.add("Dinner");
        spinnerOptions.add("Movie");
        spinnerOptions.add("Picnic");
        spinnerOptions.add("Roadtrip");
        spinnerOptions.add("Shop");
        spinnerOptions.add("Ski");
        spinnerOptions.add("Smores");
        ImageSpinnerAdapter spinnerAdapter = new ImageSpinnerAdapter(mContext,spinnerOptions,setupLastItemInSpinnerIfNotSetup());
        imageSpinner.setAdapter(spinnerAdapter);

        imageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == spinnerOptions.size()) {
                    selectImage();
                } else {
                    imgLocal = true;
                    TextView tv = (TextView) imageSpinner.getSelectedView().findViewById(R.id.spinner_item_tv);
                    imgResource = tv.getText().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // Launches custom photo chooser
    public void pickPhoto() {
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment
                            .getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void uploadPhoto(final URI imageUri) throws InterruptedException {
        Log.i(TAG,"uploadPhoto called");

        Thread upload_http = new Thread() {
            @Override
            public void run() {
                URL url = null;
                String url_dl_string = "http://planit.austinodell.com/upload_dl.php";
                String url_ul_string = "http://planit.austinodell.com/upload.php";
                String location = imageUri.toString();
                String response_val = null;

                if(location.startsWith("content://com.google.android.apps.photos.content")) {
                    String dl_url = null;
                    String dl_url_decoded = null;
                    try {
                        dl_url = location.split("/0/")[1];
                        dl_url_decoded = URLDecoder.decode(dl_url, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG,"Download URL: " + dl_url_decoded);

                    try {
                        url = new URL(url_dl_string);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    Log.i(TAG,"Connecting to host " + url.getHost() + " on port " + String.valueOf(url.getPort()));
                    HttpURLConnection conn = null;
                    try {
                        conn = (HttpURLConnection) url.openConnection();
                    } catch (MalformedURLException e) {
                        Toast.makeText(getApplicationContext(),"URL is invalid!",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        conn.setRequestMethod("POST");          // note default method is GET
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    }

                    String params = "user=aodell&url=" + dl_url;

                    conn.setDoOutput(true);                 // required for POST method to upload data
                    conn.setFixedLengthStreamingMode(params.length());

                    try {
                        try {
                            Log.i(TAG,"opening output stream");
                            OutputStream os = conn.getOutputStream();
                            os.write(params.getBytes());
                            os.close();
                            Log.i(TAG,"opening input stream");
                            InputStream is = new BufferedInputStream(conn.getInputStream());
                            byte[] contents = new byte[1024];
                            int bytesRead = 0;
                            while ((bytesRead = is.read(contents)) != -1) {
                                response_val = new String(contents, 0, bytesRead);
                            }
                            Log.i(TAG,"Response: " + response_val);
                            imgNameOnServer = response_val;
                        } finally {
                            conn.disconnect();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    imgLocal = false;
                }

                Log.i(TAG,"Uri: " + imageUri.toString());

                /*File file = new File(imageUri.getPath());
                try {
                    HttpClient httpclient = new DefaultHttpClient();

                    HttpPost httppost = new HttpPost(url);

                    InputStreamEntity reqEntity = new InputStreamEntity(new FileInputStream(file), -1);
                    reqEntity.setContentType("binary/octet-stream");
                    reqEntity.setChunked(true); // Send in multiple parts if needed
                    httppost.setEntity(reqEntity);
                    HttpResponse response = httpclient.execute(httppost);
                    Toast.makeText(mContext,response.toString(),Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Log.e(TAG,"Error uploading file!");
                    e.printStackTrace();
                }*/
            }
        };

        upload_http.start();
        upload_http.join();

        updateLastSpinnerItemImg(imgNameOnServer);
    }

    private View setupLastItemInSpinnerIfNotSetup() {
        if(lastSpinnerItemView == null) {
            lastSpinnerItemView = inflator.inflate(R.layout.choose_image_spinner_item, null);
            TextView name = (TextView) lastSpinnerItemView.findViewById(R.id.spinner_item_tv);
            ImageView picture = (ImageView) lastSpinnerItemView.findViewById(R.id.spinner_item_img);

            name.setText("Choose Picture");
        }

        return lastSpinnerItemView;
    }

    private void updateLastSpinnerItemImg(String imgUrl) {
        String url = "http://planit.austinodell.com/img/" + imgUrl;
        imgLib.imageLoader.displayImage(url, (ImageView) lastSpinnerItemView.findViewById(R.id.spinner_item_img),imgLib.imageOptions);
    }

    private void nextStep() {
        pickDateIntent.putExtra("EventTitle",event_name.getText());
        pickDateIntent.putExtra("EventDetails",event_details.getText());
        pickDateIntent.putExtra("FriendsNames", friendsNames);
        pickDateIntent.putExtra("FriendsIds", friendsIds);
        pickDateIntent.putExtra("EventImageLocal",imgLocal);
        pickDateIntent.putExtra("EventImageResource", imgResource);
        pickDateIntent.putExtra("EventImageUrl", imgNameOnServer);

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
            case REQUEST_CAMERA:
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                break;
            case SELECT_FILE:
                Uri selectedImageUri = data.getData();
                URI selectedImageJavaUri = null;
                try {
                    selectedImageJavaUri = new URI(selectedImageUri.toString());
                    Log.i(TAG,"Created URI from Uri");
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                try {
                    uploadPhoto(selectedImageJavaUri);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case RESULT_LOAD_IMAGE:
                Log.i(TAG,"RESULT_LOAD_IMAGE started");
                if (resultCode == RESULT_OK && null != data) {
                    Log.i(TAG,"RESULT_LOAD_IMAGE if passed");
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    //int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    int columnIndex = 0;
                    String picturePath = cursor.getString(columnIndex);

                    for(int counter = 0; counter < cursor.getColumnCount(); counter++) {
                        Log.i(TAG,"RESULT_LOAD_IMAGE column="+cursor.getColumnName(counter)+" value="+cursor.getString(counter));
                    }

                    cursor.close();

                    // String picturePath contains the path of selected Image

                    Log.i(TAG,"RESULT_LOAD_IMAGE path: " + picturePath);
                    //uploadPhoto(picturePath);
                }
                break;
            default:
                Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
                break;
        }
    }

    public String getPath(Uri uri, Activity activity) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
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
        Log.i(TAG,"Friend ("+name+") ID: "+id);
        friendsList.add(friend);
    }

}

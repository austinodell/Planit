package edu.villanvoa.together;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by wildcat on 2/22/2015.
 */
public class ParseApplication extends Application {

    private static final String LOG = "DebuggingFilter";

    ParseObject eventObject;

    @Override
    public void onCreate() {
        super.onCreate();

        //initializes database
        Parse.initialize(this, "YMPhMAAd5vjkITGtdjD2pNsLmfAIhYZ5u3gXFteJ", "5w3m3Zex78Knrz69foyli8FKAv96PEzNlhBNJL3l");
    }
}

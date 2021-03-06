package edu.villanova.planit;

import android.app.Application;
import android.content.Context;

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
        init();
    }

    public void init() {
        Parse.initialize(this, "YMPhMAAd5vjkITGtdjD2pNsLmfAIhYZ5u3gXFteJ", "5w3m3Zex78Knrz69foyli8FKAv96PEzNlhBNJL3l");
    }

    public static void init(Context mContext) {
        Parse.initialize(mContext, "YMPhMAAd5vjkITGtdjD2pNsLmfAIhYZ5u3gXFteJ", "5w3m3Zex78Knrz69foyli8FKAv96PEzNlhBNJL3l");
    }
}

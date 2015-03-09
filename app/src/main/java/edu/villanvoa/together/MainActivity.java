package edu.villanvoa.together;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.parse.Parse;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainFragment";

    private LoginFragment loginFragment;

    private Button login_btn;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Parse.initialize(this, "YMPhMAAd5vjkITGtdjD2pNsLmfAIhYZ5u3gXFteJ", "5w3m3Zex78Knrz69foyli8FKAv96PEzNlhBNJL3l");

        mContext = this;

        if (savedInstanceState == null) {
            // Add the fragment button on initial activity setup
            loginFragment = new LoginFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, loginFragment)
                    .commit();
        } else {
            // Or set the fragment button from restored state info
            loginFragment = (LoginFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.container);
        }

        login_btn = (Button) findViewById(R.id.login_btn);

        /* If user clicks image, open up recipe details activity */
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext,Home.class));
            }
        });
    }
}

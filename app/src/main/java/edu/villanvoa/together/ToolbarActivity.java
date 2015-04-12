package edu.villanvoa.together;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by aodell on 3/16/15.
 */
public class ToolbarActivity extends ActionBarActivity {
    private Toolbar toolbar;

    protected void setupToolbar(int toolbarTitle) {
        /* Set up toolbar to replace Actionbar */
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(toolbarTitle);
        setSupportActionBar(toolbar);
        ActionBar mSupportActionBar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    protected void setupToolbar(String toolbarTitle) {
        /* Set up toolbar to replace Actionbar */
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(toolbarTitle);
        setSupportActionBar(toolbar);
        ActionBar mSupportActionBar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    public Toolbar getToolbar() {
        return this.toolbar;
    }
}

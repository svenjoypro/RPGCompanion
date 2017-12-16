package com.mpvreeken.rpgcompanion;

/**
 * Created by Sven on 9/13/2017.
 *
 * All activities that require the user to be logged in should extend this activity
 */

public class RPGCAuthActivity extends RPGCActivity {

    @Override
    public void onLoggedOut() {
        //if user logs out while in this activity finish()
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //if user resumes this activity but is no longer logged in, finish()
        if (!application.getLoggedIn()) { finish(); }
    }
}

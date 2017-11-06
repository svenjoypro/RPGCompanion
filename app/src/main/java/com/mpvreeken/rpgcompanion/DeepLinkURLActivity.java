package com.mpvreeken.rpgcompanion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/*
 * This Activity is created when a user clicks a url link
 * We then get the uri data, open the correct activity and pass in the related url parameters
 */

public class DeepLinkURLActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO parse url and redirect to correct activity
        //as of now the only time we deep link is for email activation
        //but we may use it later for something else

        Intent intent = getIntent();
        String intentUrl = intent.getDataString();
        Intent newIntent = new Intent(this, ConfirmEmailActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        newIntent.putExtra("intentUrl",intentUrl);
        newIntent.setAction(Long.toString(System.currentTimeMillis()));

        startActivity(newIntent);
        finish();
    }
}

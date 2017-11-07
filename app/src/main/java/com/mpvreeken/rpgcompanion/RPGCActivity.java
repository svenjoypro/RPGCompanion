package com.mpvreeken.rpgcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

/**
 * Created by Sven on 11/1/2017.
 */

public class RPGCActivity extends AppCompatActivity {

    public RPGCApplication application;
    private View loading_anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set up back button to appear in action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        application = (RPGCApplication) getApplication();
    }





    public void setupLoadingAnim() {
        final ViewGroup root_view = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        LayoutInflater li = LayoutInflater.from(getBaseContext());
        loading_anim = li.inflate(R.layout.layout_loading_anim, root_view, false);
        root_view.addView(loading_anim);

        //TODO for some reason the cancel/change buttons appear above (z-depth) this loading anim
        //TODO even with the following 3 lines it doesn't fix it
        loading_anim.bringToFront();
        loading_anim.requestLayout();
        loading_anim.invalidate();
    }

    public void onHttpResponseError(final String error) {

        RPGCActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(),error,Toast.LENGTH_LONG).show();
                if (error.equals("token_expired") || error.equals("token_invalid")) {
                    //TODO go to login activity
                    //I don't think we currently have expirations on our tokens, but we should check
                    application.logout();
                    //TODO should this be startactivityforresult?
                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(intent);
                    //TODO this could all be handled in RPGCApplication?
                }
            }
        });
    }

    public void showLoadingAnim() {
        RPGCActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loading_anim.setVisibility(View.VISIBLE);
            }
        });
    }
    public void hideLoadingAnim() {
        RPGCActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loading_anim.setVisibility(View.GONE);
            }
        });

    }


    public void onUnsuccessfulResponse(String s, Response response) {
        //TODO implement on all http calls
        try {
            JSONObject r = new JSONObject(s);
            //Error
            String error = r.has("error") ? r.getString("error") : "unknown_error";
            String readable_error;
            switch (error) {
                case "token_not_provided":
                    readable_error = "You are not logged in. Please log in first.";
                    //TODO Redirect to login
                    break;
                case "token_expired":
                    readable_error = "Your session has expired. Please log in again.";
                    //TODO redirect to login
                    break;
                default:
                    readable_error = "An unknown error has occurred. Please try again.";
                    break;
            }
        }
        catch (JSONException e) {
            Log.d("RPGCApplication", e.getMessage());
        }
        //throw new IOException("Unexpected code " + response);
    }







    public void disableUpButton() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem item;
        if (application.getLoggedIn()) {
            item = menu.findItem(R.id.menu_login);
            item.setVisible(false);
            item = menu.findItem(R.id.menu_register);
            item.setVisible(false);
        }
        else {
            item = menu.findItem(R.id.menu_logout);
            item.setVisible(false);
        }

        return true;
    }

    //Set click listener for back button in action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_settings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_login:
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_logout:
                //TODO
                return true;
            case R.id.menu_register:
                intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

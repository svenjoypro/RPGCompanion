package com.mpvreeken.rpgcompanion;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by Sven on 11/1/2017.
 */

public class RPGCActivity extends AppCompatActivity {

    public RPGCApplication application;
    private View loading_anim;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set up back button to appear in action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        application = (RPGCApplication) getApplication();
        context = getBaseContext();
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
                    application.logout(RPGCActivity.this);
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


    public void onUnsuccessfulResponse(Response response) {
        String readable_error = "An unknown error has occurred. Please try again.";
        try {
            JSONObject r = new JSONObject(response.body().string());
            Log.d("onUnsuccessfulResponse", r.toString());
            //Error
            String error = r.has("error") ? r.getString("error") : "unknown_error";
            switch (error) {
                case "token_not_provided":
                    readable_error = "You are not logged in. Please log in first.";
                    //TODO Redirect to login
                    break;
                case "token_expired":
                    readable_error = "Your session has expired. Please log in again.";
                    //TODO redirect to login
                    break;
                case "invalid_credentials":
                    readable_error = "Your username or password are incorrect";
                    break;
                case "invalid_email":
                    readable_error = "The email you entered is invali";
                    break;
                case "account_unconfirmed":
                    Intent intent = new Intent(getApplicationContext(), ResendEmailActivity.class);
                    startActivity(intent);
                    readable_error = "Please confirm your account by checking your email.";
                    break;
                default:
                    readable_error = "An unknown error has occurred. Please try again.";
                    break;
            }
        }
        catch (JSONException e) {
            Log.d("RPGCActivity", e.getMessage());
        } catch (IOException e) {
            Log.d("RPGCActivity", e.getMessage());
        }
        final String uiError = readable_error;

        RPGCActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RPGCActivity.this, uiError, Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void onHttpCallbackFail(final String s) {
        //http error, show error via Toast message
        RPGCActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RPGCActivity.this, s, Toast.LENGTH_LONG).show();
            }
        });
    }







    public void disableUpButton() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
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
            case R.id.menu_release_notes:
                intent = new Intent(getApplicationContext(), ReleaseNotesActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_settings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_login:
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_logout:
                application.logout(this);
                return true;
            case R.id.menu_register:
                intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_email:
                intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","svenjoypro@gmail.com", null));
                startActivity(Intent.createChooser(intent, "Send email..."));
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

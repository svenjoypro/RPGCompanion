package com.mpvreeken.rpgcompanion;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.mpvreeken.rpgcompanion.Classes.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Sven on 11/1/2017.
 */
/*
    *   This data will only persist for the lifetime of the app
    *
    *   If the app is sent to the background and garbage collection
    *   comes through, it may destroy this and all data it carries
    *   so at the very least initialize all vars with data so we
    *   don't get NullPointerException crashes.
    *
    *   If we need to persist data between application shutdowns
    *   we should use SharedPreferences
    *   https://developer.android.com/reference/android/content/SharedPreferences.html
    *
    */

public class RPGCApplication extends Application {
    private String username;
    private String token;
    private boolean loggedIn;
    private SharedPreferences prefs;
    public User user;

    private Activity activity;

    @Override
    public void onCreate () {
        super.onCreate();

        prefs = this.getSharedPreferences("com.mpvreeken.rpgcompanion", Context.MODE_PRIVATE);

        //TODO get user from sharedPreferences, also save it to preferences at some point

        this.username = prefs.getString("username", "");//second param is default value
        this.token = prefs.getString("token", "");


        //TODO check if token is valid, if so mark loggedIn as true
        this.loggedIn = false;

    }

    public void login(String token, Activity a) {
        this.token = token;
        this.loggedIn = true;
        prefs.edit().putString("token", this.token).apply();
        a.invalidateOptionsMenu();
    }

    public void logout(Activity a) {
        this.token = "";
        this.loggedIn = false;
        prefs.edit().putString("token", this.token).apply();
        a.invalidateOptionsMenu();
        //TODO finish()? what if they are on a logged in only page?
    }

    public void setUserData(User u) {
        user = u;

        SharedPreferences.Editor editor = prefs.edit();
        /*
        if (user.getStreamStatus() == 0) {
            this.wantToStream = false;
            editor.putBoolean("stream", false);
        }
        else {
            this.wantToStream = true;
            editor.putBoolean("stream", true);
        }
        editor.putString("username", user.getUsername());
        */
        editor.commit();
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        prefs.edit().putString("username", this.username).apply();
    }

    public String getToken() {
        if (token == null) {
            prefs = this.getSharedPreferences("com.mpvreeken.rpgcompanion", Context.MODE_PRIVATE);
            token = prefs.getString("token", "");
        }
        return token;
    }

    public Boolean getLoggedIn() {  return loggedIn; }

    public void checkToken(Activity a) {
        if (token==null) {
            prefs = this.getSharedPreferences("com.mpvreeken.rpgcompanion", Context.MODE_PRIVATE);
            token = prefs.getString("token", "");
        }
        if (token.length()==0) {
            loggedIn=false;
            return;
        }

        this.activity=a;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getResources().getString(R.string.url_check_token))
                .header("Authorization", "Bearer" + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("RPGCApplication", call.request().body().toString());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                loggedIn=false;
                if (!response.isSuccessful()) {
                    //response code isn't 200
                    Log.d("$$$$$$$$", response.body().string());
                }
                else {
                            /*
                             *    possible responses:
                             * { "success":"valid_token" }
                             *
                             */
                    try {
                        JSONObject r = new JSONObject(response.body().string());
                        Log.d("^^^^^^^^^^", r.toString());
                        if (r.has("success")) {
                            //TODO what do I do with this? Nothing?
                            loggedIn=true;
                            activity.invalidateOptionsMenu();
                        }
                        else {
                            //"An unknown error has occurred. Please try again.";
                        }
                    }
                    catch (JSONException e) {
                        Log.d("RPGCApplication", e.getMessage());
                    }
                }
            }
        });
    }
}


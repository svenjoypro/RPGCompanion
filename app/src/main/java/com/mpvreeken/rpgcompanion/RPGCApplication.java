package com.mpvreeken.rpgcompanion;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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
    *   It may be wise to integrate SharedPreferences here too, so
    *   we always have persistent data
    */

public class RPGCApplication extends Application {
    private String username;
    private String token;
    private boolean loggedIn;
    private SharedPreferences prefs;
    public User user;


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

    public void login(String token) {
        this.token = token;
        this.loggedIn = true;
        prefs.edit().putString("token", this.token).apply();
    }

    public void logout() {
        this.token = "";
        this.loggedIn = false;
        prefs.edit().putString("token", this.token).apply();
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

    public String getToken() { return token; }

    public Boolean getLoggedIn() { return this.loggedIn; }

    public void checkToken() {
        if (token.length()==0) { return; }

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
                if (!response.isSuccessful()) {
                    //response code isn't 200
                    onUnsuccessfulResponse(response.body().string(), response);
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

    public void onUnsuccessfulResponse(String s, Response response) {
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
            Log.d("$$$$$$$$$$$$$$$$$$$$$$$", r.toString());
        }
        catch (JSONException e) {
            Log.d("RPGCApplication", e.getMessage());
        }
        //throw new IOException("Unexpected code " + response);
    }
}


package com.mpvreeken.rpgcompanion;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.mpvreeken.rpgcompanion.Classes.User;

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


}


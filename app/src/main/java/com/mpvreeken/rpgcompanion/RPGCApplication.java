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
    *
    */

public class RPGCApplication extends Application {
    private String username;
    private String token;
    private boolean loggedIn;
    private SharedPreferences prefs;
    public User user;

    private Activity activity;

    private final String SHARED_PREFS = "com.mpvreeken.rpgcompanion";

    @Override
    public void onCreate () {
        super.onCreate();

        prefs = this.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        //TODO get user from sharedPreferences, also save it to preferences at some point

        this.username = prefs.getString("username", "");//second param is default value
        this.token = prefs.getString("token", "");

        this.loggedIn = false;
    }

    public void login(String token, Activity a) {
        this.token = token;
        this.loggedIn = true;
        if (prefs==null) { prefs = this.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE); }
        prefs.edit().putString("token", this.token).apply();
        if (a!=null) { a.invalidateOptionsMenu(); }
    }

    public void logout(RPGCActivity a) {
        this.token = "";
        this.loggedIn = false;
        if (prefs==null) { prefs = this.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE); }
        prefs.edit().putString("token", this.token).apply();
        if (a!=null) {
            a.invalidateOptionsMenu();
            a.onLoggedOut();
        }
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
        if (prefs == null) { prefs = this.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE); }
        if (token == null) { token = prefs.getString("token", ""); }
        return token;
    }

    public Boolean getLoggedIn() {  return loggedIn; }

    public void checkToken(Activity a) {
        if (prefs == null) { prefs = this.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE); }
        if (token==null) { token = prefs.getString("token", ""); }
        if (token.length()==0) {
            loggedIn=false;
            return;
        }
        if (a!=null) { this.activity = a; }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getResources().getString(R.string.url_check_token))
                .header("Authorization", "Bearer" + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //TODO keep this?
                loggedIn=false;
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
                            if (activity!=null) {
                                activity.invalidateOptionsMenu();
                            }
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


package com.mpvreeken.rpgcompanion.Classes;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sven on 11/2/2017.
 */

public class User {
    private String username, email;
    private int accountType;
    private Boolean isBanned;

    public User(JSONObject j) {
        try {
            this.username = j.has("username") ? j.getString("username") : "";
            this.email = j.has("email") ? j.getString("email") : "";
            this.accountType = j.has("account_type") ? j.getInt("account_type") : 0;
            this.isBanned = j.has("is_banned") ? j.getBoolean("is_banned") : true;
        } catch (JSONException e) {
            Log.d("User", e.getMessage());
            e.printStackTrace();
        }

    }
}

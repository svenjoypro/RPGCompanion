package com.mpvreeken.rpgcompanion;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.mpvreeken.rpgcompanion.Classes.User;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Sven on 11/1/2017.
 */

    /*
    *
    */

public class RPGCApplication extends Application {

    private SharedPreferences prefs;
    public User user;

    private RPGCActivity activity;

    public static final String SHARED_PREFS = "com.mpvreeken.rpgcompanion";

    @Override
    public void onCreate () {
        super.onCreate();

        getUserFromPrefs();
    }

    public void login(String token) {
        user.login(token);
        saveUserToPrefs();
        if (activity!=null) {
            try { activity.invalidateOptionsMenu(); }
            catch (Exception e) {}
        }
    }

    public void logout() {
        user.logout();
        saveUserToPrefs();
        if (activity!=null) {
            try {
                activity.invalidateOptionsMenu();
                activity.onLoggedOut();
            }
            catch (Exception e) {}
        }
    }

    public void setExternalLinkAlert(Boolean b) {
        user.setExternalLinkAlert(b);
        saveUserToPrefs();

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("externalLinkAlert", b);
        editor.apply();
    }
    public Boolean getExternalLinkAlert() {
        return user.externalLinkAlert;
    }

    public void setCurrentActivity(RPGCActivity a) {
        activity=a;
    }

    public int getMyID() {
        if (user==null) {
            getUserFromPrefs();
        }
        if (user==null || user.id==-1) {
            //todo toast here?
            logout();
            return -1;
        }
        return user.id;
    }

    public void setUserData(User u) {
        user = u;

        prefs = this.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        user.setExternalLinkAlert(prefs.getBoolean("externalLinkAlert", true));
        saveUserToPrefs();
    }

    private void getUserFromPrefs() {
        prefs = this.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("User", "");
        try {
            this.user = gson.fromJson(json, User.class);
            user.setExternalLinkAlert(prefs.getBoolean("externalLinkAlert", true));
        }
        catch (Exception e) {
            this.user=new User();
        }
    }

    private void saveUserToPrefs() {
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString("User", json);
        editor.apply();
    }


    public String getUsername() {
        return user.username;
    }

    public String getToken() {
        return user.token;
    }

    public Boolean getLoggedIn() {  return user.loggedIn; }

    public void checkToken() {
        if (user.token.length()==0) {
            logout();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getResources().getString(R.string.url_check_token))
                .header("Authorization", "Bearer" + user.token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //TODO keep this?
                logout();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    //response code isn't 200
                    Log.d("$$$$$$$$", response.body().string());
                    logout();
                }
                else {
                    try {
                        JSONObject r = new JSONObject(response.body().string());
                        if (r.has("success")) {
                            user.refreshToken(r);
                            login(r.getString("jwt"));
                        }
                        else {
                            logout();
                        }
                    }
                    catch (Exception e) {
                        logout();
                        Log.e("RPGCApplication", e.getMessage());
                    }
                }
            }
        });
    }
}


package com.mpvreeken.rpgcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AccountActivity extends RPGCAuthActivity {

    private TextView username_tv, email_tv, created_at_tv, total_upvotes_tv, total_downvotes_tv;
    private TextView maps_tv, map_ups_tv, map_downs_tv;
    private TextView riddles_tv, riddle_ups_tv, riddle_downs_tv;
    private TextView hooks_tv, hook_ups_tv, hook_downs_tv;
    private TextView items_tv, item_ups_tv, item_downs_tv;
    private TextView puzzles_tv, puzzle_ups_tv, puzzle_downs_tv;
    private int upvotes, downvotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        setupLoadingAnim();

        showLoadingAnim();

        setupViews();

        getAccountInfo();
    }

    private void setupViews() {
        username_tv=findViewById(R.id.account_username_tv);
        email_tv=findViewById(R.id.account_email_tv);
        created_at_tv=findViewById(R.id.account_created_at_tv);
        total_upvotes_tv=findViewById(R.id.account_total_upvotes_tv);
        total_downvotes_tv=findViewById(R.id.account_total_downvotes_tv);
        maps_tv=findViewById(R.id.account_maps_tv);
        map_ups_tv=findViewById(R.id.account_map_ups_tv);
        map_downs_tv=findViewById(R.id.account_map_downs_tv);
        riddles_tv=findViewById(R.id.account_riddles_tv);
        riddle_ups_tv=findViewById(R.id.account_riddle_ups_tv);
        riddle_downs_tv=findViewById(R.id.account_riddle_downs_tv);
        hooks_tv=findViewById(R.id.account_hooks_tv);
        hook_ups_tv=findViewById(R.id.account_hook_ups_tv);
        hook_downs_tv=findViewById(R.id.account_hook_downs_tv);
        items_tv=findViewById(R.id.account_items_tv);
        item_ups_tv=findViewById(R.id.account_item_ups_tv);
        item_downs_tv=findViewById(R.id.account_item_downs_tv);
        puzzles_tv=findViewById(R.id.account_puzzles_tv);
        puzzle_ups_tv=findViewById(R.id.account_puzzle_ups_tv);
        puzzle_downs_tv=findViewById(R.id.account_puzzle_downs_tv);
    }

    private void getAccountInfo() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getResources().getString(R.string.url_get_account_info))
                .header("Authorization", "Bearer" + application.getToken())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                hideLoadingAnim();
                onHttpCallbackFail(e.getMessage());
                finish();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                hideLoadingAnim();
                if (!response.isSuccessful()) {
                    onUnsuccessfulResponse(response);
                    finish();
                }
                else {
                    try {
                        final JSONObject r = new JSONObject(response.body().string());
                        AccountActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setTexts(r);
                            }
                        });
                    }
                    catch (JSONException e) {
                        Log.d("RPGCApplication", e.getMessage());
                        onHttpResponseError("An unknown error has occurred. Please try again.");
                        finish();
                    }
                }
            }
        });
    }

    private void setTexts(JSONObject r) {
        try {
            username_tv.setText("Username: "+r.getString("username"));
            email_tv.setText("Email: "+r.getString("email"));
            String c = r.getString("created_at");
            c = c.substring(c.indexOf(":")+2, c.indexOf(" "));
            created_at_tv.setText("Member Since: "+c);
            maps_tv.setText("Number of Maps submitted: "+r.getString("maps"));
            map_ups_tv.setText("Total Map upvotes: "+r.getString("map_ups"));
            map_downs_tv.setText("Total Map downvotes: "+r.getString("map_downs"));
            riddles_tv.setText("Number of Riddles submitted: "+r.getString("riddles"));
            riddle_ups_tv.setText("Total Riddle upvotes: "+r.getString("riddle_ups"));
            riddle_downs_tv.setText("Total Riddle downvotes: "+r.getString("riddle_downs"));
            hooks_tv.setText("Number of Plot Hooks submitted: "+r.getString("hooks"));
            hook_ups_tv.setText("Total Plot Hook upvotes: "+r.getString("hook_ups"));
            hook_downs_tv.setText("Total Plot Hook downvotes: "+r.getString("hook_downs"));
            items_tv.setText("Number of Unique Items submitted: "+r.getString("items"));
            item_ups_tv.setText("Total Unique Item upvotes: "+r.getString("item_ups"));
            item_downs_tv.setText("Total Unique Item downvotes: "+r.getString("item_downs"));
            puzzles_tv.setText("Number of Puzzles submitted: "+r.getString("puzzles"));
            puzzle_ups_tv.setText("Total Puzzle upvotes: "+r.getString("puzzle_ups"));
            puzzle_downs_tv.setText("Total Puzzle downvotes: "+r.getString("puzzle_downs"));

            upvotes = r.getInt("map_ups") + r.getInt("riddle_ups") + r.getInt("puzzle_ups") + r.getInt("hook_ups") + r.getInt("item_ups");
            downvotes = r.getInt("map_downs") + r.getInt("riddle_downs") + r.getInt("puzzle_downs") + r.getInt("hook_downs") + r.getInt("item_downs");
            total_upvotes_tv.setText("Total Upvotes: " + String.valueOf(upvotes));
            total_downvotes_tv.setText("Total Downvotes: " + String.valueOf(downvotes));
        } catch (JSONException e) {
            Log.d("RPGCApplication", e.getMessage());
            onHttpResponseError("An unknown error has occurred. Please try again.");
        }
    }
}

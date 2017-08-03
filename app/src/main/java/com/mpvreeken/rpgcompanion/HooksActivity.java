package com.mpvreeken.rpgcompanion;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mpvreeken.rpgcompanion.Classes.Hook;
import com.mpvreeken.rpgcompanion.Classes.HookArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HooksActivity extends AppCompatActivity {

    ArrayList<Hook> hooksArray = new ArrayList<>();
    HookArrayAdapter hookArrayAdapter;
    ListView hooks_lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hooks);

        //Set up back button to appear in action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Fetch hooks from db
        this.hooksArray = new ArrayList<>();
        this.hookArrayAdapter = new HookArrayAdapter(this, hooksArray);
        this.hooks_lv = findViewById(R.id.hooks_lv);

        String hooks_url = "http://192.168.1.100/rpg_companion/api/hooks";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(hooks_url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //TODO - Handle this
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) { throw new IOException("Unexpected code " + response); }
                else {
                    try {
                        JSONArray r = new JSONArray(response.body().string());
                        for (int i=0; i<r.length(); i++) {
                            /*
                            //The plan is to eventually add more to each hook, like user_id, and voting
                            //but as of now, we just get simple info
                            hooksArray.add(
                                    new Hook(r.getJSONObject(i).getString("id"),
                                    r.getJSONObject(i).getString("title"),
                                    r.getJSONObject(i).getString("user_id"),
                                    r.getJSONObject(i).getString("desc"),
                                    r.getJSONObject(i).getString("votes"),
                                    r.getJSONObject(i).getString("created_at")));
                            */
                            hooksArray.add(
                                    new Hook(r.getJSONObject(i).getString("title"),
                                    r.getJSONObject(i).getString("description")));
                        }

                        //We can't update the UI on a background thread, so run on the UI thread
                        HooksActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setupUI();
                            }
                        });
                    }
                    catch (JSONException e) { e.printStackTrace(); }
                }
            }
        });
    }

    public void setupUI() {
        hooks_lv.setAdapter(hookArrayAdapter);

        hooks_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Intent intent = new Intent(parent.getContext(), DisplayHookActivity.class);

                Bundle bundle = new Bundle();
                //bundle.putSerializable("MOVIE_OBJ", (Serializable) moviesArray.get(position));

                //intent.putExtras(bundle);
                intent.putExtra("hook_id", hooksArray.get(position).getId());
                startActivity(intent);
            }
        });
    }

    //Set click listener for back button in action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

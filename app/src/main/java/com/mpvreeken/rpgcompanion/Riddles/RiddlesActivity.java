package com.mpvreeken.rpgcompanion;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mpvreeken.rpgcompanion.Classes.Riddle;
import com.mpvreeken.rpgcompanion.Classes.RiddleArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RiddlesActivity extends AppCompatActivity {

    ArrayList<Riddle> riddlesArray = new ArrayList<>();
    RiddleArrayAdapter riddleArrayAdapter;
    ListView riddles_lv;
    Context context;

    ConstraintLayout loading_screen;
    ProgressBar loading_progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riddles);

        //Set up back button to appear in action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        context = this.getBaseContext();

        loading_screen = findViewById(R.id.riddles_loading_screen);
        loading_progressBar = findViewById(R.id.riddles_loading_screen_progressBar);

        Button new_btn = findViewById(R.id.riddles_new_btn);

        new_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NewPostActivity.class);
                intent.putExtra("type", "riddle");
                startActivity(intent);
            }
        });

        //Fetch riddles from db
        this.riddlesArray = new ArrayList<>();
        this.riddleArrayAdapter = new RiddleArrayAdapter(this, riddlesArray);
        this.riddles_lv = findViewById(R.id.riddles_lv);

        String riddles_url = getResources().getString(R.string.url_get_riddles);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(riddles_url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                displayError("Could not connect to server. Please try again");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    displayError("An unknown error occurred. Please try again");
                    throw new IOException("Unexpected code " + response);
                }
                else {
                    try {
                        JSONArray r = new JSONArray(response.body().string());
                        for (int i=0; i<r.length(); i++) {
                            riddlesArray.add(
                                    new Riddle(
                                            r.getJSONObject(i).getString("id"),
                                            r.getJSONObject(i).getString("user_id"),
                                            r.getJSONObject(i).getString("riddle"),
                                            r.getJSONObject(i).getString("answer"),
                                            r.getJSONObject(i).getInt("upvotes"),
                                            r.getJSONObject(i).getInt("downvotes"),
                                            r.getJSONObject(i).getInt("voted"),
                                            r.getJSONObject(i).getString("created_at")
                                    )
                            );
                        }

                        //We can't update the UI on a background thread, so run on the UI thread
                        RiddlesActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setupUI();
                            }
                        });
                    }
                    catch (JSONException e) {
                        displayError("An unknown error occurred. Please try again");
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void setupUI() {

        hideLoadingScreen();
        riddles_lv.setAdapter(riddleArrayAdapter);
        Button btn = new Button(this);
        btn.setText("Load More");

        riddles_lv.addFooterView(btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMoreRiddles();
            }
        });

        riddles_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Intent intent = new Intent(parent.getContext(), DisplayRiddleActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("RIDDLE_OBJ", (Serializable) riddlesArray.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    public void loadMoreRiddles() {
        String riddles_url = getResources().getString(R.string.url_get_riddles);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(riddles_url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                displayError("Could not connect to server. Please try again");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    displayError("An unknown error occurred. Please try again");
                    throw new IOException("Unexpected code " + response);
                }
                else {
                    try {
                        JSONArray r = new JSONArray(response.body().string());
                        for (int i=0; i<r.length(); i++) {
                            riddlesArray.add(
                                    new Riddle(
                                            r.getJSONObject(i).getString("id"),
                                            r.getJSONObject(i).getString("user_id"),
                                            r.getJSONObject(i).getString("riddle"),
                                            r.getJSONObject(i).getString("answer"),
                                            r.getJSONObject(i).getInt("upvotes"),
                                            r.getJSONObject(i).getInt("downvotes"),
                                            r.getJSONObject(i).getInt("voted"),
                                            r.getJSONObject(i).getString("created_at")
                                    )
                            );
                        }

                        RiddlesActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Get scroll position of listview, so we can retain their position after loading more
                                int firstVisibleItem = riddles_lv.getFirstVisiblePosition();
                                int oldCount = riddleArrayAdapter.getCount();
                                View view = riddles_lv.getChildAt(0);
                                int pos = (view == null ? 0 :  view.getBottom());

                                //Set the new data
                                riddles_lv.setAdapter(riddleArrayAdapter);

                                //Set the listview position back to where they were
                                riddles_lv.setSelectionFromTop(firstVisibleItem + riddleArrayAdapter.getCount() - oldCount + 1, pos);
                            }
                        });

                    }
                    catch (JSONException e) {
                        displayError("An unknown error occurred. Please try again");
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void showLoadingScreen() {
        loading_screen.setVisibility(View.VISIBLE);
    }
    private void hideLoadingScreen() {
        loading_screen.setVisibility(View.GONE);
    }

    private void displayError(final String s) {
        RiddlesActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, s, Toast.LENGTH_LONG).show();
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

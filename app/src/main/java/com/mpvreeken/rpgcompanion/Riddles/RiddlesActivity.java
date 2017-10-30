package com.mpvreeken.rpgcompanion.Riddles;

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

import com.mpvreeken.rpgcompanion.NewPostActivity;
import com.mpvreeken.rpgcompanion.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RiddlesActivity extends AppCompatActivity {

    private ArrayList<Riddle> riddlesArray = new ArrayList<>();
    private RiddleArrayAdapter riddleArrayAdapter;
    private ListView riddles_lv;
    private Context context;

    private int total;
    private List<Integer> past_ids, get_ids;

    private ConstraintLayout loading_screen;
    private ProgressBar loading_progressBar;

    private int GET_QUANTITY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riddles);

        //Set up back button to appear in action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        context = this.getBaseContext();

        past_ids = new ArrayList<>();

        //# of riddles to get from db
        GET_QUANTITY = Integer.valueOf(getResources().getString(R.string.url_get_riddles_quantity));

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

        String riddles_url = getResources().getString(R.string.url_get_riddles_first);

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
                        JSONObject o = new JSONObject(response.body().string());
                        total = o.getInt("total");
                        JSONArray r = o.getJSONArray("riddles");
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
        //TODO eventually cache these on the user's device by id?
        //TODO If they aren't going to change id, then caching might be a wise choice

        //TODO the issue with this method is that if a riddle is deleted, then an id no longer exists
        //TODO So if the user requests a riddle with a deleted id, what happens?

        if (past_ids.size() > total-GET_QUANTITY) {
            //No more possible ids
            Toast.makeText(context, "No more riddles to get", Toast.LENGTH_LONG).show();
            return;
        }

        List<Integer> get_ids = new ArrayList<>();
        Random ran = new Random();

        while (get_ids.size() < GET_QUANTITY) {
            int x = ran.nextInt(total)+1;

            if (!past_ids.contains(x) && !get_ids.contains(x)) {
                get_ids.add(x);
                past_ids.add(x);
            }
            else {
                //already have that id, so lets either inc or dec to get a new one
                int i=0;
                if (x % 2 == 0) {
                    do {
                        x++;
                        if (x>total) { x=1; }
                        if (++i > 30) { break; } // max 30 iterations
                    } while(past_ids.contains(x) || get_ids.contains(x));
                }
                else {
                    do {
                        x--;
                        if (x<=0) { x=total; }
                        if (++i > 30) { break; } // max 30 iterations
                    } while(past_ids.contains(x) || get_ids.contains(x));
                }
                //make sure new value isn't in arrays before adding, otherwise skip it
                if (!past_ids.contains(x) && !get_ids.contains(x)) {
                    get_ids.add(x);
                    past_ids.add(x);
                }
            }

        }

        String riddles_url = getResources().getString(R.string.url_get_riddles_ids);

        for (int id : get_ids) {
            riddles_url += "ids[]="+String.valueOf(id)+"&";
        }

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
                        JSONObject o = new JSONObject(response.body().string());
                        JSONArray r = o.getJSONArray("riddles");
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

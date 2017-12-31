package com.mpvreeken.rpgcompanion.Riddles;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.mpvreeken.rpgcompanion.R;
import com.mpvreeken.rpgcompanion.RPGCActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RiddlesActivity extends RPGCActivity {

    private ArrayList<Riddle> riddlesArray = new ArrayList<>();
    private RiddleArrayAdapter riddleArrayAdapter;
    private ListView riddles_lv;

    private String seed; //random seed for server
    private int page; //pagination for server to get next set of maps

    private Boolean firstLoad=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riddles);
        setupLoadingAnimTransparent();

        page = 0;
        Random rand = new Random();
        seed = String.valueOf(rand.nextFloat());

        Button new_btn = findViewById(R.id.riddles_new_btn);

        new_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NewRiddleActivity.class);
                startActivity(intent);
            }
        });

        //Fetch riddles from db
        this.riddlesArray = new ArrayList<>();
        this.riddleArrayAdapter = new RiddleArrayAdapter(this, riddlesArray);
        this.riddles_lv = findViewById(R.id.riddles_lv);

        setupUI();

        loadMoreRiddles();
    }

    public void setupUI() {
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
                bundle.putInt("RIDDLE_ID", riddlesArray.get(position).getId());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    public void loadMoreRiddles() {
        showLoadingAnim();

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("seed", seed)
                .add("page", String.valueOf(page))
                .build();

        Request request = new Request.Builder()
                .url(getResources().getString(R.string.url_get_riddles))
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                hideLoadingAnim();
                displayError("Could not connect to server. Please try again");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                hideLoadingAnim();
                if (!response.isSuccessful()) {
                    onUnsuccessfulResponse(response);
                }
                else {
                    try {
                        JSONArray r = new JSONArray(response.body().string());

                        if (r.length()==0) {
                            displayError("No More Riddles. Help everyone by submitting new riddles.");
                            return;
                        }

                        for (int i=0; i<r.length(); i++) {
                            riddlesArray.add(
                                    new Riddle(
                                            r.getJSONObject(i).getInt("id"),
                                            r.getJSONObject(i).getString("username"),
                                            r.getJSONObject(i).getInt("user_id"),
                                            r.getJSONObject(i).getString("riddle"),
                                            r.getJSONObject(i).getString("answer"),
                                            r.getJSONObject(i).getInt("upvotes"),
                                            r.getJSONObject(i).getInt("downvotes"),
                                            r.getJSONObject(i).getInt("voted"),
                                            r.getJSONObject(i).getString("created_at"),
                                            r.getJSONObject(i).getString("updated_at")
                                    )
                            );
                        }

                        //Increment page value for server
                        page+=1;

                        RiddlesActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (firstLoad) {
                                    firstLoad=false;
                                    //Set the new data
                                    riddles_lv.setAdapter(riddleArrayAdapter);
                                }
                                else {
                                    //Get scroll position of listview, so we can retain their position after loading more
                                    int firstVisibleItem = riddles_lv.getFirstVisiblePosition();
                                    int oldCount = riddleArrayAdapter.getCount();
                                    View view = riddles_lv.getChildAt(0);
                                    int pos = (view == null ? 0 : view.getBottom());

                                    //Set the new data
                                    riddles_lv.setAdapter(riddleArrayAdapter);

                                    //Set the listview position back to where they were
                                    riddles_lv.setSelectionFromTop(firstVisibleItem + riddleArrayAdapter.getCount() - oldCount + 1, pos);
                                }
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

    private void displayError(final String s) {
        RiddlesActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, s, Toast.LENGTH_LONG).show();
            }
        });
    }
}

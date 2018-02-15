package com.mpvreeken.rpgcompanion.Riddles;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.mpvreeken.rpgcompanion.R;
import com.mpvreeken.rpgcompanion.RPGCActivity;
import org.json.JSONArray;
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

    private Spinner sort_spinner;
    private int sort_selection;
    private static final String[] SORT_METHODS = {"r", "uv", "dv", "dd", "da"};

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
        seed = String.valueOf(rand.nextInt(1000000));

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
    }

    public void setupUI() {
        sort_spinner = findViewById(R.id.riddles_sort_spinner);
        sort_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sort_selection=i;
                if (sort_selection==0) {
                    Random rand = new Random();
                    seed = String.valueOf(rand.nextInt(1000000));
                }
                resetAndLoad();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sort_spinner.setAdapter(adapter);
        sort_selection=0;

        riddles_lv.setAdapter(riddleArrayAdapter);
        Button btn = new Button(this);
        btn.setText(R.string.lbl_load_more);

        riddles_lv.addFooterView(btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMoreRiddles();
            }
        });
    }

    private void resetAndLoad() {
        page=0;
        firstLoad=true;
        riddleArrayAdapter.clear();
        loadMoreRiddles();
    }

    public void loadMoreRiddles() {
        showLoadingAnim();

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody;

        int i = sort_spinner.getSelectedItemPosition();
        if (i==0) {
            //Random
            requestBody = new FormBody.Builder()
                    .add("method", SORT_METHODS[0])
                    .add("seed", seed)
                    .add("page", String.valueOf(page))
                    .build();
        }
        else {
            requestBody = new FormBody.Builder()
                    .add("method", SORT_METHODS[i])
                    .add("page", String.valueOf(page))
                    .build();
        }

        Request request = new Request.Builder()
                .url(getResources().getString(R.string.url_get_riddles))
                .header("Authorization", "Bearer" + application.getToken())
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
                            riddlesArray.add(new Riddle(context, riddlesArray.size(), r.getJSONObject(i)));
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
                    catch (Exception e) {
                        displayError("An unknown error occurred. Please try again");
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (1) : {
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        if (data.hasExtra("DELETED")) {
                            SerialRiddle serialRiddle = (SerialRiddle) data.getSerializableExtra("SERIALIZED_OBJ");
                            if (riddlesArray != null && serialRiddle != null) {
                                riddlesArray.remove(serialRiddle.position);
                                if (riddleArrayAdapter != null) {
                                    riddleArrayAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                        else {
                            SerialRiddle serialRiddle = (SerialRiddle) data.getSerializableExtra("SERIALIZED_OBJ");
                            if (riddlesArray != null && serialRiddle != null) {
                                riddlesArray.get(serialRiddle.position).updateLocal(serialRiddle);
                                if (riddleArrayAdapter != null) {
                                    riddleArrayAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                    catch (Exception e) { Log.e("RiddlesActivity", e.getMessage()); }
                }
                break;
            }
        }
    }
}

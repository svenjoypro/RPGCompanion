package com.mpvreeken.rpgcompanion.EncMaps;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.mpvreeken.rpgcompanion.R;
import com.mpvreeken.rpgcompanion.RPGCActivity;
import com.mpvreeken.rpgcompanion.Widgets.MultiselectSpinner;

import org.json.JSONArray;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EncMapsActivity extends RPGCActivity {

    private MultiselectSpinner spinner;

    private List<Integer> tempSelectedIndices;

    private ArrayList<EncMap> mapsArray = new ArrayList<>();
    private EncMapArrayAdapter mapArrayAdapter;
    private ListView maps_lv;

    private Spinner sort_spinner, filter_spinner;
    private static final String[] SORT_METHODS = {"r", "uv", "dv", "dd", "da"};

    private String seed; //random seed for server
    private int page; //pagination for server to get next set of maps

    private Boolean firstLoad=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enc_maps);

        setupLoadingAnimTransparent();

        page = 0;
        Random rand = new Random();
        seed = String.valueOf(rand.nextInt(1000000));

        Button new_btn = findViewById(R.id.maps_new_btn);

        new_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!application.getLoggedIn()) {
                    Toast.makeText(application, "You must be logged in to post a new Map", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(context, NewEncMapActivity.class);
                startActivity(intent);
            }
        });

        spinner = findViewById(R.id.maps_envs_spinner);
        spinner.setItems(EncMap.ENVS);
        spinner.setSelection(EncMap.ENVS);

        spinner.setEventListener(new MultiselectSpinner.OnSpinnerEventsListener() {
            @Override
            public void onSpinnerOpened() {
                tempSelectedIndices = spinner.getSelectedIndicies();
            }

            @Override
            public void onSpinnerClosed() {
                if (!tempSelectedIndices.equals(spinner.getSelectedIndicies())) {
                    /*
                    mapArrayAdapter.clear();
                    mapArrayAdapter.notifyDataSetChanged();
                    page=0;
                    loadMoreEncMaps();
                    */
                    resetAndLoad();
                }
            }
        });

        //Fetch maps from db
        this.mapsArray = new ArrayList<>();
        this.mapArrayAdapter = new EncMapArrayAdapter(this, mapsArray);
        this.maps_lv = findViewById(R.id.maps_lv);

        setupUI();
    }

    public void setupUI() {
        sort_spinner = findViewById(R.id.maps_sort_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sort_spinner.setAdapter(adapter);
        sort_spinner.setSelected(false);
        sort_spinner.setSelection(0,true);
        sort_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==0) {
                    Random rand = new Random();
                    seed = String.valueOf(rand.nextInt(1000000));
                }
                resetAndLoad();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        filter_spinner = findViewById(R.id.maps_filter_spinner);
        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(this,
                R.array.filter_array, android.R.layout.simple_spinner_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filter_spinner.setAdapter(filterAdapter);
        filter_spinner.setSelected(false);
        filter_spinner.setSelection(0,true);
        filter_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                resetAndLoad();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        if (application.getLoggedIn()) {
            filter_spinner.setVisibility(View.VISIBLE);
        }

        maps_lv.setAdapter(mapArrayAdapter);
        Button btn = new Button(this);
        btn.setText(R.string.lbl_load_more);

        maps_lv.addFooterView(btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMoreEncMaps();
            }
        });

        loadMoreEncMaps();
    }

    private void resetAndLoad() {
        page=0;
        firstLoad=true;
        mapArrayAdapter.clear();
        loadMoreEncMaps();
    }

    public void loadMoreEncMaps() {
        Log.e("MAPS", "LoadMore");
        showLoadingAnim();

        StringBuilder ar = new StringBuilder();
        for(int i:spinner.getSelectedIndicies()) {
            ar.append(i).append(",");
        }
        if (ar.length()==0) {
            Toast.makeText(application, "Please select at least one environment", Toast.LENGTH_SHORT).show();
            hideLoadingAnim();
            return;
        }
        ar = new StringBuilder(ar.substring(0, ar.length() - 1));

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody;

        String filter = application.getLoggedIn() ? String.valueOf(filter_spinner.getSelectedItemPosition()) : "0";

        int i = sort_spinner.getSelectedItemPosition();
        if (i==0) {
            //Random
            requestBody = new FormBody.Builder()
                    .add("method", SORT_METHODS[0])
                    .add("seed", seed)
                    .add("page", String.valueOf(page))
                    .add("filter", filter)
                    .add("envs", ar.toString())
                    .build();
        }
        else {
            requestBody = new FormBody.Builder()
                    .add("method", SORT_METHODS[i])
                    .add("page", String.valueOf(page))
                    .add("filter", filter)
                    .add("envs", ar.toString())
                    .build();
        }

        Request request = new Request.Builder()
                .url(getResources().getString(R.string.url_get_maps))
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
                            displayError("No More Maps. Help everyone by submitting new maps.");
                            return;
                        }

                        for (int i=0; i<r.length(); i++) {
                            mapsArray.add(new EncMap(context, mapsArray.size(), r.getJSONObject(i)));
                        }


                        //Increment page value for server
                        page+=1;

                        EncMapsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (firstLoad) {
                                    firstLoad=false;
                                    //Set the new data
                                    maps_lv.setAdapter(mapArrayAdapter);
                                }
                                else {
                                    //Get scroll position of listview, so we can retain their position after loading more
                                    int firstVisibleItem = maps_lv.getFirstVisiblePosition();
                                    int oldCount = mapArrayAdapter.getCount();
                                    View view = maps_lv.getChildAt(0);
                                    int pos = (view == null ? 0 : view.getBottom());

                                    //Set the new data
                                    maps_lv.setAdapter(mapArrayAdapter);

                                    //Set the listview position back to where they were
                                    maps_lv.setSelectionFromTop(firstVisibleItem + mapArrayAdapter.getCount() - oldCount + 1, pos);
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
                            SerialEncMap serialEncMap = (SerialEncMap) data.getSerializableExtra("SERIALIZED_OBJ");
                            if (mapsArray != null && serialEncMap != null) {
                                mapsArray.remove(serialEncMap.position);
                                if (mapArrayAdapter != null) {
                                    mapArrayAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            SerialEncMap serialEncMap = (SerialEncMap) data.getSerializableExtra("SERIALIZED_OBJ");
                            if (mapsArray != null && serialEncMap != null) {
                                mapsArray.get(serialEncMap.position).updateLocal(serialEncMap);
                                if (mapArrayAdapter != null) {
                                    mapArrayAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                    catch (Exception e) { Log.e("EncMapsActivity", e.getMessage()); }
                }
                break;
            }
        }
    }
}

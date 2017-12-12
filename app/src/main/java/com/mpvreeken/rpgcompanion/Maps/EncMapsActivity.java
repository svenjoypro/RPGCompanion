package com.mpvreeken.rpgcompanion.Maps;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.mpvreeken.rpgcompanion.R;
import com.mpvreeken.rpgcompanion.RPGCActivity;
import com.mpvreeken.rpgcompanion.Widgets.MultiselectSpinner;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EncMapsActivity extends RPGCActivity {

    private List<String> envs;
    private Menu menu;
    private MultiselectSpinner spinner;
    private Button get_btn, initial_load_btn;

    private ArrayList<EncMap> mapsArray = new ArrayList<>();
    private EncMapArrayAdapter encMapArrayAdapter;
    private ListView maps_lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enc_maps);

        setupLoadingAnim();

        spinner = findViewById(R.id.maps_envs_spinner);
        get_btn = findViewById(R.id.maps_get_btn);

        maps_lv = findViewById(R.id.maps_lv);
        mapsArray = new ArrayList<>();
        encMapArrayAdapter = new EncMapArrayAdapter(this, mapsArray);

        initial_load_btn = findViewById(R.id.maps_init_load_btn);

        getEnvironments();
    }

    private void getEnvironments() {
        showLoadingAnim();

        String env_url = getResources().getString(R.string.url_get_environments);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(env_url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                displayError("Could not connect to server. Please try again");
                hideLoadingAnim();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                hideLoadingAnim();
                if (!response.isSuccessful()) {
                    displayError("Unable to get Map data from server");
                    finish();
                    return;
                    //onUnsuccessfulResponse(response);
                }
                else {
                    try {
                        JSONArray r = new JSONArray(response.body().string());
                        envs = new ArrayList();
                        for (int i=0; i<r.length(); i++) {
                            envs.add(r.getString(i));
                        }
                        setupUI();
                    }
                    catch (JSONException e) {
                        displayError("An unknown error occurred. Please try again");
                        finish();
                        return;
                    }
                }
            }
        });
    }

    private void setupUI() {
        EncMapsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (envs==null || envs.size()==0) {
                    Toast.makeText(context, "Unable to get Map data from server", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }

                spinner.setItems(envs);
                spinner.setSelection(envs);

                initial_load_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadMoreMaps();
                    }
                });


                Button btn = new Button(context);
                btn.setText("Load More");

                maps_lv.addFooterView(btn);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadMoreMaps();
                    }
                });

                maps_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        Intent intent = new Intent(parent.getContext(), DisplayEncMapActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putString("MAP_ID", mapsArray.get(position).getId());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                Button new_map_btn = findViewById(R.id.maps_new_btn);
                new_map_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getBaseContext(), NewEncMapActivity.class);
                        intent.putStringArrayListExtra("ENVS", new ArrayList<String>(envs));
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private void loadMoreMaps() {
        showLoadingAnim();

        OkHttpClient client = new OkHttpClient();

        String maps_url = "http://192.168.1.100/rpg_companion/api/maps";
        //getResources().getString(R.string.url_get_maps)
        String ar = "";
        for(int i:spinner.getSelectedIndicies()) {
            ar+=i+",";
        }
        ar = ar.substring(0,ar.length()-1);

        RequestBody requestBody = new FormBody.Builder()
                .add("envs", ar)
                .build();

        Request request = new Request.Builder()
                .url(getResources().getString(R.string.url_get_maps))
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                hideLoadingAnim();
                displayError("Could not connect to server. Please try again");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                hideLoadingAnim();
                if (!response.isSuccessful()) {
                    displayError("An unknown error occurred. Please try again");
                    throw new IOException("Unexpected code " + response);
                }
                else {
                    try {
                        JSONArray r = new JSONArray(response.body().string());
                        for (int i=0; i<r.length(); i++) {
                            mapsArray.add(
                                    new EncMap(
                                            r.getJSONObject(i).getString("id"),
                                            r.getJSONObject(i).getString("title"),
                                            r.getJSONObject(i).getString("username"),
                                            r.getJSONObject(i).getInt("user_id"),
                                            r.getJSONObject(i).getString("description"),
                                            r.getJSONObject(i).getString("link"),
                                            r.getJSONObject(i).getInt("upvotes"),
                                            r.getJSONObject(i).getInt("downvotes"),
                                            r.getJSONObject(i).getInt("voted"),
                                            r.getJSONObject(i).getString("created_at"),
                                            r.getJSONObject(i).getString("updated_at")
                                    )
                            );
                        }

                        EncMapsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initial_load_btn.setVisibility(View.INVISIBLE);

                                //Get scroll position of listview, so we can retain their position after loading more
                                int firstVisibleItem = maps_lv.getFirstVisiblePosition();
                                int oldCount = encMapArrayAdapter.getCount();
                                View view = maps_lv.getChildAt(0);
                                int pos = (view == null ? 0 :  view.getBottom());



                                //Set the new data
                                maps_lv.setAdapter(encMapArrayAdapter);

                                //Set the listview position back to where they were
                                maps_lv.setSelectionFromTop(firstVisibleItem + encMapArrayAdapter.getCount() - oldCount + 1, pos);
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
        EncMapsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, s, Toast.LENGTH_LONG).show();
            }
        });
    }
}

package com.mpvreeken.rpgcompanion.Hooks;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
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

public class HooksActivity extends RPGCActivity {

    ArrayList<Hook> hooksArray = new ArrayList<>();
    HookArrayAdapter hookArrayAdapter;
    ListView hooks_lv;

    private String seed; //random seed for server
    private int page; //pagination for server to get next set of maps

    private Boolean firstLoad=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hooks);

        setupLoadingAnimTransparent();

        page = 0;
        Random rand = new Random();
        seed = String.valueOf(rand.nextInt(1000000));

        Button new_btn = findViewById(R.id.hooks_new_btn);

        new_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NewHookActivity.class);
                startActivity(intent);
            }
        });

        //Fetch hooks from db
        this.hooksArray = new ArrayList<>();
        this.hookArrayAdapter = new HookArrayAdapter(this, hooksArray);
        this.hooks_lv = findViewById(R.id.hooks_lv);

        setupUI();

        loadMoreHooks();
    }

    public void setupUI() {
        hooks_lv.setAdapter(hookArrayAdapter);
        Button btn = new Button(this);
        btn.setText(R.string.lbl_load_more);

        hooks_lv.addFooterView(btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMoreHooks();
            }
        });
    }

    public void loadMoreHooks() {
        showLoadingAnim();

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("seed", seed)
                .add("page", String.valueOf(page))
                .build();

        Request request = new Request.Builder()
                .url(getResources().getString(R.string.url_get_hooks))
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
                            displayError("No More Plot Hooks. Help everyone by submitting new plot hooks.");
                            return;
                        }

                        for (int i=0; i<r.length(); i++) {
                            hooksArray.add(new Hook(context, hooksArray.size(), r.getJSONObject(i)));
                        }

                        //Increment page value for server
                        page+=1;

                        HooksActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (firstLoad) {
                                    firstLoad=false;
                                    //Set the new data
                                    hooks_lv.setAdapter(hookArrayAdapter);
                                }
                                else {
                                    //Get scroll position of listview, so we can retain their position after loading more
                                    int firstVisibleItem = hooks_lv.getFirstVisiblePosition();
                                    int oldCount = hookArrayAdapter.getCount();
                                    View view = hooks_lv.getChildAt(0);
                                    int pos = (view == null ? 0 : view.getBottom());

                                    //Set the new data
                                    hooks_lv.setAdapter(hookArrayAdapter);

                                    //Set the listview position back to where they were
                                    hooks_lv.setSelectionFromTop(firstVisibleItem + hookArrayAdapter.getCount() - oldCount + 1, pos);
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
                        SerialHook serialHook = (SerialHook) data.getSerializableExtra("SERIALIZED_OBJ");
                        if (hooksArray != null && serialHook != null) {
                            hooksArray.get(serialHook.position).updateLocal(serialHook);
                            if (hookArrayAdapter != null) {
                                hookArrayAdapter.notifyDataSetChanged();
                            }
                        }
                        else {

                        }
                    }
                    catch (Exception e) { Log.e("HooksActivity", e.getMessage()); }
                }
                break;
            }
        }
    }
}

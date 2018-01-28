package com.mpvreeken.rpgcompanion.Items;

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

public class ItemsActivity extends RPGCActivity {

    ArrayList<Item> itemsArray = new ArrayList<>();
    ItemArrayAdapter itemArrayAdapter;
    ListView items_lv;

    private String seed; //random seed for server
    private int page; //pagination for server to get next set of maps

    private Boolean firstLoad=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        setupLoadingAnimTransparent();

        page = 0;
        Random rand = new Random();
        seed = String.valueOf(rand.nextInt(1000000));

        Button new_btn = findViewById(R.id.items_new_btn);

        new_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NewItemActivity.class);
                startActivity(intent);
            }
        });

        //Fetch items from db
        this.itemsArray = new ArrayList<>();
        this.itemArrayAdapter = new ItemArrayAdapter(this, itemsArray);
        this.items_lv = findViewById(R.id.items_lv);

        setupUI();

        loadMoreItems();
    }

    public void setupUI() {
        items_lv.setAdapter(itemArrayAdapter);
        Button btn = new Button(this);
        btn.setText(R.string.lbl_load_more);

        items_lv.addFooterView(btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMoreItems();
            }
        });
    }

    public void loadMoreItems() {
        showLoadingAnim();

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("seed", seed)
                .add("page", String.valueOf(page))
                .build();

        Request request = new Request.Builder()
                .url(getResources().getString(R.string.url_get_items))
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
                            displayError("No More Unique Items. Help everyone by submitting new unique items.");
                            return;
                        }

                        for (int i=0; i<r.length(); i++) {
                            itemsArray.add(new Item(context, itemsArray.size(), r.getJSONObject(i)));
                        }

                        //Increment page value for server
                        page+=1;

                        ItemsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (firstLoad) {
                                    firstLoad=false;
                                    //Set the new data
                                    items_lv.setAdapter(itemArrayAdapter);
                                }
                                else {
                                    //Get scroll position of listview, so we can retain their position after loading more
                                    int firstVisibleItem = items_lv.getFirstVisiblePosition();
                                    int oldCount = itemArrayAdapter.getCount();
                                    View view = items_lv.getChildAt(0);
                                    int pos = (view == null ? 0 : view.getBottom());

                                    //Set the new data
                                    items_lv.setAdapter(itemArrayAdapter);

                                    //Set the listview position back to where they were
                                    items_lv.setSelectionFromTop(firstVisibleItem + itemArrayAdapter.getCount() - oldCount + 1, pos);
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
                        SerialItem serialItem = (SerialItem) data.getSerializableExtra("SERIALIZED_OBJ");
                        if (itemsArray != null && serialItem != null) {
                            itemsArray.get(serialItem.position).updateLocal(serialItem);
                            if (itemArrayAdapter != null) {
                                itemArrayAdapter.notifyDataSetChanged();
                            }
                        }
                        else {

                        }
                    }
                    catch (Exception e) { Log.e("ItemsActivity", e.getMessage()); }
                }
                break;
            }
        }
    }
}

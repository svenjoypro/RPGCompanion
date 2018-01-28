package com.mpvreeken.rpgcompanion.Items;

import android.content.Intent;
import android.os.Bundle;
import com.mpvreeken.rpgcompanion.R;
import com.mpvreeken.rpgcompanion.RPGCActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewItemActivity extends RPGCActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        //TODO how to better handle this?
        if (!application.getLoggedIn()) {
            Toast.makeText(this, "You must be logged in to post a new Unique Item", Toast.LENGTH_LONG).show();
            //Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            //finish();
            //startActivity(intent);
        }


        Button cancel_btn = findViewById(R.id.new_item_cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        Button submit_btn = findViewById(R.id.new_item_submit_btn);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO check body text content, post to server
                EditText itemBody_et = findViewById(R.id.new_item_body_et);
                String body = itemBody_et.getText().toString();
                EditText itemTitle_et = findViewById(R.id.new_item_title_et);
                String title = itemTitle_et.getText().toString();

                if (body.length()==0) {
                    Toast.makeText(context, "Make sure you include a description for your unique item", Toast.LENGTH_LONG).show();
                    return;
                }
                if (title.length()==0) {
                    Toast.makeText(context, "Make sure you include a title for your unique item", Toast.LENGTH_LONG).show();
                    return;
                }

                EditText external_et = findViewById(R.id.new_item_external_et);
                String external = external_et.getText().toString();
                EditText image_et = findViewById(R.id.new_item_image_et);
                String image = image_et.getText().toString();

                if (external.length()>0) {
                    if (!external.substring(0, 7).equals("http://") && !external.substring(0,8).equals("https://")) {
                        external = "http://"+external;
                    }
                }

                if (image.length()>0) {
                    if (image.length()<5) {
                        Toast.makeText(context, "The Image URL must be a direct image link and end with .png, .jpg, .gif or similar", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!image.substring(0, 7).equals("http://") && !image.substring(0,8).equals("https://")) {
                        image = "http://"+image;
                    }
                    String suffix = image.substring(image.length()-4);
                    if (!suffix.equals(".jpg") && !suffix.equals(".png")) {
                        Toast.makeText(context, "The Image URL must be a direct image link and end with .png, .jpg, .gif or similar", Toast.LENGTH_LONG).show();
                        return;
                    }
                }


                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new FormBody.Builder()
                        .add("item_body", body)
                        .add("item_title", title)
                        .add("external_link", external)
                        .add("image_link", image)
                        .build();

                Request request = new Request.Builder()
                        .url(getResources().getString(R.string.url_submit_item))
                        .header("Authorization", "Bearer" + application.getToken())
                        .post(requestBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        //e.printStackTrace();
                        Log.e("NewItemActivity", "okhttp onFailure()");
                        onHttpCallbackFail("Unable to connect to server");
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            onUnsuccessfulResponse(response);
                        }
                        else {
                            try {
                                JSONObject all = new JSONObject(response.body().string());

                                if (all.has("error")) {
                                    Log.e("NewItemActivity", "Error: "+ all.getString("error"));
                                    onHttpCallbackFail(all.getString("error"));
                                }
                                else if (all.has("success") && all.has("id")) {
                                    Intent intent = new Intent(NewItemActivity.this, DisplayItemActivity.class);

                                    Bundle bundle = new Bundle();
                                    bundle.putInt("POST_ID", Integer.valueOf(all.getString("id")));
                                    intent.putExtras(bundle);
                                    finish();
                                    startActivity(intent);
                                }
                                else {
                                    //TODO
                                    //unknown error
                                    Log.e("NewPostActivity", "Unknown Error: "+ all.toString());
                                    onHttpCallbackFail("An unknown error has occurred. Please try again.");
                                }
                            }
                            catch (JSONException e) {
                                Log.e("NewItemActivity", e.getMessage());
                                onHttpCallbackFail("Parsing error. Please try again.");
                                //e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }
}


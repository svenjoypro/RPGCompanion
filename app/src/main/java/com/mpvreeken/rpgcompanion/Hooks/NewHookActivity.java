package com.mpvreeken.rpgcompanion.Hooks;

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

public class NewHookActivity extends RPGCActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_hook);

        //TODO how to better handle this?
        if (!application.getLoggedIn()) {
            Toast.makeText(this, "You must be logged in to post a new Hook", Toast.LENGTH_LONG).show();
            //Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            //finish();
            //startActivity(intent);
        }


        Button cancel_btn = findViewById(R.id.new_hook_cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        Button submit_btn = findViewById(R.id.new_hook_submit_btn);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //TODO check body text content, post to server
            EditText hookBody_et = findViewById(R.id.new_hook_body_et);
            String body = hookBody_et.getText().toString();
            EditText hookTitle_et = findViewById(R.id.new_hook_title_et);
            String title = hookTitle_et.getText().toString();

            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new FormBody.Builder()
                    .add("hook_body", body)
                    .add("hook_title", title)
                    .build();

            Request request = new Request.Builder()
                    .url(getResources().getString(R.string.url_submit_hook))
                    .header("Authorization", "Bearer" + application.getToken())
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    //e.printStackTrace();
                    Log.e("NewHookActivity", "okhttp onFailure()");
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
                                Log.e("NewHookActivity", "Error: "+ all.getString("error"));
                                onHttpCallbackFail(all.getString("error"));
                            }
                            else if (all.has("success") && all.has("id")) {
                                Intent intent = new Intent(NewHookActivity.this, DisplayHookActivity.class);

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
                            Log.e("NewHookActivity", e.getMessage());
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


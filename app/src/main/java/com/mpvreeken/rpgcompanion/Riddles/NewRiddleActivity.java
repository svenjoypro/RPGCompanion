package com.mpvreeken.rpgcompanion.Riddles;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mpvreeken.rpgcompanion.Hooks.DisplayHookActivity;
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

public class NewRiddleActivity extends RPGCActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_riddle);

        //TODO how to better handle this?
        if (!application.getLoggedIn()) {
            Toast.makeText(this, "You must be logged in to post a new Riddle", Toast.LENGTH_LONG).show();
            //Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            //finish();
            //startActivity(intent);
        }


        Button cancel_btn = findViewById(R.id.new_riddle_cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        Button submit_btn = findViewById(R.id.new_riddle_submit_btn);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO check body text content, post to server
                EditText riddleBody_et = findViewById(R.id.new_riddle_body_et);
                String body = riddleBody_et.getText().toString();
                EditText riddleAnswer_et = findViewById(R.id.new_riddle_answer_et);
                String answer = riddleAnswer_et.getText().toString();

                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new FormBody.Builder()
                        .add("riddle_body", body)
                        .add("riddle_answer", answer)
                        .build();

                Request request = new Request.Builder()
                        .url(getResources().getString(R.string.url_submit_riddle))
                        .header("Authorization", "Bearer" + application.getToken())
                        .post(requestBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        onHttpCallbackFail("Unable to connect to server");
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            onUnsuccessfulResponse(response);
                        }
                        else {
                            try {
                                JSONObject all = new JSONObject(response.body().string());

                                if (all.has("error")) {
                                    Log.e("NewRiddleActivity", "Error: "+ all.getString("error"));
                                    onHttpCallbackFail(all.getString("error"));
                                }
                                else if (all.has("success") && all.has("id")) {
                                    Intent intent = new Intent(NewRiddleActivity.this, DisplayRiddleActivity.class);

                                    Bundle bundle = new Bundle();
                                    bundle.putInt("POST_ID", Integer.valueOf(all.getString("id")));
                                    intent.putExtras(bundle);
                                    finish();
                                    startActivity(intent);
                                }
                                else {
                                    //TODO
                                    //unknown error
                                    Log.e("NewRiddleActivity", "Unknown Error: "+ all.toString());
                                    onHttpCallbackFail("An unknown error has occurred. Please try again.");
                                }
                            }
                            catch (JSONException e) {
                                Log.e("NewRiddleActivity", e.getMessage());
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


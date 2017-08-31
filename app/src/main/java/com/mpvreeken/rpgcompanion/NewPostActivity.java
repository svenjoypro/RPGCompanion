package com.mpvreeken.rpgcompanion;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mpvreeken.rpgcompanion.R;

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

public class NewPostActivity extends AppCompatActivity {

    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        //Set up back button to appear in action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();

        TextView title_tv = findViewById(R.id.post_title_tv);

        type = (String) bundle.get("type");
        switch(type) {
            case "hook":
                title_tv.setText("New Hook");
                break;
            case "encounter":
                title_tv.setText("New Encounter");
                break;
            case "adventure":
                title_tv.setText("New Adventure");
                break;
            case "puzzle":
                title_tv.setText("New Puzzle");
                break;
            case "riddle":
                title_tv.setText("New Riddle");
                break;
            default:
                //TODO show error?
                finish();
        }


        Button cancel_btn = findViewById(R.id.post_cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        Button submit_btn = findViewById(R.id.post_submit_btn);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO check body text content, post to server
                EditText postBody = findViewById(R.id.post_body_et);
                String body = postBody.getText().toString();

                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new FormBody.Builder()
                        .add("post_body", body)
                        .add("type", type)
                        .build();

                //.header("Authorization", "Bearer" + limelightApplication.getToken())
                Request request = new Request.Builder()
                        .url(getResources().getString(R.string.url_submit_post))
                        .post(requestBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        Log.e("NewPostActivity", "okhttp onFailure()");
                        httpCallbackFail("Unable to connect to server");
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            Log.e("NewPostActivity", "okhttp response failure");
                            httpCallbackFail("An unknown error has occurred. Please try again.");
                            throw new IOException("Unexpected code " + response);
                        }
                        else {
                            try {
                                JSONObject all = new JSONObject(response.body().string());

                                if (all.has("error")) {
                                    Log.e("NewPostActivity", "Error: "+ all.getString("error"));
                                    httpCallbackFail(all.getString("error"));
                                }
                                else if (all.has("msg")) {
                                    //success
                                    Log.e("NewPostActivity", "Success");
                                    //TODO

                                    //We can't update the UI on a background thread, so run on the UI thread
                                    NewPostActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d("NewPostActivity", "Post Successful");
                                            //After successful post
                                            //Intent returnIntent = new Intent();
                                            //returnIntent.putExtra("result",result);
                                            //setResult(Activity.RESULT_OK,returnIntent);
                                            //finish();
                                        }
                                    });
                                }
                                else {
                                    //TODO
                                    //unknown error
                                    Log.e("NewPostActivity", "Unknown Error: "+ all.toString());
                                    httpCallbackFail("An unknown error has occurred. Please try again.");
                                }
                            }
                            catch (JSONException e) {
                                Log.e("NewPostActivity", e.getMessage());
                                httpCallbackFail("Parsing error. Please try again.");
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });

    }


    private void httpCallbackFail(final String s) {
        //http error, show error via Toast message
        NewPostActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(NewPostActivity.this, s, Toast.LENGTH_LONG).show();
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

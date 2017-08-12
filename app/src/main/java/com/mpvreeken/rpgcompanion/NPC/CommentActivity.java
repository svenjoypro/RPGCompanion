package com.mpvreeken.rpgcompanion.NPC;

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

import com.mpvreeken.rpgcompanion.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommentActivity extends AppCompatActivity {

    String id, commentType;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        //Set up back button to appear in action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        id = (String) bundle.get("id");
        commentType = (String) bundle.get("commentType");
        switch(commentType) {
            case "hook":
                url = getResources().getString(R.string.url_submit_hook_comment)+id;
                break;
            case "encounter":
                url = getResources().getString(R.string.url_submit_encounter_comment)+id;
                break;
            case "puzzle":
                url = getResources().getString(R.string.url_submit_puzzle_comment)+id;
                break;
            default:
                //Invalid "commentType", finish intent
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
        }

        final EditText comment_body_edit = findViewById(R.id.post_body_input);

        Button post_btn = findViewById(R.id.post_submit_btn);
        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("comment", comment_body_edit.getText().toString())
                        .build();

                //.header("Authorization", "Bearer" + limelightApplication.getToken())
                Request request = new Request.Builder()
                        .url(url)
                        .method("POST", RequestBody.create(null, new byte[0]))
                        .post(requestBody)
                        .build();


                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //TODO
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            //TODO
                            throw new IOException("Unexpected code " + response);
                        }
                        else {
                            try {
                                JSONObject all = new JSONObject(response.body().string());

                                if (all.has("error")) {
                                    //error
                                    Log.e("CommentActivity", "Error: "+ all.getString("error"));
                                }
                                else if (all.has("msg")) {
                                    //success
                                    Log.e("CommentActivity", "Success");
                                    //TODO

                                    //We can't update the UI on a background thread, so run on the UI thread
                                    CommentActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //After successful post
                                            Intent returnIntent = new Intent();
                                            //returnIntent.putExtra("result",result);
                                            setResult(Activity.RESULT_OK,returnIntent);
                                            finish();
                                        }
                                    });

                                }
                                else {
                                    //unknown error
                                    Log.e("CommentActivity", "Unknown Error: "+ all.toString());
                                }
                            }
                            catch (JSONException e) {
                                Log.e("err", e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                });


            }
        });
        Button cancel_btn = findViewById(R.id.post_cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Invalid "commentType", finish intent
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });


    }

    private void submitComment() {

    }


    //Set click listener for back button in action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Invalid "commentType", finish intent
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

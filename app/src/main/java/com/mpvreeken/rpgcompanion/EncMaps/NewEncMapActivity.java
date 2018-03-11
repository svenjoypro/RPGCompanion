package com.mpvreeken.rpgcompanion.EncMaps;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mpvreeken.rpgcompanion.R;
import com.mpvreeken.rpgcompanion.RPGCActivity;
import com.mpvreeken.rpgcompanion.Widgets.MultiselectSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewEncMapActivity extends RPGCActivity {

    private EditText title_et, desc_et, link_et, img_et;
    private MultiselectSpinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_enc_map);

        setupLoadingAnim();


        spinner = findViewById(R.id.new_map_envs_spinner);

        spinner.setItems(EncMap.ENVS);

        title_et = findViewById(R.id.new_map_title_input);
        desc_et = findViewById(R.id.new_map_desc_input);
        link_et = findViewById(R.id.new_map_link_input);
        img_et = findViewById(R.id.new_map_img_input);

        Button cancel_btn = findViewById(R.id.new_map_cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button submit_btn = findViewById(R.id.new_map_submit_btn);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = title_et.getText().toString();
                String desc = desc_et.getText().toString();
                String link = link_et.getText().toString();
                String img = img_et.getText().toString();

                if (title.length()==0 || desc.length()==0 || link.length()==0 || img.length()==0) {
                    Toast.makeText(context, "Please fill out all form fields", Toast.LENGTH_LONG).show();
                    return;
                }

                if (img.length()<8) {
                    Toast.makeText(context, "The thumbnail image url must be a direct link to a jpg or png file", Toast.LENGTH_LONG).show();
                    return;
                }
                //Force link to begin with http if it doesn't already
                if (!img.substring(0, 7).equals("http://") && !img.substring(0,8).equals("https://")) {
                    img = "http://"+img;
                }
                String suffix = img.substring(img.length()-4);
                if (!suffix.equals(".jpg") && !suffix.equals(".png")) {
                    Toast.makeText(context, "The thumbnail image url must be a direct link to a jpg or png file", Toast.LENGTH_LONG).show();
                    return;
                }

                //Force link to begin with http if it doesn't already
                if (!link.substring(0, 7).equals("http://") && !link.substring(0,8).equals("https://")) {
                    link = "http://"+link;
                }

                String ar = "";
                for(int i:spinner.getSelectedIndicies()) {
                    ar+=i+",";
                }
                if (ar.length()==0) {
                    Toast.makeText(context, "Please select at least one environment", Toast.LENGTH_LONG).show();
                    return;
                }
                ar = ar.substring(0,ar.length()-1);

                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new FormBody.Builder()
                        .add("title", title)
                        .add("desc", desc)
                        .add("link", link)
                        .add("img", img)
                        .add("envs", ar)
                        .build();

                Request request = new Request.Builder()
                        .url(getResources().getString(R.string.url_submit_map))
                        .header("Authorization", "Bearer" + application.getToken())
                        .post(requestBody)
                        .build();

                showLoadingAnim();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        hideLoadingAnim();
                        onHttpCallbackFail("Unable to connect to server");
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        hideLoadingAnim();
                        if (!response.isSuccessful()) {
                            onUnsuccessfulResponse(response);
                        }
                        else {
                            try {
                                JSONObject all = new JSONObject(response.body().string());

                                if (all.has("error")) {
                                    Log.e("NewEncMapActivity", "Error: "+ all.getString("error"));
                                    onHttpCallbackFail(all.getString("error"));
                                }
                                else if (all.has("success") && all.has("id")) {
                                    Intent intent = new Intent(context, DisplayEncMapActivity.class);

                                    Bundle bundle = new Bundle();
                                    bundle.putInt("POST_ID", Integer.valueOf(all.getString("id")));
                                    intent.putExtras(bundle);
                                    finish();
                                    startActivity(intent);
                                }
                                else {
                                    //TODO
                                    //unknown error
                                    Log.e("NewEncMapActivity", "Unknown Error: "+ all.toString());
                                    onHttpCallbackFail("An unknown error has occurred. Please try again.");
                                }
                            }
                            catch (JSONException e) {
                                Log.e("NewEncMapActivity", e.getMessage());
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

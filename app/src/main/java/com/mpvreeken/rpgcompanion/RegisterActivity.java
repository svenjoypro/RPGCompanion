package com.mpvreeken.rpgcompanion;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

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

public class RegisterActivity extends RPGCActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setupLoadingAnim();


        Button register_btn = findViewById(R.id.register_register_btn);
        register_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText username_et = findViewById(R.id.register_username_et);
                EditText email_et = findViewById(R.id.register_email_et);
                EditText password_et = findViewById(R.id.register_password_et);
                EditText pconfirm_et = findViewById(R.id.register_pconfirm_et);

                if (!password_et.getText().toString().equals(pconfirm_et.getText().toString())) {
                    showError("Passwords don't match");
                    return;
                }

                showLoadingAnim();


                final RequestBody postBody = new FormBody.Builder()
                        .add("username", username_et.getText().toString())
                        .add("email", email_et.getText().toString())
                        .add("password", password_et.getText().toString())
                        .build();

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(getResources().getString(R.string.url_register))
                        .post(postBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        hideLoadingAnim();
                        onHttpResponseError(e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        hideLoadingAnim();
                        if (!response.isSuccessful()) {
                            onHttpResponseError("Unexpected error occurred");
                            throw new IOException("Unexpected code " + response);
                        }
                        else {
                            /*
                             *    possible responses:
                             * { "error":"<Validator Error>" } - username is already taken, etc
                             * { "error":"could_not_create_user" }
                             * { "success":"Confirmation Email Sent" }
                             *
                             */
                            try {
                                JSONObject r = new JSONObject(response.body().string());

                                if (r.has("success")) {
                                    //Success
                                    RegisterActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() { showSuccess(); }
                                    });
                                }
                                else {
                                    //Error
                                    final String error = r.has("error") ? r.getString("error") : "unknown_error";
                                    onHttpResponseError(error);
                                }
                            }
                            catch (JSONException e) {
                                onHttpResponseError(e.getMessage());
                            }
                        }
                    }

                });

            }
        });
    }

    public void showSuccess() {
        //Change layout to "email sent" message
        ViewFlipper vf = findViewById(R.id.register_viewFlipper);
        vf.showNext();
    }

    public void showError(String error) {
        Toast.makeText(getBaseContext(), error, Toast.LENGTH_LONG).show();
        TextView errors_tv = findViewById(R.id.register_errors_tv);
        errors_tv.setText(error);
    }
}

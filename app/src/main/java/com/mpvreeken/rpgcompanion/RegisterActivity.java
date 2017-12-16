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
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends RPGCActivity {

    private TextView errors_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setupLoadingAnim();

        errors_tv = findViewById(R.id.register_errors_tv);

        Button register_btn = findViewById(R.id.register_register_btn);
        register_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                errors_tv.setText("");
                EditText username_et = findViewById(R.id.register_username_et);
                EditText email_et = findViewById(R.id.register_email_et);
                EditText password_et = findViewById(R.id.register_password_et);
                EditText pconfirm_et = findViewById(R.id.register_pconfirm_et);
                String username = username_et.getText().toString();
                String password = password_et.getText().toString();
                String email = email_et.getText().toString();

                if (password.length() < 6) {
                    showError("Your password must be a minimum of 6 characters (this is the only restriction)");
                    return;
                }
                if (!password.equals(pconfirm_et.getText().toString())) {
                    showError("Passwords don't match");
                    return;
                }
                if (!username.matches("^[a-zA-Z0-9._-]{3,}$")) {
                    showError("Your username may only contain letters, numbers, periods, dashes, and underscores, and must be at least 3 characters long");
                    return;
                }
                if (username.equals("admin") || username.equals("administrator")) {
                    showError("You are not an admin :)");
                    return;
                }
                if (email.indexOf("@")==-1 || email.indexOf(".")==-1) {
                    showError("Please enter a valid email address");
                    return;
                }

                showLoadingAnim();


                final RequestBody postBody = new FormBody.Builder()
                        .add("username", username)
                        .add("email", email)
                        .add("password", password)
                        .build();

                OkHttpClient.Builder b = new OkHttpClient.Builder();
                b.connectTimeout(15, TimeUnit.SECONDS);
                b.readTimeout(15, TimeUnit.SECONDS);
                b.writeTimeout(15, TimeUnit.SECONDS);

                OkHttpClient client = b.build();

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
                            onUnsuccessfulResponse(response);
                        }
                        else {
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
        errors_tv.setText(error);
    }
}

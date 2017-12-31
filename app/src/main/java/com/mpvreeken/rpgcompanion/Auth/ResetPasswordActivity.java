package com.mpvreeken.rpgcompanion.Auth;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mpvreeken.rpgcompanion.R;
import com.mpvreeken.rpgcompanion.RPGCActivity;

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

public class ResetPasswordActivity extends RPGCActivity {

    private EditText email_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        setupLoadingAnim();

        email_et = findViewById(R.id.reset_email_et);

        Button reset_btn = findViewById(R.id.reset_send_btn);
        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_et.getText().toString();

                if (!email.contains("@") || !email.contains(".")) {
                    Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_LONG).show();
                    return;
                }

                showLoadingAnim();

                final RequestBody postBody = new FormBody.Builder()
                        .add("email", email)
                        .build();

                OkHttpClient.Builder b = new OkHttpClient.Builder();
                b.connectTimeout(15, TimeUnit.SECONDS);
                b.readTimeout(15, TimeUnit.SECONDS);
                b.writeTimeout(15, TimeUnit.SECONDS);

                OkHttpClient client = b.build();

                Request request = new Request.Builder()
                        .url(getResources().getString(R.string.url_create_pw_reset))
                        .post(postBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        hideLoadingAnim();
                        onHttpResponseError("Unable to connect to server");
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
                                    onResponseSuccess();
                                }
                                else {
                                    //Error
                                    String error = r.has("error") ? r.getString("error") : "unknown_error";
                                    String readable_error;
                                    switch (error) {
                                        case "invalid_email":
                                            readable_error = "The email you entered is incorrect";
                                            break;
                                        case "could_not_send_email":
                                            readable_error = "An unknown error has occurred. Please try again.";
                                            break;
                                        default:
                                            readable_error = "An unknown error has occurred. Please try again.";
                                            break;
                                    }
                                    onResponseError(readable_error);
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

    public void onResponseSuccess() {
        ResetPasswordActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), "Please check your email for reset instructions", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    public void onResponseError(final String error) {
        ResetPasswordActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }
}

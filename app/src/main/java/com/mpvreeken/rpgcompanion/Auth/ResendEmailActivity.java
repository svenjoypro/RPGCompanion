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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ResendEmailActivity extends RPGCActivity {

    EditText email_et;
    Button resend_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resend_email);

        setupLoadingAnim();

        email_et = findViewById(R.id.resend_confirmation_email_et);
        resend_btn = findViewById(R.id.resend_confirmation_btn);
        resend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingAnim();

                String email = email_et.getText().toString();
                if (email.length()==0 || email.indexOf("@")==-1 || email.indexOf(".")==-1) {
                    Toast.makeText(ResendEmailActivity.this, "Please enter a valid email address", Toast.LENGTH_LONG).show();
                    hideLoadingAnim();
                    return;
                }

                final RequestBody postBody = new FormBody.Builder()
                        .add("email", email)
                        .build();

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(getResources().getString(R.string.url_resend_email))
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
                                    ResendEmailActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                        Toast.makeText(ResendEmailActivity.this, "Email Sent", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                else {
                                    //Error
                                    String error = r.has("error") ? r.getString("error") : "unknown_error";
                                    final String readable_error;
                                    switch (error) {
                                        case "invalid_email":
                                            readable_error = "The email you entered isn't waiting to be confirmed";
                                            break;
                                        default:
                                            readable_error = "An unknown error has occurred. Please try again.";
                                            break;
                                    }
                                    ResendEmailActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ResendEmailActivity.this, readable_error, Toast.LENGTH_LONG).show();
                                        }
                                    });
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
}

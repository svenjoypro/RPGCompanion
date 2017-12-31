package com.mpvreeken.rpgcompanion.Auth;

import android.content.Intent;
import android.net.Uri;
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

public class ResetPasswordDeepLinkActivity extends RPGCActivity {

    EditText pw_et, conf_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_deep_link);
        setupLoadingAnim();

        Intent intent = getIntent();
        String intentUrl = intent.getDataString();
        Uri data = Uri.parse(intentUrl);
        final String token = data.getQueryParameter("t");

        if (token.length()!=20) {
            Toast.makeText(ResetPasswordDeepLinkActivity.this, "Invalid reset token", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Button submit_btn = findViewById(R.id.reset_dl_submit_btn);
        pw_et = findViewById(R.id.reset_dl_password_et);
        conf_et = findViewById(R.id.reset_dl_confirm_et);

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = pw_et.getText().toString();
                String conf = conf_et.getText().toString();

                if (!password.equals(conf)) {
                    Toast.makeText(ResetPasswordDeepLinkActivity.this, "Your passwords don't match", Toast.LENGTH_LONG).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(ResetPasswordDeepLinkActivity.this, "Your password must be at least 6 characters", Toast.LENGTH_LONG).show();
                    return;
                }

                showLoadingAnim();

                final RequestBody postBody = new FormBody.Builder()
                        .add("t", token)
                        .add("password", password)
                        .build();

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(getResources().getString(R.string.url_reset_pw))
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
                        if (!response.isSuccessful()) { onUnsuccessfulResponse(response); }
                        else {
                            try {
                                JSONObject r = new JSONObject(response.body().string());
                                if (r.has("success")) { onResponseSuccess(); }
                                else { onHttpResponseError("An unknown error has occurred. Please try again."); }
                            }
                            catch (JSONException e) { onHttpResponseError(e.getMessage()); }
                        }
                    }
                });
            }
        });
    }

    public void onResponseSuccess() {
        ResetPasswordDeepLinkActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), "Your password has been reset. You may now log in with your new password.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }
}

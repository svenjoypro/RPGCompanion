package com.mpvreeken.rpgcompanion;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ConfirmEmailActivity extends RPGCActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_email);

        Intent intent = getIntent();
        Uri data = Uri.parse(intent.getStringExtra("intentUrl"));
        String confirm = data.getQueryParameter("c");

        String url = getResources().getString(R.string.url_confirm_email)+"?c="+confirm+"&app=true";

        //TODO create an actual layout with an editText and populate
        //it with the code, so if something goes wrong they can manually try again.

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { onHttpCallbackFail(e.getMessage()); }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) { onUnsuccessfulResponse(response); }
                else {
                    try {
                        JSONObject r = new JSONObject(response.body().string());

                        if (r.has("success") && r.has("jwt")) {
                            final String token = r.getString("jwt");
                            //Success
                            ConfirmEmailActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() { loginAndRedirect(token); }
                            });
                        }
                        else {
                            //Error
                            final String error = r.has("error") ? r.getString("error") : "unknown_error";
                            Log.d("LOGIN", error);
                            ConfirmEmailActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() { showError(error); }
                            });
                        }
                    }
                    catch (JSONException e) { e.printStackTrace(); }
                }
            }
        });
    }

    private void loginAndRedirect(String token) {
        application.login(token);
        Toast.makeText(getBaseContext(), "Account verified successfully", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        finish();
        startActivity(intent);
    }

    public void showError(String error) {
        Toast.makeText(getBaseContext(), error, Toast.LENGTH_LONG).show();
    }

}

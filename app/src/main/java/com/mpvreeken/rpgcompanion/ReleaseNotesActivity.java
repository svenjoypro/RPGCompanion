package com.mpvreeken.rpgcompanion;

import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ReleaseNotesActivity extends RPGCActivity {

    private TextView notes_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_notes);
        setupLoadingAnim();

        notes_tv = findViewById(R.id.release_notes_tv);

        getReleaseNotes();
    }

    private void getReleaseNotes() {
        showLoadingAnim();
        String url = getResources().getString(R.string.url_get_release_notes);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                hideLoadingAnim();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                hideLoadingAnim();
                if (!response.isSuccessful()) {
                    onUnsuccessfulResponse(response);
                }
                else {
                    JSONObject r = null;
                    String o = "";
                    try {
                        r = new JSONObject(response.body().string());
                        o = r.getString("msg");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (o.length()>0) {
                        //We can't update the UI on a background thread, so run on the UI thread
                        final String output = o;
                        ReleaseNotesActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                notes_tv.setText(output);
                            }
                        });
                    }
                }
            }
        });
    }
}

package com.mpvreeken.rpgcompanion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mpvreeken.rpgcompanion.Hooks.HooksActivity;
import com.mpvreeken.rpgcompanion.Riddles.RiddlesActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
	
	/**
	 *	TODO
	 *      Add Spell summaries?
	 *
	 *
	 */
	private TextView webalert_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //checkLoggedIn();

        ImageButton hooks_btn = findViewById(R.id.main_hooks_button);
        ImageButton encounters_btn = findViewById(R.id.main_encounters_button);
        ImageButton puzzles_btn = findViewById(R.id.main_puzzles_button);
        ImageButton npc_btn = findViewById(R.id.main_npc_button);
        ImageButton adventures_btn = findViewById(R.id.main_adventures_button);
        ImageButton riddles_btn = findViewById(R.id.main_riddles_button);
        ImageButton misc_btn = findViewById(R.id.main_misc_button);

        webalert_tv = findViewById(R.id.main_web_alert_tv);


        View.OnClickListener buttonHandler = new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent;
                switch(v.getId()) {
                    case R.id.main_hooks_button:
                        intent = new Intent(v.getContext(), HooksActivity.class);
                        break;
                    case R.id.main_encounters_button:
                        intent = new Intent(v.getContext(), EncountersActivity.class);
                        break;
                    case R.id.main_puzzles_button:
                        intent = new Intent(v.getContext(), PuzzlesActivity.class);
                        break;
                    case R.id.main_npc_button:
                        intent = new Intent(v.getContext(), NPCsActivity.class);
                        break;
                    case R.id.main_adventures_button:
                        intent = new Intent(v.getContext(), AdventuresActivity.class);
                        break;
                    case R.id.main_riddles_button:
                        intent = new Intent(v.getContext(), RiddlesActivity.class);
                        break;
                    case R.id.main_misc_button:
                        intent = new Intent(v.getContext(), MiscActivity.class);
                        break;
                    default:
                        intent = new Intent(v.getContext(), NPCsActivity.class);
                }
                //intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
            }
        };

        hooks_btn.setOnClickListener(buttonHandler);
        //encounters_btn.setOnClickListener(buttonHandler);
        //puzzles_btn.setOnClickListener(buttonHandler);
        npc_btn.setOnClickListener(buttonHandler);
        riddles_btn.setOnClickListener(buttonHandler);
        adventures_btn.setOnClickListener(buttonHandler);
        misc_btn.setOnClickListener(buttonHandler);

        getWebAlert();
    }

    private void getWebAlert() {
        String hooks_url = getResources().getString(R.string.url_get_webalert);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(hooks_url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { e.printStackTrace(); }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) { throw new IOException("Unexpected code " + response); }
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
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                webalert_tv.setText(output);
                                webalert_tv.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
            }
        });
    }
}

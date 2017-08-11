package com.mpvreeken.rpgcompanion;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mpvreeken.rpgcompanion.Classes.Hook;
import com.mpvreeken.rpgcompanion.Classes.HookArrayAdapter;
import com.mpvreeken.rpgcompanion.Classes.HookComment;
import com.mpvreeken.rpgcompanion.Classes.HookCommentsArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DisplayHookActivity extends AppCompatActivity {

    ArrayList<HookComment> commentsArray = new ArrayList<>();
    HookCommentsArrayAdapter commentArrayAdapter;
    LinearLayout comments_layout;
    Hook hook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_hook);

        //Set up back button to appear in action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        final Bundle movie = intent.getExtras();
        String hook_id = (String) movie.get("hook_id");

        //Fetch hooks from db
        this.commentsArray = new ArrayList<>();
        this.commentArrayAdapter = new HookCommentsArrayAdapter(this, commentsArray);
        //this.comments_lv = findViewById(R.id.hook_comments_lv);

        this.comments_layout = findViewById(R.id.hook_comments_linear_layout);

        String hooks_url = getResources().getString(R.string.url_get_hook)+hook_id;

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
                    try {
                        JSONObject all = new JSONObject(response.body().string());

                        JSONObject h = all.getJSONObject("hook");
                        hook = new Hook(h.getString("id"),
                                h.getString("title"),
                                h.getString("username"),
                                h.getString("description"),
                                h.getString("votes"),
                                h.getString("created_at")
                        );

                        JSONArray cs = all.getJSONArray("comments");
                        for (int i=0; i<cs.length(); i++) {
                            commentsArray.add(
                                    new HookComment(cs.getJSONObject(i).getString("id"),
                                            cs.getJSONObject(i).getString("hook_id"),
                                            cs.getJSONObject(i).getString("username"),
                                            cs.getJSONObject(i).getString("comment"),
                                            cs.getJSONObject(i).getString("votes"),
                                            cs.getJSONObject(i).getString("created_at")
                                    )
                            );
                        }


                        //We can't update the UI on a background thread, so run on the UI thread
                        DisplayHookActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setupUI();
                            }
                        });
                    }
                    catch (JSONException e) {
                        Log.e("err", e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void setupUI() {
        TextView votes_tv = findViewById(R.id.hook_details_votes_tv);
        votes_tv.setText(String.valueOf(hook.getVotes()));
        TextView title_tv = findViewById(R.id.hook_details_title_tv);
        title_tv.setText(String.valueOf(hook.getTitle()));
        TextView description_tv = findViewById(R.id.hook_details_description_tv);
        description_tv.setText(String.valueOf(hook.getDescription()));

        //comments_lv.setAdapter(commentArrayAdapter);

        for (int i=0; i<commentsArray.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.comment_layout, null);
            TextView comment_tv = (TextView) view.findViewById(R.id.comment_layout_comment_tv);


            comment_tv.setText(commentsArray.get(i).getComment());
            comments_layout.addView(view);
        }

        /*
        comments_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Intent intent = new Intent(parent.getContext(), DisplayHookActivity.class);

                Bundle bundle = new Bundle();
                //bundle.putSerializable("MOVIE_OBJ", (Serializable) moviesArray.get(position));

                //intent.putExtras(bundle);
                intent.putExtra("hook_id", hooksArray.get(position).getId());
                startActivity(intent);
            }
        });
        */
    }


    //Set click listener for back button in action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

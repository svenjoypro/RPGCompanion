package com.mpvreeken.rpgcompanion;

import android.app.Activity;
import android.content.Context;
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
import com.mpvreeken.rpgcompanion.NPC.CommentActivity;

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
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_hook);

        //Set up back button to appear in action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        context = this.getBaseContext();

        Intent intent = getIntent();
        final Bundle hookBundle = intent.getExtras();
        hook = (Hook) hookBundle.getSerializable("HOOK_OBJ");

        Button upvote_btn = (Button) findViewById(R.id.hook_details_vote_up_btn);
        Button downvote_btn = (Button) findViewById(R.id.hook_details_vote_down_btn);
        Button comment_btn = (Button) findViewById(R.id.hook_details_comment_btn);
        View.OnClickListener buttonHandler = new View.OnClickListener() {
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.hook_details_vote_up_btn:
                        upvote();
                        break;
                    case R.id.hook_details_vote_down_btn:
                        downvote();
                        break;
                    case R.id.hook_details_comment_btn:
                        displayCommentInput();
                        break;
                    default:
                }
            }
        };

        upvote_btn.setOnClickListener(buttonHandler);
        downvote_btn.setOnClickListener(buttonHandler);
        comment_btn.setOnClickListener(buttonHandler);




        //Fetch hooks from db
        this.commentsArray = new ArrayList<>();
        this.commentArrayAdapter = new HookCommentsArrayAdapter(this, commentsArray);
        //this.comments_lv = findViewById(R.id.hook_comments_lv);

        this.comments_layout = findViewById(R.id.hook_comments_linear_layout);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(getResources().getString(R.string.url_get_hook)+hook.getId())
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
                        /*
                        JSONObject h = all.getJSONObject("hook");
                        hook = new Hook(h.getString("id"),
                                h.getString("title"),
                                h.getString("username"),
                                h.getString("description"),
                                h.getString("votes"),
                                h.getString("created_at")
                        );
                        */
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
                        Log.e("DisplayHookActivity", e.getMessage());
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

    private void downvote() {
        String url = getResources().getString(R.string.url_downvote)+hook.getId();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //TODO
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    //TODO
                    throw new IOException("Unexpected code " + response);
                }
                else {
                    try {
                        JSONObject all = new JSONObject(response.body().string());

                        if (all.has("error")) {
                            //error
                            Log.e("DisplayHookActivity", "Error: "+ all.getString("error"));
                        }
                        else if (all.has("msg")) {
                            //success
                            Log.e("DisplayHookActivity", "Success");
                            //TODO
                            //  Update downvote count, change ui color or something to indicate upvote or revert back after initial "expected" success
                        }
                        else {
                            //unknown error
                            Log.e("DisplayHookActivity", "Unknown Error: "+ all.toString());
                        }
                        /*
                        //We can't update the UI on a background thread, so run on the UI thread
                        DisplayHookActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setupUI();
                            }
                        });
                        */
                    }
                    catch (JSONException e) {
                        Log.e("err", e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void upvote() {
        String url = getResources().getString(R.string.url_upvote)+hook.getId();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //TODO
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    //TODO
                    throw new IOException("Unexpected code " + response);
                }
                else {
                    try {
                        JSONObject all = new JSONObject(response.body().string());

                        if (all.has("error")) {
                            //error
                            Log.e("DisplayHookActivity", "Error: "+ all.getString("error"));
                        }
                        else if (all.has("msg")) {
                            //success
                            Log.e("DisplayHookActivity", "Success");
                            //TODO
                            //  Update upvote count, change ui color or something to indicate upvote or revert back after initial "expected" success
                        }
                        else {
                            //unknown error
                            Log.e("DisplayHookActivity", "Unknown Error: "+ all.toString());
                        }
                        /*
                        //We can't update the UI on a background thread, so run on the UI thread
                        DisplayHookActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setupUI();
                            }
                        });
                        */
                    }
                    catch (JSONException e) {
                        Log.e("err", e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void displayCommentInput() {
        Intent intent = new Intent(context, CommentActivity.class);

        intent.putExtra("id", hook.getId());
        intent.putExtra("commentType", "hook");

        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Should only happen after comment is submitted or cancelled
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                //String result=data.getStringExtra("result");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //user pressed back btn
            }
        }
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

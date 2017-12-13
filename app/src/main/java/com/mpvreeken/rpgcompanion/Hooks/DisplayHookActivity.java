package com.mpvreeken.rpgcompanion.Hooks;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;

import com.mpvreeken.rpgcompanion.CommentActivity;
import com.mpvreeken.rpgcompanion.R;
import com.mpvreeken.rpgcompanion.RPGCActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DisplayHookActivity extends RPGCActivity {

    ArrayList<HookComment> commentsArray = new ArrayList<>();
    HookCommentsArrayAdapter commentArrayAdapter;
    LinearLayout comments_layout;
    Hook hook;
    String hook_id;
    private Button upvote_btn, downvote_btn;
    private TextView votes_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_hook);
        setupLoadingAnim();

        Intent intent = getIntent();
        final Bundle hookBundle = intent.getExtras();
        assert hookBundle != null;
        hook_id = hookBundle.getString("HOOK_ID");

        upvote_btn = findViewById(R.id.hook_details_vote_up_btn);
        downvote_btn = findViewById(R.id.hook_details_vote_down_btn);
        Button comment_btn = findViewById(R.id.hook_details_comment_btn);
        View.OnClickListener buttonHandler = new View.OnClickListener() {
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.hook_details_vote_up_btn:
                        vote(true);
                        break;
                    case R.id.hook_details_vote_down_btn:
                        vote(false);
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

        showLoadingAnim();

        this.comments_layout = findViewById(R.id.hook_comments_linear_layout);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(getResources().getString(R.string.url_get_hook)+hook_id)
                .header("Authorization", "Bearer" + application.getToken())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                hideLoadingAnim();
                displayError("Could not connect to server. Please try again");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                hideLoadingAnim();
                if (!response.isSuccessful()) {
                    onUnsuccessfulResponse(response);
                }
                else {
                    try {
                        JSONObject all = new JSONObject(response.body().string());

                        JSONObject h = all.getJSONObject("hook");
                        hook = new Hook(h.getString("id"),
                                h.getString("title"),
                                h.getString("username"),
                                h.getInt("user_id"),
                                h.getString("description"),
                                h.getInt("upvotes"),
                                h.getInt("downvotes"),
                                h.getInt("voted"),
                                h.getString("created_at"),
                                h.getString("updated_at")
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
                        displayError("An unknown error occurred. Please try again");
                        Log.e("DisplayHookActivity", e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void setupUI() {
        votes_tv = findViewById(R.id.hook_details_votes_tv);
        votes_tv.setText(String.valueOf(hook.getCalculatedVotes()));
        TextView title_tv = findViewById(R.id.hook_details_title_tv);
        title_tv.setText(String.valueOf(hook.getTitle()));
        TextView description_tv = findViewById(R.id.hook_details_description_tv);
        description_tv.setText(String.valueOf(hook.getDescription()));


        if (hook.getVoted() == 1) {
            upvote_btn.setTextColor(getResources().getColor(R.color.textVoted));
        }
        else if (hook.getVoted() == 0) {
            downvote_btn.setTextColor(getResources().getColor(R.color.textVoted));
        }

        //comments_lv.setAdapter(commentArrayAdapter);

        for (int i=0; i<commentsArray.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.comment_layout, null);
            TextView comment_tv = view.findViewById(R.id.comment_layout_comment_tv);


            comment_tv.setText(commentsArray.get(i).getComment());
            comments_layout.addView(view);
        }
    }

    private void vote(boolean v) {
        if (v && hook.getVoted()==1) { return; }
        if (!v && hook.getVoted()==0) { return; }

        if (v) {
            upvote_btn.setTextColor(getResources().getColor(R.color.textVoted));
            downvote_btn.setTextColor(getResources().getColor(R.color.textButtonPrimary));
        }
        else {
            downvote_btn.setTextColor(getResources().getColor(R.color.textVoted));
            upvote_btn.setTextColor(getResources().getColor(R.color.textButtonPrimary));
        }

        final String vote = v ? "1" : "0";
        final RequestBody postBody = new FormBody.Builder()
                .add("type", "hook")
                .add("id", String.valueOf(hook_id))
                .add("vote", vote)
                .build();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getResources().getString(R.string.url_vote))
                .header("Authorization", "Bearer" + application.getToken())
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                displayError("Could not connect to server. Please try again");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                boolean success=false;
                if (!response.isSuccessful()) {
                    onUnsuccessfulResponse(response);
                }
                else {
                    try {
                        JSONObject all = new JSONObject(response.body().string());

                        if (all.has("success")) {
                            //success
                            Log.e("DisplayHookActivity", "Success");
                            success=true;
                            onVoteSuccess(vote);
                        }
                        else {
                            Log.e("DisplayHookActivity", "Unknown Error: "+ all.toString());
                            displayError("An unknown error occurred. Please try again");
                        }
                    }
                    catch (JSONException e) {
                        Log.e("err", e.getMessage());
                        displayError("An unknown error occurred. Please try again");
                        e.printStackTrace();
                    }
                }
                if (!success) {
                    resetButtons();
                }
            }
        });
    }

    private void resetButtons() {
        DisplayHookActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (hook.getVoted() == 0) {
                    upvote_btn.setTextColor(getResources().getColor(R.color.textButtonPrimary));
                    downvote_btn.setTextColor(getResources().getColor(R.color.textVoted));
                } else if (hook.getVoted() == 1) {
                    upvote_btn.setTextColor(getResources().getColor(R.color.textVoted));
                    downvote_btn.setTextColor(getResources().getColor(R.color.textButtonPrimary));
                } else {
                    upvote_btn.setTextColor(getResources().getColor(R.color.textButtonPrimary));
                    downvote_btn.setTextColor(getResources().getColor(R.color.textButtonPrimary));
                }
            }
        });
    }

    private void onVoteSuccess(final String v) {
        DisplayHookActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (v.equals("1")) {
                    //upvote
                    if (hook.getVoted()==0) {
                        hook.updateUpvotes(1);
                        hook.updateDownvotes(-1);
                    }
                    else if (hook.getVoted()==-1) {
                        hook.updateUpvotes(1);
                    }
                }
                else {
                    //downvote
                    if (hook.getVoted()==1) {
                        hook.updateDownvotes(1);
                        hook.updateUpvotes(-1);
                    }
                    else if (hook.getVoted()==-1) {
                        hook.updateDownvotes(1);
                    }
                }
                votes_tv.setText(String.valueOf(hook.getCalculatedVotes()));
                hook.setVoted(Integer.valueOf(v));
            }
        });
    }

    private void displayError(final String s) {
        DisplayHookActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, s, Toast.LENGTH_LONG).show();
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
            if (resultCode == Activity.RESULT_OK){
                //String result=data.getStringExtra("result");
                //TODO Add user's new comment to the list of comments
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //user pressed back btn
            }
        }
    }


}

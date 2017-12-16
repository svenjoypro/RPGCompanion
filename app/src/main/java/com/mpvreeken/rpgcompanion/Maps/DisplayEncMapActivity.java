package com.mpvreeken.rpgcompanion.Maps;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mpvreeken.rpgcompanion.CommentActivity;
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

public class DisplayEncMapActivity extends RPGCActivity {

    private String map_id;
    //private ArrayList<MapComment> commentsArray = new ArrayList<>();
    //private CommentsArrayAdapter commentArrayAdapter;
    //private LinearLayout comments_layout;
    private EncMap map;
    private Button upvote_btn;
    private Button downvote_btn;
    private TextView votes_tv;
    private ImageView img;
    private ConstraintLayout popup_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_enc_map);

        setupLoadingAnim();

        map_id = getIntent().getExtras().getString("MAP_ID");

        upvote_btn = findViewById(R.id.map_details_vote_up_btn);
        downvote_btn = findViewById(R.id.map_details_vote_down_btn);
        Button comment_btn = findViewById(R.id.map_details_comment_btn);
        View.OnClickListener buttonHandler = new View.OnClickListener() {
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.map_details_vote_up_btn:
                        vote(true);
                        break;
                    case R.id.map_details_vote_down_btn:
                        vote(false);
                        break;
                    case R.id.map_details_comment_btn:
                        displayCommentInput();
                        break;
                    default:
                }
            }
        };

        upvote_btn.setOnClickListener(buttonHandler);
        downvote_btn.setOnClickListener(buttonHandler);
        comment_btn.setOnClickListener(buttonHandler);

        img = findViewById(R.id.map_details_img);


        /*
        commentsArray = new ArrayList<>();
        commentArrayAdapter = new MapCommentsArrayAdapter(this, commentsArray);
        comments_lv = findViewById(R.id.map_comments_lv);
        comments_layout = findViewById(R.id.map_comments_linear_layout);
        */


        //Fetch maps from db
        getMapData();
    }

    private void getMapData() {
        showLoadingAnim();

        Glide.with(context).load(context.getResources().getString(R.string.url_map_thumbs)+map_id+".jpg").into(img);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(getResources().getString(R.string.url_get_map)+map_id)
                .header("Authorization", "Bearer" + application.getToken())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                hideLoadingAnim();
                displayError("Could not connect to server. Please try again");
                e.printStackTrace();
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

                        JSONObject m = all.getJSONObject("map");
                        map = new EncMap(m.getString("id"),
                                m.getString("title"),
                                m.getString("username"),
                                m.getInt("user_id"),
                                m.getString("description"),
                                m.getString("link"),
                                m.getInt("upvotes"),
                                m.getInt("downvotes"),
                                m.getInt("voted"),
                                m.getString("created_at"),
                                m.getString("updated_at")
                        );
                        /*
                        JSONArray cs = all.getJSONArray("comments");
                        for (int i=0; i<cs.length(); i++) {
                            commentsArray.add(
                                    new Comment(cs.getJSONObject(i).getString("id"),
                                            cs.getJSONObject(i).getString("map_id"),
                                            cs.getJSONObject(i).getString("username"),
                                            cs.getJSONObject(i).getString("comment"),
                                            cs.getJSONObject(i).getString("votes"),
                                            cs.getJSONObject(i).getString("created_at")
                                    )
                            );
                        }
                        */

                        //We can't update the UI on a background thread, so run on the UI thread
                        DisplayEncMapActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setupUI();
                            }
                        });
                    }
                    catch (JSONException e) {
                        displayError("An unknown error occurred. Please try again");
                        Log.e("DisplayEncMapActivity", e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void setupUI() {
        votes_tv = findViewById(R.id.map_details_votes_tv);
        votes_tv.setText(String.valueOf(map.getCalculatedVotes()));
        TextView title_tv = findViewById(R.id.map_details_title_tv);
        title_tv.setText(String.valueOf(map.getTitle()));
        TextView description_tv = findViewById(R.id.map_details_description_tv);
        description_tv.setText(String.valueOf(map.getDescription()));


        if (map.getVoted() == 1) {
            upvote_btn.setTextColor(getResources().getColor(R.color.textVoted));
        }
        else if (map.getVoted() == 0) {
            downvote_btn.setTextColor(getResources().getColor(R.color.textVoted));
        }

        Button view_map_btn = findViewById(R.id.map_details_view_btn);
        view_map_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup_view.setVisibility(View.VISIBLE);
            }
        });

        popup_view = findViewById(R.id.map_details_popup);

        Button popup_cancel_btn = findViewById(R.id.map_details_popup_cancel_btn);
        popup_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup_view.setVisibility(View.GONE);
            }
        });
        TextView link = findViewById(R.id.map_details_popup_link_tv);
        link.setText("URL:\n"+map.getLink());
        link.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("MapURL", map.getLink());
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "URL copied to clipboard", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        Button popup_go_btn = findViewById(R.id.map_details_popup_go_btn);
        popup_go_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(map.getLink()));
                startActivity(i);
            }
        });

        /*
        //comments_lv.setAdapter(commentArrayAdapter);

        for (int i=0; i<commentsArray.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.comment_layout, null);
            TextView comment_tv = (TextView) view.findViewById(R.id.comment_layout_comment_tv);


            comment_tv.setText(commentsArray.get(i).getComment());
            comments_layout.addView(view);
        }
        */
    }


    private void vote(boolean v) {
        if (v && map.getVoted()==1) { return; }
        if (!v && map.getVoted()==0) { return; }

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
                .add("type", "map")
                .add("id", String.valueOf(map_id))
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
                            Log.e("DisplayEncMapActivity", "Success");
                            success=true;
                            onVoteSuccess(vote);
                        }
                        else {
                            Log.e("DisplayEncMapActivity", "Unknown Error: "+ all.toString());
                            displayError("An unknown error occurred. Please try again");
                        }
                    }
                    catch (JSONException e) {
                        Log.e("DisplayEncMapActivity", e.getMessage());
                        displayError("An unknown error occurred. Please try again");
                        e.printStackTrace();
                    }
                }
                if (!success) {
                    //if something failed, then reset up/down vote buttons to their original state
                    resetButtons();
                }
            }
        });
    }

    private void resetButtons() {
        DisplayEncMapActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (map.getVoted() == 0) {
                    upvote_btn.setTextColor(getResources().getColor(R.color.textButtonPrimary));
                    downvote_btn.setTextColor(getResources().getColor(R.color.textVoted));
                } else if (map.getVoted() == 1) {
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
        DisplayEncMapActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (v.equals("1")) {
                    //upvote
                    if (map.getVoted()==0) {
                        map.updateUpvotes(1);
                        map.updateDownvotes(-1);
                    }
                    else if (map.getVoted()==-1) {
                        map.updateUpvotes(1);
                    }
                }
                else {
                    //downvote
                    if (map.getVoted()==1) {
                        map.updateDownvotes(1);
                        map.updateUpvotes(-1);
                    }
                    else if (map.getVoted()==-1) {
                        map.updateDownvotes(1);
                    }
                }
                votes_tv.setText(String.valueOf(map.getCalculatedVotes()));
                map.setVoted(Integer.valueOf(v));
            }
        });
    }

    private void displayError(final String s) {
        DisplayEncMapActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, s, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayCommentInput() {
        Intent intent = new Intent(context, CommentActivity.class);

        intent.putExtra("id", map.getId());
        intent.putExtra("commentType", "map");

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

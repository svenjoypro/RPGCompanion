package com.mpvreeken.rpgcompanion.EncMaps;

import android.app.Activity;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mpvreeken.rpgcompanion.Classes.PostObjectBase;
import com.mpvreeken.rpgcompanion.ExternalLinkAlertActivity;
import com.mpvreeken.rpgcompanion.R;
import com.mpvreeken.rpgcompanion.RPGCActivity;

import org.json.JSONArray;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DisplayEncMapActivity extends RPGCActivity {

    private ArrayList<EncMapComment> commentsArray = new ArrayList<>();
    private EncMapCommentsArrayAdapter commentArrayAdapter;
    private LinearLayout comments_layout;
    private EncMap map;
    private ImageButton upvote_btn, downvote_btn, img_btn;
    private ImageView bookmark_iv;
    private TextView votes_tv;
    private SerialEncMap serialized;
    private Button external_btn;
    private TextView title_tv, description_tv, user_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_enc_map);
        setupLoadingAnim();

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        assert bundle != null;

        if (bundle.containsKey("SERIALIZED_OBJ")) {
            serialized = (SerialEncMap) bundle.getSerializable("SERIALIZED_OBJ");
        }
        else if (bundle.containsKey("POST_ID")) {
            serialized = new SerialEncMap(bundle.getInt("POST_ID"));
        }
        else {
            Toast.makeText(application, "An unknown error has occurred. Please try again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        upvote_btn = findViewById(R.id.map_details_vote_up_btn);
        upvote_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upvote_btn.setImageResource(R.mipmap.arrow_upvote);
                downvote_btn.setImageResource(R.mipmap.arrow_neutral);
                map.upvote();
            }
        });
        downvote_btn = findViewById(R.id.map_details_vote_down_btn);
        downvote_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downvote_btn.setImageResource(R.mipmap.arrow_downvote);
                upvote_btn.setImageResource(R.mipmap.arrow_neutral);
                map.downvote();
            }
        });

        //Fetch maps from db
        this.commentsArray = new ArrayList<>();
        this.commentArrayAdapter = new EncMapCommentsArrayAdapter(this, commentsArray);
        //this.comments_lv = findViewById(R.id.map_comments_lv);

        showLoadingAnim();

        this.comments_layout = findViewById(R.id.map_comments_linear_layout);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(getResources().getString(R.string.url_get_map)+ serialized.id)
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

                        JSONObject h = all.getJSONObject("map");
                        map = new EncMap(context, serialized.position, h);
                        JSONArray cs = all.getJSONArray("comments");
                        for (int i=0; i<cs.length(); i++) {
                            commentsArray.add(
                                    new EncMapComment(cs.getJSONObject(i).getString("id"),
                                            cs.getJSONObject(i).getString("map_id"),
                                            cs.getJSONObject(i).getString("username"),
                                            cs.getJSONObject(i).getString("comment"),
                                            cs.getJSONObject(i).getString("votes"),
                                            cs.getJSONObject(i).getString("created_at")
                                    )
                            );
                        }


                        //We can't update the UI on a background thread, so run on the UI thread
                        DisplayEncMapActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setupUI();
                            }
                        });
                    }
                    catch (Exception e) {
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
        title_tv = findViewById(R.id.map_details_title_tv);
        title_tv.setText(map.getTitle());
        description_tv = findViewById(R.id.map_details_description_tv);
        description_tv.setText(map.getDescription());
        user_tv = findViewById(R.id.map_details_user_tv);
        user_tv.setText(map.getDetailSubtitle());

        if (map.getVoted() == 1) {
            upvote_btn.setImageResource(R.mipmap.arrow_upvote);
        }
        else if (map.getVoted() == 0) {
            downvote_btn.setImageResource(R.mipmap.arrow_downvote);
        }

        if (map.isMine()) {
            ImageView edit_iv = findViewById(R.id.map_details_edit_iv);
            edit_iv.setVisibility(View.VISIBLE);
            edit_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditEncMapActivity.map=map;
                    Intent intent = new Intent(context, EditEncMapActivity.class);
                    startActivityForResult(intent, 1);
                }
            });
        }
        else {
            bookmark_iv = findViewById(R.id.map_details_bookmark_iv);
            resetBookmark();
            bookmark_iv.setVisibility(View.VISIBLE);
            bookmark_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (map.isBookmarked()) {
                        bookmark_iv.setImageResource(R.mipmap.ic_bookmark);
                        map.bookmark(false);
                    }
                    else {
                        bookmark_iv.setImageResource(R.mipmap.ic_bookmarked);
                        map.bookmark(true);
                    }
                }
            });
        }

        external_btn = findViewById(R.id.map_details_external_btn);
        external_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExternalLink();
            }
        });

        img_btn = findViewById(R.id.map_details_img_btn);
        //TODO how to fail gracefully with this
        try {
            Glide.with(context).load(context.getResources().getString(R.string.url_map_thumbs) + map.getId() + ".jpg").into(img_btn);
        }
        catch (Exception e) {
            Toast.makeText(application, "Error loading map image", Toast.LENGTH_SHORT).show();
        }
        img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExternalLink();
            }
        });


        //comments_lv.setAdapter(commentArrayAdapter);

        for (int i=0; i<commentsArray.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.comment_layout, null);
            TextView comment_tv = view.findViewById(R.id.comment_layout_comment_tv);


            comment_tv.setText(commentsArray.get(i).getComment());
            comments_layout.addView(view);
        }

        map.setVoteEventListener(new PostObjectBase.VoteEventListener() {
            @Override
            public void onVoteFail(final String msg) {
                DisplayEncMapActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!msg.isEmpty()) {
                            Toast.makeText(application, msg, Toast.LENGTH_SHORT).show();
                        }
                        resetButtons();
                    }
                });
            }

            @Override
            public void onVoteSuccess() {
                DisplayEncMapActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        votes_tv.setText(String.valueOf(map.getCalculatedVotes()));
                    }
                });
                setOnResult();
            }

            @Override
            public void onBookmarkFail(final String msg) {
                DisplayEncMapActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bookmark_iv.setImageResource(R.mipmap.ic_bookmarked);
                        resetBookmark();
                        if (!msg.isEmpty()) {
                            Toast.makeText(application, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void resetBookmark() {
        if (map.isBookmarked()) {
            bookmark_iv.setImageResource(R.mipmap.ic_bookmarked);
        }
        else {
            bookmark_iv.setImageResource(R.mipmap.ic_bookmark);
        }
    }

    private void showExternalLink() {
        String link = map.getExternalLink();
        if (link.length() == 0) {
            Toast.makeText(application, "No External Link was provided for this map", Toast.LENGTH_LONG).show();
            return;
        }
        if (application.getExternalLinkAlert()) {
            Intent intent = new Intent(getApplicationContext(), ExternalLinkAlertActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("LINK", link);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        else {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(link));
            startActivity(i);
        }
    }

    private void setOnResult() {
        if (serialized.position!=-1) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("SERIALIZED_OBJ", map.getSerialized());
            setResult(Activity.RESULT_OK, resultIntent);
        }
    }

    private void resetButtons() {
        DisplayEncMapActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (map.getVoted() == 0) {
                    upvote_btn.setImageResource(R.mipmap.arrow_neutral);
                    downvote_btn.setImageResource(R.mipmap.arrow_downvote);
                } else if (map.getVoted() == 1) {
                    upvote_btn.setImageResource(R.mipmap.arrow_upvote);
                    downvote_btn.setImageResource(R.mipmap.arrow_neutral);
                } else {
                    upvote_btn.setImageResource(R.mipmap.arrow_neutral);
                    downvote_btn.setImageResource(R.mipmap.arrow_neutral);
                }
            }
        });
    }

    private void updateAfterEdit() {
        title_tv.setText(map.getTitle());
        description_tv.setText(map.getDescription());
        user_tv.setText(map.getDetailSubtitle());
        //Glide.with(context).load(context.getResources().getString(R.string.url_map_thumbs)+map.getId()+".jpg").into(img_btn);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (1) : {
                if (resultCode == Activity.RESULT_OK) {
                    if (data.hasExtra("DELETED")) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("DELETED", true);
                        resultIntent.putExtra("SERIALIZED_OBJ", map.getSerialized());
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                    else {
                        updateAfterEdit();
                        setOnResult();
                    }
                }
                break;
            }
        }
    }

}

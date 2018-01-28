package com.mpvreeken.rpgcompanion.Items;

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

public class DisplayItemActivity extends RPGCActivity {

    private ArrayList<ItemComment> commentsArray = new ArrayList<>();
    private ItemCommentsArrayAdapter commentArrayAdapter;
    private LinearLayout comments_layout;
    private Item item;
    private ImageButton upvote_btn, downvote_btn;
    private TextView votes_tv;
    private SerialItem serialized;
    private Button external_btn;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_item);
        setupLoadingAnim();

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        assert bundle != null;

        if (bundle.containsKey("SERIALIZED_OBJ")) {
            serialized = (SerialItem) bundle.getSerializable("SERIALIZED_OBJ");
        }
        else if (bundle.containsKey("POST_ID")) {
            serialized = new SerialItem(bundle.getInt("POST_ID"));
        }
        else {
            Toast.makeText(application, "An unknown error has occurred. Please try again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        upvote_btn = findViewById(R.id.item_details_vote_up_btn);
        upvote_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upvote_btn.setImageResource(R.mipmap.arrow_upvote);
                downvote_btn.setImageResource(R.mipmap.arrow_neutral);
                item.upvote();
            }
        });
        downvote_btn = findViewById(R.id.item_details_vote_down_btn);
        downvote_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downvote_btn.setImageResource(R.mipmap.arrow_downvote);
                upvote_btn.setImageResource(R.mipmap.arrow_neutral);
                item.downvote();
            }
        });

        //Fetch items from db
        this.commentsArray = new ArrayList<>();
        this.commentArrayAdapter = new ItemCommentsArrayAdapter(this, commentsArray);
        //this.comments_lv = findViewById(R.id.item_comments_lv);

        showLoadingAnim();

        this.comments_layout = findViewById(R.id.item_comments_linear_layout);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(getResources().getString(R.string.url_get_item)+ serialized.id)
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

                        JSONObject h = all.getJSONObject("item");
                        item = new Item(context, serialized.position, h);

                        JSONArray cs = all.getJSONArray("comments");
                        for (int i=0; i<cs.length(); i++) {
                            commentsArray.add(
                                    new ItemComment(cs.getJSONObject(i).getString("id"),
                                            cs.getJSONObject(i).getString("item_id"),
                                            cs.getJSONObject(i).getString("username"),
                                            cs.getJSONObject(i).getString("comment"),
                                            cs.getJSONObject(i).getString("votes"),
                                            cs.getJSONObject(i).getString("created_at")
                                    )
                            );
                        }


                        //We can't update the UI on a background thread, so run on the UI thread
                        DisplayItemActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setupUI();
                            }
                        });
                    }
                    catch (Exception e) {
                        displayError("An unknown error occurred. Please try again");
                        Log.e("DisplayItemActivity", e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void setupUI() {
        votes_tv = findViewById(R.id.item_details_votes_tv);
        votes_tv.setText(String.valueOf(item.getCalculatedVotes()));
        TextView title_tv = findViewById(R.id.item_details_title_tv);
        title_tv.setText(String.valueOf(item.getTitle()));
        TextView description_tv = findViewById(R.id.item_details_description_tv);
        description_tv.setText(String.valueOf(item.getDescription()));
        TextView user_tv = findViewById(R.id.item_details_user_tv);
        user_tv.setText(item.getDetailSubtitle());

        if (item.getVoted() == 1) {
            upvote_btn.setImageResource(R.mipmap.arrow_upvote);
        }
        else if (item.getVoted() == 0) {
            downvote_btn.setImageResource(R.mipmap.arrow_downvote);
        }

        if (item.isMine()) {

        }

        external_btn = findViewById(R.id.item_details_external_btn);
        if (item.getExternalLink().length()>0) {
            external_btn.setVisibility(View.VISIBLE);
            external_btn = findViewById(R.id.item_details_external_btn);
            external_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showExternalLink();
                }
            });
        }

        img = findViewById(R.id.item_details_img);
        String img_url = item.getImageLink();
        if (img_url.length()>0) {
            img.setVisibility(View.VISIBLE);
            Glide.with(context).load(item.getImageLink()).into(img);
        }
        //comments_lv.setAdapter(commentArrayAdapter);

        for (int i=0; i<commentsArray.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.comment_layout, null);
            TextView comment_tv = view.findViewById(R.id.comment_layout_comment_tv);


            comment_tv.setText(commentsArray.get(i).getComment());
            comments_layout.addView(view);
        }

        item.setEventListener(new Item.EventListener() {
            @Override
            public void onVoteFail() {
                DisplayItemActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resetButtons();
                    }
                });
            }

            @Override
            public void onVoteSuccess() {
                DisplayItemActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        votes_tv.setText(String.valueOf(item.getCalculatedVotes()));
                    }
                });
                setOnResult();
            }

            @Override
            public void onUpdatePostFail() {
                DisplayItemActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

            @Override
            public void onUpdatePostSuccess() {
                DisplayItemActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });
    }

    private void showExternalLink() {
        String link = item.getExternalLink();
        if (link.length() == 0) {
            Toast.makeText(application, "No External Link was provided for this Unique Item", Toast.LENGTH_LONG).show();
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
            resultIntent.putExtra("SERIALIZED_OBJ", item.getSerialized());
            setResult(Activity.RESULT_OK, resultIntent);
        }
    }

    private void resetButtons() {
        DisplayItemActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (item.getVoted() == 0) {
                    upvote_btn.setImageResource(R.mipmap.arrow_neutral);
                    downvote_btn.setImageResource(R.mipmap.arrow_downvote);
                } else if (item.getVoted() == 1) {
                    upvote_btn.setImageResource(R.mipmap.arrow_upvote);
                    downvote_btn.setImageResource(R.mipmap.arrow_neutral);
                } else {
                    upvote_btn.setImageResource(R.mipmap.arrow_neutral);
                    downvote_btn.setImageResource(R.mipmap.arrow_neutral);
                }
            }
        });
    }

}

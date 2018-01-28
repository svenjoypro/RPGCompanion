package com.mpvreeken.rpgcompanion.Classes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.mpvreeken.rpgcompanion.R;
import com.mpvreeken.rpgcompanion.RPGCActivity;
import com.mpvreeken.rpgcompanion.RPGCApplication;
import org.json.JSONObject;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Sven on 1/22/2018.
 * Extended by Hook, Item, Puzzle, Riddle, and EncMap
 */

public class PostObjectBase {

    protected Context context;
    protected RPGCApplication application;
    protected RPGCActivity activity;
    protected String title, user, description;
    protected int id, position, user_id, upvotes, downvotes, voted;
    protected String created_at, updated_at;
    protected String submissionType;
    protected String externalLink, imageLink;
    protected String riddle, answer;

    private EventListener eventListener;
    public interface EventListener {
        void onVoteFail();
        void onVoteSuccess();
        void onUpdatePostFail();
        void onUpdatePostSuccess();
    }
    public void setEventListener(EventListener listener) {
        eventListener = listener;
    }

    public Boolean isMine() {
        return application.getMyID() == id;
    }

    public int getId() {return id; }

    public String getTitle() {
        return title;
    }

    public String getUser() {
        return user;
    }

    public String getDescription() {
        return description;
    }

    public int getUpvotes() { return upvotes; }
    public int getDownvotes() { return downvotes; }
    public int getVoted() { return voted; }

    public String getExternalLink() {
        return externalLink != null ? externalLink : "";
    }

    public String getImageLink() {
        return imageLink != null ? imageLink : "";
    }

    public String getRiddle() {
        return riddle != null ? riddle : "";
    }
    public String getAnswer() {
        return answer != null ? answer : "";
    }

    public int getCalculatedVotes() { return upvotes-downvotes; }

    public String getListItemVotes() {
        return String.valueOf(getCalculatedVotes());
        //return "User Votes: " + getCalculatedVotes() + " | +" + upvotes + ", -" + downvotes;
    }

    public String getDetailSubtitle() {
        return user + " - " + getCreatedAt();
    }

    public String getCreatedAt() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date convertedDate = new Date();
        try { convertedDate = dateFormat.parse(created_at); }
        catch (ParseException e) { return ""; }
        SimpleDateFormat s = new SimpleDateFormat("MM/dd/yyyy");
        return s.format(convertedDate);
    }
    public String getUpdatedAt() {
        return updated_at;
    }

    public void setVoted(int i) { voted=i; }

    public void upvote() { vote(true); }
    public void downvote() { vote(false); }

    protected void vote(boolean v) {
        if (v && voted==1) { return; }
        if (!v && voted==0) { return; }

        final int vote = v ? 1 : 0;
        final RequestBody postBody = new FormBody.Builder()
                .add("type", submissionType)
                .add("id", String.valueOf(id))
                .add("vote", String.valueOf(vote))
                .build();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(context.getResources().getString(R.string.url_vote))
                .header("Authorization", "Bearer" + application.getToken())
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.displayError("Could not connect to server. Please try again");
                eventListener.onVoteFail();
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    activity.onUnsuccessfulResponse(response);
                    eventListener.onVoteFail();
                }
                else {
                    try {
                        JSONObject all = new JSONObject(response.body().string());

                        if (all.has("success")) {
                            //success
                            if (vote==1) {
                                upvotes+=1;
                                if (voted==0) {
                                    downvotes-=1;
                                }
                            }
                            if (vote==0) {
                                downvotes+=1;
                                if (voted==1) {
                                    upvotes-=1;
                                }
                            }
                            voted = vote;
                            eventListener.onVoteSuccess();
                        }
                        else {
                            Log.e("PostObjectBase.vote()", "Unknown Error: "+ all.toString());
                            eventListener.onVoteFail();
                            activity.displayError("An unknown error occurred. Please try again");
                        }
                    }
                    catch (Exception e) {
                        Log.e("err", e.getMessage());
                        eventListener.onVoteFail();
                        activity.displayError("An unknown error occurred. Please try again");
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    protected void updatePost(RequestBody rb) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(context.getResources().getString(R.string.url_update_post))
                .header("Authorization", "Bearer" + application.getToken())
                .post(rb)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.displayError("Could not connect to server. Please try again");
                eventListener.onUpdatePostFail();
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    activity.onUnsuccessfulResponse(response);
                }
                else {
                    try {
                        JSONObject all = new JSONObject(response.body().string());

                        if (all.has("success")) {
                            //success
                            eventListener.onUpdatePostSuccess();

                            return;
                        }
                        else {
                            Log.e("PostObjectBase.vote()", "Unknown Error: "+ all.toString());
                            activity.displayError("An unknown error occurred. Please try again");
                        }
                    }
                    catch (Exception e) {
                        Log.e("err", e.getMessage());
                        activity.displayError("An unknown error occurred. Please try again");
                        e.printStackTrace();
                    }
                }
                eventListener.onUpdatePostFail();
            }
        });
    }
}

package com.mpvreeken.rpgcompanion.Riddles;

import android.content.Context;

import com.mpvreeken.rpgcompanion.Classes.PostObjectBase;
import com.mpvreeken.rpgcompanion.RPGCActivity;
import com.mpvreeken.rpgcompanion.RPGCApplication;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Sven on 7/4/2017.
 */

public class Riddle extends PostObjectBase {

    public Riddle(Context context, int position, JSONObject r) throws Exception {
        this.context=context;
        this.submissionType="riddle";
        this.activity = (RPGCActivity) context;
        this.application = (RPGCApplication) context.getApplicationContext();
        this.position=position;
        this.id=r.getInt("id");
        this.user_id=r.getInt("user_id");
        this.answer=r.getString("answer");
        this.user=r.getString("username");
        this.riddle=r.getString("riddle");
        this.upvotes=r.getInt("upvotes");
        this.downvotes=r.getInt("downvotes");
        this.voted=r.getInt("voted"); // -1=didn't vote, 0=downvoted, 1=upvoted
        this.created_at=r.getString("created_at");
        this.updated_at=r.getString("updated_at");

    }

    public void updatePostOnServer() {
        final RequestBody postBody = new FormBody.Builder()
                .add("type", submissionType)
                .add("id", String.valueOf(id))
                .add("riddle", riddle)
                .add("answer", answer)
                .build();

        updatePost(postBody);
    }

    public SerialRiddle getSerialized() {
        return new SerialRiddle(position, id, riddle, answer, upvotes, downvotes, voted);
    }

    public void updateLocal(SerialRiddle h) {
        this.upvotes=h.upvotes;
        this.downvotes=h.downvotes;
        this.voted=h.voted;
        this.riddle=h.riddle;
        this.answer=h.answer;
    }
}

package com.mpvreeken.rpgcompanion.Items;

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

public class Item extends PostObjectBase {


    public Item(Context context, int position, JSONObject r) throws Exception {
        this.context=context;
        this.submissionType="item";
        this.activity = (RPGCActivity) context;
        this.application = (RPGCApplication) context.getApplicationContext();
        this.position=position;
        this.id=r.getInt("id");
        this.user_id=r.getInt("user_id");
        this.title=r.getString("title");
        this.user=r.getString("username");
        this.description=r.getString("description");
        this.externalLink=r.getString("external_link");
        this.imageLink=r.getString("image_link");
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
                .add("title", title)
                .add("desc", description)
                .add("external_link", externalLink)
                .add("image_link", imageLink)
                .build();

        updatePost(postBody);
    }

    public SerialItem getSerialized() {
        return new SerialItem(position, id, title, description, externalLink, imageLink, upvotes, downvotes, voted);
    }

    public void updateLocal(SerialItem h) {
        this.upvotes=h.upvotes;
        this.downvotes=h.downvotes;
        this.voted=h.voted;
        this.title=h.title;
        this.description=h.description;
        this.externalLink=h.externalLink;
        this.imageLink=h.imageLink;
    }
}

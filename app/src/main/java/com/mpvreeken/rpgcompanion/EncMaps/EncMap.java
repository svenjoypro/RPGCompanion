package com.mpvreeken.rpgcompanion.EncMaps;

import android.content.Context;
import android.util.Log;

import com.mpvreeken.rpgcompanion.Classes.PostObjectBase;
import com.mpvreeken.rpgcompanion.RPGCActivity;
import com.mpvreeken.rpgcompanion.RPGCApplication;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Sven on 12/9/2017.
 */

public class EncMap extends PostObjectBase {

    public static final List<String> ENVS = Arrays.asList("Abyss/Nine Hells","Air/Sky Ship","Cave","City/Urban","Desert","Dungeon","Extraplanar","Feywild","Forest","House/Mansion","Island","Jungle","Megadungeon","Mountain","Other","Ruins","Sewer","Shadowfell","Ship","Stronghold/Castle/Tower","Swamp","Temple","Town/Village","Underdark","Underwater","Wilderness");

    public EncMap(Context context, int position, JSONObject r) throws Exception {
        this.context=context;
        this.submissionType="map";
        this.activity = (RPGCActivity) context;
        this.application = (RPGCApplication) context.getApplicationContext();
        this.position=position;

        this.id=r.getInt("id");
        this.user_id=r.getInt("user_id");
        this.title=r.getString("title");
        this.user=r.getString("username");
        this.description=r.getString("description");
        //TODO - server uses "link" instead of the standard "external_link", this should eventually be changed
        this.externalLink=r.getString("link");
        this.upvotes=r.getInt("upvotes");
        this.downvotes=r.getInt("downvotes");
        this.voted=r.getInt("voted"); // -1=didn't vote, 0=downvoted, 1=upvoted
        int b = r.has("bookmarked") ? r.getInt("bookmarked") : 0;
        this.bookmarked = b==1;
        this.created_at=r.getString("created_at");
        this.updated_at=r.getString("updated_at");

        if (r.has("envs")) {
            List<String> l = Arrays.asList(r.getString("envs").split(","));
            this.environments = new ArrayList<>();
            for (int i=0; i<l.size(); i++) {
                this.environments.add(Integer.valueOf(l.get(i)));
            }
        }
        else {
            this.environments = new ArrayList<>();
        }
    }

    public void updatePostOnServer() {
        final RequestBody postBody = new FormBody.Builder()
                .add("type", submissionType)
                .add("id", String.valueOf(id))
                .add("title", title)
                .add("description", description)
                .add("link", externalLink)
                .add("envs", strigifyEnvironments())
                .build();

        updatePost(postBody);
    }

    public String strigifyEnvironments() {
        String s = "";
        for (int i=0; i<environments.size(); i++) {
            s+=environments.get(i)+",";
        }
        return s.substring(0, s.length()-1);
    }

    public SerialEncMap getSerialized() {
        return new SerialEncMap(position, id, title, description, externalLink, upvotes, downvotes, voted);
    }

    public void updateLocal(SerialEncMap h) {
        this.upvotes=h.upvotes;
        this.downvotes=h.downvotes;
        this.voted=h.voted;
        this.title=h.title;
        this.description=h.description;
        this.externalLink=h.externalLink;
    }


}

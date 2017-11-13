package com.mpvreeken.rpgcompanion.Hooks;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by Sven on 7/4/2017.
 */

public class Hook implements Serializable {

    private String id;
    private String title;
    private String user;
    private String description;
    private Integer user_id, upvotes, downvotes, voted;
    private String created_at, updated_at;

    public Hook(String title, String description) {
        this.id="-1";
        this.user_id=0;
        this.title=title;
        this.user="";
        this.description=description;
        this.upvotes=0;
        this.downvotes=0;
        this.created_at="";
        this.updated_at="";
    }

    public Hook(String id, String title, String user, Integer user_id, String description, Integer upvotes, Integer downvotes, Integer voted, String created_at, String updated_at) {
        this.id=id;
        this.user_id=user_id;
        this.title=title;
        this.user=user;
        this.description=description;
        this.upvotes=upvotes;
        this.downvotes=downvotes;
        this.voted=voted; // -1=didn't vote, 0=downvoted, 1=upvoted
        //todo convert to readable time
        this.created_at=created_at;
        this.updated_at=updated_at;
    }

    public String getId() {return id; }

    public String getTitle() {
        return title;
    }

    public String getUser() {
        return user;
    }

    public String getDescription() {
        return description;
    }

    public Integer getUpvotes() { return upvotes; }
    public Integer getDownvotes() { return downvotes; }
    public Integer getVoted() { return voted; }

    public Integer getCalculatedVotes() { return upvotes-downvotes; }

    public String getListItemVotes() {
        return "User Votes: " + getCalculatedVotes() + " | +" + upvotes + ", -" + downvotes;
    }

    public String getCreatedAt() {
        return created_at;
    }
    public String getUpdatedAt() {
        return updated_at;
    }

    public void updateUpvotes(int i) { upvotes+=i; }
    public void updateDownvotes(int i) { downvotes+=i; }
    public void setVoted(int i) { voted=i; }

}

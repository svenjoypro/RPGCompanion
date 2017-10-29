package com.mpvreeken.rpgcompanion.Classes;

import java.io.Serializable;

/**
 * Created by Sven on 7/4/2017.
 */

public class Hook implements Serializable {

    private String id;
    private String title;
    private String user;
    private String description;
    private Integer upvotes, downvotes, voted;
    private String date;

    public Hook(String title, String description) {
        this.id="-1";
        this.title=title;
        this.user="";
        this.description=description;
        this.upvotes=0;
        this.downvotes=0;
        this.date="";
    }

    public Hook(String id, String title, String user, String description, Integer upvotes, Integer downvotes, Integer voted, String timestamp) {

        this.id=id;
        this.title=title;
        this.user=user;
        this.description=description;
        this.upvotes=upvotes;
        this.downvotes=downvotes;
        this.voted=voted; // -1=didn't vote, 0=downvoted, 1=upvoted
        //todo convert to readable time
        this.date=timestamp;
    }

    public String getId() { return id; }

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
    public Integer getMyVote() { return voted; }

    public Integer getCalculatedVotes() { return upvotes-downvotes; }

    public String getListItemVotes() {
        return "User Votes: " + getCalculatedVotes() + " | +" + upvotes + ", -" + downvotes;
    }

    public String getDate() {
        return date;
    }

}

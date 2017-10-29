package com.mpvreeken.rpgcompanion.Riddles;

import java.io.Serializable;

/**
 * Created by Sven on 7/4/2017.
 */

public class Riddle implements Serializable {

    private String id;
    private String riddle, answer;
    private String user;
    private Integer upvotes, downvotes, voted;
    private String date;

    public Riddle(String riddle, String answer) {
        this.id="-1";
        this.riddle=riddle;
        this.user="";
        this.answer=answer;
        this.upvotes=0;
        this.downvotes=0;
        this.date="";
    }

    public Riddle(String id, String user, String riddle, String answer, Integer upvotes, Integer downvotes, Integer voted, String timestamp) {

        this.id=id;
        this.riddle=riddle;
        this.user=user;
        this.answer=answer;
        this.upvotes=upvotes;
        this.downvotes=downvotes;
        this.voted=voted; // -1=didn't vote, 0=downvoted, 1=upvoted
        //todo convert to readable time
        this.date=timestamp;
    }

    public String getId() { return id; }

    public String getRiddle() {
        return riddle;
    }

    public String getUser() {
        return user;
    }

    public String getAnswer() {
        return answer;
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

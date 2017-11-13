package com.mpvreeken.rpgcompanion.Riddles;

import java.io.Serializable;

/**
 * Created by Sven on 7/4/2017.
 */

public class Riddle implements Serializable {

    private Integer id, user_id;
    private String username, riddle, answer;
    private Integer upvotes, downvotes, voted;
    private String created_at, updated_at;

    public Riddle(Integer id, String username, Integer user_id, String riddle, String answer, Integer upvotes, Integer downvotes, Integer voted, String created_at, String updated_at) {

        this.id=id;
        this.username=username;
        this.user_id=user_id;
        this.riddle=riddle;
        this.answer=answer;
        this.upvotes=upvotes;
        this.downvotes=downvotes;
        this.voted=voted; // -1=didn't vote, 0=downvoted, 1=upvoted
        //todo convert to readable time
        this.created_at=created_at;
        this.updated_at=updated_at;

    }


    public Integer getCalculatedVotes() { return upvotes-downvotes; }

    public String getListItemVotes() {
        return "User Votes: " + getCalculatedVotes() + " | +" + upvotes + ", -" + downvotes;
    }

    public Integer getId() {return id;}
    public Integer getUser_id() {return user_id;}
    public String getUsername() {return username;}
    public String getRiddle() {return riddle;}
    public String getAnswer() {return answer;}
    public Integer getUpvotes() {return upvotes;}
    public Integer getDownvotes() {return downvotes;}
    public Integer getVoted() {return voted;}
    public String getCreated_at() {return created_at;}
    public String getUpdated_at() {return updated_at;}

    public void updateUpvotes(int i) { upvotes+=i; }
    public void updateDownvotes(int i) { downvotes+=i; }
    public void setVoted(int i) { voted=i; }
}

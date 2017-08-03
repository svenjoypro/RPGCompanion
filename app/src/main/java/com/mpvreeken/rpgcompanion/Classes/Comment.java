package com.mpvreeken.rpgcompanion.Classes;

/**
 * Created by Sven on 7/4/2017.
 */

public class Comment {

    private Integer id;
    private Integer parent_id;
    private String user;
    private String comment;
    private Integer votes;
    private String date;


    public Comment(Integer id, Integer parent_id, String user, String comment, Integer votes, String date) {

        this.id=id;
        this.parent_id=parent_id;
        this.user=user;
        this.comment=comment;
        this.votes=votes;
        //todo convert to readable time
        this.date=date;
    }

    public Integer getId() { return id; }
    public Integer getParent_id() { return parent_id; }
    public String getUser() { return user; }
    public String getComment() { return comment; }
    public Integer getVotes() { return votes; }
    public String getDate() { return date; }
}

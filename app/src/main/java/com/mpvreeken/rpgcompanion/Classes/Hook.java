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
    private Integer votes;
    private String date;

    public Hook(String title, String description) {
        this.id="-1";
        this.title=title;
        this.user="";
        this.description=description;
        this.votes=0;
        this.date="";
    }

    public Hook(String id, String title, String user, String description, String votes, String timestamp) {

        this.id=id;
        this.title=title;
        this.user=user;
        this.description=description;
        this.votes=Integer.parseInt(votes);
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

    public Integer getVotes() { return votes; }

    public String getDate() {
        return date;
    }

}

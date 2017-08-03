package com.mpvreeken.rpgcompanion.Classes;

/**
 * Created by Sven on 7/4/2017.
 */

public class Encounter {

    private String id;
    private String title;
    private String user;
    private String description;
    private Integer cr;
    private String date;

    public Encounter(String id, String title, String user, String description, Integer cr, String timestamp) {

        this.id=id;
        this.title=title;
        this.user=user;
        this.description=description;
        this.cr=cr;
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

    public Integer getCr() {
        return cr;
    }

    public String getDate() {
        return date;
    }

}

package com.mpvreeken.rpgcompanion.Items;

import java.io.Serializable;

/**
 * Created by Sven on 1/23/2018.
 */

public class SerialItem implements Serializable {
    public int id, upvotes, downvotes, voted, position;
    public String title, description, externalLink, imageLink;

    public SerialItem(int position, int id, String title, String description, String externalLink, String imageLink, int upvotes, int downvotes, int voted) {
        this.position=position;
        this.id=id;
        this.title=title;
        this.description=description;
        this.externalLink=externalLink;
        this.imageLink=imageLink;
        this.upvotes=upvotes;
        this.downvotes=downvotes;
        this.voted=voted;
    }

    public SerialItem(int id) {
        this.position=-1;
        this.id=id;
        this.title="";
        this.description="";
        this.externalLink="";
        this.imageLink="";
        this.upvotes=-1;
        this.downvotes=-1;
        this.voted=-1;
    }
}

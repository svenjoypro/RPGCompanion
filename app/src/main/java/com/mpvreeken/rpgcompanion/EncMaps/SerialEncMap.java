package com.mpvreeken.rpgcompanion.EncMaps;

import java.io.Serializable;

/**
 * Created by Sven on 1/23/2018.
 */

public class SerialEncMap implements Serializable {
    public int id, upvotes, downvotes, voted, position;
    public String title, description, externalLink;

    public SerialEncMap(int position, int id, String title, String description, String externalLink, int upvotes, int downvotes, int voted) {
        this.position=position;
        this.id=id;
        this.title=title;
        this.description=description;
        this.externalLink=externalLink;
        this.upvotes=upvotes;
        this.downvotes=downvotes;
        this.voted=voted;
    }

    public SerialEncMap(int id) {
        this.position=-1;
        this.id=id;
        this.title="";
        this.description="";
        this.externalLink="";
        this.upvotes=-1;
        this.downvotes=-1;
        this.voted=-1;
    }
}

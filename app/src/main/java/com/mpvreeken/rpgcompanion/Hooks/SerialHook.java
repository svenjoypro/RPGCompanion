package com.mpvreeken.rpgcompanion.Hooks;

import java.io.Serializable;

/**
 * Created by Sven on 1/21/2018.
 */

public class SerialHook implements Serializable {
    public int id, upvotes, downvotes, voted, position;
    public String title, description;

    public SerialHook(int position, int id, String title, String description, int upvotes, int downvotes, int voted) {
        this.position=position;
        this.id=id;
        this.title=title;
        this.description=description;
        this.upvotes=upvotes;
        this.downvotes=downvotes;
        this.voted=voted;
    }

    public SerialHook(int id) {
        this.position=-1;
        this.id=id;
        this.title="";
        this.description="";
        this.upvotes=-1;
        this.downvotes=-1;
        this.voted=-1;
    }
}

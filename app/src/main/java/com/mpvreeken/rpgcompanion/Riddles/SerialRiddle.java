package com.mpvreeken.rpgcompanion.Riddles;

import java.io.Serializable;

/**
 * Created by Sven on 1/23/2018.
 */

public class SerialRiddle implements Serializable {
    public int id, upvotes, downvotes, voted, position;
    public String riddle, answer;

    public SerialRiddle(int position, int id, String riddle, String answer, int upvotes, int downvotes, int voted) {
        this.position=position;
        this.id=id;
        this.answer=answer;
        this.riddle=riddle;
        this.upvotes=upvotes;
        this.downvotes=downvotes;
        this.voted=voted;
    }

    public SerialRiddle(int id) {
        this.position=-1;
        this.id=id;
        this.riddle="";
        this.answer="";
        this.upvotes=-1;
        this.downvotes=-1;
        this.voted=-1;
    }
}

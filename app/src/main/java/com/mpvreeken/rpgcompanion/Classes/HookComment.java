package com.mpvreeken.rpgcompanion.Classes;

/**
 * Created by Sven on 7/6/2017.
 */

public class HookComment extends Comment {

    public HookComment(String id, String parent_id, String user, String comment, String votes, String date) {
        super(Integer.parseInt(id), Integer.parseInt(parent_id), user, comment, Integer.parseInt(votes), date);

    }
}

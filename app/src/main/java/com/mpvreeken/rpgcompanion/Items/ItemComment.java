package com.mpvreeken.rpgcompanion.Items;

import com.mpvreeken.rpgcompanion.Classes.Comment;

/**
 * Created by Sven on 7/6/2017.
 */

public class ItemComment extends Comment {

    public ItemComment(String id, String parent_id, String user, String comment, String votes, String date) {
        super(Integer.parseInt(id), Integer.parseInt(parent_id), user, comment, Integer.parseInt(votes), date);

    }
}

package com.mpvreeken.rpgcompanion.Riddles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mpvreeken.rpgcompanion.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sven on 7/4/2017.
 */

public class RiddleCommentsArrayAdapter extends ArrayAdapter<RiddleComment> {

    private final Context context;
    private final List<RiddleComment> comments;
    //TODO
    //Consider using a RecyclerView instead of ListView

    public RiddleCommentsArrayAdapter(Context context, ArrayList<RiddleComment> values) {
        super(context, R.layout.comments_list_item_layout, values);
        this.context = context;
        this.comments = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final RiddleComment comment = comments.get(position);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.comments_list_item_layout, parent, false);
        TextView comment_tv = (TextView) rowView.findViewById(R.id.comment_list_item_comment_tv);
        TextView vote_tv = (TextView) rowView.findViewById(R.id.comment_list_item_vote_tv);

        comment_tv.setText(comment.getComment());
        vote_tv.setText(String.valueOf(comment.getVotes()));

        return rowView;
    }
}
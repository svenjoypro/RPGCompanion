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

public class RiddleArrayAdapter extends ArrayAdapter<Riddle> {

    private final Context context;
    private final List<Riddle> riddles;
    //TODO
    //Consider using a RecyclerView instead of ListView

    public RiddleArrayAdapter(Context context, ArrayList<Riddle> values) {
        super(context, R.layout.riddles_list_item_layout, values);
        this.context = context;
        this.riddles = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Riddle riddle = riddles.get(position);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.riddles_list_item_layout, parent, false);
        TextView riddle_tv = rowView.findViewById(R.id.riddle_list_item_riddle_tv);
        TextView votes_tv = rowView.findViewById(R.id.riddle_list_item_votes_tv);
        TextView answer_tv = rowView.findViewById(R.id.riddle_list_item_answer_tv);

        riddle_tv.setText(riddle.getRiddle());
        votes_tv.setText(riddle.getListItemVotes());
        answer_tv.setText(riddle.getAnswer());

        return rowView;
    }
}
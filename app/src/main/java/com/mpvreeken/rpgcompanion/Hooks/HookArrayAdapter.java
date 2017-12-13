package com.mpvreeken.rpgcompanion.Hooks;

import android.content.Context;
import android.support.annotation.NonNull;
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

public class HookArrayAdapter extends ArrayAdapter<Hook> {

    private final Context context;
    private final List<Hook> hooks;
    //TODO
    //Consider using a RecyclerView instead of ListView

    public HookArrayAdapter(Context context, ArrayList<Hook> values) {
        super(context, R.layout.hooks_list_item_layout, values);
        this.context = context;
        this.hooks = values;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        final Hook hook = hooks.get(position);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.hooks_list_item_layout, parent, false);
        TextView title_tv = rowView.findViewById(R.id.hook_list_item_title_tv);
        TextView votes_tv = rowView.findViewById(R.id.hook_list_item_votes_tv);
        TextView desc_tv = rowView.findViewById(R.id.hook_list_item_desc_tv);

        title_tv.setText(hook.getTitle());
        votes_tv.setText(hook.getListItemVotes());
        desc_tv.setText(hook.getDescription());

        return rowView;
    }
}
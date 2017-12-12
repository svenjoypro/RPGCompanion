package com.mpvreeken.rpgcompanion.Maps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mpvreeken.rpgcompanion.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Sven on 12/9/2017.
 */

public class EncMapArrayAdapter extends ArrayAdapter<EncMap> {

    private final Context context;
    private final List<EncMap> maps;
    //TODO
    //Consider using a RecyclerView instead of ListView

    public EncMapArrayAdapter(Context context, ArrayList<EncMap> values) {
        super(context, R.layout.maps_list_item_layout, values);
        this.context = context;
        this.maps = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final EncMap map = maps.get(position);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.maps_list_item_layout, parent, false);
        TextView title_tv = rowView.findViewById(R.id.map_list_item_title_tv);
        TextView votes_tv = rowView.findViewById(R.id.map_list_item_votes_tv);
        TextView desc_tv = rowView.findViewById(R.id.map_list_item_desc_tv);
        ImageView img = rowView.findViewById(R.id.map_list_item_img);

        title_tv.setText(map.getTitle());
        votes_tv.setText(map.getListItemVotes());
        desc_tv.setText(map.getDescription());

        Glide.with(context).load(context.getResources().getString(R.string.url_map_thumbs)+map.getId()+".jpg").into(img);

        return rowView;
    }
}
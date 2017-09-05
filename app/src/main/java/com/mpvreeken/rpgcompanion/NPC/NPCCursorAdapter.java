package com.mpvreeken.rpgcompanion.NPC;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.mpvreeken.rpgcompanion.R;
import com.mpvreeken.rpgcompanion.SavedNPCActivity;

/**
 * Created by Sven on 9/3/2017.
 */

public class NPCCursorAdapter extends ResourceCursorAdapter {

    public NPCCursorAdapter(Context context, int layout, Cursor cursor, int flags) {
        super(context, layout, cursor, flags);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView name_tv = (TextView) view.findViewById(R.id.npc_list_item_name_tv);
        name_tv.setText(cursor.getString(cursor.getColumnIndex("name")));

        TextView summary_tv = (TextView) view.findViewById(R.id.npc_list_item_summary_tv);
        summary_tv.setText(cursor.getString(cursor.getColumnIndex("summary")));
        view.setTag(cursor.getString(cursor.getColumnIndex("_id")));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SavedNPCActivity.class);
                intent.putExtra("id", view.getTag().toString());
                context.startActivity(intent);
            }
        });
    }
}

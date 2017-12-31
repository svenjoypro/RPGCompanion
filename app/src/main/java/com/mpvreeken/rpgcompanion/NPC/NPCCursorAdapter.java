package com.mpvreeken.rpgcompanion.NPC;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.Button;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.mpvreeken.rpgcompanion.R;

/**
 * Created by Sven on 9/3/2017.
 */



public class NPCCursorAdapter extends ResourceCursorAdapter {

    private NPCsActivity npcsActivity;

    public NPCCursorAdapter(Context context, int layout, Cursor cursor, int flags) {
        super(context, layout, cursor, flags);

        npcsActivity = (NPCsActivity) context;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView name_tv = view.findViewById(R.id.npc_list_item_name_tv);
        name_tv.setText(cursor.getString(cursor.getColumnIndex("name")));

        TextView summary_tv = view.findViewById(R.id.npc_list_item_summary_tv);
        summary_tv.setText(cursor.getString(cursor.getColumnIndex("summary")));

        final Button delete_btn = view.findViewById(R.id.npc_list_item_delete_btn);
        final Button confirm_btn = view.findViewById(R.id.npc_list_item_delete_confirm_btn);
        final Button cancel_btn = view.findViewById(R.id.npc_list_item_cancel_btn);

        view.setTag(cursor.getString(cursor.getColumnIndex("_id")));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SavedNPCActivity.class);
                intent.putExtra("id", view.getTag().toString());
                context.startActivity(intent);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                delete_btn.setVisibility(View.VISIBLE);
                return true;
            }
        });
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = (View) view.getParent();
                v.setVisibility(View.GONE);
                View p = (View) v.getParent();
                View v2 = p.findViewById(R.id.npc_list_item_undo);
                v2.setVisibility(View.VISIBLE);
            }
        });

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = (View) view.getParent();
                View p = (View) v.getParent();
                npcsActivity.deleteNPC(p.getTag().toString());
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = (View) view.getParent();
                v.setVisibility(View.GONE);
                View p = (View) v.getParent();
                View v2 = p.findViewById(R.id.npc_list_item_visible);
                v2.setVisibility(View.VISIBLE);
                delete_btn.setVisibility(View.GONE);
            }
        });
    }
}

package com.mpvreeken.rpgcompanion;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mpvreeken.rpgcompanion.Classes.DBHelper;
import com.mpvreeken.rpgcompanion.NPC.NPC;
import com.mpvreeken.rpgcompanion.NPC.NPCLayout;

public class SavedNPCActivity extends RPGCActivity {

    private NPC npc;
    private DBHelper dbHelper;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_npc);

        dbHelper = new DBHelper(this.getBaseContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();


        id = getIntent().getStringExtra("id");
        String query = "SELECT * FROM "+DBHelper.NPCS_TABLE_NAME+" WHERE _id = "+id+" LIMIT 1";

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        String json = cursor.getString(cursor.getColumnIndex("npc"));
        Gson gson = new Gson();
        npc = gson.fromJson(json, NPC.class);

        //TextView npc_tv = findViewById(R.id.saved_npc_body_tv);
        NPCLayout npcLayout = findViewById(R.id.saved_npc_npclayout);
        npcLayout.setNPC(npc);
        npcLayout.setSaveable(this);

        ImageButton copy_btn = findViewById(R.id.saved_npc_copy_btn);
        copy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (npc != null) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("NPCData", npc.getNPCText());
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(context, "NPC copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updateNPC() {
        Gson gson = new Gson();
        String json = gson.toJson(npc);

        DBHelper dbHelper = new DBHelper(this.getBaseContext());
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DBHelper.NPCS_COL_NPC, json);
        values.put(DBHelper.NPCS_COL_NAME, npc.getName()+" - "+npc.getRace()+" "+npc.getProfession());
        if (npc.getSummary().equals(npc.DEFAULT_SUMMARY)) {
            values.put(DBHelper.NPCS_COL_SUMMARY, "No Summary Provided");
        }
        else {
            values.put(DBHelper.NPCS_COL_SUMMARY, npc.getSummary());
        }


        db.update(DBHelper.NPCS_TABLE_NAME, values, "_id="+id, null);
    }



}

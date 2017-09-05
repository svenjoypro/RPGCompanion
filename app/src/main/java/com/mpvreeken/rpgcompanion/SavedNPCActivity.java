package com.mpvreeken.rpgcompanion;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mpvreeken.rpgcompanion.Classes.DBHelper;
import com.mpvreeken.rpgcompanion.NPC.NPC;

public class SavedNPCActivity extends AppCompatActivity {

    private NPC npc;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_npc);

        //Set up back button to appear in action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        dbHelper = new DBHelper(this.getBaseContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();


        String id = getIntent().getStringExtra("id");
        String query = "SELECT * FROM "+DBHelper.NPCS_TABLE_NAME+" WHERE _id = "+id+" LIMIT 1";

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        String json = cursor.getString(cursor.getColumnIndex("npc"));
        Gson gson = new Gson();
        npc = gson.fromJson(json, NPC.class);

        TextView npc_tv = findViewById(R.id.saved_npc_body_tv);
        npc_tv.setText(npc.output());
    }

    //Set click listener for back button in action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

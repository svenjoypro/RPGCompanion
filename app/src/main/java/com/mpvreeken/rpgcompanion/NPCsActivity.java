package com.mpvreeken.rpgcompanion;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.mpvreeken.rpgcompanion.Classes.DBHelper;
import com.mpvreeken.rpgcompanion.NPC.NPCCursorAdapter;

public class NPCsActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private ListView npcListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_npcs);

        //Set up back button to appear in action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        Button random_btn = findViewById(R.id.npcs_random_btn);
        random_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), RandomNPCActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        npcListView = findViewById(R.id.npcs_listview);
        dbHelper = new DBHelper(this.getBaseContext());
        populateNPCList();

    }
    private void populateNPCList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM "+DBHelper.NPCS_TABLE_NAME+" ORDER BY _id ASC";
        Cursor cursor = db.rawQuery(query, null);

        NPCCursorAdapter adapter = new NPCCursorAdapter(
                this, R.layout.npc_list_item_layout, cursor, 0 );

        npcListView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                //This should only occur if an npc was saved
                //String result=data.getStringExtra("npc_saved");
                populateNPCList();
            }
            if (resultCode == RESULT_CANCELED) {
                //No npc was saved
            }
        }
    }

    public void deleteNPC(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Define 'where' part of query.
        String selection = "_id = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { id };
        // Issue SQL statement.
        db.delete(DBHelper.NPCS_TABLE_NAME, selection, selectionArgs);

        populateNPCList();
    }


    public void updateNPC(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        String summary = "Updated Summary";
        values.put(DBHelper.NPCS_COL_SUMMARY, summary);

        // Which row to update, based on the id
        String selection = "_id = ?";
        String[] selectionArgs = { id };

        int count = db.update(
                DBHelper.NPCS_TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
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

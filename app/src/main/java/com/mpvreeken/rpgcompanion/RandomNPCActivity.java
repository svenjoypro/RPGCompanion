package com.mpvreeken.rpgcompanion;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mpvreeken.rpgcompanion.Classes.DBHelper;
import com.mpvreeken.rpgcompanion.NPC.NPC;
import com.mpvreeken.rpgcompanion.NPC.NPCData;
import android.view.ViewGroup.LayoutParams;

/**
 * Activity to display randomly generated NPCs
 *
 * Reads in RAW json data then sends it to NPCData which parses it.
 *
 * Allows generation of random NPCs and displays them on screen
 *
 * @author Michael Vreeken
 * @version 2017.0727
 * @since 1.0
 */
public class RandomNPCActivity extends AppCompatActivity {

    NPCData npcData;
    TextView npc_tv;
    NPC currentNPC;
    private PopupWindow popupWindow;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_npc);

        //Set up back button to appear in action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //set up variable to hold npc output text view
        npc_tv = findViewById(R.id.npc_body_tv);
        npc_tv.setMovementMethod(new ScrollingMovementMethod());

        //Get the json data from raw, then parse it into a nicer, more accessible object
        npcData = new NPCData(getApplicationContext());

        //Initialize the new activity with a freshly generated NPC
        generateNewNPC();

        //Set up button for generating new NPCs
        Button gen_btn = findViewById(R.id.npc_generate_btn);
        gen_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                generateNewNPC();
            }
        });

        Button save_btn = findViewById(R.id.npc_save_btn);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSaveDialog();
                //saveNPC();
            }
        });

        scrollView = findViewById(R.id.npc_scrollView);

        //By default set result to canceled, we re-set this to RESULT_OK if we need to later
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
    }

    public void showSaveDialog() {
        //https://android--code.blogspot.com/2016/01/android-popup-window-example.html

        // Initialize a new instance of LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        View popupView = inflater.inflate(R.layout.popup_npc_save_layout, null);


        popupWindow = new PopupWindow(
                popupView,
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );

        // Set an elevation value for popup window
        // Call requires API level 21
        if(Build.VERSION.SDK_INT>=21){
            popupWindow.setElevation(5.0f);
        }

        // Get a reference for the custom view close button
        Button saveButton = popupView.findViewById(R.id.npc_popup_save_btn);
        Button cancelButton = popupView.findViewById(R.id.npc_popup_cancel_btn);
        final EditText summary = popupView.findViewById(R.id.npc_popup_summary_et);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNPC(summary.getText().toString());
                popupWindow.dismiss();
                Intent intent = new Intent();
                intent.putExtra("npc_saved", "true");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(scrollView, Gravity.CENTER,0,0);
        popupWindow.setFocusable(true);
        popupWindow.update();
    }

    public void saveNPC(String summary) {
        Gson gson = new Gson();
        String json = gson.toJson(currentNPC);

        DBHelper dbHelper = new DBHelper(this.getBaseContext());
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DBHelper.NPCS_COL_NPC, json);
        values.put(DBHelper.NPCS_COL_NAME, currentNPC.name+" - "+currentNPC.race.name+" "+currentNPC.profession);
        values.put(DBHelper.NPCS_COL_SUMMARY, summary);
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DBHelper.NPCS_TABLE_NAME, null, values);
    }

    public void generateNewNPC() {
        //Generate new NPC
        currentNPC = npcData.getRandomNPC();
        //Show the new NPC in the output text view
        npc_tv.setText(currentNPC.output());
        npc_tv.scrollTo(0,0);
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

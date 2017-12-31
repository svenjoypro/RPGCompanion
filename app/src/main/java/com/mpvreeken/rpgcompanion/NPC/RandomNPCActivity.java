package com.mpvreeken.rpgcompanion.NPC;


import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mpvreeken.rpgcompanion.Classes.DBHelper;
import com.mpvreeken.rpgcompanion.NPC.NPC;
import com.mpvreeken.rpgcompanion.NPC.NPCData;
import com.mpvreeken.rpgcompanion.NPC.NPCLayout;
import com.mpvreeken.rpgcompanion.R;
import com.mpvreeken.rpgcompanion.RPGCActivity;

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
public class RandomNPCActivity extends RPGCActivity {

    NPCData npcData;
    TextView npc_tv;
    NPC currentNPC;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_npc);

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
                //showSaveDialog();
                saveNPC();
                //dialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("npc_saved", "true");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        //By default set result to canceled, we re-set this to RESULT_OK if we need to later
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);

        ImageButton copy_btn = findViewById(R.id.saved_npc_copy_btn);
        copy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentNPC != null) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("NPCData", currentNPC.getNPCText());
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(context, "NPC copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void showSaveDialog() {

        // Initialize a new instance of LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        View popupView = inflater.inflate(R.layout.popup_npc_save_layout, null);

        final Dialog dialog = new Dialog(this, R.style.AlertDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(popupView);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();


        // Set an elevation value for popup window
        // Call requires API level 22
        if(Build.VERSION.SDK_INT>=22){
            dialog.getWindow().setElevation(5.0f);
        }

        // Get a reference for the custom view close button
        Button saveButton = popupView.findViewById(R.id.npc_popup_save_btn);
        Button cancelButton = popupView.findViewById(R.id.npc_popup_cancel_btn);
        final EditText summary = popupView.findViewById(R.id.npc_popup_summary_et);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNPC();
                dialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("npc_saved", "true");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    public void saveNPC() {
        Gson gson = new Gson();
        String json = gson.toJson(currentNPC);

        DBHelper dbHelper = new DBHelper(this.getBaseContext());
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DBHelper.NPCS_COL_NPC, json);
        values.put(DBHelper.NPCS_COL_NAME, currentNPC.getName()+" - "+currentNPC.getRace()+" "+currentNPC.getProfession());
        if (currentNPC.getSummary().equals(currentNPC.DEFAULT_SUMMARY)) {
            values.put(DBHelper.NPCS_COL_SUMMARY, "No Summary Provided");
        }
        else {
            values.put(DBHelper.NPCS_COL_SUMMARY, currentNPC.getSummary());
        }
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DBHelper.NPCS_TABLE_NAME, null, values);
    }

    public void generateNewNPC() {
        //Generate new NPC
        currentNPC = npcData.getRandomNPC();
        NPCLayout npcLayout = findViewById(R.id.npc_npc_npclayout);
        npcLayout.setNPC(currentNPC);
    }

}

package com.mpvreeken.rpgcompanion;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.mpvreeken.rpgcompanion.Dice.DiceActivity;
import com.mpvreeken.rpgcompanion.Hooks.HooksActivity;
import com.mpvreeken.rpgcompanion.Items.ItemsActivity;
import com.mpvreeken.rpgcompanion.Maps.EncMapsActivity;
import com.mpvreeken.rpgcompanion.NPC.NPCsActivity;
import com.mpvreeken.rpgcompanion.Names.NameGeneratorActivity;
import com.mpvreeken.rpgcompanion.Puzzles.PuzzlesActivity;
import com.mpvreeken.rpgcompanion.Riddles.RiddlesActivity;

public class MainActivity extends RPGCActivity {
	
	/**
	 *	TODO
	 *      Add Spell summaries?
	 *
	 *
	 */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Remove the "up" or back button from the ActionBar
        disableUpButton();

        ImageButton hooks_btn = findViewById(R.id.main_hooks_button);
        ImageButton encounters_btn = findViewById(R.id.main_encounters_button);
        ImageButton puzzles_btn = findViewById(R.id.main_puzzles_button);
        ImageButton npc_btn = findViewById(R.id.main_npc_button);
        ImageButton names_btn = findViewById(R.id.main_names_button);
        ImageButton adventures_btn = findViewById(R.id.main_adventures_button);
        ImageButton riddles_btn = findViewById(R.id.main_riddles_button);
        ImageButton misc_btn = findViewById(R.id.main_misc_button);
        ImageButton loot_gen_btn = findViewById(R.id.main_loot_gen_button);
        ImageButton maps_btn = findViewById(R.id.main_maps_button);
        ImageButton dice_btn = findViewById(R.id.main_dice_button);
        ImageButton items_btn = findViewById(R.id.main_items_button);

        View.OnClickListener buttonHandler = new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent;
                switch(v.getId()) {
                    case R.id.main_hooks_button:
                        intent = new Intent(v.getContext(), HooksActivity.class);
                        break;
                    case R.id.main_encounters_button:
                        intent = new Intent(v.getContext(), EncountersActivity.class);
                        break;
                    case R.id.main_puzzles_button:
                        intent = new Intent(v.getContext(), PuzzlesActivity.class);
                        break;
                    case R.id.main_npc_button:
                        intent = new Intent(v.getContext(), NPCsActivity.class);
                        break;
                    case R.id.main_names_button:
                        intent = new Intent(v.getContext(), NameGeneratorActivity.class);
                        break;
                    case R.id.main_adventures_button:
                        intent = new Intent(v.getContext(), AdventuresActivity.class);
                        break;
                    case R.id.main_riddles_button:
                        intent = new Intent(v.getContext(), RiddlesActivity.class);
                        break;
                    case R.id.main_misc_button:
                        intent = new Intent(v.getContext(), D100Activity.class);
                        break;
                    case R.id.main_loot_gen_button:
                        intent = new Intent(v.getContext(), LootGeneratorActivity.class);
                        break;
                    case R.id.main_maps_button:
                        intent = new Intent(v.getContext(), EncMapsActivity.class);
                        break;
                    case R.id.main_items_button:
                        intent = new Intent(v.getContext(), ItemsActivity.class);
                        break;
                    case R.id.main_dice_button:
                        intent = new Intent(v.getContext(), DiceActivity.class);
                        break;
                    default:
                        intent = new Intent(v.getContext(), NPCsActivity.class);
                }
                //intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
            }
        };

        hooks_btn.setOnClickListener(buttonHandler);
        encounters_btn.setOnClickListener(buttonHandler);
        puzzles_btn.setOnClickListener(buttonHandler);
        npc_btn.setOnClickListener(buttonHandler);
        names_btn.setOnClickListener(buttonHandler);
        riddles_btn.setOnClickListener(buttonHandler);
        adventures_btn.setOnClickListener(buttonHandler);
        misc_btn.setOnClickListener(buttonHandler);
        loot_gen_btn.setOnClickListener(buttonHandler);
        maps_btn.setOnClickListener(buttonHandler);
        dice_btn.setOnClickListener(buttonHandler);
        items_btn.setOnClickListener(buttonHandler);

        application.checkToken(this);
    }
}

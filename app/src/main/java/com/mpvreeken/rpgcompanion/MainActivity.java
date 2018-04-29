package com.mpvreeken.rpgcompanion;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.mpvreeken.rpgcompanion.Dice.DiceActivity;
import com.mpvreeken.rpgcompanion.Dungeons.DungeonsActivity;
import com.mpvreeken.rpgcompanion.Hooks.HooksActivity;
import com.mpvreeken.rpgcompanion.InitiativeTracker.InitiativeTrackerActivity;
import com.mpvreeken.rpgcompanion.Items.ItemsActivity;
import com.mpvreeken.rpgcompanion.EncMaps.EncMapsActivity;
import com.mpvreeken.rpgcompanion.NPC.NPCsActivity;
import com.mpvreeken.rpgcompanion.Names.NameGeneratorActivity;
import com.mpvreeken.rpgcompanion.PCs.PCsActivity;
import com.mpvreeken.rpgcompanion.Puzzles.PuzzlesActivity;
import com.mpvreeken.rpgcompanion.Riddles.RiddlesActivity;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
        ImageButton pcs_btn = findViewById(R.id.main_player_characters_button);
        ImageButton initiative_btn = findViewById(R.id.main_initiative_tracker_button);
        ImageButton dungeons_btn = findViewById(R.id.main_dungeons_button);

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
                    case R.id.main_player_characters_button:
                        intent = new Intent(v.getContext(), PCsActivity.class);
                        break;
                    case R.id.main_initiative_tracker_button:
                        intent = new Intent(v.getContext(), InitiativeTrackerActivity.class);
                        break;
                    case R.id.main_dungeons_button:
                        intent = new Intent(v.getContext(), DungeonsActivity.class);
                        break;
                    default:
                        intent = new Intent(v.getContext(), NPCsActivity.class);
                }
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
        initiative_btn.setOnClickListener(buttonHandler);
        pcs_btn.setOnClickListener(buttonHandler);
        dungeons_btn.setOnClickListener(buttonHandler);

        application.checkToken();




        /*
         * APP NAME POLL
         */
        final SharedPreferences prefs = this.getSharedPreferences("com.mpvreeken.rpgcompanion", Context.MODE_PRIVATE);
        Boolean voted = prefs.getBoolean("voted", false);
        if (!voted) {
            final RadioGroup rg = findViewById(R.id.main_vote_rg);
            final LinearLayout ll = findViewById(R.id.main_vote_layout);
            ll.setVisibility(View.VISIBLE);

            Button vote_btn = findViewById(R.id.main_vote_btn);
            vote_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int i = -1;
                    if (rg.getCheckedRadioButtonId() == R.id.main_rb_1) {
                        i = 1;
                    } else if (rg.getCheckedRadioButtonId() == R.id.main_rb_2) {
                        i = 2;
                    } else if (rg.getCheckedRadioButtonId() == R.id.main_rb_3) {
                        i = 3;
                    }
                    if (i == -1) {
                        Toast.makeText(application, "Please select one of the three options before voting", Toast.LENGTH_LONG).show();
                        return;
                    }

                    final RequestBody postBody = new FormBody.Builder()
                            .add("vote", String.valueOf(i))
                            .build();

                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://107.150.7.141/rpg_companion/api/poll-vote")
                            .post(postBody)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(application, "Unable to connect to server", Toast.LENGTH_LONG).show();
								}
							});
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                            if (!response.isSuccessful()) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										Toast.makeText(application, "Error saving vote. Please try again.", Toast.LENGTH_LONG).show();
									}
                                });
                            } else {
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean("voted", true);
                                editor.apply();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(application, "Thank you for voting", Toast.LENGTH_LONG).show();
                                        ll.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
    }
}

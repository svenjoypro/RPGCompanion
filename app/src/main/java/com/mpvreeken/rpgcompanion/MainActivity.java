package com.mpvreeken.rpgcompanion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
	
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

        //checkLoggedIn();

        ImageButton hooks_btn = (ImageButton) findViewById(R.id.main_hooks_button);
        ImageButton encounters_btn = (ImageButton) findViewById(R.id.main_encounters_button);
        ImageButton puzzles_btn = (ImageButton) findViewById(R.id.main_puzzles_button);
        ImageButton npc_btn = (ImageButton) findViewById(R.id.main_npc_button);
        ImageButton adventures_btn = (ImageButton) findViewById(R.id.main_adventures_button);
        ImageButton riddles_btn = (ImageButton) findViewById(R.id.main_riddles_button);
        ImageButton misc_btn = (ImageButton) findViewById(R.id.main_misc_button);


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
                    case R.id.main_adventures_button:
                        intent = new Intent(v.getContext(), AdventuresActivity.class);
                        break;
                    case R.id.main_riddles_button:
                        intent = new Intent(v.getContext(), RiddlesActivity.class);
                        break;
                    case R.id.main_misc_button:
                        intent = new Intent(v.getContext(), MiscActivity.class);
                        break;
                    default:
                        intent = new Intent(v.getContext(), NPCsActivity.class);
                }
                //intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
            }
        };

        hooks_btn.setOnClickListener(buttonHandler);
        //encounters_btn.setOnClickListener(buttonHandler);
        //puzzles_btn.setOnClickListener(buttonHandler);
        npc_btn.setOnClickListener(buttonHandler);
        //riddles_btn.setOnClickListener(buttonHandler);
        adventures_btn.setOnClickListener(buttonHandler);
        misc_btn.setOnClickListener(buttonHandler);


    }
}

package com.mpvreeken.rpgcompanion;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mpvreeken.rpgcompanion.R;

public class NewPostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        //Set up back button to appear in action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();

        TextView title_tv = findViewById(R.id.post_title_tv);
        switch((String) bundle.get("type")) {
            case "hook":
                title_tv.setText("New Hook");
                break;
            case "encounter":
                title_tv.setText("New Encounter");
                break;
            case "adventure":
                title_tv.setText("New Adventure");
                break;
            case "puzzle":
                title_tv.setText("New Puzzle");
                break;
            case "riddle":
                title_tv.setText("New Riddle");
                break;
            default:
                //TODO show error?
                finish();
        }


        Button cancel_btn = findViewById(R.id.post_cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        Button submit_btn = findViewById(R.id.post_submit_btn);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO check body text content, post to server
            }
        });

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

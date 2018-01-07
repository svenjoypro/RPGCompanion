package com.mpvreeken.rpgcompanion;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CreditsActivity extends RPGCActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        Button bb_btn = findViewById(R.id.credits_benbrommel_btn);
        bb_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.reddit.com/user/BenBrommell";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        Button bb_img_btn = findViewById(R.id.credits_brommell_src_btn);
        bb_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.reddit.com/r/DnD/comments/2ekles/created_a_map_for_my_homebrew_world_mighta/ck0d6eo/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        Button mv_btn = findViewById(R.id.credits_mrvalor_btn);
        mv_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.reddit.com/user/mrvalor";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        Button mv_img_btn = findViewById(R.id.credits_mrvalor_src_btn);
        mv_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.reddit.com/r/FantasyMaps/comments/7enbzc/all_of_my_maps_since_march_when_i_started_full/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        Button dnd_btn = findViewById(R.id.credits_dnd_btn);
        dnd_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.reddit.com/r/DnD/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        Button dungeons_btn = findViewById(R.id.credits_dungeons_btn);
        dungeons_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.reddit.com/r/DungeonsAndDragons/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        Button d100_btn = findViewById(R.id.credits_d100_btn);
        d100_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.reddit.com/r/d100/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        Button dmacad_btn = findViewById(R.id.credits_dmacademy_btn);
        dmacad_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.reddit.com/r/DMAcademy/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        Button screen_btn = findViewById(R.id.credits_screen_btn);
        screen_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.reddit.com/r/DnDBehindTheScreen/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        Button tables_btn = findViewById(R.id.credits_tables_btn);
        tables_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.reddit.com/r/BehindTheTables/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });



    }
}

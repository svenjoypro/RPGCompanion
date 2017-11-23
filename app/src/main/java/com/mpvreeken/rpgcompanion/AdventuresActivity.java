package com.mpvreeken.rpgcompanion;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AdventuresActivity extends RPGCActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adventures);

        TextView msg = findViewById(R.id.activity_message_tv);
        msg.setText("Perhaps in the future I will implement something for adventures and one-shots, " +
                "but as of now I think there is already a great resource for them called Adventure Lookup\n\nwww.adventurelookup.com\n\n" +
                "Clicking the button below will take you to a third party website which I am unaffiliated with.");

        Button link_btn = findViewById(R.id.activity_link_btn);
        link_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://www.adventurelookup.com"));
                startActivity(i);
            }
        });
    }
}

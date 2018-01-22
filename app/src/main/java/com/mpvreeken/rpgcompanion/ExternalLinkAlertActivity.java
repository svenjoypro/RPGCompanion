package com.mpvreeken.rpgcompanion;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ExternalLinkAlertActivity extends AppCompatActivity {

    private TextView url_tv;
    private Button disable_btn, go_btn, cancel_btn;
    private RPGCApplication app;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_external_link_alert);

        app = (RPGCApplication) getApplication();

        url_tv = findViewById(R.id.external_link_url_tv);
        url_tv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("externalURL", url_tv.getText().toString());
                if (clipboard != null) { clipboard.setPrimaryClip(clip); }
                Toast.makeText(app, "URL copied to clipboard", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        try {
            Intent intent = getIntent();
            final Bundle bundle = intent.getExtras();
            if (bundle == null) throw new Exception();
            url = bundle.getString("LINK");
            if (url == null || url.length()==0) throw new Exception();
            url_tv.setText(url);
        }
        catch (Exception e) {
            Toast.makeText(app, "Invalid External Link", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        go_btn = findViewById(R.id.external_link_go_btn);
        go_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    finish();
                    return;
                }
                catch(Exception e) {
                    //If intent fails, it might just be that url doesn't begin with http, try again
                    url = "http://"+url;
                }
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    finish();
                }
                catch(Exception e) {
                    Toast.makeText(app, "Invalid URL format, can't open link", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancel_btn = findViewById(R.id.external_link_cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        disable_btn = findViewById(R.id.external_link_enable_bipass_btn);
        disable_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (app.getExternalLinkAlert()) {
                    app.setExternalLinkAlert(false);
                    disable_btn.setText("Enable this alert");
                    Toast.makeText(app, "You will no longer see these external link alerts", Toast.LENGTH_LONG).show();
                }
                else {
                    app.setExternalLinkAlert(true);
                    disable_btn.setText("Disable this alert");
                    Toast.makeText(app, "You will now see these external link alerts before visiting links", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}

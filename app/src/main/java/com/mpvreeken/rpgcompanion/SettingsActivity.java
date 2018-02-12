package com.mpvreeken.rpgcompanion;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsActivity extends RPGCActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch ela_switch = findViewById(R.id.settings_external_link_alert_switch);

        ela_switch.setChecked(application.getExternalLinkAlert());

        ela_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                application.setExternalLinkAlert(b);
            }
        });
    }
}

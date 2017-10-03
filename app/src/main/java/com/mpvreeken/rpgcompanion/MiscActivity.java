package com.mpvreeken.rpgcompanion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Random;

public class MiscActivity extends AppCompatActivity {

    public JSONArray tavern_adjs, tavern_nouns;
    private TextView output_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_misc);

        output_tv = findViewById(R.id.misc_output_tv);

        try {
            tavern_adjs = getRawData(R.raw.d100_tavern_names).getJSONArray("adjs");
            tavern_nouns = getRawData(R.raw.d100_tavern_names).getJSONArray("nouns");
        } catch (JSONException e) {
            Log.e("MiscActivity", e.getMessage());
            e.printStackTrace();
        }

        Button tavern_name_btn = findViewById(R.id.misc_tavern_name_btn);

        tavern_name_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MiscActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        output_tv.setText(generateTavernName());
                    }
                });
            }
        });

    }

    public String generateTavernName() {
        String name = "";
        Random ran = new Random();
        int x = ran.nextInt(3);
        if (x == 0) {
            name = "The " + getRandom(tavern_adjs) + " " + getRandom(tavern_nouns);
        }
        else if (x == 1) {
            String a = getRandom(tavern_adjs);
            String b = getRandom(tavern_adjs);
            while (a.equals(b)) {
                b = getRandom(tavern_adjs);
            }
            name = "The " + a + " and " + b;
        }
        else {
            String a = getRandom(tavern_nouns);
            String b = getRandom(tavern_nouns);
            while (a.equals(b)) {
                b = getRandom(tavern_nouns);
            }
            name = "The " + a + " and " + b;
        }

        return name;
    }


    public JSONObject getRawData(int res) {
        //Read in the raw json data from resource folder
        InputStream is = getBaseContext().getResources().openRawResource(res);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        JSONObject obj = new JSONObject();
        try {
            obj = new JSONObject(writer.toString());
        } catch (JSONException e) {
            //TODO close activity
            Log.e("TownBuilder", e.getMessage());
            e.printStackTrace();
        }

        return obj;
    }

    /**
     * Method that selects a random "entry" from the provided JSONArray
     * Then passes it through the parseMe method to replace any {{variables}} with
     * randomly chosen values
     *
     * @return The clean, legible String containing the randomly chosen NPC detail
     */
    public String getRandom(JSONArray a) {
        String s = "";
        try {
            Random ran = new Random();
            int x = ran.nextInt(a.length());
            s = a.getString(x);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return s;
    }
}

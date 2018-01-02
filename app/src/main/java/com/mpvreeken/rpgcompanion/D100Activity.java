package com.mpvreeken.rpgcompanion;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class D100Activity extends RPGCActivity {

    private List<String> categories;
    private ArrayList<ArrayList<JSONObject>> jsonObjects;

    private LinearLayout linear;
    private TextView output_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_d100);

        linear = findViewById(R.id.d100_buttons_linearlayout);
        output_tv = findViewById(R.id.d100_output_tv);

        output_tv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("d100Data", output_tv.getText().toString());
                if (clipboard != null) { clipboard.setPrimaryClip(clip); }
                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        categories = new ArrayList<>();
        categories.add("Encounters");
        categories.add("Items");
        categories.add("NPCs");
        categories.add("World Building");
        categories.add("Misc");
        jsonObjects = new ArrayList<>();
        jsonObjects.add(new ArrayList<JSONObject>());
        jsonObjects.add(new ArrayList<JSONObject>());
        jsonObjects.add(new ArrayList<JSONObject>());
        jsonObjects.add(new ArrayList<JSONObject>());
        jsonObjects.add(new ArrayList<JSONObject>());

        try {
            String[] f = getAssets().list("d100");
            for(String f1 : f){
                loadJSON(f1);
            }
        } catch (IOException e) {
            Toast.makeText(this, "An error occurred", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            finish();
            return;
        }

        createList();
    }

    private void createList() {
        if (categories.size() != jsonObjects.size()) {
            Toast.makeText(this, "An error occurred", Toast.LENGTH_LONG).show();
            return;
        }
        for (int i=0; i<categories.size(); i++) {
            TextView categoryView = new TextView(this);
            categoryView.setText(categories.get(i));
            //https://stackoverflow.com/a/36738935/1330096
            linear.addView(categoryView);

            for (int j=0; j<jsonObjects.get(i).size(); j++) {
                Button btn = new Button(this);
                try {
                    btn.setText(jsonObjects.get(i).get(j).getString("name"));
                    final int finalI = i;
                    final int finalJ = j;
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getRandom(jsonObjects.get(finalI).get(finalJ));
                        }
                    });
                    btn.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            showSource(jsonObjects.get(finalI).get(finalJ));
                            return true;
                        }
                    });
                    linear.addView(btn);
                } catch (JSONException e) {
                    Toast.makeText(this, "An error occurred", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
    }

    private void showSource(JSONObject o) {
        try {
            output_tv.setText("Source: "+o.get("source").toString());
        } catch (JSONException e) {
            Toast.makeText(context, "An unknown error has occurred. Please try again", Toast.LENGTH_LONG).show();
        }
    }

    private void getRandom(JSONObject o) {
        Random random = new Random();
        try {
            if (o.get("name").equals("Tavern Names")) {
                generateTavernName(o);
                return;
            }
            JSONArray data = o.getJSONArray("data");

            StringBuilder output = new StringBuilder();
            for (int i=0; i<data.length(); i++) {
                JSONArray ar = data.getJSONObject(i).getJSONArray("data");
                output.append(data.getJSONObject(i).get("name")).append(": ").append(ar.get(random.nextInt(ar.length())).toString()).append("\n");
            }
            output_tv.setText(output.toString());

        } catch (JSONException e) {
            Toast.makeText(this, "An error occurred", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void generateTavernName(JSONObject o) {
        Random random = new Random();
        try {
            JSONArray data = o.getJSONArray("data");

            if (data.length() != 2) {
                Toast.makeText(this, "An error occurred", Toast.LENGTH_LONG).show();
                return;
            }
            JSONArray adjs = data.getJSONObject(0).getJSONArray("data");
            JSONArray nouns = data.getJSONObject(1).getJSONArray("data");

            String name;
            Random ran = new Random();
            int x = ran.nextInt(3);
            if (x == 0) {
                name = "The " + adjs.get(random.nextInt(adjs.length())).toString() + " " + nouns.get(random.nextInt(nouns.length())).toString();
            }
            else if (x == 1) {
                String a = adjs.get(random.nextInt(adjs.length())).toString();
                String b = adjs.get(random.nextInt(adjs.length())).toString();
                while (a.equals(b)) {
                    b = adjs.get(random.nextInt(adjs.length())).toString();
                }
                name = "The " + a + " and " + b;
            }
            else {
                String a = nouns.get(random.nextInt(nouns.length())).toString();
                String b = nouns.get(random.nextInt(nouns.length())).toString();
                while (a.equals(b)) {
                    b = nouns.get(random.nextInt(nouns.length())).toString();
                }
                name = "The " + a + " and " + b;
            }

            output_tv.setText(name);
        } catch (JSONException e) {
            Toast.makeText(this, "An error occurred", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void loadJSON(String s) {
        Log.d("D100Activity loadJSON", s);
        JSONObject obj;
        try {
            obj = new JSONObject(loadJSONFromAsset(s));

            String cat = obj.getString("category");
            int catIndex;
            if (categories.contains(cat)) {
                catIndex=categories.indexOf(cat);
            }
            else {
                categories.add(cat);
                catIndex=categories.indexOf(cat);
                jsonObjects.add(new ArrayList<JSONObject>());
            }

            jsonObjects.get(catIndex).add(obj);
        }
        catch (JSONException e) {
            Toast.makeText(this, "An error occurred", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private String loadJSONFromAsset(String s) {
        String json;
        try {
            InputStream is = getAssets().open("d100/"+s);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            Toast.makeText(this, "An error occurred", Toast.LENGTH_LONG).show();
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}

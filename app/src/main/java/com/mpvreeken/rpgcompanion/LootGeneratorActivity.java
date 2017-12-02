package com.mpvreeken.rpgcompanion;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mpvreeken.rpgcompanion.Widgets.MultiselectSpinner;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class LootGeneratorActivity extends RPGCActivity {

    private MultiselectSpinner spinner;
    private List<String> items;
    private CheckBox magic_item_cb, wondrous_item_cb;
    private RadioButton standard_rb, mini_rb, boss_rb, hoard_rb, individual_rb;
    private EditText cr_et, enemies_et;
    private JSONObject gemstones, art, armor, rings, potions, rods, staffs, wands, weapons, wondrouses;
    private JSONArray spells;
    private TextView output_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loot_generator);

        //magic_item_cb = findViewById(R.id.loot_gen_force_magic_cb);
        //wondrous_item_cb = findViewById(R.id.loot_gen_force_wondrous_cb);

        standard_rb = findViewById(R.id.loot_gen_enc_standard_rb);
        mini_rb = findViewById(R.id.loot_gen_enc_mini_rb);
        boss_rb = findViewById(R.id.loot_gen_enc_boss_rb);

        individual_rb = findViewById(R.id.loot_gen_individual_rb);
        hoard_rb = findViewById(R.id.loot_gen_hoard_rb);

        cr_et = findViewById(R.id.loot_gen_cr_et);
        enemies_et = findViewById(R.id.loot_gen_enemies_et);

        output_tv = findViewById(R.id.loot_gen_output_tv);


        spinner = findViewById(R.id.loot_gen_items_spinner);
        items = Arrays.asList("Gold", "Gemstones", "Wondrous Items", "Weapons", "Rings", "Rods", "Staffs", "Armor", "Wands", "Potions", "Art/Jewelry", "Spell Scrolls");

        spinner.setItems(items);
        spinner.setSelection(items);


        Button generate_btn = findViewById(R.id.loot_gen_generate_btn);
        generate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { generateLoot(); }
        });

        loadJSON();
    }

    private void loadJSON() {
        JSONObject j = new JSONObject();
        try {
            j = getRawData(R.raw.loot_generator);
            gemstones = j.getJSONObject("gemstones");
            art = j.getJSONObject("art");
            armor = j.getJSONObject("armor");
            rings = j.getJSONObject("rings");
            potions = j.getJSONObject("potions");
            rods = j.getJSONObject("rods");
            staffs = j.getJSONObject("staffs");
            wands = j.getJSONObject("wands");
            weapons = j.getJSONObject("weapons");
            wondrouses = j.getJSONObject("wondrous");
        } catch (JSONException e) {
            Log.e("LootGenerator", e.getMessage());
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }

        try {
            spells = getRawData(R.raw.spells).getJSONArray("spells");
        } catch (JSONException e) {
            Log.e("LootGenerator", e.getMessage());
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
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
            Log.e("LootGenerator", e.getMessage());
            e.printStackTrace();
        }

        return obj;
    }

    private void generateLoot() {
        //String s = spinner.getSelectedItemsAsString();
        //Log.e("getSelected", s);
        int cr, enemies;
        try {
            cr = cr_et.getText().toString().replaceAll("\\D", "").length() == 0 ? 0 : Integer.valueOf(cr_et.getText().toString().replaceAll("\\D", ""));
            if (cr < 0) {
                cr = cr * -1;
            }
            enemies = enemies_et.getText().toString().replaceAll("\\D", "").length() == 0 ? 0 : Integer.valueOf(enemies_et.getText().toString().replaceAll("\\D", ""));
            if (enemies < 0) {
                enemies = enemies * -1;
            }
        }
        catch (Exception e) {
            cr=0;
            enemies=0;
        }

        List<String> commons = Arrays.asList("common", "uncommon", "rare", "very_rare", "legendary");
        int commonIndex = (int) cr/4;
        if (commonIndex>4) { commonIndex=4; }

        boolean individual = individual_rb.isChecked();

        int enc_type = 1;
        if (mini_rb.isChecked()) { enc_type=2; }
        else if (boss_rb.isChecked()) { enc_type=3; }

        //boolean wondrous = wondrous_item_cb.isChecked();


        Random ran = new Random();
        int gold=0;
        int silver=0;
        int copper=0;

        List<String> possibles = spinner.getSelectedStrings();

        int minTotal = (enemies+1)/2;
        if (minTotal==0) { minTotal=1; }
        int total = ran.nextInt(minTotal) + (minTotal);

        if (!individual) {
            total = 4 + (enemies * 3);
        }

        List<String> loot = new ArrayList<>();
        int count=0;
        while (loot.size() < total && count<1000) {
            int x = ran.nextInt(100);

            //Randomly get lower rarity items
            int cx = commonIndex>0 ? ran.nextInt(commonIndex+1) : 0;
            //If in a miniboss or bbeg encounter, increase minimum rarity
            if (cx<commonIndex-(3-enc_type)) {
                cx=commonIndex-(3-enc_type);
            }

            String ci = commons.get(cx);

            if (x<15 && possibles.contains("Gemstones")) {
                getRandomObject(loot, gemstones, ci);
            }
            else if(x<30 && possibles.contains("Weapons")) {
                getRandomObject(loot, weapons, ci);
            }
            else if(x<45 && possibles.contains("Armor")) {
                getRandomObject(loot, armor, ci);
            }
            else if(x<60 && possibles.contains("Art/Jewelry")) {
                getRandomObject(loot, art, ci);
            }
            else if(x<70 && possibles.contains("Potions")) {
                getRandomObject(loot, potions, ci);
            }
            else if(x<75 && possibles.contains("Spell Scrolls")) {
                getRandomSpell(loot, cr);
            }
            else if(x<80 && possibles.contains("Staffs")) {
                getRandomObject(loot, staffs, ci);
            }
            else if(x<85 && possibles.contains("Rods")) {
                getRandomObject(loot, rods, ci);
            }
            else if(x<90 && possibles.contains("Wands")) {
                getRandomObject(loot, wands, ci);
            }
            else if(x<90 && possibles.contains("Rings")) {
                getRandomObject(loot, rings, ci);
            }
            else if(x>90 && possibles.contains("Wondrous Items")) {
                getRandomObject(loot, wondrouses, ci);
            }
            count+=1;
        }

        if (loot.size() < total) {
            Toast.makeText(context, "Error finding the right loot. Please try again.", Toast.LENGTH_SHORT).show();
            output_tv.setText("");
            return;
        }

        if (possibles.contains("Gold")) {
            double gold_max = (cr * cr * cr * cr * .07) + .2;
            //Hoard = more
            if (!individual) { gold_max = gold_max * (1.5*cr); }
            //mini boss and bbeg = more
            gold_max = gold_max * enc_type;

            double gold_min = gold_max / 2;
            double rand = gold_min + (gold_min * ran.nextDouble());

            gold = (int) rand;
            int rem = (int) ((rand - ((int) rand)) * 100);
            silver = rem / 10;
            copper = rem % 10;
        }

        String output = "";
        for (int i=0; i<loot.size(); i++) {
            output += loot.get(i).toString() + "\n";
        }
        output += "Gold: "+gold+", Silver: "+silver+", Copper: "+copper;

        output_tv.setText(output);
    }

    private void getRandomSpell(List<String> l, int cr) {
        Random ran = new Random();
        try {
            String spell="";
            while(spell.length()==0) {
                int x = ran.nextInt(spells.length());
                JSONObject o = spells.getJSONObject(x);
                String level = o.get("level").toString().replaceAll("\\D","").length() == 0 ? "0" : o.get("level").toString().replaceAll("\\D","");
                int lvl = 0;
                if (!level.equals("cantrip")) {
                    try {
                        lvl = Integer.valueOf(level);
                    }
                    catch (Exception e) { }
                }
                if (lvl < (cr / 2)+2) {
                    spell = "Spell Scroll: "+o.get("name").toString() + " (Level: "+level+")";
                }
            }
            l.add(spell);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getRandomObject(List<String> l, JSONObject o, String c) {
        Random ran = new Random();
        try {
            JSONArray a = o.getJSONArray(c);
            if (a.length() > 0) {
                int x = ran.nextInt(a.length());
                l.add(a.get(x).toString());
            }
        } catch (JSONException e) {
            Log.d("LootGen", e.getMessage());
            e.printStackTrace();
        }
    }
}

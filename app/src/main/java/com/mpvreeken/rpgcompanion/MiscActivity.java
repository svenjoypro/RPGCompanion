package com.mpvreeken.rpgcompanion;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

public class MiscActivity extends RPGCActivity {

    private JSONArray tavern_adjs, tavern_nouns, long_rests, road_encounters, romantic_love_interests, slums_encounters, strange_encounters, trail_encounters, backpack_contents_tools, backpack_contents_edibles, backpack_contents_personals, backpack_contents_strange, book_titles, necromancers_lair, curious_items, kitchen_items, magic_umbrellas, minor_magic_items, mundane_treasures, potion_colors, potion_scents, potion_appearances, potion_effects, ruined_building_contents, weapon_names, tavern_drinks, tavern_foods, deep_dark_woods, trinkets, magic_skulls, town_names, curses, flawed_utopias, lone_graves;
    private JSONArray good_smells, witches, monster_deaths, blacksmith_wares, suspicious_village_encounters, nice_village_encounters, jungle_encounters, misc_items, antagonist_bios, necromantic_effects, unique_horses, rich_corpses, zombie_variations, unique_spellbooks, gossip, plants_trees, rival_parties;
    private JSONArray interesting_books, stores_shops, dungeon_levers, pilgrimage_quests;
    private TextView output_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_misc);

        output_tv = findViewById(R.id.misc_output_tv);

        Button lre_btn = findViewById(R.id.misc_long_rest_encounters_btn);
        Button re_btn = findViewById(R.id.misc_road_encounters_btn);
        Button rli_btn = findViewById(R.id.misc_romantic_love_interests_btn);
        Button se_btn = findViewById(R.id.misc_slums_encounters_btn);
        Button sfe_btn = findViewById(R.id.misc_strange_encounters_btn);
        Button te_btn = findViewById(R.id.misc_trail_encounters_btn);
        Button sve_btn = findViewById(R.id.misc_suspicious_village_encounters_btn);
        Button nve_btn = findViewById(R.id.misc_nice_village_encounters_btn);
        Button je_btn = findViewById(R.id.misc_jungle_encounters_btn);

        Button bc_btn = findViewById(R.id.misc_backpack_contents_btn);
        Button bt_btn = findViewById(R.id.misc_book_titles_btn);
        Button tnl_btn = findViewById(R.id.misc_creepy_necromancers_lair_btn);
        Button ci_btn = findViewById(R.id.misc_curious_items_btn);
        Button ki_btn = findViewById(R.id.misc_kitchen_items_btn);
        Button mu_btn = findViewById(R.id.misc_magic_umbrellas_btn);
        Button mmi_btn = findViewById(R.id.misc_minor_magic_items_btn);
        Button mt_btn = findViewById(R.id.misc_mundane_treasures_btn);
        Button p_btn = findViewById(R.id.misc_potions_btn);
        Button rbc_btn = findViewById(R.id.misc_ruined_building_contents_btn);
        Button td_btn = findViewById(R.id.misc_tavern_drinks_btn);
        Button tf_btn = findViewById(R.id.misc_tavern_food_btn);
        Button tddw_btn = findViewById(R.id.misc_things_in_the_woods_btn);
        Button t_btn = findViewById(R.id.misc_trinkets_btn);
        Button ms_btn = findViewById(R.id.misc_magic_skulls_btn);
        Button bw_btn = findViewById(R.id.misc_blacksmith_wares_btn);
        Button mi_btn = findViewById(R.id.misc_misc_items_btn);
        Button rc_btn = findViewById(R.id.misc_rich_corpses_btn);
        Button sb_btn = findViewById(R.id.misc_unique_spellbooks_btn);
        Button pt_btn = findViewById(R.id.misc_plants_trees_btn);

        Button tn_btn = findViewById(R.id.misc_town_names_btn);
        Button tavern_btn = findViewById(R.id.misc_tavern_name_btn);
        Button wn_btn = findViewById(R.id.misc_weapon_names_btn);

        Button w_btn = findViewById(R.id.misc_witches_btn);
        Button ab_btn = findViewById(R.id.misc_antagonist_bios_btn);
        Button uh_btn = findViewById(R.id.misc_unique_horses_btn);
        Button zv_btn = findViewById(R.id.misc_zombie_variations_btn);
        Button rp_btn = findViewById(R.id.misc_rival_parties_btn);


        Button c_btn = findViewById(R.id.misc_curses_btn);
        Button fu_btn = findViewById(R.id.misc_flawed_utopias_btn);
        Button slg_btn = findViewById(R.id.misc_lone_graves_btn);
        Button gs_btn = findViewById(R.id.misc_good_smells_btn);
        Button md_btn = findViewById(R.id.misc_monster_deaths_btn);
        Button ne_btn = findViewById(R.id.misc_necromantic_effects_btn);
        Button g_btn = findViewById(R.id.misc_gossip_btn);

        Button ib_btn = findViewById(R.id.misc_interesting_books_btn);
        Button pq_btn = findViewById(R.id.misc_pilgrimage_quests_btn);
        Button dl_btn = findViewById(R.id.misc_dungeon_levers_btn);
        Button ss_btn = findViewById(R.id.misc_stores_shops_btn);





        View.OnClickListener buttonHandler = new View.OnClickListener() {
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.misc_long_rest_encounters_btn:
                        if (long_rests == null) { long_rests = loadJSON(R.raw.d100_long_rest_encounters, "long_rest_encounters"); }
                        showOutput(getRandomBasic(long_rests));
                        break;
                    case R.id.misc_road_encounters_btn:
                        if (road_encounters == null) { road_encounters = loadJSON(R.raw.d100_road_encounters, "road_encounters"); }
                        showOutput(getRandomBasic(road_encounters));
                        break;
                    case R.id.misc_romantic_love_interests_btn:
                        if (romantic_love_interests == null) { romantic_love_interests = loadJSON(R.raw.d100_romantic_love_interests, "romantic_love_interests"); }
                        showOutput(getRandomBasic(romantic_love_interests));
                        break;
                    case R.id.misc_slums_encounters_btn:
                        if (slums_encounters == null) { slums_encounters = loadJSON(R.raw.d100_slums_encounters, "slums_encounters"); }
                        showOutput(getRandomBasic(slums_encounters));
                        break;
                    case R.id.misc_strange_encounters_btn:
                        if (strange_encounters == null) { strange_encounters = loadJSON(R.raw.d100_strange_encounters, "strange_encounters"); }
                        showOutput(getRandomBasic(strange_encounters));
                        break;
                    case R.id.misc_trail_encounters_btn:
                        if (trail_encounters == null) { trail_encounters = loadJSON(R.raw.d100_trail_encounters, "trail_encounters"); }
                        showOutput(getRandomBasic(trail_encounters));
                        break;
                    case R.id.misc_backpack_contents_btn:
                        if (backpack_contents_tools == null) {
                            backpack_contents_tools = loadJSON(R.raw.d100_backpack_contents, "backpack_contents_tools");
                            backpack_contents_edibles = loadJSON(R.raw.d100_backpack_contents, "backpack_contents_edibles");
                            backpack_contents_personals = loadJSON(R.raw.d100_backpack_contents, "backpack_contents_personals");
                            backpack_contents_strange = loadJSON(R.raw.d100_backpack_contents, "backpack_contents_strange");
                        }
                        showOutput(generateBackpackContents());
                        break;
                    case R.id.misc_book_titles_btn:
                        if (book_titles == null) { book_titles = loadJSON(R.raw.d100_book_titles, "book_titles"); }
                        showOutput(getRandomBasic(book_titles));
                        break;
                    case R.id.misc_interesting_books_btn:
                        if (interesting_books == null) { interesting_books = loadJSON(R.raw.d100_interesting_books, "interesting_books"); }
                        showOutput(getRandomBasic(interesting_books));
                        break;
                    case R.id.misc_creepy_necromancers_lair_btn:
                        if (necromancers_lair == null) { necromancers_lair = loadJSON(R.raw.d100_creepy_things_in_necromancers_lair, "creepy_things_in_necromancers_lair"); }
                        showOutput(getRandomBasic(necromancers_lair));
                        break;
                    case R.id.misc_curious_items_btn:
                        if (curious_items == null) { curious_items = loadJSON(R.raw.d100_curious_items, "curious_items"); }
                        showOutput(getRandomBasic(curious_items));
                        break;
                    case R.id.misc_kitchen_items_btn:
                        if (kitchen_items == null) { kitchen_items = loadJSON(R.raw.d100_kitchen_items, "kitchen_items"); }
                        showOutput(getRandomBasic(kitchen_items));
                        break;
                    case R.id.misc_magic_umbrellas_btn:
                        if (magic_umbrellas == null) { magic_umbrellas = loadJSON(R.raw.d100_magic_umbrellas, "magic_umbrellas"); }
                        showOutput(getRandomBasic(magic_umbrellas));
                        break;
                    case R.id.misc_minor_magic_items_btn:
                        if (minor_magic_items == null) { minor_magic_items = loadJSON(R.raw.d100_minor_magic_items, "minor_magic_items"); }
                        showOutput(getRandomBasic(minor_magic_items));
                        break;
                    case R.id.misc_mundane_treasures_btn:
                        if (mundane_treasures == null) { mundane_treasures = loadJSON(R.raw.d100_mundane_treasures, "mundane_treasures"); }
                        showOutput(getRandomBasic(mundane_treasures));
                        break;
                    case R.id.misc_potions_btn:
                        if (potion_colors == null) {
                            potion_colors = loadJSON(R.raw.d100_potions, "potion_colors");
                            potion_scents = loadJSON(R.raw.d100_potions, "potion_scents");
                            potion_appearances = loadJSON(R.raw.d100_potions, "potion_appearances");
                            potion_effects = loadJSON(R.raw.d100_potions, "potion_effects");
                        }
                        showOutput(generatePotion());
                        break;
                    case R.id.misc_ruined_building_contents_btn:
                        if (ruined_building_contents == null) { ruined_building_contents = loadJSON(R.raw.d100_ruined_building_contents, "ruined_building_contents"); }
                        showOutput(getRandomBasic(ruined_building_contents));
                        break;
                    case R.id.misc_tavern_drinks_btn:
                        if (tavern_drinks == null) { tavern_drinks = loadJSON(R.raw.d100_tavern_drinks, "tavern_drinks"); }
                        showOutput(getRandomBasic(tavern_drinks));
                        break;
                    case R.id.misc_tavern_food_btn:
                        if (tavern_foods == null) { tavern_foods = loadJSON(R.raw.d100_tavern_food, "tavern_food"); }
                        showOutput(getRandomBasic(tavern_foods));
                        break;
                    case R.id.misc_things_in_the_woods_btn:
                        if (deep_dark_woods == null) { deep_dark_woods = loadJSON(R.raw.d100_deep_dark_woods, "things_in_the_deep_dark_woods"); }
                        showOutput(getRandomBasic(deep_dark_woods));
                        break;
                    case R.id.misc_trinkets_btn:
                        if (trinkets == null) { trinkets = loadJSON(R.raw.d100_trinkets, "trinkets"); }
                        showOutput(getRandomBasic(trinkets));
                        break;
                    case R.id.misc_magic_skulls_btn:
                        if (magic_skulls == null) { magic_skulls = loadJSON(R.raw.d100_magic_skulls, "magic_skulls"); }
                        showOutput(getRandomBasic(magic_skulls));
                        break;
                    case R.id.misc_town_names_btn:
                        if (town_names == null) { town_names = loadJSON(R.raw.d100_town_names, "town_names"); }
                        showOutput(getRandomBasic(town_names));
                        break;
                    case R.id.misc_weapon_names_btn:
                        if (weapon_names == null) { weapon_names = loadJSON(R.raw.d100_weapon_names, "weapon_names"); }
                        showOutput(getRandomBasic(weapon_names));
                        break;
                    case R.id.misc_curses_btn:
                        if (curses == null) { curses = loadJSON(R.raw.d100_curses, "curses"); }
                        showOutput(getRandomBasic(curses));
                        break;
                    case R.id.misc_flawed_utopias_btn:
                        if (flawed_utopias == null) { flawed_utopias = loadJSON(R.raw.d100_flawed_utopias, "flawed_utopias"); }
                        showOutput(getRandomBasic(flawed_utopias));
                        break;
                    case R.id.misc_lone_graves_btn:
                        if (lone_graves == null) { lone_graves = loadJSON(R.raw.d100_strange_lone_graves, "strange_lone_graves"); }
                        showOutput(getRandomBasic(lone_graves));
                        break;
                    case R.id.misc_tavern_name_btn:
                        if (tavern_adjs == null) {
                            tavern_adjs = loadJSON(R.raw.d100_tavern_names, "adjs");
                            tavern_nouns = loadJSON(R.raw.d100_tavern_names, "nouns");
                        }
                        showOutput(generateTavernName());
                        break;


                    case R.id.misc_good_smells_btn:
                        if (good_smells == null) {
                            good_smells = loadJSON(R.raw.d100_good_smells, "good_smells");
                        }
                        showOutput(getRandomBasic(good_smells));
                        break;
                    case R.id.misc_witches_btn:
                        if (witches == null) {
                            witches = loadJSON(R.raw.d100_witches, "witches");
                        }
                        showOutput(getRandomBasic(witches));
                        break;
                    case R.id.misc_monster_deaths_btn:
                        if (monster_deaths == null) {
                            monster_deaths = loadJSON(R.raw.d100_monster_deaths, "monster_deaths");
                        }
                        showOutput(getRandomBasic(monster_deaths));
                        break;
                    case R.id.misc_blacksmith_wares_btn:
                        if (blacksmith_wares == null) {
                            blacksmith_wares = loadJSON(R.raw.d100_blacksmith_wares, "blacksmith_wares");
                        }
                        showOutput(getRandomBasic(blacksmith_wares));
                        break;
                    case R.id.misc_suspicious_village_encounters_btn:
                        if (suspicious_village_encounters == null) {
                            suspicious_village_encounters = loadJSON(R.raw.d100_suspicious_village_encounters, "suspicious_village_encounters");
                        }
                        showOutput(getRandomBasic(suspicious_village_encounters));
                        break;
                    case R.id.misc_nice_village_encounters_btn:
                        if (nice_village_encounters == null) {
                            nice_village_encounters = loadJSON(R.raw.d100_nice_village_encounters, "nice_village_encounters");
                        }
                        showOutput(getRandomBasic(nice_village_encounters));
                        break;
                    case R.id.misc_jungle_encounters_btn:
                        if (jungle_encounters == null) {
                            jungle_encounters = loadJSON(R.raw.d100_jungle_encounters, "jungle_encounters");
                        }
                        showOutput(getRandomBasic(jungle_encounters));
                        break;
                    case R.id.misc_misc_items_btn:
                        if (misc_items == null) {
                            misc_items = loadJSON(R.raw.d100_misc_items, "misc_items");
                        }
                        showOutput(getRandomBasic(misc_items));
                        break;
                    case R.id.misc_antagonist_bios_btn:
                        if (antagonist_bios == null) {
                            antagonist_bios = loadJSON(R.raw.d100_antagonist_bios, "antagonist_bios");
                        }
                        showOutput(getRandomBasic(antagonist_bios));
                        break;
                    case R.id.misc_necromantic_effects_btn:
                        if (necromantic_effects == null) {
                            necromantic_effects = loadJSON(R.raw.d100_necromantic_effects, "necromantic_effects");
                        }
                        showOutput("Allows caster to: "+getRandomBasic(necromantic_effects));
                        break;
                    case R.id.misc_unique_horses_btn:
                        if (unique_horses == null) {
                            unique_horses = loadJSON(R.raw.d100_unique_horses, "unique_horses");
                        }
                        showOutput(getRandomBasic(unique_horses));
                        break;
                    case R.id.misc_rival_parties_btn:
                        if (rival_parties == null) {
                            rival_parties = loadJSON(R.raw.d100_rival_parties, "rival_parties");
                        }
                        showOutput(getRandomBasic(rival_parties));
                        break;
                    case R.id.misc_rich_corpses_btn:
                        if (rich_corpses == null) {
                            rich_corpses = loadJSON(R.raw.d100_rich_corpses, "rich_corpses");
                        }
                        showOutput(getRandomBasic(rich_corpses));
                        break;
                    case R.id.misc_zombie_variations_btn:
                        if (zombie_variations == null) {
                            zombie_variations = loadJSON(R.raw.d100_zombie_variations, "zombie_variations");
                        }
                        showOutput(getRandomBasic(zombie_variations));
                        break;
                    case R.id.misc_unique_spellbooks_btn:
                        if (unique_spellbooks == null) {
                            unique_spellbooks = loadJSON(R.raw.d100_unique_spellbooks, "unique_spellbooks");
                        }
                        showOutput(getRandomBasic(unique_spellbooks));
                        break;
                    case R.id.misc_plants_trees_btn:
                        if (plants_trees == null) {
                            plants_trees = loadJSON(R.raw.d100_plants_trees, "plants_trees");
                        }
                        showOutput(getRandomBasic(plants_trees));
                        break;
                    case R.id.misc_gossip_btn:
                        if (gossip == null) {
                            gossip = loadJSON(R.raw.d100_gossip, "gossip");
                        }
                        showOutput(getRandomBasic(gossip));
                        break;
                    case R.id.misc_stores_shops_btn:
                        if (stores_shops == null) {
                            stores_shops = loadJSON(R.raw.d100_stores_shops, "stores_shops");
                        }
                        showOutput(getRandomBasic(stores_shops));
                        break;
                    case R.id.misc_dungeon_levers_btn:
                        if (dungeon_levers == null) {
                            dungeon_levers = loadJSON(R.raw.d100_dungeon_levers, "dungeon_levers");
                        }
                        showOutput(getRandomBasic(dungeon_levers));
                        break;
                    case R.id.misc_pilgrimage_quests_btn:
                        if (pilgrimage_quests == null) {
                            pilgrimage_quests = loadJSON(R.raw.d100_pilgrimage_quests, "pilgrimage_quests");
                        }
                        showOutput(getRandomBasic(pilgrimage_quests));
                        break;
                    default:

                }
            }
        };

        lre_btn.setOnClickListener(buttonHandler);
        re_btn.setOnClickListener(buttonHandler);
        rli_btn.setOnClickListener(buttonHandler);
        se_btn.setOnClickListener(buttonHandler);
        sfe_btn.setOnClickListener(buttonHandler);
        te_btn.setOnClickListener(buttonHandler);
        bc_btn.setOnClickListener(buttonHandler);
        bt_btn.setOnClickListener(buttonHandler);
        tnl_btn.setOnClickListener(buttonHandler);
        ci_btn.setOnClickListener(buttonHandler);
        ki_btn.setOnClickListener(buttonHandler);
        mu_btn.setOnClickListener(buttonHandler);
        mmi_btn.setOnClickListener(buttonHandler);
        mt_btn.setOnClickListener(buttonHandler);
        p_btn.setOnClickListener(buttonHandler);
        rbc_btn.setOnClickListener(buttonHandler);
        td_btn.setOnClickListener(buttonHandler);
        tf_btn.setOnClickListener(buttonHandler);
        tddw_btn.setOnClickListener(buttonHandler);
        t_btn.setOnClickListener(buttonHandler);
        ms_btn.setOnClickListener(buttonHandler);
        tn_btn.setOnClickListener(buttonHandler);
        tavern_btn.setOnClickListener(buttonHandler);
        wn_btn.setOnClickListener(buttonHandler);
        c_btn.setOnClickListener(buttonHandler);
        fu_btn.setOnClickListener(buttonHandler);
        slg_btn.setOnClickListener(buttonHandler);

        sve_btn.setOnClickListener(buttonHandler);
        nve_btn.setOnClickListener(buttonHandler);
        je_btn.setOnClickListener(buttonHandler);
        bw_btn.setOnClickListener(buttonHandler);
        mi_btn.setOnClickListener(buttonHandler);
        rc_btn.setOnClickListener(buttonHandler);
        sb_btn.setOnClickListener(buttonHandler);
        pt_btn.setOnClickListener(buttonHandler);
        w_btn.setOnClickListener(buttonHandler);
        ab_btn.setOnClickListener(buttonHandler);
        uh_btn.setOnClickListener(buttonHandler);
        zv_btn.setOnClickListener(buttonHandler);
        gs_btn.setOnClickListener(buttonHandler);
        md_btn.setOnClickListener(buttonHandler);
        ne_btn.setOnClickListener(buttonHandler);
        g_btn.setOnClickListener(buttonHandler);
        rp_btn.setOnClickListener(buttonHandler);

        ib_btn.setOnClickListener(buttonHandler);
        pq_btn.setOnClickListener(buttonHandler);
        dl_btn.setOnClickListener(buttonHandler);
        ss_btn.setOnClickListener(buttonHandler);

    }

    private String getRandomBasic(JSONArray a) {
        return getRandom(a);
    }

    private void showOutput(String s) {
        output_tv.setText(s);
    }

    private JSONArray loadJSON(int r, String s) {
        JSONArray a = new JSONArray();
        try {
            a = getRawData(r).getJSONArray(s);
        } catch (JSONException e) {
            Log.e("MiscActivity", e.getMessage());
            e.printStackTrace();
        }
        return a;
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

    public String generatePotion() {
        String potion = "Color: "+getRandom(potion_colors);
        potion += "\nAppearance: "+getRandom(potion_appearances);
        potion += "\nSmell/Flavor: "+getRandom(potion_scents);
        potion += "\nEffect: "+getRandom(potion_effects);

        return potion;
    }

    public String generateBackpackContents() {
        String b = "Contents: ";
        b += getRandom(backpack_contents_tools) + ", ";
        b += getRandom(backpack_contents_edibles) + ", ";
        b += getRandom(backpack_contents_personals) + ", and ";
        b += getRandom(backpack_contents_strange);

        return b;
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
            Log.e("MiscActivity", e.getMessage());
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

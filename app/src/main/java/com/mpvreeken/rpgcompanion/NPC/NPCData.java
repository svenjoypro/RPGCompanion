package com.mpvreeken.rpgcompanion.NPC;

import android.content.Context;
import android.util.Log;

import com.mpvreeken.rpgcompanion.R;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.lang.Math.floor;
import static java.lang.Math.pow;


/**
 * Parses string from RAW json file with all NPC data in it
 *
 * Also provides methods to get random NPC details and parses any {{variables}} in the details
 *
 * @author Michael Vreeken
 * @version 2017.0727
 * @since 1.0
 */

public class NPCData {

    /**
     * Arrays that hold lists of each of the different detail strings from the json data file
     */
    public JSONArray appearances, bonds, flaws, ideals, voices, curses, blessings, details, entities,
            obsessions, destinations, items, motivations, titles, personalities,
            cityPOI, professions, quirks, gods, lifeEvents, traits, relatives,
            relationships, hooks, racesList, racesCommon, racesModerate, racesRare;
    public JSONObject jRaces;
    public ArrayList<Race> races = new ArrayList<Race>();

    private Context context;

    /**
     * map of {{variables}} found in the json data file to jsonArray members of this class
     */
    Map<String, JSONArray> parseMap;


    /**
     * Constructor that takes in the json format string of all the raw NPC data and parses it into
     * an accessible class
     *     *
     */
    public NPCData(Context context) {
        this.context = context;



        try {
            appearances = getRawData(R.raw.npc_appearances).getJSONArray("appearances");
            blessings = getRawData(R.raw.npc_blessings).getJSONArray("blessings");
            bonds = getRawData(R.raw.npc_bonds).getJSONArray("bonds");
            cityPOI = getRawData(R.raw.npc_city_points_of_interest).getJSONArray("city-points-of-interest");
            curses = getRawData(R.raw.npc_curses).getJSONArray("curses");
            destinations = getRawData(R.raw.npc_destinations).getJSONArray("destinations");
            details = getRawData(R.raw.npc_details).getJSONArray("details");
            entities = getRawData(R.raw.npc_entities).getJSONArray("entities");
            flaws = getRawData(R.raw.npc_flaws).getJSONArray("flaws");
            gods = getRawData(R.raw.npc_gods).getJSONArray("gods");
            hooks = getRawData(R.raw.npc_hooks).getJSONArray("hooks");
            ideals = getRawData(R.raw.npc_ideals).getJSONArray("ideals");
            items = getRawData(R.raw.npc_items).getJSONArray("items");
            lifeEvents = getRawData(R.raw.npc_life_events).getJSONArray("life-events");
            motivations = getRawData(R.raw.npc_motivations).getJSONArray("motivations");
            obsessions = getRawData(R.raw.npc_obsessions).getJSONArray("obsessions");
            personalities = getRawData(R.raw.npc_personalities).getJSONArray("personalities");
            professions = getRawData(R.raw.npc_professions).getJSONArray("professions");
            quirks = getRawData(R.raw.npc_quirks).getJSONArray("quirks");
            relationships = getRawData(R.raw.npc_relationships).getJSONArray("relationships");
            relatives = getRawData(R.raw.npc_relatives).getJSONArray("relatives");
            titles = getRawData(R.raw.npc_titles).getJSONArray("titles");
            traits = getRawData(R.raw.npc_traits).getJSONArray("traits");
            voices = getRawData(R.raw.npc_voices).getJSONArray("voices");


            jRaces = getRawData(R.raw.npc_races);

            racesList = jRaces.getJSONArray("races-list");
            racesCommon = jRaces.getJSONArray("races-common");
            racesModerate = jRaces.getJSONArray("races-moderate");
            racesRare = jRaces.getJSONArray("races-rare");

            JSONArray jr = jRaces.getJSONArray("races");
            int l=jr.length();
            for (int i=0; i<l; i++) {
                races.add(new Race((JSONObject) jr.get(i)));
            }


            parseMap = new HashMap<>();
            parseMap.put("curse", curses);
            parseMap.put("blessing", blessings);
            parseMap.put("destination", destinations);
            parseMap.put("item", items);
            parseMap.put("obsession", obsessions);
            parseMap.put("entity", entities);
            parseMap.put("relative", relatives);
            parseMap.put("god", gods);
            parseMap.put("profession", professions);
            parseMap.put("race", racesList);
        } catch (JSONException e) {
            Log.e("NPCData", e.getMessage());
            e.printStackTrace();
        }
    }

    public JSONObject getRawData(int res) {
        //Read in the raw json data from resource folder
        InputStream is = context.getResources().openRawResource(res);
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
            Log.e("NPCData", e.getMessage());
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

    public String getRandomDetail() {
        return parseMe(getRandom(details));
    }
    public String getRandomAppearance() {
        return parseMe(getRandom(appearances));
    }
    public String getRandomBond() {
        return parseMe(getRandom(bonds));
    }
    public String getRandomFlaw() {
        return parseMe(getRandom(flaws));
    }
    public String getRandomIdeal() {
        return parseMe(getRandom(ideals));
    }
    public String getRandomVoice() {
        return parseMe(getRandom(voices));
    }
    public String getRandomProfession() {
        return parseMe(getRandom(professions));
    }
    public String getRandomQuirk() {
        return parseMe(getRandom(quirks));
    }
    public String getRandomLifeEvent() {
        return parseMe(getRandom(lifeEvents));
    }
    public String getRandomTrait() {
        return parseMe(getRandom(traits));
    }
    public String getRandomRelationship() {
        return parseMe(getRandom(relationships));
    }
    public String getRandomHook() {
        return parseMe(getRandom(hooks));
    }
    public String getRandomMotivation() {
        return parseMe(getRandom(motivations));
    }

    public String getRandomGod() {
        Random ran = new Random();
        //Exponentially weight the front of the gods array, which are more good/neutral
        //while evil are more toward the end of the array
        Double r = ran.nextInt(100)/100.00;
        Double d = floor( pow(  r, 2)*gods.length() );
        int x = d.intValue();

        String god = "Tymora";
        try {
            god = gods.getString(x);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return god;
    }

    public String getRandomWorshipHabit() {
        //TODO put these into .json file in RAW?
        String[] adv = {"secretly worships", "discretely worships", "proudly worships", "loudly worships", "claims to, but doesn't actually worship", "zealously worships"};

        Random ran = new Random();
        int x = ran.nextInt(adv.length);

        return adv[x] + " " + getRandomGod();
    }



    public String getRandomPersonality() {
        Random ran = new Random();

        int x = ran.nextInt(personalities.length());
        int x2 = ran.nextInt(personalities.length());
        while (x2 == x) {
            x2 = ran.nextInt(personalities.length());
        }
        int x3 = ran.nextInt(personalities.length());
        while (x3 == x || x3 == x2) {
            x3 = ran.nextInt(personalities.length());
        }

        String personality = "Friendly, Honest, and Patient";

        try {
            personality = personalities.getString(x)+", "+personalities.getString(x2)+", and "+personalities.getString(x3);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseMe(personality);
    }

    //3% chance of having a title (Samwise "the Brave")
    public String getRandomTitle() {
        Random ran = new Random();
        String title = "";
        int x = ran.nextInt(100);
        if (x<3) {
            x = ran.nextInt(titles.length());
            try {
                title = " "+titles.getString(x);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return title;
    }

    /**
     * Method that searches a string for {{variables}} and replaces them with randomly chosen
     *  values from the mapped arrays
     *
     * @param s raw string to be parsed
     *
     * @return Parsed string with randomly chosen values
     */
    public String parseMe(String s) {

        String k;
        Random ran = new Random();
        int x, i, j;
        JSONArray a;

        while (s.indexOf("{{") >= 0) {
            i = s.indexOf("{{");
            j = s.indexOf("}}");

            k = s.substring(i+2, j);
            a = parseMap.get(k);
            if (a == null) {
                s = s.substring(0, i) + " ... " + s.substring(j + 2);
            }

            x = ran.nextInt(a.length());

            try {
                s = s.substring(0, i) + a.get(x) + s.substring(j + 2);
            } catch (JSONException e) {
                s = s.substring(0, i) + " ... " + s.substring(j + 2);
                Log.e("NPCData", e.getMessage());
                e.printStackTrace();
            }
        }

        return s;
    }

    /**
     * Method that randomly selects a new NPC race, detailed generation of NPC comes later
     *
     * @return NPC object with randomly generated race
     */
    public NPC getRandomNPC() {
        Race race = races.get(18);//default to Human

        try {
            //Randomly choose a number between 0 and 99
            //to choose race based off of rarity
            Random ran = new Random();
            int x = ran.nextInt(100);
            String raceName;
            if (x < 5) {
                x = ran.nextInt(racesRare.length());
                raceName = racesRare.getString(x);
            }
            else if (x < 30) {
                x = ran.nextInt(racesModerate.length());
                raceName = racesModerate.getString(x);
            }
            else {
                x = ran.nextInt(racesCommon.length());
                raceName = racesCommon.getString(x);
            }
            for (int i = 0; i< races.size(); i++) {
                if (races.get(i).name.equals(raceName)) {
                    //Clone instance of the chosen race
                    race = new Race(races.get(i));
                    break;
                }
            }
        } catch (JSONException e) {
            Log.e("NPCData", e.getMessage());
            e.printStackTrace();
        }

        NPC npc = new NPC(race, this);
        return npc;
    }
}

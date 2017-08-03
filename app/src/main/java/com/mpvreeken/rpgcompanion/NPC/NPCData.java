package com.mpvreeken.rpgcompanion.NPC;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.lang.Math.ceil;
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
    public JSONArray curses, blessings, details, entities,
            obsessions, destinations, items,motivations, titles, personalities,
            professions, quirks, gods, lifeEvents, traits, relatives, relationships, hooks, racesList, racesCommon, racesModerate, racesRare, jRaces;
    public ArrayList<Race> races = new ArrayList<Race>();

    /**
     * map of {{variables}} found in the json data file to jsonArray members of this class
     */
    Map<String, JSONArray> parseMap;


    /**
     * Constructor that takes in the json format string of all the raw NPC data and parses it into
     * an accessible class
     *
     * @param json json data in String format
     *
     */
    public NPCData(String json) {
        try {
            JSONObject j = new JSONObject(json);

            curses = j.getJSONArray("curses");
            blessings = j.getJSONArray("blessings");
            details = j.getJSONArray("details");
            entities = j.getJSONArray("entities");
            obsessions = j.getJSONArray("obsessions");
            motivations = j.getJSONArray("motivations");
            destinations = j.getJSONArray("destinations");
            items = j.getJSONArray("items");
            titles = j.getJSONArray("titles");
            personalities = j.getJSONArray("personalities");
            professions = j.getJSONArray("professions");
            quirks = j.getJSONArray("quirks");
            gods = j.getJSONArray("gods");
            lifeEvents = j.getJSONArray("lifeevents");
            traits = j.getJSONArray("traits");
            relatives = j.getJSONArray("relatives");
            relationships = j.getJSONArray("relationships");
            hooks = j.getJSONArray("hooks");

            racesList = j.getJSONArray("races-list");
            racesCommon = j.getJSONArray("races-common");
            racesModerate = j.getJSONArray("races-moderate");
            racesRare = j.getJSONArray("races-rare");

            jRaces = j.getJSONArray("races");
            int l=jRaces.length();
            for (int i=0; i<l; i++) {
                races.add(new Race((JSONObject) jRaces.get(i)));
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

    /**
     * Method that selects a random "detail" from the details JSONArray
     * Then passes it through the parseMe method to replace any {{variables}} with
     * randomly chosen values
     *
     * @return The clean, legible String containing the randomly chosen NPC detail
     */
    public String getRandomDetail() {
        String detail = "Owns a map to the city they're in";
        try {
            Random ran = new Random();
            int x = ran.nextInt(details.length());

            detail = details.getString(x);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return parseMe(detail);
    }

    public String getRandomProfession() {
        Random ran = new Random();
        int x = ran.nextInt(professions.length());
        String profession = "Merchant";
        try {
            profession = professions.getString(x);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return profession;
    }

    public String getRandomQuirk() {
        Random ran = new Random();
        int x = ran.nextInt(quirks.length());
        String quirk = "doesn't like change";
        try {
            quirk = quirks.getString(x);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseMe(quirk);
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
        String[] adv = {"secretly worships", "discretely worships", "proudly worships", "loudly worships", "claims to, but doesn't actually worship", "zealously worships"};

        Random ran = new Random();
        int x = ran.nextInt(adv.length);

        return adv[x] + " " + getRandomGod();
    }

    public String getRandomLifeEvent() {
        Random ran = new Random();
        int x = ran.nextInt(lifeEvents.length());
        String lifeEvent = "just got back from vacation";
        try {
            lifeEvent = lifeEvents.getString(x);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseMe(lifeEvent);
    }

    public String getRandomTrait() {
        Random ran = new Random();
        int x = ran.nextInt(traits.length());
        String trait = "is always prepared";
        try {
            trait = traits.getString(x);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseMe(trait);
    }

    public String getRandomRelationship() {
        Random ran = new Random();
        int x = ran.nextInt(relationships.length());
        String relationship = "single";
        try {
            relationship = relationships.getString(x);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseMe(relationship);
    }

    public String getRandomHook() {
        Random ran = new Random();
        int x = ran.nextInt(hooks.length());
        String hook = "needs help finding their lost dog";
        try {
            hook = hooks.getString(x);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseMe(hook);
    }

    public String getRandomMotivation() {
        Random ran = new Random();
        int x = ran.nextInt(motivations.length());
        String motivation = "Past mistakes";
        try {
            motivation = motivations.getString(x);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseMe(motivation);
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

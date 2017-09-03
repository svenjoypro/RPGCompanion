package com.mpvreeken.rpgcompanion.NPC;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * Contains different Race data, as well as a randomize method to randomize details
 *
 *
 * @author Michael Vreeken
 * @version 2017.0727
 * @since 1.0
 */

public class Race {

    public String name, size, speed, flySpeed, source;

    //Initial ability score stats all set to 8
    public int[] asi = {8,8,8,8,8,8};
    public ArrayList<String> languages = new ArrayList<String>();
    public ArrayList<String> extras = new ArrayList<String>();
    public JSONObject jNames;
    public JSONArray jSubRace;
    public String myName;
    public String sex = "Male";
    public String ageDescriptive;
    public int agePercent;


    public Race(JSONObject o) {
        //Initialize Race data from JSON
        Random ran = new Random();
        try {
            //Initialize all values with the corresponding JSON value or ""
            name = o.has("name") ? o.getString("name") : "";
            size = o.has("size") ? o.getString("size") : "";
            speed = o.has("speed") ? o.getString("speed") : "";
            flySpeed = o.has("fly-speed") ? o.getString("fly-speed") : "";
            source = o.has("source") ? o.getString("source") : "";

            //Copy ASI array
            JSONArray jAsi = o.has("asi") ? o.getJSONArray("asi") : new JSONArray();
            int l = jAsi.length();
            for (int i=0; i<l; i++) {
                asi[i] += Integer.parseInt(jAsi.getString(i));
            }

            //Transfer languages array to internal ArrayList
            JSONArray jLanguages = o.has("languages") ? o.getJSONArray("languages") : new JSONArray();
            l = jLanguages.length();
            for (int i=0; i<l; i++) {
                languages.add(jLanguages.getString(i));
            }

            //Transfer racial extras array to internal ArrayList,
            JSONArray jExtras = o.has("extras") ? o.getJSONArray("extras") : new JSONArray();
            l = jExtras.length();
            for (int i=0; i<l; i++) {
                extras.add(jExtras.getString(i));
            }

            //Retain array of names for use in randomize()
            jNames = o.has("names") ? o.getJSONObject("names") : new JSONObject();
            //Retain array of sub-races for use in randomize()
            jSubRace = o.has("subRace") ? o.getJSONArray("subRace") : new JSONArray();
        } catch (JSONException e) {
            Log.e("Race", e.getMessage());
            e.printStackTrace();
        }
    }

    public Race(String rname, String rsize, String rspeed, String rflyspeed, String rsource, int[] rasi, ArrayList<String> rlanguages, ArrayList<String> rextras, JSONObject rjnames, JSONArray rjsubrace, String rmyname, String rsex) {
        //For cloning
        this.name = rname;
        this.size = rsize;
        this.speed = rspeed;
        this.flySpeed = rflyspeed;
        this.source = rsource;
        //Have to clone these arrays or else java just points to the original array
        this.asi = rasi.clone();
        this.languages = (ArrayList<String>) rlanguages.clone();
        this.extras = (ArrayList<String>) rextras.clone();

        this.jNames = rjnames;
        this.jSubRace = rjsubrace;
        this.myName = rmyname;
        this.sex = rsex;
    }

    public Race(Race r) {
        //For cloning
        this(r.name, r.size, r.speed, r.flySpeed, r.source, r.asi, r.languages, r.extras, r.jNames, r.jSubRace, r.myName, r.sex);
    }

    public void randomize() {
        //Wrap everything in JSONException try/catch block
        try {
            //Name and Sex
            Random ran = new Random();
            int x;
            //Names can be either simple (single name, no sex)
            //or male/female - sometimes with a last name
            if (jNames.has("simple")) {
                sex="N/A";
                JSONArray simple = jNames.getJSONArray("simple");
                //Get a random simple name
                x = ran.nextInt(simple.length());
                myName = simple.getString(x);
            }
            else {
                //Randomize sex and set appropriate names array
                x=ran.nextInt(2);
                JSONArray names;
                if (x==0) {
                    names = jNames.getJSONArray("male");
                }
                else {
                    names = jNames.getJSONArray("female");
                    sex="Female";
                }
                //Get a random name
                x = ran.nextInt(names.length());
                myName = names.getString(x);

                //Check if last names are applicable to race
                if (jNames.has("last")) {
                    JSONArray lasts = jNames.getJSONArray("last");
                    x = ran.nextInt(lasts.length());
                    myName += " "+lasts.getString(x);
                }
            }

            //Age
            agePercent = ran.nextInt(90)+10;
            if (agePercent<18) { ageDescriptive = "Youth"; }
            else if (agePercent<25) { ageDescriptive="Young Adult"; }
            else if (agePercent<50) { ageDescriptive="Prime Adult"; }
            else if (agePercent<75) { ageDescriptive="Older Adult"; }
            else if (agePercent<90) { ageDescriptive="Elderly"; }
            else if (agePercent<99) { ageDescriptive="Very Old"; }
            else { ageDescriptive = "On their death bed"; }

            //Sub-race
            if (jSubRace.length() > 0) {
                x = ran.nextInt(jSubRace.length());
                JSONObject sub = jSubRace.getJSONObject(x);

                //Change name/source if they are different for the sub-race
                name = sub.has("name") ? sub.getString("name") : name;
                source = sub.has("source") ? sub.getString("source") : source;

                //Add any extra languages to array
                //TODO write a list of languages to RAW json, then if we get "+1 language"
                //  randomly select a language, make sure they don't already know that language
                if (sub.has("languages")){
                    JSONArray jLangs = sub.getJSONArray("languages");
                    int l = jLangs.length();
                    for (int i=0; i<l; i++) {
                        languages.add(jLangs.getString(i));
                    }
                }


                //Add any racial extras to array
                if (sub.has("extras")){
                    JSONArray jExtras = sub.getJSONArray("extras");
                    int l = jExtras.length();
                    for (int i=0; i<l; i++) {
                        //Check if the skill is -SKILL, if it is, remove from extras
                        String e = jExtras.getString(i);
                        if (e.indexOf("-") == 0) {
                            int j = extras.indexOf(e.substring(1));
                            if (j>=0) {
                                extras.remove(j);
                            }
                        }
                        else {
                            extras.add(e);
                        }
                    }
                }

                //Loop through racial extras and increase ability score if it is in the array
                //We do this now instead of within the above loop because some racial increases
                //are in "parent" races, not just sub races, and if we randomized them at
                //race generation they would always be the same each time we got that race
                int l = extras.size();
                for (int i=0; i<l; i++) {
                    String e = extras.get(i);
                    if (e.equals("+1 ASI")) {
                        //Increase random ASI
                        x = ran.nextInt(asi.length);
                        asi[x]+=1;
                    }
                }

                //Increase/decrease any ASI's
                JSONArray jAsi = sub.has("asi") ? sub.getJSONArray("asi") : new JSONArray();
                l = jAsi.length();
                for (int i=0; i<l; i++) {
                    asi[i] += Integer.parseInt(jAsi.getString(i));
                }


                //Now just randomize their ability scores a bit for fun
                l = asi.length;
                for (int i=0; i<l; i++) {
                    //Random number between -2 and +3
                    asi[i] += (ran.nextInt(6)-2);
                }
            }
        } catch (JSONException e) {
            Log.e("Race", e.getMessage());
            e.printStackTrace();
        }
    }
}

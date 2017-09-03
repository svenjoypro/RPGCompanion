package com.mpvreeken.rpgcompanion.NPC;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Arrays;

import static android.icu.lang.UProperty.INT_START;

/**
 * Parses NPC object class to hold all info for a randomly generated NPC
 *
 * Also provides an output method to return a nicely formatted string of all the NPC data
 *
 * @author Michael Vreeken
 * @version 2017.0727
 * @since 1.0
 */

public class NPC {

    public Race race;

    public String name, detail, profession, motivation, personality, quirk, worshipHabit, lifeEvent,
            appearance, bond, flaw, ideal, voice, title, trait, relationship, hook;

    public NPC(Race r, NPCData d) {
        race = r;

        //Randomize race details
        race.randomize();
        name = race.myName;

        //Randomize NPC details
        appearance = d.getRandomAppearance();
        bond = d.getRandomBond();
        flaw = d.getRandomFlaw();
        ideal = d.getRandomIdeal();
        voice = d.getRandomVoice();
        title = d.getRandomTitle();
        detail = d.getRandomDetail();
        profession = d.getRandomProfession();
        motivation = d.getRandomMotivation();
        personality = d.getRandomPersonality();
        quirk = d.getRandomQuirk();
        worshipHabit = d.getRandomWorshipHabit();
        lifeEvent = d.getRandomLifeEvent();
        trait = d.getRandomTrait();
        relationship = d.getRandomRelationship();
        hook = d.getRandomHook();
    }

    /**
     * Returns a string formatted so that the first String is bold, and append the second string
     *
     * @param p prefix string to be bolded
     * @param s data string to be appended unbolded
     *
     * @return Formatted SpannableString Builder
     */
    public SpannableStringBuilder formatString(String p, String s) {
        SpannableStringBuilder str = new SpannableStringBuilder(p);
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, p.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str.append(s);
    }

    /**
     * Method that outputs all of the NPC's data in a long \n delineated string, nicely formatted
     *
     * @return SpannableStringBuilder output of npc data to be set into a TextView
     */
    public SpannableStringBuilder output() {
        //Make things nice to read, bold all descriptive titles
        SpannableStringBuilder s = new SpannableStringBuilder();

        s.append(formatString("Name: ",name+" "+title));
        s.append(formatString("\nRace: ", race.name));
        s.append(formatString("\nSex: ", race.sex));
        s.append(formatString("\nAge: ", race.ageDescriptive+" ("+ race.agePercent+"% of their lifespan)"));
        s.append(formatString("\nAppearance: ",appearance));
        s.append(formatString("\nVoice: ",voice));
        s.append(formatString("\nPersonality: ",personality));
        s.append(formatString("\nTrait: ",trait));
        s.append(formatString("\nBond: ",bond));
        s.append(formatString("\nMotivated by: ",motivation));
        s.append(formatString("\nIdeal: ",ideal));
        s.append(formatString("\nFlaw: ",flaw));
        s.append(formatString("\nQuirk: ",quirk));
        s.append(formatString("\nDetail: ",detail));

        s.append(formatString("\nProfession: ",profession));
        s.append(formatString("\nReligion: ",worshipHabit));
        s.append(formatString("\nRelationship Status: ",relationship));
        s.append(formatString("\nLife Event: ",lifeEvent));
        s.append(formatString("\nSpeed: ", race.speed));
        //Check if there is a fly speed, only show if there is
        if (race.flySpeed.length()>0) {
            //Same line
            s.append(formatString(", Fly Speed: ", race.flySpeed));
        }

        //ASIs
        s.append(formatString("\nSTR: ",String.valueOf(race.asi[0])));
        s.append(formatString("   DEX: ",String.valueOf(race.asi[1])));
        s.append(formatString("\nCON: ",String.valueOf(race.asi[2])));
        s.append(formatString("   INT: ",String.valueOf(race.asi[3])));
        s.append(formatString("\nWIS: ",String.valueOf(race.asi[4])));
        s.append(formatString("   CHA: ",String.valueOf(race.asi[5])));

        //Simple output of array, could be nicer
        s.append(formatString("\nLanguages: ", Arrays.toString(race.languages.toArray())));
        //Simple output of array, could be nicer
        s.append(formatString("\nRacial Extras: ", Arrays.toString(race.extras.toArray())));
        s.append(formatString("\nPlot Hook: ",hook));

        return s;
    }

}

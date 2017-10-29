package com.mpvreeken.rpgcompanion.NPC;
import android.text.Spannable;
import android.text.SpannableStringBuilder;

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


    public String name, detail, profession, motivation, personality, quirk, worshipHabit, lifeEvent,
            appearance, bond, flaw, ideal, voice, trait, relationship, hook, raceName, sex, age, speed, flySpeed, languages, racials,
            strength, dexterity, constitution, intelligence, wisdom, charisma, summary;

    public static final String DEFAULT_SUMMARY = "*EDIT THIS* This will be displayed as a summary on your list of saved NPCs";

    public NPC(Race race, NPCData d) {


        //Randomize race details
        race.randomize();
        name = race.myName;
        name += d.getRandomTitle();
        raceName = race.name;
        age = race.ageDescriptive+" ("+ race.agePercent+"% of their lifespan)";
        sex = race.sex;
        speed = race.speed;
        flySpeed = race.flySpeed;
        languages = race.languages.toString().substring(1, race.languages.toString().length()-1);
        racials = race.extras.toString().substring(1, race.extras.toString().length()-1);
        strength = String.valueOf(race.asi[0]) + " (" + (Integer.valueOf(race.asi[0]/2)-5) + ")";
        dexterity = String.valueOf(race.asi[1]) + " (" + (Integer.valueOf(race.asi[1]/2)-5) + ")";
        constitution = String.valueOf(race.asi[2]) + " (" + (Integer.valueOf(race.asi[2]/2)-5) + ")";
        intelligence = String.valueOf(race.asi[3]) + " (" + (Integer.valueOf(race.asi[3]/2)-5) + ")";
        wisdom = String.valueOf(race.asi[4]) + " (" + (Integer.valueOf(race.asi[4]/2)-5) + ")";
        charisma = String.valueOf(race.asi[5]) + " (" + (Integer.valueOf(race.asi[5]/2)-5) + ")";


        //Randomize NPC details
        appearance = d.getRandomAppearance();
        bond = d.getRandomBond();
        flaw = d.getRandomFlaw();
        ideal = d.getRandomIdeal();
        voice = d.getRandomVoice();

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

        summary = DEFAULT_SUMMARY;
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
    /*
    public SpannableStringBuilder output() {
        //Make things nice to read, bold all descriptive titles
        SpannableStringBuilder s = new SpannableStringBuilder();

        s.append(formatString("Name: ",name));
        s.append(formatString("\nRace: ", raceName));
        s.append(formatString("\nSex: ", sex));
        s.append(formatString("\nAge: ", age));
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
        s.append(formatString("\nSpeed: ", speed));
        //Check if there is a fly speed, only show if there is
        if (flySpeed.length()>0) {
            //Same line
            s.append(formatString(", Fly Speed: ", flySpeed));
        }

        s.append(formatString("\nSTR: ",strength));
        s.append(formatString("   DEX: ",dexterity));
        s.append(formatString("\nCON: ",constitution));
        s.append(formatString("   INT: ",intelligence));
        s.append(formatString("\nWIS: ",wisdom));
        s.append(formatString("   CHA: ",charisma));
        s.append(formatString("\nLanguages: ", languages));
        s.append(formatString("\nRacial Extras: ", racials));
        s.append(formatString("\nPlot Hook: ",hook));

        return s;
    }
    */


    public String getRace() {return raceName;}
    public String getSex() {return sex;}
    public String getAge() {return age;}
    public String getSpeed() {return speed;}
    public String getFlySpeed() {
        if (flySpeed != null && flySpeed.length()>0) {return flySpeed;}
        else {return "0";}
    }
    public String getLanguages() {return languages;}
    public String getRacial() {return racials;}

    public String getStrength() {return strength;}
    public String getDexterity() {return dexterity;}
    public String getConstitution() {return constitution;}
    public String getIntelligence() {return intelligence;}
    public String getWisdom() {return wisdom;}
    public String getCharisma() {return charisma;}


    public String getName() {return name;}
    public String getDetail() {return detail;}
    public String getProfession() {return profession;}
    public String getMotivation() {return motivation;}
    public String getPersonality() {return personality;}
    public String getQuirk() {return quirk;}
    public String getWorshipHabit() {return worshipHabit;}
    public String getLifeEvent() {return lifeEvent;}
    public String getAppearance() {return appearance;}
    public String getBond() {return bond;}
    public String getFlaw() {return flaw;}
    public String getIdeal() {return ideal;}
    public String getVoice() {return voice;}
    public String getTrait() {return trait;}
    public String getRelationship() {return relationship;}
    public String getHook() {return hook;}

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary=summary; }


    public void setAttribute(String a, String v) {
        switch(a) {
            case "Name":
                name = v;
                break;
            case "Race":
                raceName = v;
                break;
            case "Sex":
                sex = v;
                break;
            case "Age":
                age = v;
                break;
            case "Appearance":
                appearance = v;
                break;
            case "Voice":
                voice = v;
                break;
            case "Personality":
                personality = v;
                break;
            case "Trait":
                trait = v;
                break;
            case "Bond":
                bond = v;
                break;
            case "Motivation":
                motivation = v;
                break;
            case "Ideal":
                ideal = v;
                break;
            case "Flaw":
                flaw = v;
                break;
            case "Quirk":
                quirk = v;
                break;
            case "Detail":
                detail = v;
                break;
            case "Profession":
                profession = v;
                break;
            case "Religion":
                worshipHabit = v;
                break;
            case "Relationship":
                relationship = v;
                break;
            case "Life Event":
                lifeEvent = v;
                break;
            case "Speed":
                speed = v;
                break;
            case "Fly Speed":
                flySpeed = v;
                break;
            case "Strength":
                strength = v;
                break;
            case "Dexterity":
                dexterity = v;
                break;
            case "Constitution":
                constitution = v;
                break;
            case "Intelligence":
                intelligence = v;
                break;
            case "Wisdom":
                wisdom = v;
                break;
            case "Charisma":
                charisma = v;
                break;
            case "Languages":
                languages = v;
                break;
            case "Racial Extras":
                racials = v;
                break;
            case "Plot Hook":
                hook = v;
                break;
            case "Summary":
                summary = v;
                break;
            default:
        }
    }
}

package com.mpvreeken.rpgcompanion.NPC;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mpvreeken.rpgcompanion.R;


/**
 * Created by Sven on 10/23/2017.
 */


public class NPCLayout extends LinearLayout {

    private TextView name_tv, race_tv, sex_tv, age_tv, appearance_tv, voice_tv, personality_tv, trait_tv, bond_tv, motivation_tv, ideal_tv, flaw_tv, quirk_tv, detail_tv, profession_tv, religion_tv, relationship_tv, lifeevent_tv, speed_tv, flyspeed_tv, languages_tv, racial_tv, hook_tv, strength_tv, dexterity_tv, constitution_tv, intelligence_tv, wisdom_tv, charisma_tv, summary_tv;
    private Context context;

    private TextView edit_popup_title;
    private EditText edit_popup_input;
    private Button edit_popup_save_btn, edit_popup_cancel_btn;

    private String currentEdit;

    private NPC npc;

    private boolean dbSaveable;
    private SavedNPCActivity savedNPCActivity;

    LinearLayout edit_popup;

    public NPCLayout(Context context) {
        super(context);
        this.context = context;
        inflateView();
    }
    public NPCLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflateView();
    }

    public NPCLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        inflateView();
    }

    private void inflateView() {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.npc_output, this, true);

        edit_popup = findViewById(R.id.popup_npc_edit_parent);
        edit_popup_title = findViewById(R.id.popup_npc_edit_title);
        edit_popup_input = findViewById(R.id.popup_npc_edit_et);
        edit_popup_save_btn = findViewById(R.id.popup_npc_edit_save_btn);
        edit_popup_cancel_btn = findViewById(R.id.popup_npc_edit_cancel_btn);

        edit_popup_save_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) { editAttribute(); }
        });
        edit_popup_cancel_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) { edit_popup.setVisibility(GONE); }
        });
        dbSaveable=false;
    }

    public void setNPC(NPC n) {
        npc=n;
        name_tv = findViewById(R.id.npc_name);
        race_tv = findViewById(R.id.npc_race);
        sex_tv = findViewById(R.id.npc_sex);
        age_tv = findViewById(R.id.npc_age);
        appearance_tv = findViewById(R.id.npc_appearance);
        voice_tv = findViewById(R.id.npc_voice);
        personality_tv = findViewById(R.id.npc_personality);
        trait_tv = findViewById(R.id.npc_trait);
        bond_tv = findViewById(R.id.npc_bond);
        motivation_tv = findViewById(R.id.npc_motivation);
        ideal_tv = findViewById(R.id.npc_ideal);
        flaw_tv = findViewById(R.id.npc_flaw);
        quirk_tv = findViewById(R.id.npc_quirk);
        detail_tv = findViewById(R.id.npc_detail);
        profession_tv = findViewById(R.id.npc_profession);
        religion_tv = findViewById(R.id.npc_religion);
        relationship_tv = findViewById(R.id.npc_relationship);
        lifeevent_tv = findViewById(R.id.npc_lifeevent);
        speed_tv = findViewById(R.id.npc_speed);
        flyspeed_tv = findViewById(R.id.npc_flyspeed);
        languages_tv = findViewById(R.id.npc_languages);
        racial_tv = findViewById(R.id.npc_racial);
        hook_tv = findViewById(R.id.npc_hook);
        strength_tv = findViewById(R.id.npc_strength);
        dexterity_tv = findViewById(R.id.npc_dexterity);
        constitution_tv = findViewById(R.id.npc_constitution);
        intelligence_tv = findViewById(R.id.npc_intelligence);
        wisdom_tv = findViewById(R.id.npc_wisdom);
        charisma_tv = findViewById(R.id.npc_charisma);
        summary_tv = findViewById(R.id.npc_summary);

        name_tv.setText(formatString("Name: ", npc.getName()));
        race_tv.setText(formatString("Race: ", npc.getRace()));
        sex_tv.setText(formatString("Sex: ", npc.getSex()));
        age_tv.setText(formatString("Age: ", npc.getAge()));
        appearance_tv.setText(formatString("Appearance: ", npc.getAppearance()));
        voice_tv.setText(formatString("Voice: ", npc.getVoice()));
        personality_tv.setText(formatString("Personality: ", npc.getPersonality()));
        trait_tv.setText(formatString("Trait: ", npc.getTrait()));
        bond_tv.setText(formatString("Bond: ", npc.getBond()));
        motivation_tv.setText(formatString("Motivation: ", npc.getMotivation()));
        ideal_tv.setText(formatString("Ideal: ", npc.getIdeal()));
        flaw_tv.setText(formatString("Flaw: ", npc.getFlaw()));
        quirk_tv.setText(formatString("Quirk: ", npc.getQuirk()));
        detail_tv.setText(formatString("Detail: ", npc.getDetail()));
        profession_tv.setText(formatString("Profession: ", npc.getProfession()));
        religion_tv.setText(formatString("Religion: ", npc.getWorshipHabit()));
        relationship_tv.setText(formatString("Relationship: ", npc.getRelationship()));
        lifeevent_tv.setText(formatString("Life Event: ", npc.getLifeEvent()));

        speed_tv.setText(formatString("Speed: ", npc.getSpeed()));
        flyspeed_tv.setText(formatString("Fly Speed: ", npc.getFlySpeed()));

        strength_tv.setText(formatString("STR: ",npc.getStrength()));
        dexterity_tv.setText(formatString("DEX: ",npc.getDexterity()));
        constitution_tv.setText(formatString("CON: ",npc.getConstitution()));
        intelligence_tv.setText(formatString("INT: ",npc.getIntelligence()));
        wisdom_tv.setText(formatString("WIS: ",npc.getWisdom()));
        charisma_tv.setText(formatString("CHA: ",npc.getCharisma()));

        languages_tv.setText(formatString("Languages: ", npc.getLanguages()));
        racial_tv.setText(formatString("Racial Extras: ", npc.getRacial()));
        hook_tv.setText(formatString("Plot Hook: ", npc.getHook()));
        summary_tv.setText(formatString("Summary: ", npc.getSummary()));

        View.OnClickListener tvClickHandler = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.npc_name:
                        showEditPopup("Name", npc.getName());
                        break;
                    case R.id.npc_race:
                        showEditPopup("Race", npc.getRace());
                        break;
                    case R.id.npc_sex:
                        showEditPopup("Sex", npc.getSex());
                        break;
                    case R.id.npc_age:
                        showEditPopup("Age", npc.getAge());
                        break;
                    case R.id.npc_appearance:
                        showEditPopup("Appearance", npc.getAppearance());
                        break;
                    case R.id.npc_voice:
                        showEditPopup("Voice", npc.getVoice());
                        break;
                    case R.id.npc_personality:
                        showEditPopup("Personality", npc.getPersonality());
                        break;
                    case R.id.npc_trait:
                        showEditPopup("Trait", npc.getTrait());
                        break;
                    case R.id.npc_bond:
                        showEditPopup("Bond", npc.getBond());
                        break;
                    case R.id.npc_motivation:
                        showEditPopup("Motivation", npc.getMotivation());
                        break;
                    case R.id.npc_ideal:
                        showEditPopup("Ideal", npc.getIdeal());
                        break;
                    case R.id.npc_flaw:
                        showEditPopup("Flaw", npc.getFlaw());
                        break;
                    case R.id.npc_quirk:
                        showEditPopup("Quirk", npc.getQuirk());
                        break;
                    case R.id.npc_detail:
                        showEditPopup("Detail", npc.getDetail());
                        break;
                    case R.id.npc_profession:
                        showEditPopup("Profession", npc.getProfession());
                        break;
                    case R.id.npc_religion:
                        showEditPopup("Religion", npc.getWorshipHabit());
                        break;
                    case R.id.npc_relationship:
                        showEditPopup("Relationship", npc.getRelationship());
                        break;
                    case R.id.npc_lifeevent:
                        showEditPopup("Life Event", npc.getLifeEvent());
                        break;
                    case R.id.npc_speed:
                        showEditPopup("Speed", npc.getSpeed());
                        break;
                    case R.id.npc_flyspeed:
                        showEditPopup("Fly Speed", npc.getFlySpeed());
                        break;
                    case R.id.npc_strength:
                        showEditPopup("Strength", npc.getStrength());
                        break;
                    case R.id.npc_dexterity:
                        showEditPopup("Dexterity", npc.getDexterity());
                        break;
                    case R.id.npc_constitution:
                        showEditPopup("Constitution", npc.getConstitution());
                        break;
                    case R.id.npc_intelligence:
                        showEditPopup("Intelligence", npc.getIntelligence());
                        break;
                    case R.id.npc_wisdom:
                        showEditPopup("Wisdom", npc.getWisdom());
                        break;
                    case R.id.npc_charisma:
                        showEditPopup("Charisma", npc.getCharisma());
                        break;
                    case R.id.npc_languages:
                        showEditPopup("Languages", npc.getLanguages());
                        break;
                    case R.id.npc_racial:
                        showEditPopup("Racial Extras", npc.getRacial());
                        break;
                    case R.id.npc_hook:
                        showEditPopup("Plot Hook", npc.getHook());
                        break;
                    case R.id.npc_summary:
                        showEditPopup("Summary", npc.getSummary());
                        break;
                    default:
                }
            }
        };

        name_tv.setOnClickListener(tvClickHandler);
        race_tv.setOnClickListener(tvClickHandler);
        sex_tv.setOnClickListener(tvClickHandler);
        age_tv.setOnClickListener(tvClickHandler);
        appearance_tv.setOnClickListener(tvClickHandler);
        voice_tv.setOnClickListener(tvClickHandler);
        personality_tv.setOnClickListener(tvClickHandler);
        trait_tv.setOnClickListener(tvClickHandler);
        bond_tv.setOnClickListener(tvClickHandler);
        motivation_tv.setOnClickListener(tvClickHandler);
        ideal_tv.setOnClickListener(tvClickHandler);
        flaw_tv.setOnClickListener(tvClickHandler);
        quirk_tv.setOnClickListener(tvClickHandler);
        detail_tv.setOnClickListener(tvClickHandler);
        profession_tv.setOnClickListener(tvClickHandler);
        religion_tv.setOnClickListener(tvClickHandler);
        relationship_tv.setOnClickListener(tvClickHandler);
        lifeevent_tv.setOnClickListener(tvClickHandler);
        speed_tv.setOnClickListener(tvClickHandler);
        flyspeed_tv.setOnClickListener(tvClickHandler);
        languages_tv.setOnClickListener(tvClickHandler);
        racial_tv.setOnClickListener(tvClickHandler);
        hook_tv.setOnClickListener(tvClickHandler);
        strength_tv.setOnClickListener(tvClickHandler);
        dexterity_tv.setOnClickListener(tvClickHandler);
        constitution_tv.setOnClickListener(tvClickHandler);
        intelligence_tv.setOnClickListener(tvClickHandler);
        wisdom_tv.setOnClickListener(tvClickHandler);
        charisma_tv.setOnClickListener(tvClickHandler);
        summary_tv.setOnClickListener(tvClickHandler);
    }

    private SpannableStringBuilder formatString(String p, String s) {
        if (s == null) {
            s="";
        }
        SpannableStringBuilder str = new SpannableStringBuilder(p);
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, p.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str.append(s);
    }

    private void showEditPopup(String s, String v) {
        edit_popup_input.setText(v);
        edit_popup_title.setText("Edit "+s);
        edit_popup.setVisibility(VISIBLE);
        edit_popup.bringToFront();
        currentEdit = s;
    }

    private void editAttribute() {
        edit_popup.setVisibility(GONE);

        npc.setAttribute(currentEdit, edit_popup_input.getText().toString());
        setNPC(npc);
        if (dbSaveable && savedNPCActivity!=null) {
            savedNPCActivity.updateNPC();
        }
    }

    public void setSaveable(SavedNPCActivity a) {
        savedNPCActivity = a;
        dbSaveable=true;
    }

}

package com.mpvreeken.rpgcompanion.Dice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mpvreeken.rpgcompanion.Classes.DBHelper;
import com.mpvreeken.rpgcompanion.Dice.Dice;
import com.mpvreeken.rpgcompanion.R;
import com.mpvreeken.rpgcompanion.RPGCActivity;

import java.util.ArrayList;
import java.util.Random;

public class DiceActivity extends RPGCActivity {

    private Button roll_btn, save_custom_btn, dialog_save_save_btn, dialog_save_cancel_btn, dialog_edit_save_btn, dialog_edit_cancel_btn, dialog_edit_delete_btn;
    private LinearLayout custom_ll;
    private TextView output_tv;
    private EditText custom_et, dialog_save_name_et, dialog_save_roll_et, dialog_edit_name_et, dialog_edit_roll_et;
    private Random random;
    private ConstraintLayout save_dialog, edit_dialog;
    private Dice currentDice;

    private static final String DB_NAME = "RPGCompanion";
    private static final String DB_DICE_TABLE = "dice";
    private static final int DB_DICE_COL_ID = 0;
    private static final int DB_DICE_COL_LABEL = 1;
    private static final int DB_DICE_COL_ROLL = 2;
    private static final String DB_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + DB_DICE_TABLE + "(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, lbl VARCHAR, roll VARCHAR);";
    private static final String DB_QUERY_GET_DICE = "SELECT * FROM " + DB_DICE_TABLE + " ORDER BY _id ASC";

    //TODO color text red for rolling a 1 and green for rolling crit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dice);

        View.OnClickListener predefinedButtonHandler = new View.OnClickListener() {
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.dice_d2_btn:
                        roll("1d2");
                        break;
                    case R.id.dice_d3_btn:
                        roll("1d3");
                        break;
                    case R.id.dice_d4_btn:
                        roll("1d4");
                        break;
                    case R.id.dice_d6_btn:
                        roll("1d6");
                        break;
                    case R.id.dice_d8_btn:
                        roll("1d8");
                        break;
                    case R.id.dice_d10_btn:
                        roll("1d10");
                        break;
                    case R.id.dice_d12_btn:
                        roll("1d12");
                        break;
                    case R.id.dice_d20_btn:
                        roll("1d20");
                        break;
                    case R.id.dice_d100_btn:
                        roll("1d100");
                        break;
                }
            }
        };

        Button d2 = findViewById(R.id.dice_d2_btn);
        Button d3 = findViewById(R.id.dice_d3_btn);
        Button d4 = findViewById(R.id.dice_d4_btn);
        Button d6 = findViewById(R.id.dice_d6_btn);
        Button d8 = findViewById(R.id.dice_d8_btn);
        Button d10 = findViewById(R.id.dice_d10_btn);
        Button d12 = findViewById(R.id.dice_d12_btn);
        Button d20 = findViewById(R.id.dice_d20_btn);
        Button d100 = findViewById(R.id.dice_d100_btn);
        d2.setOnClickListener(predefinedButtonHandler);
        d3.setOnClickListener(predefinedButtonHandler);
        d4.setOnClickListener(predefinedButtonHandler);
        d6.setOnClickListener(predefinedButtonHandler);
        d8.setOnClickListener(predefinedButtonHandler);
        d10.setOnClickListener(predefinedButtonHandler);
        d12.setOnClickListener(predefinedButtonHandler);
        d20.setOnClickListener(predefinedButtonHandler);
        d100.setOnClickListener(predefinedButtonHandler);

        roll_btn = findViewById(R.id.dice_roll_btn);
        save_custom_btn = findViewById(R.id.dice_save_custom_btn);
        custom_ll = findViewById(R.id.dice_custom_linearlayout);
        output_tv = findViewById(R.id.dice_output_tv);
        custom_et = findViewById(R.id.dice_custom_et);
        save_dialog = findViewById(R.id.dice_save_dialog);
        edit_dialog = findViewById(R.id.dice_edit_dialog);

        dialog_save_save_btn = findViewById(R.id.dice_save_dialog_save_btn);
        dialog_save_cancel_btn = findViewById(R.id.dice_save_dialog_cancel_btn);
        dialog_save_name_et = findViewById(R.id.dice_save_dialog_name_et);
        dialog_save_roll_et = findViewById(R.id.dice_save_dialog_roll_et);
        dialog_save_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSaveDialog();
            }
        });
        dialog_save_save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCustomRoll();
            }
        });

        dialog_edit_save_btn = findViewById(R.id.dice_edit_dialog_save_btn);
        dialog_edit_cancel_btn = findViewById(R.id.dice_edit_dialog_cancel_btn);
        dialog_edit_name_et = findViewById(R.id.dice_edit_dialog_name_et);
        dialog_edit_roll_et = findViewById(R.id.dice_edit_dialog_roll_et);
        dialog_edit_delete_btn = findViewById(R.id.dice_edit_dialog_delete_btn);
        dialog_edit_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideEditDialog();
            }
        });
        dialog_edit_save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCustomRoll();
            }
        });
        dialog_edit_delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Long press the delete button to delete a custom roll", Toast.LENGTH_LONG).show();
            }
        });
        dialog_edit_delete_btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                deleteCustomRoll();
                return true;
            }
        });

        random = new Random();

        roll_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roll(custom_et.getText().toString());
            }
        });

        save_custom_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displaySaveDialog();
            }
        });

        getSavedRolls();
    }

    private void roll(String r) {
        if (r.length()==0) {
            Toast.makeText(getBaseContext(), "Please type out a custom roll first", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            r = r.toLowerCase();
            StringBuilder output = new StringBuilder();
            int total = 0;

            String[] split = r.split("[-+]+");
            String[] ops = new String[split.length];
            int currOpp = 1;
            for (int i = 0; i < r.length(); i++) {
                if (r.substring(i, i + 1).equals("+") || r.substring(i, i + 1).equals("-")) {
                    ops[currOpp] = r.substring(i, i + 1);
                    currOpp += 1;
                }
            }
            if (r.substring(0, 1).equals("-")) {
                ops[0] = "-";
            } else {
                ops[0] = "+";
            }

            for (int i = 0; i < split.length; i++) {
                String s = split[i];
                if (s.length() > 0) {
                    if (s.contains("d")) {
                        if (s.indexOf("d") == s.length() - 1) {
                            throw new Exception();
                        }

                        int qty;
                        if (s.indexOf("d") == 0) {
                            qty = 1;
                        } else {
                            qty = Integer.valueOf(s.substring(0, s.indexOf("d")));
                        }

                        int die = Integer.valueOf(s.substring(s.indexOf("d") + 1));

                        for (int j = 0; j < qty; j++) {
                            int k = random.nextInt(die) + 1;

                            if (output.length() == 0 && ops[i].equals("+")) {

                                output.append(k).append(" ");
                            } else {
                                output.append(ops[i]).append(" ").append(k).append(" ");
                            }

                            if (ops[i].equals("+")) {
                                total += k;
                            } else {
                                total -= k;
                            }
                        }
                    } else {
                        if (output.length() == 0 && ops[i].equals("+")) {
                            output.append(s).append(" ");
                        } else {
                            output.append(ops[i]).append(" ").append(s).append(" ");
                        }

                        if (ops[i].equals("+")) {
                            total += Integer.valueOf(s);
                        } else {
                            total -= Integer.valueOf(s);
                        }
                    }
                }
            }

            if (split.length == 1) {
                output_tv.setText(String.valueOf(total));
            } else {
                output_tv.setText(output + "= " + total);
            }
        }
        catch(Exception e) {
            Toast.makeText(getBaseContext(), "Invalid Format. Must be \"1d20+2d6+5\"", Toast.LENGTH_LONG).show();
        }
    }

    private boolean testCustomRoll(String r) {

        if (r.length()==0) {
            Toast.makeText(getBaseContext(), "Please type out a custom roll first", Toast.LENGTH_LONG).show();
            return false;
        }

        try {
            r = r.toLowerCase();
            StringBuilder output = new StringBuilder();
            int total = 0;

            String[] split = r.split("[-+]+");
            String[] ops = new String[split.length];
            int currOpp = 1;
            for (int i = 0; i < r.length(); i++) {
                if (r.substring(i, i + 1).equals("+") || r.substring(i, i + 1).equals("-")) {
                    ops[currOpp] = r.substring(i, i + 1);
                    currOpp += 1;
                }
            }
            if (r.substring(0, 1).equals("-")) {
                ops[0] = "-";
            } else {
                ops[0] = "+";
            }

            for (int i = 0; i < split.length; i++) {
                String s = split[i];
                if (s.length() > 0) {
                    if (s.contains("d")) {
                        if (s.indexOf("d") == s.length() - 1) {
                            throw new Exception();
                        }

                        int qty;
                        if (s.indexOf("d") == 0) {
                            qty = 1;
                        } else {
                            qty = Integer.valueOf(s.substring(0, s.indexOf("d")));
                        }

                        int die = Integer.valueOf(s.substring(s.indexOf("d") + 1));

                        for (int j = 0; j < qty; j++) {
                            int k = random.nextInt(die) + 1;

                            if (output.length() == 0 && ops[i].equals("+")) {

                                output.append(k).append(" ");
                            } else {
                                output.append(ops[i]).append(" ").append(k).append(" ");
                            }

                            if (ops[i].equals("+")) {
                                total += k;
                            } else {
                                total -= k;
                            }
                        }
                    } else {
                        if (output.length() == 0 && ops[i].equals("+")) {
                            output.append(s).append(" ");
                        } else {
                            output.append(ops[i]).append(" ").append(s).append(" ");
                        }

                        if (ops[i].equals("+")) {
                            total += Integer.valueOf(s);
                        } else {
                            total -= Integer.valueOf(s);
                        }
                    }
                }
            }
            return true;
        }
        catch(Exception e) {
            Toast.makeText(getBaseContext(), "Invalid Format. Must be \"1d20+2d6+5\"", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void getSavedRolls() {
        SQLiteDatabase db=openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        db.execSQL(DB_CREATE_TABLE);

        custom_ll.removeAllViews();

        try (Cursor cursor = db.rawQuery(DB_QUERY_GET_DICE, null)) {
            while (cursor.moveToNext()) {

                Button btn = new Button(context);
                Dice d = new Dice(cursor.getString(DB_DICE_COL_ID), cursor.getString(DB_DICE_COL_LABEL), cursor.getString(DB_DICE_COL_ROLL));
                btn.setTag(d);
                btn.setText(cursor.getString(DB_DICE_COL_LABEL));
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dice di = null;
                        try {
                            di = (Dice) view.getTag();
                        } catch (Exception e) {
                            Toast.makeText(context, "An unknown error occurred. Please try again", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (di != null) {
                            roll(di.roll);
                        }
                        else {
                            Toast.makeText(context, "An unknown error occurred. Please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                btn.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        showEditDialog((Button) view);
                        return true;
                    }
                });
                custom_ll.addView(btn);
            }
        }
    }

    private void showEditDialog(Button b) {
        currentDice = null;
        try {
            currentDice = (Dice) b.getTag();
        } catch (Exception e) {
            Toast.makeText(context, "An unknown error occurred. Please try again", Toast.LENGTH_LONG).show();
            return;
        }
        if (currentDice == null) {
            Toast.makeText(context, "An unknown error occurred. Please try again", Toast.LENGTH_LONG).show();
            return;
        }

        dialog_edit_name_et.setText(currentDice.label);
        dialog_edit_roll_et.setText(currentDice.roll);
        edit_dialog.setVisibility(View.VISIBLE);
    }

    private void hideEditDialog() {
        currentDice=null;
        edit_dialog.setVisibility(View.GONE);

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
    private void updateCustomRoll() {
        if (currentDice==null) {
            hideSaveDialog();
            Toast.makeText(context, "An unknown error has occurred. Please try again", Toast.LENGTH_LONG).show();
            return;
        }

        SQLiteDatabase db=openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        db.execSQL(DB_CREATE_TABLE);
        db.execSQL("UPDATE " + DB_DICE_TABLE + " SET lbl = '" + dialog_edit_name_et.getText().toString() + "', roll = '" + dialog_edit_roll_et.getText().toString() + "' WHERE _id = '" + currentDice.id + "';");

        getSavedRolls();
        hideEditDialog();
    }
    private void deleteCustomRoll() {
        if (currentDice==null) {
            hideSaveDialog();
            Toast.makeText(context, "An unknown error has occurred. Please try again", Toast.LENGTH_LONG).show();
            return;
        }

        SQLiteDatabase db=openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        db.execSQL(DB_CREATE_TABLE);
        db.execSQL("DELETE FROM " + DB_DICE_TABLE + " WHERE _id = '" + currentDice.id + "';");

        getSavedRolls();
        hideEditDialog();
    }

    private void hideSaveDialog() {
        save_dialog.setVisibility(View.GONE);
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void displaySaveDialog() {
        save_dialog.setVisibility(View.VISIBLE);
        dialog_save_name_et.setText("");
        dialog_save_roll_et.setText(custom_et.getText().toString());
    }

    private void saveCustomRoll() {
        String n = dialog_save_name_et.getText().toString();
        if (n.length()==0 || n.replace(" ", "").length()==0) {
            Toast.makeText(context, "Please enter a label for this custom roll", Toast.LENGTH_LONG).show();
            return;
        }
        String r = dialog_save_roll_et.getText().toString();
        if (!testCustomRoll(r)) {
            return;
        }

        SQLiteDatabase db=openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        db.execSQL(DB_CREATE_TABLE);
        db.execSQL("INSERT INTO " + DB_DICE_TABLE + " VALUES(null, '"+n+"','"+r+"');");

        getSavedRolls();
        ScrollView scroll = (ScrollView) custom_ll.getParent();
        scroll.scrollTo(0, scroll.getBottom());
        hideSaveDialog();
    }

    private void deleteSavedRoll() {
        //db.execSQL("DELETE FROM student WHERE rollno='"+editRollno.getText()+"'");
    }

}

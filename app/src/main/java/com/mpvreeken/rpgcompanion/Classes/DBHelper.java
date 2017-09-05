package com.mpvreeken.rpgcompanion.Classes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sven on 9/3/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "MyNPCs.db";
    public static final String NPCS_TABLE_NAME = "my_npcs";
    public static final String NPCS_COL_NPC = "npc";
    public static final String NPCS_COL_SUMMARY = "summary";
    public static final String NPCS_COL_NAME = "name";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        String create = "CREATE TABLE " + NPCS_TABLE_NAME + " (" +
                "_id INTEGER PRIMARY KEY," +
                NPCS_COL_NPC + " TEXT," +
                NPCS_COL_SUMMARY + " TEXT," +
                NPCS_COL_NAME + " TEXT)";
        db.execSQL(create);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String delete = "DROP TABLE IF EXISTS " + NPCS_TABLE_NAME;
        db.execSQL(delete);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

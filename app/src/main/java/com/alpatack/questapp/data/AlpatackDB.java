package com.alpatack.questapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AlpatackDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "alpatack.db";

    // paltan to after each change ng codes sa file na to: DB_VERSION++;
    private static final int DB_VERSION = 3;

    public AlpatackDB(Context c) {
        super(c, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true); // lets deleting a quest delete its steps
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL UNIQUE COLLATE NOCASE, email TEXT, pass_hash TEXT, " +
                "full_name TEXT, birthday TEXT, address TEXT, joined TEXT, last_checkin TEXT, " +
                "quest_date TEXT, logged_in INTEGER, phone TEXT)");
        db.execSQL("CREATE TABLE player (user_id INTEGER PRIMARY KEY, name TEXT, level INTEGER, " +
                "current_exp INTEGER, max_exp INTEGER, coins INTEGER, streak INTEGER, quests_completed INTEGER)");
        db.execSQL("CREATE TABLE category_exp (user_id INTEGER, idx INTEGER, exp INTEGER, " +
                "PRIMARY KEY (user_id, idx))");
        db.execSQL("CREATE TABLE quests (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER NOT NULL, " +
                "title TEXT, description TEXT, category TEXT, difficulty TEXT, type TEXT, " +
                "custom_exp INTEGER, quest_no INTEGER, completed INTEGER)");
        db.execSQL("CREATE TABLE quest_steps (id INTEGER PRIMARY KEY AUTOINCREMENT, quest_id INTEGER NOT NULL, " +
                "position INTEGER, text TEXT, done INTEGER, " +
                "FOREIGN KEY (quest_id) REFERENCES quests(id) ON DELETE CASCADE)");
        db.execSQL("CREATE TABLE rewards (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER NOT NULL, " +
                "name TEXT, description TEXT, cost INTEGER, times_redeemed INTEGER)");
        db.execSQL("CREATE TABLE achievements (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER NOT NULL, " +
                "title TEXT, description TEXT, type TEXT, required_value INTEGER, coin_reward INTEGER, " +
                "unlocked INTEGER, claimed INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        if (oldV >= 2) {   // 2 -> 3 only adds the contact number, so keep everyone's data
            if (oldV < 3) db.execSQL("ALTER TABLE users ADD COLUMN phone TEXT");
            return;
        }
        String[] tables = {"quest_steps", "quests", "category_exp", "player", "users", "rewards", "achievements"};
        for (String t : tables) db.execSQL("DROP TABLE IF EXISTS " + t);
        onCreate(db);
    }
}

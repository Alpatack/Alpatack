package com.alpatack.questapp.data;

import com.alpatack.questapp.model.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.DatabaseUtils;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;



// Holds the Player + QuestManager and saves everything locally.
public class AppState {
    public static final int[] CHECKIN_REWARDS = {100, 150, 200, 250, 300, 400, 500};

    private static AppState inst;

    public static AppState get(Context c) {
        if (inst == null) inst = new AppState(c.getApplicationContext());
        return inst;
    }

    private final Context ctx;
    private final AlpatackDB dbh;
    private long userId = -1;   // which user is logged in (-1 = nobody)
    public Player player;
    public QuestManager manager;
    public boolean loggedIn;
    public String fullName = "", username = "", email = "", phone = "", birthday = "", address = "", joined = "";
    public String lastCheckin = "", questDate = "";

    private AppState(Context c) {
        ctx = c;
        dbh = new AlpatackDB(c);
        load();
        rollDaily();
    }

    // ACCOUNTS
    public boolean hasAccount() {
        return DatabaseUtils.queryNumEntries(dbh.getReadableDatabase(), "users") > 0;
    }

    // Returns false if the username is already taken.
    public boolean register(String user, String mail, String password) {
        SQLiteDatabase db = dbh.getWritableDatabase();
        Cursor c = db.query("users", new String[]{"id"}, "username = ?", new String[]{user}, null, null, null);
        boolean taken = c.moveToFirst();
        c.close();
        if (taken) return false;

        ContentValues u = new ContentValues();
        u.put("username", user);
        u.put("email", mail);
        u.put("pass_hash", hash(password));
        u.put("full_name", user);
        u.put("birthday", "");
        u.put("address", "");
        u.put("phone", "");
        u.put("joined", new SimpleDateFormat("MMMM yyyy", Locale.US).format(new Date()));
        u.put("last_checkin", "");
        u.put("quest_date", "");
        u.put("logged_in", 0);
        long id = db.insert("users", null, u);
        if (id < 0) return false;

        db.execSQL("UPDATE users SET logged_in = 0");
        userId = id;
        username = user;
        fullName = user;
        email = mail;
        phone = "";
        birthday = "";
        address = "";
        joined = u.getAsString("joined");
        loggedIn = true;
        resetGame();   // fresh player, quests, rewards, achievements for this user
        save();
        return true;
    }

    public boolean login(String user, String password) {
        SQLiteDatabase db = dbh.getWritableDatabase();
        Cursor c = db.query("users", new String[]{"id", "pass_hash"}, "username = ?",
                new String[]{user}, null, null, null);
        long id = -1;
        if (c.moveToFirst() && hash(password).equals(c.getString(1))) id = c.getLong(0);
        c.close();
        if (id < 0) return false;

        db.execSQL("UPDATE users SET logged_in = 0");
        db.execSQL("UPDATE users SET logged_in = 1 WHERE id = " + id);
        loadUser(id);
        rollDaily();
        return true;
    }

    public void logout() {
        loggedIn = false;
        save();
    }
    private String hash(String s) {
        try {
            byte[] d = MessageDigest.getInstance("SHA-256").digest(s.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return s;
        }
    }

    // SETUP
    private void resetGame() {
        player = new Player(username.isEmpty() ? "Player" : username);
        manager = new QuestManager(player);
        manager.generateDailyQuests();
        seedDefaults();
        lastCheckin = "";
        questDate = today();
    }

    private void seedDefaults() {
        ArrayList<Achievement> a = manager.getAchievementList();
        a.add(new Achievement("First Quest", "Complete your first quest", "quests", 1, 50));
        a.add(new Achievement("7-Day Streak", "Check in 7 days in a row", "streak", 7, 100));
        a.add(new Achievement("Master of Focus", "Earn 150 Mental EXP", "cat:mental", 150, 150));
        a.add(new Achievement("Social Butterfly", "Earn 100 Social EXP", "cat:social", 100, 75));
        a.add(new Achievement("Quest Hunter", "Complete 10 quests", "quests", 10, 100));
        a.add(new Achievement("Rookie", "Reach level 2", "level", 2, 20));
        a.add(new Achievement("Adept", "Reach level 5", "level", 5, 50));
        a.add(new Achievement("Veteran", "Reach level 10", "level", 10, 100));
        ArrayList<Reward> r = manager.getRewardList();
        r.add(new Reward("Snack", "Food", 100));
        r.add(new Reward("Movie", "Gift", 100));
        r.add(new Reward("Clothing", "Clothing", 100));
        r.add(new Reward("Mall", "Shopping", 100));
        r.add(new Reward("Shopping", "Shopping", 100));
    }

    // -setting dateformat
    private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public String today() { return DF.format(new Date()); }

    private String yesterday() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -1);
        return DF.format(c.getTime());
    }

    // New day: new daily quests, and a missed day breaks the streak.
    private void rollDaily() {
        boolean changed = false;
        if (!today().equals(questDate)) {
            manager.getQuestList().removeIf(q -> q instanceof PredefinedQuest);
            manager.generateDailyQuests();
            questDate = today();
            changed = true;
        }
        if (!lastCheckin.equals(today()) && !lastCheckin.equals(yesterday()) && player.getDailyStreak() > 0) {
            player.setDailyStreak(0);
            changed = true;
        }
        if (changed) save();
    }

    public boolean checkedInToday() { return lastCheckin.equals(today()); }

    // Returns coins earned, or -1 if already checked in.
    public int checkIn() {
        if (checkedInToday()) return -1;
        player.updateStreak();
        lastCheckin = today();
        int reward = CHECKIN_REWARDS[(player.getDailyStreak() - 1) % 7];
        player.addCoins(reward);
        manager.checkAchievements();
        save();
        return reward;
    }

    public int totalXp() {
        int t = 0;
        for (int v : player.getCategoryExp()) t += v;
        return t;
    }

    // persistence (SQLite)
    public void save() {
        if (userId < 0) return;   // nobody is logged in
        SQLiteDatabase db = dbh.getWritableDatabase();
        String[] uid = {String.valueOf(userId)};
        db.beginTransaction();
        try {
            ContentValues u = new ContentValues();
            u.put("username", username);
            u.put("email", email);
            u.put("phone", phone);
            u.put("full_name", fullName);
            u.put("birthday", birthday);
            u.put("address", address);
            u.put("joined", joined);
            u.put("last_checkin", lastCheckin);
            u.put("quest_date", questDate);
            u.put("logged_in", loggedIn ? 1 : 0);
            db.updateWithOnConflict("users", u, "id = ?", uid, SQLiteDatabase.CONFLICT_IGNORE);

            ContentValues p = new ContentValues();
            p.put("user_id", userId);
            p.put("name", player.getName());
            p.put("level", player.getLevel());
            p.put("current_exp", player.getCurrentExp());
            p.put("max_exp", player.getMaxExp());
            p.put("coins", player.getCoin());
            p.put("streak", player.getDailyStreak());
            p.put("quests_completed", player.getQuestsCompleted());
            db.insertWithOnConflict("player", null, p, SQLiteDatabase.CONFLICT_REPLACE);

            db.delete("category_exp", null, null);   // steps are deleted too (ON DELETE CASCADE)
            for (Category cat : Category.values()) {
                ContentValues c = new ContentValues();
                c.put("idx", cat.getIndex());
                c.put("exp", player.getCategoryExp(cat));
                db.insert("category_exp", null, c);
            }

            db.delete("quests", "user_id = ?", uid);   // steps are deleted too (ON DELETE CASCADE)
            for (Quest q : manager.getQuestList()) {
                ContentValues c = new ContentValues();
                c.put("user_id", userId);
                c.put("title", q.getTitle());
                c.put("description", q.getDescription());
                c.put("category", q.getCategory());
                c.put("difficulty", q.getDifficulty());
                c.put("completed", q.isCompleted() ? 1 : 0);
                if (q instanceof CustomQuest) {
                    c.put("type", "custom");
                    c.put("custom_exp", ((CustomQuest) q).getCustomExp());
                    c.put("quest_no", 0);
                } else {
                    c.put("type", "predefined");
                    c.put("custom_exp", 0);
                    c.put("quest_no", ((PredefinedQuest) q).getQuestId());
                }
                long questId = db.insert("quests", null, c);
                for (int i = 0; i < q.getSteps().size(); i++) {
                    ContentValues sv = new ContentValues();
                    sv.put("quest_id", questId);
                    sv.put("position", i);
                    sv.put("text", q.getSteps().get(i));
                    sv.put("done", q.isStepDone(i) ? 1 : 0);
                    db.insert("quest_steps", null, sv);
                }
            }

            db.delete("rewards", "user_id = ?", uid);
            for (Reward r : manager.getRewardList()) {
                ContentValues c = new ContentValues();
                c.put("user_id", userId);
                c.put("name", r.getName());
                c.put("description", r.getDescription());
                c.put("cost", r.getCost());
                c.put("times_redeemed", r.getTimesRedeemed());
                db.insert("rewards", null, c);
            }

            db.delete("achievements", "user_id = ?", uid);
            for (Achievement a : manager.getAchievementList()) {
                ContentValues c = new ContentValues();
                c.put("user_id", userId);
                c.put("title", a.getTitle());
                c.put("description", a.getDescription());
                c.put("type", a.getType());
                c.put("required_value", a.getRequiredValue());
                c.put("coin_reward", a.getCoinReward());
                c.put("unlocked", a.isUnlocked() ? 1 : 0);
                c.put("claimed", a.isClaimed() ? 1 : 0);
                db.insert("achievements", null, c);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
    private String str(Cursor c, String col) { return c.getString(c.getColumnIndexOrThrow(col)); }
    private int num(Cursor c, String col) { return c.getInt(c.getColumnIndexOrThrow(col)); }

    // App start: restore whoever was logged in, otherwise start blank.
    private void load() {
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = db.query("users", new String[]{"id"}, "logged_in = 1", null, null, null, null);
        if (c.moveToFirst()) {
            long id = c.getLong(0);
            c.close();
            loadUser(id);
        } else {
            c.close();
            resetGame();
        }
    }

    // Loads one user's profile and game data.
    private void loadUser(long id) {
        SQLiteDatabase db = dbh.getReadableDatabase();
        String[] uid = {String.valueOf(id)};

        Cursor u = db.query("users", null, "id = ?", uid, null, null, null);
        if (!u.moveToFirst()) {
            u.close();
            resetGame();
            return;
        }
        userId = id;
        username = str(u, "username");
        email = str(u, "email");
        String ph = str(u, "phone");
        phone = ph == null ? "" : ph;   // null for accounts made before this column existed
        fullName = str(u, "full_name");
        birthday = str(u, "birthday");
        address = str(u, "address");
        joined = str(u, "joined");
        lastCheckin = str(u, "last_checkin");
        questDate = str(u, "quest_date");
        loggedIn = num(u, "logged_in") == 1;
        u.close();

        Cursor p = db.query("player", null, "user_id = ?", uid, null, null, null);
        if (!p.moveToFirst()) {
            p.close();
            resetGame();
            save();
            return;
        }
        int[] catExp = new int[Category.values().length];
        Cursor ce = db.query("category_exp", null, "user_id = ?", uid, null, null, "idx");
        while (ce.moveToNext()) {
            int idx = num(ce, "idx");
            if (idx >= 0 && idx < catExp.length) catExp[idx] = num(ce, "exp");        }
        ce.close();

        player = new Player(str(p, "name"), num(p, "level"), num(p, "current_exp"), num(p, "max_exp"),
                catExp, num(p, "coins"), num(p, "streak"));
        player.setQuestsCompleted(num(p, "quests_completed"));
        p.close();

        manager = new QuestManager(player);


        Cursor q = db.query("quests", null, "user_id = ?", uid, null, null, "id");
        while (q.moveToNext()) {
            Quest quest;
            if ("custom".equals(str(q, "type"))) {
                quest = new CustomQuest(str(q, "title"), str(q, "description"), str(q, "category"),
                        str(q, "difficulty"), num(q, "custom_exp"));
            } else {
                quest = new PredefinedQuest(num(q, "quest_no"), str(q, "title"), str(q, "description"),
                        str(q, "category"), str(q, "difficulty"));
            }
            quest.setCompleted(num(q, "completed") == 1);

            Cursor st = db.query("quest_steps", null, "quest_id = ?",
                    new String[]{String.valueOf(q.getLong(q.getColumnIndexOrThrow("id")))},
                    null, null, "position");
            ArrayList<String> steps = new ArrayList<>();
            ArrayList<Boolean> done = new ArrayList<>();
            while (st.moveToNext()) {
                steps.add(str(st, "text"));
                done.add(num(st, "done") == 1);
            }
            st.close();
            quest.setSteps(steps);
            for (int i = 0; i < done.size(); i++) quest.setStepDone(i, done.get(i));
            manager.addQuest(quest);
        }
        q.close();

        Cursor r = db.query("rewards", null, "user_id = ?", uid, null, null, "id");
        while (r.moveToNext()) {
            Reward rw = new Reward(str(r, "name"), str(r, "description"), num(r, "cost"));
            rw.setTimesRedeemed(num(r, "times_redeemed"));
            manager.getRewardList().add(rw);
        }
        r.close();

        Cursor a = db.query("achievements", null, "user_id = ?", uid, null, null, "id");
        while (a.moveToNext()) {
            Achievement ach = new Achievement(str(a, "title"), str(a, "description"), str(a, "type"),
                    num(a, "required_value"), num(a, "coin_reward"));
            ach.setUnlocked(num(a, "unlocked") == 1);
            ach.setClaimed(num(a, "claimed") == 1);
            manager.getAchievementList().add(ach);
        }
        a.close();
    }

    // SETTINGS
    // True if another account already uses this username.
    public boolean usernameTaken(String user) {
        Cursor c = dbh.getReadableDatabase().query("users", new String[]{"id"}, "username = ? AND id != ?",
                new String[]{user, String.valueOf(userId)}, null, null, null);
        boolean taken = c.moveToFirst();
        c.close();
        return taken;
    }

    public void updateProfile(String full, String user, String birth, String addr) {
        fullName = full;
        if (!user.isEmpty()) username = user;
        birthday = birth;
        address = addr;
        player.setName(fullName.isEmpty() ? username : fullName);
        save();
    }

    public void updateContact(String mail, String number) {
        email = mail;
        phone = number;
        save();
    }

    // Returns false if the current password is wrong.
    public boolean changePassword(String current, String next) {
        SQLiteDatabase db = dbh.getWritableDatabase();
        String[] uid = {String.valueOf(userId)};
        Cursor c = db.query("users", new String[]{"pass_hash"}, "id = ?", uid, null, null, null);
        boolean ok = c.moveToFirst() && hash(current).equals(c.getString(0));
        c.close();
        if (!ok) return false;
        ContentValues v = new ContentValues();
        v.put("pass_hash", hash(next));
        db.update("users", v, "id = ?", uid);
        return true;
    }

    public boolean passwordMatches(String password) {
        Cursor c = dbh.getReadableDatabase().query("users", new String[]{"pass_hash"}, "id = ?",
                new String[]{String.valueOf(userId)}, null, null, null);
        boolean ok = c.moveToFirst() && hash(password).equals(c.getString(0));
        c.close();
        return ok;
    }

    private SharedPreferences prefs() { return ctx.getSharedPreferences("alpatack_settings", Context.MODE_PRIVATE); }

    public boolean pushEnabled() { return prefs().getBoolean("push_" + userId, true); }

    public void setPushEnabled(boolean on) { prefs().edit().putBoolean("push_" + userId, on).apply(); }

    // Permanently removes the logged in account, its game data and its photo.
    public void deleteAccount() {
        if (userId < 0) return;
        SQLiteDatabase db = dbh.getWritableDatabase();
        String[] uid = {String.valueOf(userId)};
        db.beginTransaction();
        try {
            db.delete("quests", "user_id = ?", uid);   // steps are deleted too (ON DELETE CASCADE)
            db.delete("rewards", "user_id = ?", uid);
            db.delete("achievements", "user_id = ?", uid);
            db.delete("category_exp", "user_id = ?", uid);
            db.delete("player", "user_id = ?", uid);
            db.delete("users", "id = ?", uid);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        avatarFile(ctx).delete();
        prefs().edit().remove("push_" + userId).apply();
        userId = -1;
        loggedIn = false;
        fullName = ""; username = ""; email = ""; phone = ""; birthday = ""; address = ""; joined = "";
        resetGame();   // blank in-memory state; nothing is saved while userId is -1
    }

    public long getUserId() { return userId; }

    public java.io.File avatarFile(Context c) {
        return new java.io.File(c.getFilesDir(), "avatar_" + userId + ".png");
    }
}
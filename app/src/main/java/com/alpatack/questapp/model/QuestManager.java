package com.alpatack.questapp.model;

import java.util.ArrayList;
import java.util.Random;



public class QuestManager{
    private ArrayList<Quest> questList;
    private ArrayList<Achievement> achievementList;
    private ArrayList<Reward> rewardList;
    private Player player;
    private static final int DAILY_COUNT = 5;         // quests per day
    private static final int MAX_PER_CATEGORY = 2;    // at most this many from one category



    // basic constructor
    public QuestManager(Player player) {
        this.player = player;
        this.questList = new ArrayList<>();
        this.achievementList = new ArrayList<>();
        this.rewardList = new ArrayList<>();
    }

    PredefinedQuest pq = new PredefinedQuest();

    // Selects random quests from predefined dataset for daily completion.
    // Picks one random predefined quest per category for today.
    // Picks random quests from the whole predefined pool for today.
    public void generateDailyQuests() {
        ArrayList<PredefinedQuest> pool = new ArrayList<>(PredefinedQuest.getPredefinedPool());
        java.util.Collections.shuffle(pool);

        int[] perCategory = new int[Category.values().length];
        int picked = 0;
        for (PredefinedQuest src : pool) {
            if (picked == DAILY_COUNT) break;

            Category c = Category.fromName(src.getCategory());
            int idx = (c == null) ? 0 : c.getIndex();
            if (perCategory[idx] >= MAX_PER_CATEGORY) continue;
            perCategory[idx]++;
            picked++;

            // a fresh copy, so completing it never changes the template in the pool
            PredefinedQuest q = new PredefinedQuest();
            q.setQuestId(src.getQuestId());
            q.setTitle(src.getTitle());
            q.setDescription(src.getDescription());
            q.setCategory(src.getCategory());
            q.setDifficulty(src.getDifficulty());
            q.setSteps(src.getSteps());
            questList.add(q);
        }
    }

    // Overloaded method to add an existing quest object directly.
    public void addQuest(Quest quest) {
        questList.add(quest);
    }

    // Overloaded method to construct and add a new custom quest.
    public void addQuest(String title, String description, String category, String difficulty, int customExp) {
        questList.add(new CustomQuest(title, description, category, difficulty, customExp));
    }

    // Modifies quest details at specified index.
    public void editQuest(int index, String title, String description, String category, String difficulty) {
        if (index >= 0 && index < questList.size()) {
            Quest q = questList.get(index);
            q.setTitle(title);
            q.setDescription(description);
            q.setCategory(category);
            q.setDifficulty(difficulty);
        }
    }

    // Deletes quest item at specified index.
    public void deleteQuest(int index) {
        if (index >= 0 && index < questList.size()) {
            questList.remove(index);
        }
    }

    // Completes quest at given index and awards calculated exp.
    public void completeQuest(int index) {
        if (index >= 0 && index < questList.size()) {
            Quest q = questList.get(index);
            if (!q.isCompleted()) {
                q.markCompleted();
                int earnedExp = q.calculateExp();
                player.addExp(q.getCategory(), earnedExp);
                player.addCoins(earnedExp / 2); // coins so rewards can be redeemed
                player.addQuestCompleted();
                checkAchievements();
            }
        }
    }

    // Evaluates achievement list against current player state.
    public void checkAchievements() {
        for (Achievement a : achievementList) {
            a.checkUnlock(player);
        }
    }

    // Redeems reward item at given index if player has enough coins.
    public void redeemReward(int index) {
        if (index >= 0 && index < rewardList.size()) {
            Reward r = rewardList.get(index);
            r.redeem(player);
        }
    }

    // Saves current player progress and lists to local file storage.
    public void saveData() {
        // file write logic will be implemented here
    }

    // Loads saved data from local file storage into application memory.
    public void loadData() {
        // file read logic will be implemented here
    }

    // getters
    public ArrayList<Quest> getQuestList() { return questList; }
    public ArrayList<Achievement> getAchievementList() { return achievementList; }
    public ArrayList<Reward> getRewardList() { return rewardList; }
    public Player getPlayer() { return player; }
}

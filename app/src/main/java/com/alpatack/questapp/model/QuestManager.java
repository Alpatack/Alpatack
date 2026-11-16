package com.alpatack.questapp.model;

import java.util.ArrayList;
import java.util.Random;

public class QuestManager {
    private ArrayList<Quest> questList;
    private ArrayList<Achievement> achievementList;
    private ArrayList<Reward> rewardList;
    private Player player;
    private ArrayList<PredefinedQuest> predefinedPool = new ArrayList<>();

    // basic constructor
    public QuestManager(Player player) {
        this.player = player;
        this.questList = new ArrayList<>();
        this.achievementList = new ArrayList<>();
        this.rewardList = new ArrayList<>();
    }

    // Loads predefined dataset into quest list.
    public void loadQuests() {
        predefinedPool.clear();
        predefinedPool.add(pq(1, "30mins Workout", "Get moving", "physical", "medium",
                "10 mins Warm-up / Stretches", "15 mins Cardio (Walk/Jog)", "5 mins Cool-down", "5 mins Hydration & Pulse Recovery"));
        predefinedPool.add(pq(2, "Walk 5,000 steps", "Take a walk today", "physical", "easy", "Walk 5,000 steps"));
        predefinedPool.add(pq(3, "Journal", "Write about your day", "mental", "easy", "Write 3 things you did", "Write 1 thing you learned"));
        predefinedPool.add(pq(4, "Read 20 pages", "Read a book or article", "mental", "medium", "Pick a book", "Read 20 pages"));
        predefinedPool.add(pq(5, "Chika w the besties", "Catch up with friends", "social", "easy", "Message a friend", "Have a short chat"));
        predefinedPool.add(pq(6, "Help someone", "Do a kind act today", "social", "medium", "Find someone to help", "Do the kind act"));
        predefinedPool.add(pq(7, "Daily Prayer", "Take a quiet moment", "spiritual", "easy", "Find a quiet spot", "Pray"));
        predefinedPool.add(pq(8, "Meditate 10 mins", "Breathe and reset", "spiritual", "easy", "Sit comfortably", "10 mins of focused breathing"));
        predefinedPool.add(pq(9, "Make your bed", "Start the day organized", "discipline", "easy", "Make your bed"));
        predefinedPool.add(pq(10, "Finish a task you avoided", "Tackle it now", "discipline", "hard", "Pick the task", "Work on it for 25 mins", "Finish it"));
    }

    private PredefinedQuest pq(int id, String title, String desc, String cat, String diff, String... steps) {
        PredefinedQuest q = new PredefinedQuest(id, title, desc, cat, diff);
        q.setSteps(java.util.Arrays.asList(steps));
        return q;
    }

    // Selects random quests from predefined dataset for daily completion.
    public void generateDailyQuests() {
        Random random = new Random();
        String[] cats = {"physical", "mental", "social", "spiritual", "discipline"};
        for (String cat : cats) {
            ArrayList<PredefinedQuest> options = new ArrayList<>();
            for (PredefinedQuest p : predefinedPool) {
                if (p.getCategory().equals(cat)) options.add(p);
            }
            if (!options.isEmpty()) {
                PredefinedQuest src = options.get(random.nextInt(options.size()));
                PredefinedQuest copy = new PredefinedQuest(src.getQuestId(), src.getTitle(), src.getDescription(), src.getCategory(), src.getDifficulty());
                copy.setSteps(src.getSteps());
                questList.add(copy);
            }
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

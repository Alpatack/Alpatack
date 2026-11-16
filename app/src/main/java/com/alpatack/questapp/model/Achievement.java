package com.alpatack.questapp.model;

public class Achievement {
    private String title;
    private String description;
    private String type;          // "level", "streak", "quests" or "cat:<category>"
    private int requiredValue;
    private int coinReward;
    private boolean isUnlocked;
    private boolean isClaimed;

    // basic constructor (milestone based on player level)
    public Achievement(String title, String description, int requiredValue, int coinReward) {
        this(title, description, "level", requiredValue, coinReward);
    }

    // overloaded constructor with a progress type
    public Achievement(String title, String description, String type, int requiredValue, int coinReward) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.requiredValue = requiredValue;
        this.coinReward = coinReward;
        this.isUnlocked = false;
        this.isClaimed = false;
    }

    // Current progress of the player toward this achievement.
    public int getProgress(Player p) {
        if (type.equals("streak")) return p.getDailyStreak();
        if (type.equals("quests")) return p.getQuestsCompleted();
        if (type.startsWith("cat:")) return p.getCategoryExpFor(type.substring(4));
        return p.getLevel();
    }

    // Checks whether the player meets the requirement. Coins are given when the player claims it.
    public boolean checkUnlock(Player p) {
        if (!isUnlocked && getProgress(p) >= requiredValue) {
            unlock();
            return true;
        }
        return false;
    }

    // Gives the coin reward once.
    public boolean claim(Player p) {
        if (isUnlocked && !isClaimed) {
            isClaimed = true;
            p.addCoins(coinReward);
            return true;
        }
        return false;
    }

    public void unlock() {
        isUnlocked = true;
    }

    public void displayInfo() {
        System.out.println("Achievement: " + title + " [" + (isUnlocked ? "Unlocked" : "Locked") + "]");
        System.out.println("Description: " + description);
        System.out.println("Reward: " + coinReward + " coins");
    }

    // getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public int getRequiredValue() { return requiredValue; }
    public int getCoinReward() { return coinReward; }
    public boolean isUnlocked() { return isUnlocked; }
    public boolean isClaimed() { return isClaimed; }

    // setters
    public void setTitle(String t) { title = t; }
    public void setDescription(String d) { description = d; }
    public void setRequiredValue(int r) { requiredValue = r; }
    public void setCoinReward(int c) { coinReward = c; }
    public void setUnlocked(boolean u) { isUnlocked = u; }
    public void setClaimed(boolean c) { isClaimed = c; }
}

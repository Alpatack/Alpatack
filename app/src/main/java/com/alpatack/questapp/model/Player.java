package com.alpatack.questapp.model;

public class Player {
    private String name;
    private int level;
    private int currentExp;
    private int maxExp;
    private int expToNextLevel;

    /*
    EXP CATEGORY INDEXES
    0 - physical
    1 - mental
    2 - social
    3 - spiritual
    4 - discipline
    */
    private int[] categoryExp; 
    private int coin;
    private int dailyStreak;
    private int questsCompleted;

    // constructor for NEW Users (Default initial values)
    public Player(String name) {
        this.name = name;
        this.level = 1;
        this.currentExp = 0;
        this.maxExp = 100;
        this.expToNextLevel = this.maxExp - this.currentExp;
        this.categoryExp = new int[5];
        this.coin = 0;
        this.dailyStreak = 0;
    }

    // constructor for RETURNING Users (Loading loaded/saved data)
    public Player(String name, int level, int currentExp, int maxExp, int[] categoryExp, int coin, int dailyStreak) {
        this.name = name;
        this.level = level;
        this.currentExp = currentExp;
        this.maxExp = maxExp;
        this.expToNextLevel = this.maxExp - this.currentExp;
        this.categoryExp = categoryExp;
        this.coin = coin;
        this.dailyStreak = dailyStreak;
    }

    // Helper method to convert category name into array index
    private int getCategoryIndex(String category) {
        switch (category.toLowerCase()) {
            case "physical": return 0;
            case "mental": return 1;
            case "social": return 2;
            case "spiritual": return 3;
            case "discipline": return 4;
            default: return 0;
        }
    }

    // Adds EXP to a category and checks for level-ups
    public void addExp(String category, int amount) {
        int index = getCategoryIndex(category);
        categoryExp[index] += amount;
        currentExp += amount;

        // Auto level-up loop when required EXP threshold is met
        while (currentExp >= maxExp) {
            levelUp();
        }

        expToNextLevel = maxExp - currentExp;
    }

    // Private method called internally when leveling up
    private void levelUp() {
        currentExp -= maxExp;
        level++;
        maxExp += 50; // Increases target EXP requirement per level
        System.out.println("Congratulations! You reached Level " + level + "!");
    }

    // Economy & Streak Functions
    public void addCoins(int amount) {
        coin += amount;
    }

    public boolean spendCoins(int amount) {
        if (coin >= amount) {
            coin -= amount;
            return true;
        }
        return false;
    }

    public void updateStreak() {
        dailyStreak++;
    }

    public void addQuestCompleted() { questsCompleted++; }
    public int getQuestsCompleted() { return questsCompleted; }
    public void setQuestsCompleted(int n) { questsCompleted = n; }
    public int getCategoryExpFor(String category) { return categoryExp[getCategoryIndex(category)]; }

    // getters
    public String getName() { return name; }
    public int getLevel() { return level; }
    public int getCurrentExp() { return currentExp; }
    public int getMaxExp() { return maxExp; }
    public int getExpToNextLevel() { return expToNextLevel; }
    public int[] getCategoryExp() { return categoryExp; }
    public int getCoin() { return coin; }
    public int getDailyStreak() { return dailyStreak; }

    // setters
    public void setName(String name) { this.name = name; }
    public void setLevel(int level) { this.level = level; }
    public void setCurrentExp(int currentExp) { this.currentExp = currentExp; }
    public void setMaxExp(int maxExp) { this.maxExp = maxExp; }
    public void setCoin(int coin) { this.coin = coin; }
    public void setDailyStreak(int dailyStreak) { this.dailyStreak = dailyStreak; }
}

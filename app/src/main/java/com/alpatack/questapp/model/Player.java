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
    private int dailyQuestsCompleted;

    // constructor for NEW Users (Default initial values)
    public Player(String name) {
        this.name = name;
        this.level = 1;
        this.currentExp = 0;
        this.maxExp = 100;
        this.expToNextLevel = this.maxExp - this.currentExp;
        this.categoryExp = new int[Category.values().length];
        this.coin = 0;
        this.dailyStreak = 0;
        this.dailyQuestsCompleted = 0;
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
        this.dailyQuestsCompleted = 0;
    }

    // Helper method to convert category name into array index
    private int getCategoryIndex(String category) {
        Category c = Category.fromName(category);
        return c == null ? Category.PHYSICAL.getIndex() : c.getIndex();   // unknown names count as physical
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

    // Lifetime XP across all categories (does not reset on level-up)
    public int getTotalExp() {
        int total = currentExp;
        int need = 100;                 // XP needed for level 1 -> 2 (+50 each level after)
        for (int i = 1; i < level; i++) {
            total += need;
            need += 50;
        }
        return total;
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

    public int getCategoryExp(int index) {
        if (index < 0 || index >= categoryExp.length) return 0;
        return categoryExp[index];
    }
    public int getCategoryExp(Category c) { return getCategoryExp(c.getIndex()); }

    public int getCategoryLevel(int index) {
        int exp = getCategoryExp(index);
        int level = 1;
        int needed = 100;
        while (exp >= needed) {
            exp -= needed;
            level++;
            needed += 50;
        }
        return level;
    }
    public int getCategoryLevel(Category c) { return getCategoryLevel(c.getIndex()); }

    public int getCategoryCurrentExp(int index) {
        int exp = getCategoryExp(index);
        int needed = 100;
        while (exp >= needed) {
            exp -= needed;
            needed += 50;
        }
        return exp;
    }
    public int getCategoryCurrentExp(Category c) { return getCategoryCurrentExp(c.getIndex()); }

    public int getCategoryMaxExp(int index) {
        int level = getCategoryLevel(index);
        return 100 + (level - 1) * 50;
    }
    public int getCategoryMaxExp(Category c) { return getCategoryMaxExp(c.getIndex()); }

    // getters
    public String getName() { return name; }
    public int getLevel() { return level; }
    public int getCurrentExp() { return currentExp; }
    public int getMaxExp() { return maxExp; }
    public int getExpToNextLevel() { return expToNextLevel; }
    public int[] getCategoryExp() { return categoryExp; }
    public int getCoin() { return coin; }
    public int getDailyStreak() { return dailyStreak; }
    public int getDailyQuestsCompleted() { return dailyQuestsCompleted; }
    public void setDailyQuestsCompleted(int n) { dailyQuestsCompleted = n; }

    // setters
    public void setName(String name) { this.name = name; }
    public void setLevel(int level) { this.level = level; }
    public void setCurrentExp(int currentExp) { this.currentExp = currentExp; }
    public void setMaxExp(int maxExp) { this.maxExp = maxExp; }
    public void setCoin(int coin) { this.coin = coin; }
    public void setDailyStreak(int dailyStreak) { this.dailyStreak = dailyStreak; }
}

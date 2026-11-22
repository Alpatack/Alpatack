package com.alpatack.questapp.model;

public class CustomQuest extends Quest {
    private int customExp;

    // basic constructor
    public CustomQuest(String title, String description, String category, String difficulty, int customExp) {
        super(title, description, category, difficulty);
        this.customExp = customExp;
    }

    // Calculates user defined exp capped at a maximum limit of 100.
    @Override
    public int calculateExp() {
        int maxAllowedExp = 100;
        return Math.min(customExp, maxAllowedExp);
    }

    // Prints summary details including custom exp.
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Custom EXP Reward: " + calculateExp());
    }

    // getter
    public int getCustomExp() { return customExp; }

    // setter
    public void setCustomExp(int exp) { customExp = exp; }
}

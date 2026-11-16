package com.alpatack.questapp.model;

public class PredefinedQuest extends Quest {
    private int questId;

    // predefined quest dataset list will be placed here
    /////-------

    // basic constructor
    public PredefinedQuest(int questId, String title, String description, String category, String difficulty) {
        super(title, description, category, difficulty);
        this.questId = questId;
    }

    // Calculates fixed exp based on quest difficulty tier.
    @Override
    public int calculateExp() {
        switch (getDifficulty().toLowerCase()) {
            case "easy":
                return 10;
            case "medium":
                return 25;
            case "hard":
                return 50;
            default:
                return 10;
        }
    }

    // Prints summary details including quest id and calculated exp.
    // function visuallization purposes only-----------
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Quest ID: " + questId + " | EXP Reward: " + calculateExp());
    }

    // getter
    public int getQuestId() { return questId; }

    // setter
    public void setQuestId(int id) { questId = id; }
}

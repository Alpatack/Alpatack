package com.alpatack.questapp.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Quest {
    private String title;
    private String description;
    private String category;
    private String difficulty;
    private boolean isCompleted;
    private ArrayList<String> steps = new ArrayList<>();
    private ArrayList<Boolean> stepDone = new ArrayList<>();

    // basic constructor
    public Quest(String title, String description, String category, String difficulty) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.difficulty = difficulty;
        this.isCompleted = false;
    }

    // Abstract method that forces all concrete quest subclasses to calculate their own exp.
    public abstract int calculateExp();

    // Marks the quest as completed once finished by the user.
    public void markCompleted() {
        isCompleted = true;
    }

    // Prints summary details for the quest.
    public void displayInfo() {
        System.out.println("Quest: " + title + " [" + (isCompleted ? "Completed" : "Pending") + "]");
        System.out.println("Category: " + category + " | Difficulty: " + difficulty);
        System.out.println("Description: " + description);
    }

    // Checklist steps shown in the quest detail screen.
    public void setSteps(List<String> s) {
        steps = new ArrayList<>(s);
        stepDone = new ArrayList<>();
        for (int i = 0; i < steps.size(); i++) stepDone.add(false);
    }
    public ArrayList<String> getSteps() { return steps; }
    public boolean isStepDone(int i) { return i < stepDone.size() && stepDone.get(i); }
    public void setStepDone(int i, boolean b) { if (i < stepDone.size()) stepDone.set(i, b); }
    public int getDoneCount() {
        int n = 0;
        for (boolean b : stepDone) if (b) n++;
        return n;
    }

    // getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getDifficulty() { return difficulty; }
    public boolean isCompleted() { return isCompleted; }

    // setters
    public void setTitle(String t) { title = t; }
    public void setDescription(String d) { description = d; }
    public void setCategory(String c) { category = c; }
    public void setDifficulty(String diff) { difficulty = diff; }
    public void setCompleted(boolean comp) { isCompleted = comp; }
}

package com.alpatack.questapp.model;

import java.util.ArrayList;

public class PredefinedQuest extends Quest {
    private int questId;

    // the pool of predefined quests (templates), shared by the whole app
    private static final ArrayList<PredefinedQuest> predefinedPool = new ArrayList<>();

    // predefined quest dataset: filled once, when this class is first used
    static {
        // physical
        add(1, "30mins Workout", "Get moving", "physical", "medium",
                "10 mins Warm-up / Stretches", "15 mins Cardio (Walk/Jog)", "5 mins Cool-down", "5 mins Hydration & Pulse Recovery");
        add(2, "Walk 5,000 steps", "Take a walk today", "physical", "easy", "Walk 5,000 steps");
        add(3, "Push-ups", "Build some strength", "physical", "medium", "3 sets of 10 push-ups", "Stretch your arms");
        add(4, "Drink 8 glasses of water", "Stay hydrated", "physical", "easy", "Drink 8 glasses of water");
        // mental
        add(5, "Journal", "Write about your day", "mental", "easy", "Write 3 things you did", "Write 1 thing you learned");
        add(6, "Read 20 pages", "Read a book or article", "mental", "medium", "Pick a book", "Read 20 pages");
        add(7, "Study session", "Focus without your phone", "mental", "hard", "Pick one subject", "Study for 45 minutes", "Write a short summary");
        add(8, "Learn something new", "Watch or read something useful", "mental", "easy", "Pick a topic", "Spend 15 minutes on it");
        // social
        add(9, "Chika w the besties", "Catch up with friends", "social", "easy", "Message a friend", "Have a short chat");
        add(10, "Help someone", "Do a kind act today", "social", "medium", "Find someone to help", "Do the kind act");
        add(11, "Call your family", "Check in on family", "social", "easy", "Call a family member", "Ask how their day was");
        add(12, "Meet someone new", "Say hi to someone new", "social", "hard", "Start a conversation", "Learn their name");
        // spiritual
        add(13, "Daily Prayer", "Take a quiet moment", "spiritual", "easy", "Find a quiet spot", "Pray");
        add(14, "Meditate 10 mins", "Breathe and reset", "spiritual", "easy", "Sit comfortably", "10 mins of focused breathing");
        add(15, "Gratitude list", "Count your blessings", "spiritual", "easy", "Write 5 things you're thankful for");
        add(16, "Quiet walk", "Walk without your phone", "spiritual", "medium", "Leave your phone", "Walk for 15 minutes");
        // discipline
        add(17, "Make your bed", "Start the day organized", "discipline", "easy", "Make your bed");
        add(18, "Finish a task you avoided", "Tackle it now", "discipline", "hard", "Pick the task", "Work on it for 25 mins", "Finish it");
        add(19, "Plan tomorrow", "Prepare for the next day", "discipline", "medium", "List 3 priorities", "Set out what you need");
        add(20, "No social media for 2 hours", "Stay focused", "discipline", "hard", "Put your phone away", "Do something useful instead");
    }

    // helper used by the static block above
    private static void add(int id, String title, String desc, String cat, String diff, String... steps) {
        PredefinedQuest q = new PredefinedQuest(id, title, desc, cat, diff);
        q.setSteps(java.util.Arrays.asList(steps));
        predefinedPool.add(q);
    }

    // basic constructor
    public PredefinedQuest(int questId, String title, String description, String category, String difficulty) {
        super(title, description, category, difficulty);
        this.questId = questId;
    }

    // no-parameter constructor
    public PredefinedQuest() {
        super("", "", "physical", "easy");   // Quest has no empty constructor, so give it default values
        this.questId = 0;
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

    // getters
    public int getQuestId() { return questId; }
    public static ArrayList<PredefinedQuest> getPredefinedPool() { return predefinedPool; }

    // setter
    public void setQuestId(int id) { questId = id; }
}
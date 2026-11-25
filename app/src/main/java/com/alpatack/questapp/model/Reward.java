package com.alpatack.questapp.model;

public class Reward {
    private String name;
    private String description;
    private int cost;
    private int timesRedeemed;

    // basic constructor (Initializes default values for simple setup)
    public Reward(String name) {
        this.name = name;
        this.description = "";
        this.cost = 0;
        this.timesRedeemed = 0;
    }

    // overloaded constructor (Allows setting all basic properties at creation)
    public Reward(String name, String description, int cost) {
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.timesRedeemed = 0;
    }

    // basic skeleton functions (To be expanded with full logic later)
    public boolean canRedeem(Player p) {
        return p.getCoin() >= cost;
    }

    public void redeem(Player p) {
        if (canRedeem(p) && p.spendCoins(cost)) {
            timesRedeemed++;
        }
    }

    public void displayInfo() {
        System.out.println("Reward: " + name + " | Cost: " + cost + " coins | Redeemed: " + timesRedeemed + " times");
    }

    // getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getCost() { return cost; }
    public int getTimesRedeemed() { return timesRedeemed; }

    // setters
    public void setName(String n) { name = n; }
    public void setDescription(String d) { description = d; }
    public void setCost(int c) { cost = c; }
    public void setTimesRedeemed(int t) { timesRedeemed = t; }
}

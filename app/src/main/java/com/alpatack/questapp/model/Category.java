package com.alpatack.questapp.model;

public enum Category {
    // index = position in Player's categoryExp array (written out, so reordering this list can't break saved data)
    PHYSICAL(0, "physical"),
    MENTAL(1, "mental"),
    SOCIAL(2, "social"),
    SPIRITUAL(3, "spiritual"),
    DISCIPLINE(4, "discipline");

    private final int index;
    private final String label;

    Category(int index, String label) {
        this.index = index;
        this.label = label;
    }

    public int getIndex() { return index; }
    public String getLabel() { return label; }

    // "mental" / "MENTAL" -> Category.MENTAL, or null if the name is unknown
    public static Category fromName(String name) {
        for (Category c : values()) {
            if (c.label.equalsIgnoreCase(name)) return c;
        }
        return null;
    }
}
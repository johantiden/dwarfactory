package com.github.johantiden.dwarfactory.game.entities.factory;

public enum ItemType {
    APPLE("Apple", 10),
    APPLE_JUICE("Juice", 10),
    MONEY("$", 10000000),;

    public final String prettyName;
    public final int maxAmount;

    ItemType(String prettyName, int maxAmount) {
        this.prettyName = prettyName;
        this.maxAmount = maxAmount;
    }
}

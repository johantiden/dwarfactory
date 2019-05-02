package com.github.johantiden.dwarfactory.components;

public enum Priority {

    HIGH(true),
    NORMAL(true),
    LOW(false);

    private final boolean wantsItems;

    Priority(boolean wantsItems) {this.wantsItems = wantsItems;}

    public boolean wantsItems() {
        return wantsItems;
    }
}

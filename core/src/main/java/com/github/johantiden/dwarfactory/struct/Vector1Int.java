package com.github.johantiden.dwarfactory.struct;

public class Vector1Int {
    public int value;

    public Vector1Int(int value) {
        this.value = value;
    }

    public void add(int amount) {
        value += amount;
    }

    public void sub(int amount) {
        value -= amount;
    }
}

package com.github.johantiden.dwarfactory.game.entities;

import com.github.johantiden.dwarfactory.math.Vector1Int;

public abstract class ItemStack<T> extends Vector1Int {

    public ItemStack(int amount) {
        super(amount);
    }

    public ItemStack<T> tryDrain(int maxAmount) {

        if (value >= maxAmount) {
            value -= maxAmount;
            return createNew(maxAmount);
        } else {
            ItemStack<T> partialStack = createNew(value);
            value = 0;
            return partialStack;
        }

    }

    public abstract ItemStack<T> createNew(int amount);

    public void takeAllFrom(ItemStack<T> itemStack) {
        int amountToTake = itemStack.value;
        this.value += amountToTake;
        itemStack.value = 0;
    }

    public int getAmount() {
        return value;
    }
}

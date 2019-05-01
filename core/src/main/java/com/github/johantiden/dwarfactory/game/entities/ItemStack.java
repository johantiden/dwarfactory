package com.github.johantiden.dwarfactory.game.entities;

public abstract class ItemStack<T> {
    private int amount;

    public ItemStack(int amount) {
        this.amount = amount;
    }

    public ItemStack<T> tryDrain(int maxAmount) {

        if (amount >= maxAmount) {
            amount -= maxAmount;
            return createNew(maxAmount);
        } else {
            ItemStack<T> partialStack = createNew(amount);
            amount = 0;
            return partialStack;
        }

    }

    public abstract ItemStack<T> createNew(int amount);

    public void takeAllFrom(ItemStack<T> itemStack) {
        int amountToTake = itemStack.amount;
        this.amount += amountToTake;
        itemStack.amount = 0;
    }

    public int getAmount() {
        return amount;
    }
}

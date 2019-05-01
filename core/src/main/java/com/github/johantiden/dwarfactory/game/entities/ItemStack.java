package com.github.johantiden.dwarfactory.game.entities;

import com.github.johantiden.dwarfactory.game.entities.factory.ItemType;
import com.github.johantiden.dwarfactory.math.Vector1Int;

public class ItemStack extends Vector1Int {

    public final ItemType itemType;

    public ItemStack(ItemType itemType, int amount) {
        super(amount);
        this.itemType = itemType;
    }

    public ItemStack tryDrain(int maxAmount) {

        if (value >= maxAmount) {
            value -= maxAmount;
            return new ItemStack(itemType, maxAmount);
        } else {
            ItemStack partialStack = new ItemStack(itemType, value);
            value = 0;
            return partialStack;
        }

    }

    public void takeAllFrom(ItemStack itemStack) {
        if (itemStack.itemType != itemType) {
            throw new IllegalArgumentException("ItemStacks are not of the same type!");
        }
        int amountToTake = itemStack.value;
        this.value += amountToTake;
        itemStack.value = 0;
    }

    public int getAmount() {
        return value;
    }

    public ItemStack copyWithAmount(int amount) {
        return new ItemStack(itemType, amount);
    }

    public ItemType getItemType() {
        return itemType;
    }
}

package com.github.johantiden.dwarfactory.game.entities;

import com.github.johantiden.dwarfactory.game.entities.factory.ItemType;

public class ItemStack {

    public static final int MAX_AMOUNT = 10;

    public final ItemType itemType;

    private int amount;

    public ItemStack(ItemType itemType, int amount) {
        this.itemType = itemType;
        if (amount > MAX_AMOUNT) {
            throw new IllegalArgumentException("amount (" + amount + ") cannot be over MAX_AMOUNT ("+MAX_AMOUNT+").");
        }
        this.amount = amount;
    }

    public ItemStack output(int maxAmount) {
        if (amount >= maxAmount) {
            amount -= maxAmount;
            return new ItemStack(itemType, maxAmount);
        } else {
            ItemStack partialStack = new ItemStack(itemType, amount);
            amount = 0;
            return partialStack;
        }
    }

    public void input(ItemStack otherStack) {
        verifyType(otherStack.itemType);
        int availableSpace = MAX_AMOUNT - amount;
        ItemStack output = otherStack.output(availableSpace);
        amount += output.amount;
    }

    public void inputAllOrThrow(ImmutableItemStack otherStack) {
        verifyType(otherStack.itemType);
        int availableSpace = MAX_AMOUNT - amount;
        if (availableSpace < otherStack.amount) {
            throw new IllegalArgumentException("Stack wouldn't fit!");
        }
        amount += otherStack.amount;
    }

    private void verifyType(ItemType itemType) {
        if (itemType != this.itemType) {
            throw new IllegalArgumentException("ItemType mismatch! this.itemType:"+this.itemType + ", itemType:"+itemType);
        }
    }

    public int getAmount() {
        return amount;
    }

    public ItemStack copyWithAmount(int amount) {
        return new ItemStack(itemType, amount);
    }

    public ItemType getItemType() {
        return itemType;
    }

    public boolean canFitFully(int amount) {
        return this.amount + amount <= MAX_AMOUNT;
    }

    public boolean canFitSomeMore() {
        return amount < MAX_AMOUNT;
    }

    public ImmutableItemStack snapshot() {
        return new ImmutableItemStack(itemType, amount);
    }
}

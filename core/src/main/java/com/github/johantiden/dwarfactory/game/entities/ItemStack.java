package com.github.johantiden.dwarfactory.game.entities;

import com.github.johantiden.dwarfactory.game.entities.factory.ItemType;

public class ItemStack {

    public final ItemType itemType;

    private int amount;

    public ItemStack(ItemType itemType, int amount) {
        this.itemType = itemType;
        if (amount > itemType.maxAmount) {
            throw new IllegalArgumentException("amount (" + amount + ") cannot be over maxAmount ("+itemType.maxAmount+").");
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
        int availableSpace = itemType.maxAmount - amount;
        ItemStack output = otherStack.output(availableSpace);
        amount += output.amount;
    }

    public void inputAllOrThrow(ImmutableItemStack otherStack) {
        verifyType(otherStack.itemType);
        int availableSpace = itemType.maxAmount - amount;
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
        return this.amount + amount <= itemType.maxAmount;
    }

    public boolean canFitSomeMore() {
        return amount < itemType.maxAmount;
    }

    public ImmutableItemStack snapshot() {
        return new ImmutableItemStack(itemType, amount);
    }

    @Override
    public final boolean equals(Object obj) {
        // Don't override equals! Object reference equals are part of business logic!
        return super.equals(obj);
    }

    public ItemStack negate() {
        return new ItemStack(itemType, -amount);
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isEmpty() {
        return amount == 0;
    }
}

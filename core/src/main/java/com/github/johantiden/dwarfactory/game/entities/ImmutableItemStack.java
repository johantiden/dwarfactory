package com.github.johantiden.dwarfactory.game.entities;

import com.github.johantiden.dwarfactory.game.entities.factory.ItemType;

public class ImmutableItemStack {

    public final ItemType itemType;
    public final int amount;

    public ImmutableItemStack(ItemType itemType, int amount) {
        this.itemType = itemType;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public ImmutableItemStack copyWithAmount(int amount) {
        return new ImmutableItemStack(itemType, amount);
    }

    public ItemType getItemType() {
        return itemType;
    }

    public ItemStack toMutable() {
        return new ItemStack(itemType, amount);
    }
}

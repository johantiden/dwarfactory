package com.github.johantiden.dwarfactory.game.entities;

import com.github.johantiden.dwarfactory.game.entities.factory.ItemType;
import com.github.johantiden.dwarfactory.math.ImmutableVectorInt;

public class ImmutableItemStack extends ImmutableVectorInt {

    public final ItemType itemType;

    public ImmutableItemStack(ItemType itemType, int amount) {
        super(amount);
        this.itemType = itemType;
    }

    public int getAmount() {
        return value;
    }

    public ImmutableItemStack copyWithAmount(int amount) {
        return new ImmutableItemStack(itemType, amount);
    }

    public ItemType getItemType() {
        return itemType;
    }
}

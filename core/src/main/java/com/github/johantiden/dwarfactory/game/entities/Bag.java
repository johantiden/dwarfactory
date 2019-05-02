package com.github.johantiden.dwarfactory.game.entities;

import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.github.johantiden.dwarfactory.game.entities.factory.ItemType;
import com.github.johantiden.dwarfactory.struct.JMap;

import java.util.Collection;
import java.util.function.Predicate;

public class Bag {

    private final JMap<ItemType, ItemStack> stacks;
    private final Predicate<ItemType> itemFilter;

    public Bag(Predicate<ItemType> itemFilter) {
        this.itemFilter = itemFilter;
        this.stacks = new JMap<>();
    }

    public static Bag createFiltered(Collection<ItemType> itemTypes) {
        return new Bag(itemTypes::contains);
    }

    public boolean canHaveType(ItemType itemType) {
        return itemFilter.test(itemType);
    }

    public boolean canFitFully(ImmutableItemStack itemStack) {
        return canHaveType(itemStack.itemType)
                && getStack(itemStack.itemType).canFitFully(itemStack.getAmount());
    }

    public boolean canFitSome(ItemType itemType) {
        return canHaveType(itemType)
                && getStack(itemType).canFitSomeMore();
    }

    private ItemStack getStack(ItemType itemType) {
        verifyType(itemType);
        stacks.computeIfAbsent(itemType, () -> new ItemStack(itemType, 0));
        return stacks.get(itemType);
    }

    public void input(ItemStack itemStack) {
        verifyType(itemStack.itemType);
        getStack(itemStack.itemType).input(itemStack);
    }

    public void inputAllOrThrow(ImmutableItemStack itemStack) {
        verifyType(itemStack.itemType);
        getStack(itemStack.itemType).inputAllOrThrow(itemStack);
    }

    private void verifyType(ItemType itemType) {
        if (!canHaveType(itemType)) {
            throw new IllegalArgumentException("Bag does not support ItemType " + itemType);
        }
    }

    public ItemStack output(ImmutableItemStack itemStack) {
        return output(itemStack.itemType, itemStack.amount);
    }

    public ItemStack output(ItemType itemType, int amount) {
        verifyType(itemType);
        return getStack(itemType).output(amount);
    }

    public ImmutableItemStack getSnapshot(ItemType itemType) {
        verifyType(itemType);
        return getStack(itemType).snapshot();
    }

    public ImmutableBag snapshot() {
        return new ImmutableBag(stacks.snapshot(ItemStack::snapshot));
    }

    public ImmutableArray<ImmutableItemStack> snapshotStacks() {
        Array<ImmutableItemStack> array = new Array<>();
        for (ItemStack itemStack : stacks.values()) {
            array.add(itemStack.snapshot());
        }

        return new ImmutableArray<>(array);
    }
}

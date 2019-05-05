package com.github.johantiden.dwarfactory.game.entities;

import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.github.johantiden.dwarfactory.game.entities.factory.ItemType;
import com.github.johantiden.dwarfactory.util.JLists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Bag {

    private final Map<ItemType, ItemStack> stacks = new HashMap<>();
    private final List<ItemStack> incomingChangesFromJobs = new ArrayList<>();

    private final Predicate<ItemType> itemFilter;

    public Bag(Predicate<ItemType> itemFilter) {
        this.itemFilter = itemFilter;
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
        return canHaveType(itemType) &&
                getStack(itemType).getAmount() + getDibsSum(itemType) < itemType.maxAmount;
    }

    ItemStack getStack(ItemType itemType) {
        verifyType(itemType);
        stacks.computeIfAbsent(itemType, it -> new ItemStack(it, 0));
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
        return new ImmutableBag(JLists.immutable(stacks, Function.identity(), ItemStack::snapshot));
    }

    public ImmutableArray<ImmutableItemStack> snapshotStacks() {
        Array<ImmutableItemStack> array = new Array<>();
        for (ItemStack itemStack : stacks.values()) {
            array.add(itemStack.snapshot());
        }

        return new ImmutableArray<>(array);
    }

    public Optional<ImmutableItemStack> getBiggestStack() {
        if (isEmpty()) {
            return Optional.empty();
        }
        ItemStack biggest = stacks.values().iterator().next();
        for (ItemStack itemStack : stacks.values()) {
            if (itemStack.getAmount() > biggest.getAmount()) {
                biggest = itemStack;
            }
        }
        return Optional.of(biggest.snapshot());
    }

    public boolean isEmpty() {
        for (ItemStack itemStack : stacks.values()) {
            if (itemStack.getAmount() > 0) {
                return false;
            }
        }

        return true;
    }

    public void addDibs(ItemStack dibs) {
        incomingChangesFromJobs.add(dibs);
    }

    public void removeDibs(ItemStack dibs) {
        incomingChangesFromJobs.remove(dibs);
    }

    @Override
    public String toString() {
        List<String> rows = new ArrayList<>();

        for (ItemType itemType : stacks.keySet()) {
            ItemStack bagItemStack = stacks.get(itemType);
            int dibsSum = getDibsSum(itemType);
            if (dibsSum != 0) {
                if (dibsSum + bagItemStack.getAmount() != 0) {
                    rows.add(String.valueOf(bagItemStack.getAmount()) + withSign(dibsSum) + " " + itemType.prettyName);
                }
            } else {
                if (bagItemStack.getAmount() != 0) {
                    rows.add(String.valueOf(bagItemStack.getAmount()) + " " + itemType.prettyName);
                }
            }

        }

        return String.join(",", rows);
    }

    public int getDibsSum(ItemType itemType) {
        List<ItemStack> dibs = incomingChangesFromJobs.stream()
                .filter(is -> is.itemType == itemType)
                .collect(Collectors.toList());

        return dibs.stream().mapToInt(ItemStack::getAmount).sum();
    }

    private String withSign(int dibsSum) {
        if (dibsSum < 0) {
            return String.valueOf(dibsSum);
        }
        return sign(dibsSum) + dibsSum;
    }

    private static String sign(int integer) {
        if (integer == 0) {
            return "±";
        }
        if (integer < 0) {
            return "-";
        }
        return "+";

    }
}

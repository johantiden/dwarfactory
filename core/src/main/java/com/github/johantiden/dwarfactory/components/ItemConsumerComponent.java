package com.github.johantiden.dwarfactory.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.github.johantiden.dwarfactory.game.entities.Bag;
import com.github.johantiden.dwarfactory.game.entities.ImmutableBag;
import com.github.johantiden.dwarfactory.game.entities.ImmutableItemStack;
import com.github.johantiden.dwarfactory.game.entities.ItemStack;
import com.github.johantiden.dwarfactory.game.entities.factory.ItemType;

import java.util.Collection;
import java.util.function.Predicate;

public class ItemConsumerComponent implements Component {

    private final Entity entity;
    private final Bag bag;
    private final Priority priority;

    public ItemConsumerComponent(Entity entity, Bag bag, Priority priority) {
        this.entity = entity;
        this.bag = bag;
        this.priority = priority;
    }

    public ItemConsumerComponent(Entity entity, Predicate<ItemType> itemTypeFilter, Priority priority) {
        this.entity = entity;
        this.bag = new Bag(itemTypeFilter);
        this.priority = priority;
    }

    public static ItemConsumerComponent createFiltered(Entity entity, Collection<ItemType> outputTypes, Priority priority) {
        return new ItemConsumerComponent(entity, outputTypes::contains, priority);
    }

    public static ItemConsumerComponent createWithSharedBag(Entity entity, Bag sharedBag, Priority priority) {
        return new ItemConsumerComponent(entity, sharedBag, priority);
    }

    public void input(ItemStack itemStack) {
        bag.input(itemStack);
    }

    public boolean wants(ItemType itemType) {
        return bag.canHaveType(itemType) &&
                bag.canFitSome(itemType);
    }

    public void output(ImmutableItemStack itemStack) {
        bag.output(itemStack);
    }

    public ImmutableItemStack getSnapshot(ItemType itemType) {
        return bag.getSnapshot(itemType);
    }

    public ImmutableBag getFullSnapshot() {
        return bag.snapshot();
    }

    public Entity hack_getEntity() {
        return entity;
    }

    public Bag getBag() {
        return bag;
    }

    public Priority getPriority() {
        return priority;
    }
}

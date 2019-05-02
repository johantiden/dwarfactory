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

public class ItemProducerComponent implements Component {

    private final Entity entity;
    private final Bag bag;
    private final Priority priority;

    public ItemProducerComponent(Entity entity, Bag bag, Priority priority) {
        this.entity = entity;
        this.bag = bag;
        this.priority = priority;
    }

    public ItemProducerComponent(Entity entity, Predicate<ItemType> itemTypeFilter, Priority priority) {
        this.entity = entity;
        this.bag = new Bag(itemTypeFilter);
        this.priority = priority;
    }

    public static ItemProducerComponent createFiltered(Entity entity, Collection<ItemType> outputTypes, Priority priority) {
        return new ItemProducerComponent(entity, outputTypes::contains, priority);
    }

    public static ItemProducerComponent createWithSharedBag(Entity entity, Bag sharedBag, Priority priority) {
        return new ItemProducerComponent(entity, sharedBag, priority);
    }

    public ItemStack output(ItemType itemType, int amount) {
        return bag.output(itemType, amount);
    }

    public ImmutableBag getAvailableOutput() {
        return bag.snapshot()
                .filterByValue(stack -> stack.getAmount() > 0);
    }

    public ImmutableBag getOutputStacks() {
        return bag.snapshot();
    }

    public void input(ImmutableItemStack itemStack) {
        bag.inputAllOrThrow(itemStack);
    }

    public Bag getBag() {
        return bag;
    }

    public Entity hack_getEntity() {
        return entity;
    }

    public boolean canFitFully(ImmutableItemStack itemStack) {
        return bag.canFitFully(itemStack);
    }

    public Priority getPriority() {
        return priority;
    }
}

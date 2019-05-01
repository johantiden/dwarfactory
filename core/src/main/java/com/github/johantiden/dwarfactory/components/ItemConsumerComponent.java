package com.github.johantiden.dwarfactory.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.github.johantiden.dwarfactory.game.entities.ImmutableItemStack;
import com.github.johantiden.dwarfactory.game.entities.ItemStack;
import com.github.johantiden.dwarfactory.game.entities.factory.ItemType;

import java.util.Collection;
import java.util.EnumMap;

public class ItemConsumerComponent implements Component {

    private final Entity entity;
    private final EnumMap<ItemType, ItemStack> inputStacks;

    public ItemConsumerComponent(Entity entity, Iterable<ItemType> outputTypes) {
        this.entity = entity;
        EnumMap<ItemType, ItemStack> objectMap = new EnumMap<>(ItemType.class);

        for (ItemType outputType : outputTypes) {
            objectMap.put(outputType, new ItemStack(outputType, 0));
        }

        this.inputStacks = objectMap;
    }

    public void accept(ItemStack itemStack) {
        inputStacks.get(itemStack.itemType).add(itemStack.value);
    }

    public boolean wants(ItemType itemType) {
        return inputStacks.containsKey(itemType);
    }

    public void sub(ImmutableItemStack itemStack) {
        inputStacks.get(itemStack.itemType).sub(itemStack.value);
    }

    public ItemStack get(ItemType itemType) {
        return inputStacks.get(itemType);
    }

    public Collection<ItemStack> getCurrentBuffer() {
        return inputStacks.values();
    }

    public Entity hack_getEntity() {
        return entity;
    }
}

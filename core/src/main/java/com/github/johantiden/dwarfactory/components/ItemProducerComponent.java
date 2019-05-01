package com.github.johantiden.dwarfactory.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.kiwi.util.gdx.collection.immutable.ImmutableObjectMap;
import com.github.johantiden.dwarfactory.game.entities.ImmutableItemStack;
import com.github.johantiden.dwarfactory.game.entities.ItemStack;
import com.github.johantiden.dwarfactory.game.entities.factory.ItemType;
import com.github.johantiden.dwarfactory.util.JLists;

import java.util.List;
import java.util.stream.Collectors;

public class ItemProducerComponent implements Component {

    private final Entity entity;
    private final ImmutableObjectMap<ItemType, ItemStack> outputStacks;

    public ItemProducerComponent(Entity entity, Iterable<ItemType> outputTypes) {
        this.entity = entity;
        ObjectMap<ItemType, ItemStack> objectMap = new ObjectMap<>();

        for (ItemType outputType : outputTypes) {
            objectMap.put(outputType, new ItemStack(outputType, 0));
        }

        this.outputStacks = new ImmutableObjectMap<>(objectMap);
    }

    public ItemStack drain(ItemType itemType, int amount) {
        if (outputStacks.containsKey(itemType)) {
            return outputStacks.get(itemType).tryDrain(amount);
        } else {
            return new ItemStack(itemType, 0);
        }
    }

    public List<ItemStack> getAvailableOutput() {
        return JLists.stream(outputStacks.values())
                .filter(stack -> stack.getAmount() > 0)
                .collect(Collectors.toList());
    }

    public void add(ImmutableItemStack itemStack) {
        outputStacks.get(itemStack.itemType).add(itemStack.value);
    }

    public Entity hack_getEntity() {
        return entity;
    }
}

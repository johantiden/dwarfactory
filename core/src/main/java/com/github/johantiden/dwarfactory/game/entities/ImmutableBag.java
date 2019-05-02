package com.github.johantiden.dwarfactory.game.entities;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.github.czyzby.kiwi.util.gdx.collection.immutable.ImmutableObjectMap;
import com.github.johantiden.dwarfactory.game.entities.factory.ItemType;

import java.util.Optional;
import java.util.function.Predicate;

public class ImmutableBag {
    private final ImmutableObjectMap<ItemType, ImmutableItemStack> stacks;

    public ImmutableBag(ImmutableObjectMap<ItemType, ImmutableItemStack> stacks) {
        this.stacks = stacks;
    }

    public ImmutableBag filterByValue(Predicate<ImmutableItemStack> predicate) {
        ObjectMap<ItemType, ImmutableItemStack> newMap = new ObjectMap<>(stacks);

        for (Entry<ItemType, ImmutableItemStack> entry : stacks.entries()) {
            if (!predicate.test(entry.value)) {
                newMap.remove(entry.key);
            }
        }

        return new ImmutableBag(new ImmutableObjectMap<>(newMap));
    }

    public Optional<ImmutableItemStack> findAny() {
        if (stacks.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(stacks.values().next());
        }
    }

    public boolean isEmpty() {
        return stacks.isEmpty();
    }
}

package com.github.johantiden.dwarfactory.struct;

import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.kiwi.util.gdx.collection.immutable.ImmutableObjectMap;

import java.util.function.Function;
import java.util.function.Supplier;

public class JMap<K, V> extends ObjectMap<K, V> {

    public void computeIfAbsent(K key, Supplier<V> vSupplier) {
        if (!containsKey(key)) {
            put(key, vSupplier.get());
        }
    }

    public <I> ImmutableObjectMap<K, I> snapshot(Function<V, I> valueSnapshotter) {
        ObjectMap<K, I> newObjectMap = new ObjectMap<>();

        for (Entry<K, V> entry : entries()) {
            newObjectMap.put(entry.key, valueSnapshotter.apply(entry.value));
        }

        return new ImmutableObjectMap<>(newObjectMap);
    }
}

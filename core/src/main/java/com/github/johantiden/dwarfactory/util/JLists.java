package com.github.johantiden.dwarfactory.util;

import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.kiwi.util.gdx.collection.immutable.ImmutableObjectMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class JLists {

    public static <T> List<T> newArrayList(T... ts) {
        return Arrays.asList(ts);
    }

    public static <T> List<T> concat(List<T> a, List<T> b) {
        ArrayList<T> list = new ArrayList<>(a.size() + b.size());
        list.addAll(a);
        list.addAll(b);
        return list;
    }

    public static <T, R> ImmutableArray<R> mapToImmutable(Function<T, R> mapper, Iterable<T> ts) {
        Array<R> list = new Array<>();
        for (T t : ts) {
            list.add(mapper.apply(t));
        }
        return new ImmutableArray<>(list);
    }

    public static <T> ImmutableArray<T> filterToImmutable(Predicate<T> filter, Iterable<T> ts) {
        Array<T> list = new Array<>();
        for (T t : ts) {
            if (filter.test(t)) {
                list.add(t);
            }
        }
        return new ImmutableArray<>(list);
    }

    public static <T> Stream<T> stream(Iterable<T> iterable) {
        Stream.Builder<T> builder = Stream.builder();
        for (T t : iterable) {
            builder.accept(t);
        }
        return builder.build();
    }

    public static <K, V, KI, VI> ImmutableObjectMap<KI, VI> immutable(Map<K, V> map, Function<K, KI> keyToImmutable, Function<V, VI> valueToImmutable) {
        ObjectMap<KI, VI> objectMap = new ObjectMap<>();

        for (Map.Entry<K, V> entry : map.entrySet()) {
            objectMap.put(keyToImmutable.apply(entry.getKey()), valueToImmutable.apply(entry.getValue()));
        }

        return new ImmutableObjectMap<>(objectMap);
    }
}

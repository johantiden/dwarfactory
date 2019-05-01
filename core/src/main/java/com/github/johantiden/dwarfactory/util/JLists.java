package com.github.johantiden.dwarfactory.util;

import java.util.ArrayList;
import java.util.List;

public class JLists {

    public static <T> List<T> newArrayList(T t) {
        ArrayList<T> list = new ArrayList<>();
        list.add(t);
        return list;
    }

    public static <T> List<T> concat(List<T> a, List<T> b) {
        ArrayList<T> list = new ArrayList<>(a.size() + b.size());
        list.addAll(a);
        list.addAll(b);
        return list;
    }
}

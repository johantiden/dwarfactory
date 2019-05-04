package com.github.johantiden.dwarfactory.game.entities.factory;

import com.github.johantiden.dwarfactory.game.entities.ImmutableItemStack;

import java.util.Collections;

import static com.github.johantiden.dwarfactory.util.JLists.newArrayList;

public class Recipies {
    public static final Recipe APPLE_GARDEN = new Recipe(
            Collections.emptyList(),
            newArrayList(new ImmutableItemStack(ItemType.APPLE, 1)),
            8
    );

    public static final Recipe APPLE_JUICER = new Recipe(
            newArrayList(new ImmutableItemStack(ItemType.APPLE, 1)),
            newArrayList(new ImmutableItemStack(ItemType.APPLE_JUICE, 1)),
            6
    );

    public static final Recipe SELL_APPLE_JUICE = new Recipe(
            newArrayList(new ImmutableItemStack(ItemType.APPLE_JUICE, 1)),
            newArrayList(new ImmutableItemStack(ItemType.MONEY, 1)),
            20
    );

}

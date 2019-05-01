package com.github.johantiden.dwarfactory.game.entities.factory;

import com.badlogic.ashley.core.Entity;
import com.github.johantiden.dwarfactory.game.entities.ItemStack;

public interface ItemProducer<T> {
    ItemStack<T> drain(int amount);

    Entity getEntity();
}

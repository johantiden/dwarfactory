package com.github.johantiden.dwarfactory.game.entities;

import com.badlogic.ashley.core.Entity;

public interface ItemProducer<T> {
    ItemStack<T> drain(int amount);

    Entity getEntity();
}

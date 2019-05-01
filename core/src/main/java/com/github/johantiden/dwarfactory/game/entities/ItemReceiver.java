package com.github.johantiden.dwarfactory.game.entities;

import com.badlogic.ashley.core.Entity;

public interface ItemReceiver<T> {
    void accept(ItemStack<T> itemStack);


    Entity getEntity();
}

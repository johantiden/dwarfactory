package com.github.johantiden.dwarfactory.game.entities.factory;

import com.badlogic.ashley.core.Entity;
import com.github.johantiden.dwarfactory.game.entities.ItemStack;

public interface ItemReceiver<T> {
    void accept(ItemStack<T> itemStack);


    Entity getEntity();
}

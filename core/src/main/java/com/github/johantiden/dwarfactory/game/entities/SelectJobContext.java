package com.github.johantiden.dwarfactory.game.entities;

import com.github.johantiden.dwarfactory.components.ItemConsumerComponent;

public class SelectJobContext {
    public final Iterable<ItemConsumerComponent> allItemConsumers;

    public SelectJobContext(Iterable<ItemConsumerComponent> allItemConsumers) {this.allItemConsumers = allItemConsumers;}
}

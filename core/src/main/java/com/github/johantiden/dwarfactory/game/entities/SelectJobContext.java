package com.github.johantiden.dwarfactory.game.entities;

import com.github.johantiden.dwarfactory.components.ItemConsumerComponent;
import com.github.johantiden.dwarfactory.components.ItemProducerComponent;

public class SelectJobContext {
    public final Iterable<ItemConsumerComponent> allItemConsumers;
    public final Iterable<ItemProducerComponent> allItemProducers;

    public SelectJobContext(Iterable<ItemConsumerComponent> allItemConsumers, Iterable<ItemProducerComponent> allItemProducers) {this.allItemConsumers = allItemConsumers;
        this.allItemProducers = allItemProducers;
    }
}

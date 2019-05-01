package com.github.johantiden.dwarfactory.components;

public class TaskContext {
    public final ItemProducerComponent itemProducer;
    public final ItemConsumerComponent itemConsumerComponent;

    public TaskContext(ItemProducerComponent itemProducer, ItemConsumerComponent itemConsumerComponent) {
        this.itemProducer = itemProducer;
        this.itemConsumerComponent = itemConsumerComponent;
    }
}

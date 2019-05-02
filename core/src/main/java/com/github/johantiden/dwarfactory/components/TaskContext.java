package com.github.johantiden.dwarfactory.components;

public class TaskContext {
    public final ItemProducerComponent itemProducer;
    public final ItemConsumerComponent itemConsumer;

    public TaskContext(ItemProducerComponent itemProducer, ItemConsumerComponent itemConsumer) {
        this.itemProducer = itemProducer;
        this.itemConsumer = itemConsumer;
    }
}

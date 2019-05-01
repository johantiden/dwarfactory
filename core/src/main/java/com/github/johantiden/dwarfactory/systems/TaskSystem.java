package com.github.johantiden.dwarfactory.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.github.johantiden.dwarfactory.Dwarfactory;
import com.github.johantiden.dwarfactory.components.ItemProducerComponent;
import com.github.johantiden.dwarfactory.components.ItemConsumerComponent;
import com.github.johantiden.dwarfactory.components.TaskComponent;
import com.github.johantiden.dwarfactory.components.TaskContext;

public class TaskSystem extends EntitySystem {
    public ImmutableArray<Entity> entities;

    private ComponentMapper<TaskComponent> taskManager = ComponentMapper.getFor(TaskComponent.class);
    private ComponentMapper<ItemProducerComponent> itemProducerMapper = ComponentMapper.getFor(ItemProducerComponent.class);
    private ComponentMapper<ItemConsumerComponent> itemConsumerMapper = ComponentMapper.getFor(ItemConsumerComponent.class);

    @Override
    public void addedToEngine (Engine engine) {
        entities = engine.getEntitiesFor(Family.all(TaskComponent.class, TaskComponent.class).get());
        Dwarfactory.log("TaskSystem added to engine.");
    }

    @Override
    public void removedFromEngine (Engine engine) {
        Dwarfactory.log("TaskSystem removed from engine.");
        entities = null;
    }

    @Override
    public void update(float deltaTime) {

        for (Entity entity : entities) {

            TaskComponent task = taskManager.get(entity);
            ItemProducerComponent itemProducerComponent = itemProducerMapper.has(entity) ? itemProducerMapper.get(entity) : null;
            ItemConsumerComponent itemConsumerComponent = itemConsumerMapper.has(entity) ? itemConsumerMapper.get(entity) : null;
            TaskContext taskContext = new TaskContext(itemProducerComponent, itemConsumerComponent);
            task.addTime(taskContext, deltaTime);

            if (task.isComplete(taskContext)) {
                task.finish(taskContext);
            }
        }
    }
}

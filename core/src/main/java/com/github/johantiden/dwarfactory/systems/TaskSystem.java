package com.github.johantiden.dwarfactory.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.github.johantiden.dwarfactory.Dwarfactory;
import com.github.johantiden.dwarfactory.components.TaskComponent;

public class TaskSystem extends EntitySystem {
    public ImmutableArray<Entity> entities;

    private ComponentMapper<TaskComponent> taskManager = ComponentMapper.getFor(TaskComponent.class);

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
            task.addTime(deltaTime);

            if (task.isComplete()) {
                task.finish();
            }
        }
    }
}

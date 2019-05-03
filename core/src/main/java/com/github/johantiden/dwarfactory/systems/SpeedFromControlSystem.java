package com.github.johantiden.dwarfactory.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.github.johantiden.dwarfactory.components.ControlComponent;
import com.github.johantiden.dwarfactory.components.SpeedComponent;

public class SpeedFromControlSystem extends EntitySystem {

    private final ComponentMapper<ControlComponent> controlMapper = ComponentMapper.getFor(ControlComponent.class);
    private final ComponentMapper<SpeedComponent> speedMapper = ComponentMapper.getFor(SpeedComponent.class);

    private ImmutableArray<Entity> entities;

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(ControlComponent.class, SpeedComponent.class).get());
    }

    @Override
    public void removedFromEngine (Engine engine) {
        entities = new ImmutableArray<>(new Array<>());
    }

    @Override
    public void update(float deltaTime) {

        for (Entity entity : entities) {
            ControlComponent control = controlMapper.get(entity);
            SpeedComponent speed = speedMapper.get(entity);

            speed.x = control.asSpeed().x;
            speed.y = control.asSpeed().y;
        }
    }
}

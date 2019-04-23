package com.github.johantiden.dwarfactory.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.github.johantiden.dwarfactory.Dwarfactory;
import com.github.johantiden.dwarfactory.components.AccelerationComponent;
import com.github.johantiden.dwarfactory.components.SpeedComponent;

public class AccelerationSystem extends EntitySystem {
    public ImmutableArray<Entity> entities;

    private ComponentMapper<AccelerationComponent> am = ComponentMapper.getFor(AccelerationComponent.class);
    private ComponentMapper<SpeedComponent> mm = ComponentMapper.getFor(SpeedComponent.class);

    @Override
    public void addedToEngine (Engine engine) {
        entities = engine.getEntitiesFor(Family.all(AccelerationComponent.class, SpeedComponent.class).get());
        Dwarfactory.log("AccelerationSystem added to engine.");
    }

    @Override
    public void removedFromEngine (Engine engine) {
        Dwarfactory.log("AccelerationSystem removed from engine.");
        entities = null;
    }

    @Override
    public void update (float deltaTime) {

        for (int i = 0; i < entities.size(); ++i) {
            Entity e = entities.get(i);

            AccelerationComponent a = am.get(e);
            SpeedComponent s = mm.get(e);

            s.speed.x += a.acceleration.x * deltaTime;
            s.speed.y += a.acceleration.y * deltaTime;
        }
    }
}

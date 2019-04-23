package com.github.johantiden.dwarfactory.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.github.johantiden.dwarfactory.Dwarfactory;
import com.github.johantiden.dwarfactory.components.SpeedComponent;
import com.github.johantiden.dwarfactory.components.PositionComponent;

public class MovementSystem extends EntitySystem {
    public ImmutableArray<Entity> entities;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<SpeedComponent> mm = ComponentMapper.getFor(SpeedComponent.class);

    @Override
    public void addedToEngine (Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PositionComponent.class, SpeedComponent.class).get());
        Dwarfactory.log("MovementSystem added to engine.");
    }

    @Override
    public void removedFromEngine (Engine engine) {
        Dwarfactory.log("MovementSystem removed from engine.");
        entities = null;
    }

    @Override
    public void update (float deltaTime) {

        for (int i = 0; i < entities.size(); ++i) {
            Entity e = entities.get(i);

            PositionComponent p = pm.get(e);
            SpeedComponent s = mm.get(e);

            if (s.speed.len() > s.maxSpeed) {
                s.speed.setLength(s.maxSpeed);
            }

            p.position.x += s.speed.x * deltaTime;
            p.position.y += s.speed.y * deltaTime;
        }

//            log(entities.size() + " Entities updated in MovementSystem.");
    }
}

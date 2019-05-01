package com.github.johantiden.dwarfactory.systems.physics;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.github.johantiden.dwarfactory.Dwarfactory;
import com.github.johantiden.dwarfactory.components.AccelerationComponent;
import com.github.johantiden.dwarfactory.components.ForceContext;
import com.github.johantiden.dwarfactory.components.ForcesComponent;
import com.github.johantiden.dwarfactory.components.SpeedComponent;

public class ForceSystem extends EntitySystem {
    public ImmutableArray<Entity> entities;

    private ComponentMapper<AccelerationComponent> accelerationMapper = ComponentMapper.getFor(AccelerationComponent.class);
    private ComponentMapper<SpeedComponent> speedMapper = ComponentMapper.getFor(SpeedComponent.class);
    private ComponentMapper<ForcesComponent> forcesMapper = ComponentMapper.getFor(ForcesComponent.class);

    @Override
    public void addedToEngine (Engine engine) {
        entities = engine.getEntitiesFor(Family.all(AccelerationComponent.class, ForcesComponent.class, SpeedComponent.class).get());
        Dwarfactory.log("AccelerationSystem added to engine.");
    }

    @Override
    public void removedFromEngine (Engine engine) {
        Dwarfactory.log("AccelerationSystem removed from engine.");
        entities = null;
    }

    @Override
    public void update(float deltaTime) {

        for (Entity entity : entities) {

            ForcesComponent forces = forcesMapper.get(entity);
            AccelerationComponent acceleration = accelerationMapper.get(entity);
            SpeedComponent speedComponent = speedMapper.get(entity);

            acceleration.x = 0;
            acceleration.y = 0;
            for (Vector2 force : forces.getForces(new ForceContext(speedComponent))) {
                acceleration.x += force.x;
                acceleration.y += force.y;
            }
        }
    }
}

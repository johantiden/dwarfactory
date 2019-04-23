package com.github.johantiden.dwarfactory.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.github.johantiden.dwarfactory.Dwarfactory;
import com.github.johantiden.dwarfactory.components.AngleComponent;
import com.github.johantiden.dwarfactory.components.AngularSpeedComponent;

public class AngularMovementSystem extends EntitySystem {
    public ImmutableArray<Entity> entities;

    private ComponentMapper<AngleComponent> am = ComponentMapper.getFor(AngleComponent.class);
    private ComponentMapper<AngularSpeedComponent> sm = ComponentMapper.getFor(AngularSpeedComponent.class);

    @Override
    public void addedToEngine (Engine engine) {
        entities = engine.getEntitiesFor(Family.all(AngleComponent.class, AngularSpeedComponent.class).get());
        Dwarfactory.log("AngularMovementSystem added to engine.");
    }

    @Override
    public void removedFromEngine (Engine engine) {
        Dwarfactory.log("AngularMovementSystem removed from engine.");
        entities = null;
    }

    @Override
    public void update (float deltaTime) {

        for (int i = 0; i < entities.size(); ++i) {
            Entity e = entities.get(i);

            AngleComponent p = am.get(e);
            AngularSpeedComponent m = sm.get(e);

            p.angle += m.angularSpeed * deltaTime;
        }

//            log(entities.size() + " Entities updated in MovementSystem.");
    }
}

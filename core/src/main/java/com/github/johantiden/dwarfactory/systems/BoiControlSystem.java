package com.github.johantiden.dwarfactory.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.github.johantiden.dwarfactory.components.AccelerationComponent;
import com.github.johantiden.dwarfactory.components.ControlComponent;
import com.github.johantiden.dwarfactory.components.PositionComponent;

import java.util.Objects;

public class BoiControlSystem extends EntitySystem {

    private final ComponentMapper<PositionComponent> positionMapper = ComponentMapper.getFor(PositionComponent.class);
    private final ComponentMapper<AccelerationComponent> accelerationMapper = ComponentMapper.getFor(AccelerationComponent.class);
    private final ComponentMapper<ControlComponent> controlMapper = ComponentMapper.getFor(ControlComponent.class);
    private ImmutableArray<Entity> entities;


    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(ControlComponent.class).get());
    }

    @Override
    public void removedFromEngine (Engine engine) {
        entities = new ImmutableArray<>(new Array<>());
    }

    @Override
    public void update(float deltaTime) {

        for (Entity entity : entities) {
            ControlComponent control = controlMapper.get(entity);
            PositionComponent position = positionMapper.get(entity);
            AccelerationComponent acceleration = accelerationMapper.get(entity);

            if (control.hasJob()) {
                if (control.isInRange(position)) {
                    control.finishJob();
                }
            }
            if (!control.hasJob()) {
                control.trySelectNewJob();
            }
            if (control.hasJob()) {
                tryToAchieveGoal(control, acceleration, position);
            }
        }
    }

    private void tryToAchieveGoal(ControlComponent control, AccelerationComponent acceleration, PositionComponent position) {
        PositionComponent target = positionMapper.get(control.getTarget());
        accelerateTowards(target, acceleration, position);
    }

    private static void accelerateTowards(PositionComponent target, AccelerationComponent acceleration, PositionComponent position) {
        Objects.requireNonNull(target);
        Objects.requireNonNull(acceleration);
        Objects.requireNonNull(position);

        Vector2 targetDirectionVector = target.cpy()
                .sub(position)
                .setLength(1);

        float accelerationFactor = 100;
        Vector2 newAcceleration = targetDirectionVector.scl(accelerationFactor);
        acceleration.x = newAcceleration.x;
        acceleration.y = newAcceleration.y;
    }

}

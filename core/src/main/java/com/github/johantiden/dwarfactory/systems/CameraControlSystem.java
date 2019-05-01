package com.github.johantiden.dwarfactory.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.github.johantiden.dwarfactory.components.AccelerationComponent;
import com.github.johantiden.dwarfactory.components.PositionComponent;
import com.github.johantiden.dwarfactory.components.SpeedComponent;

public class CameraControlSystem extends EntitySystem {

    private static final float ACCELERATION_FROM_KEY_PANNING = 2000;

    private final OrthographicCamera camera;
    private final Entity cameraEntity;
    private final ComponentMapper<SpeedComponent> speedMapper = ComponentMapper.getFor(SpeedComponent.class);
    private final ComponentMapper<AccelerationComponent> accelerationMapper = ComponentMapper.getFor(AccelerationComponent.class);
    private final ComponentMapper<PositionComponent> positionComponentMapper = ComponentMapper.getFor(PositionComponent.class);

    private final PositionComponent lastCameraPosition = new PositionComponent(0, 0);

    public CameraControlSystem(OrthographicCamera camera, Entity cameraEntity) {
        this.camera = camera;
        this.cameraEntity = cameraEntity;
    }

    @Override
    public void addedToEngine(Engine engine) {
    }

    @Override
    public void removedFromEngine (Engine engine) {
    }

    @Override
    public void update(float deltaTime) {
        controlMovement();
    }

    private void controlMovement() {
        float deltaX = getDeltaX();
        float deltaY = getDeltaY();

        AccelerationComponent acceleration = accelerationMapper.get(cameraEntity);

        if (isBreak()) {
            SpeedComponent speed = speedMapper.get(cameraEntity);
            acceleration.x = -speed.x*2;
            acceleration.y = -speed.y*2;
        } else {
            acceleration.x = deltaX * camera.zoom;
            acceleration.y = deltaY * camera.zoom;
        }


        PositionComponent cameraPosition = positionComponentMapper.get(cameraEntity);
        float cameraDx = cameraPosition.x - lastCameraPosition.x;
        float cameraDy = cameraPosition.y - lastCameraPosition.y;
        lastCameraPosition.x = cameraPosition.x;
        lastCameraPosition.y = cameraPosition.y;

        camera.translate(cameraDx, cameraDy, 0);
        camera.update();
    }

    private boolean isBreak() {
        return isLeft() == isRight() &&
                isUp() == isDown();
    }

    private float getDeltaX() {
        boolean isLeft = isLeft();
        boolean isRight = isRight();

        if (isLeft == isRight) {
            return 0;
        } else if (isLeft) {
            return -ACCELERATION_FROM_KEY_PANNING;
        } else /*isRight*/ {
            return ACCELERATION_FROM_KEY_PANNING;
        }
    }

    private boolean isRight() {
        return Gdx.input.isKeyPressed(Input.Keys.D);
    }

    private boolean isLeft() {
        return Gdx.input.isKeyPressed(Input.Keys.A);
    }

    private float getDeltaY() {
        boolean isUp = isUp();
        boolean isDown = isDown();

        if (isUp == isDown) {
            return 0;
        } else if (isUp) {
            return -ACCELERATION_FROM_KEY_PANNING;
        } else /*isDown*/ {
            return ACCELERATION_FROM_KEY_PANNING;
        }
    }

    private boolean isDown() {
        return Gdx.input.isKeyPressed(Input.Keys.S);
    }

    private boolean isUp() {
        return Gdx.input.isKeyPressed(Input.Keys.W);
    }
}

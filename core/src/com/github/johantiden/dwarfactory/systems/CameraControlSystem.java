package com.github.johantiden.dwarfactory.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.github.johantiden.dwarfactory.components.AngularSpeedComponent;
import com.github.johantiden.dwarfactory.components.SpeedComponent;

public class CameraControlSystem extends EntitySystem {

    private static final float VELOCITY_FROM_KEY_PANNING = 200;
    private static final int CAMERA_ROTATION_SPEED = 60;

    private final OrthographicCamera camera;
    private final Entity cameraEntity;
    private final ComponentMapper<SpeedComponent> mm = ComponentMapper.getFor(SpeedComponent.class);
    private final ComponentMapper<AngularSpeedComponent> am = ComponentMapper.getFor(AngularSpeedComponent.class);

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
        float deltaX = getDeltaX();
        float deltaY = getDeltaY();

        SpeedComponent speedComponent = mm.get(cameraEntity);
        speedComponent.speedX = deltaX * camera.zoom;
        speedComponent.speedY = deltaY * camera.zoom;


        float deltaAngle = getDeltaAngle();
        AngularSpeedComponent angularSpeedComponent = am.get(cameraEntity);
        angularSpeedComponent.angularSpeed = deltaAngle;
    }

    private float getDeltaAngle() {
        boolean isLeft = Gdx.input.isKeyPressed(Input.Keys.Q);
        boolean isRight = Gdx.input.isKeyPressed(Input.Keys.E);

        if (isLeft == isRight) {
            return 0;
        } else if (isLeft) {
            return CAMERA_ROTATION_SPEED;
        } else /*isRight*/ {
            return -CAMERA_ROTATION_SPEED;
        }
    }

    private float getDeltaX() {
        boolean isLeft = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean isRight = Gdx.input.isKeyPressed(Input.Keys.D);

        if (isLeft == isRight) {
            return 0;
        } else if (isLeft) {
            return -VELOCITY_FROM_KEY_PANNING;
        } else /*isRight*/ {
            return VELOCITY_FROM_KEY_PANNING;
        }
    }

    private float getDeltaY() {
        boolean isUp = Gdx.input.isKeyPressed(Input.Keys.W);
        boolean isDown = Gdx.input.isKeyPressed(Input.Keys.S);

        if (isUp == isDown) {
            return 0;
        } else if (isUp) {
            return VELOCITY_FROM_KEY_PANNING;
        } else /*isDown*/ {
            return -VELOCITY_FROM_KEY_PANNING;
        }
    }
}

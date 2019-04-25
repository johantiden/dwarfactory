package com.github.johantiden.dwarfactory.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.github.johantiden.dwarfactory.components.PositionComponent;

public class CameraUpdateSystem extends EntitySystem {

    private final Entity cameraEntity;
    private final PositionComponent lastCameraPosition = new PositionComponent(0, 0);
    private final Camera camera;

    private final ComponentMapper<PositionComponent> positionComponentMapper = ComponentMapper.getFor(PositionComponent.class);

    public CameraUpdateSystem(Camera camera, Entity cameraEntity) {
        this.cameraEntity = cameraEntity;
        this.camera = camera;
    }

    @Override
    public void addedToEngine(Engine engine) {
    }

    @Override
    public void removedFromEngine (Engine engine) {
    }

    @Override
    public void update(float deltaTime) {

        PositionComponent cameraPosition = positionComponentMapper.get(cameraEntity);
        float cameraDx = cameraPosition.x - lastCameraPosition.x;
        float cameraDy = cameraPosition.y - lastCameraPosition.y;
        lastCameraPosition.x = cameraPosition.x;
        lastCameraPosition.y = cameraPosition.y;

        camera.translate(cameraDx, cameraDy, 0);
        camera.update();
    }

}

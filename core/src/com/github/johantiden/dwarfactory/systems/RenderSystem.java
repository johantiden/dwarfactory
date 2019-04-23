package com.github.johantiden.dwarfactory.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.github.johantiden.dwarfactory.components.AngleComponent;
import com.github.johantiden.dwarfactory.components.PositionComponent;
import com.github.johantiden.dwarfactory.components.VisualComponent;

public class RenderSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;

    private final SpriteBatch batch;

    private final Entity cameraEntity;
    private final PositionComponent lastCameraPosition = new PositionComponent(0, 0);
    private final AngleComponent lastCameraAngle = new AngleComponent(0);
    private final Camera camera;


    private final ComponentMapper<AngleComponent> am = ComponentMapper.getFor(AngleComponent.class);
    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private final ComponentMapper<VisualComponent> vm = ComponentMapper.getFor(VisualComponent.class);

    public RenderSystem(Camera camera, Entity cameraEntity) {


        this.cameraEntity = cameraEntity;
        batch = new SpriteBatch();
        this.camera = camera;
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PositionComponent.class, VisualComponent.class).get());
    }

    @Override
    public void removedFromEngine (Engine engine) {
    }

    @Override
    public void update(float deltaTime) {

        PositionComponent cameraPosition = pm.get(cameraEntity);
        float cameraDx = cameraPosition.x - lastCameraPosition.x;
        float cameraDy = cameraPosition.y - lastCameraPosition.y;
        lastCameraPosition.x = cameraPosition.x;
        lastCameraPosition.y = cameraPosition.y;

        AngleComponent cameraAngle = am.get(cameraEntity);
        float cameraDr = cameraAngle.angle - lastCameraAngle.angle;
        lastCameraAngle.angle = cameraAngle.angle;

        camera.translate(cameraDx, cameraDy, 0);
        camera.rotate(cameraDr, 0, 0, 1);
        camera.update();

        batch.begin();
        batch.setProjectionMatrix(camera.combined);

        for (int i = 0; i < entities.size(); ++i) {
            Entity e = entities.get(i);

            PositionComponent position = pm.get(e);
            VisualComponent visual = vm.get(e);

            batch.draw(visual.region, position.x, position.y, 32, 32);
        }

        batch.end();
    }
}

package com.github.johantiden.dwarfactory.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.github.johantiden.dwarfactory.components.AngleComponent;
import com.github.johantiden.dwarfactory.components.PositionComponent;
import com.github.johantiden.dwarfactory.components.SpeedComponent;
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
    private final ComponentMapper<SpeedComponent> sm = ComponentMapper.getFor(SpeedComponent.class);
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
        float cameraDx = cameraPosition.position.x - lastCameraPosition.position.x;
        float cameraDy = cameraPosition.position.y - lastCameraPosition.position.y;
        lastCameraPosition.position.x = cameraPosition.position.x;
        lastCameraPosition.position.y = cameraPosition.position.y;

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

            if (sm.has(e)) {
                SpeedComponent speedComponent = sm.get(e);
                batch.draw(getTextureFromState(visual.region, speedComponent), position.position.x, position.position.y, 32, 32);
            } else {
                batch.draw(visual.region, position.position.x, position.position.y, 32, 32);

            }
        }

        batch.end();
    }

    private TextureRegion getTextureFromState(TextureRegion region, SpeedComponent speedComponent) {

        double angle = (double) new Vector2(speedComponent.speed.x, speedComponent.speed.y).angle();

        if (angle >= 225 && angle <= 315) {
            // down
            return new TextureRegion(region, 10, 20, 10, 10);
        }

        if (angle >= 45 && angle <= 135) {
            // up
            return new TextureRegion(region, 10, 0, 10, 10);
        }

        if (angle >= 135 && angle <= 225) {
            // left, flip the image
            return new TextureRegion(region, 20, 10, -10, 10);
        }

        return new TextureRegion(region, 10, 10, 10, 10);
    }

}

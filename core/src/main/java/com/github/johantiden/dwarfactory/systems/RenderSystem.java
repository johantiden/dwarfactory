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
import com.github.johantiden.dwarfactory.components.AngleComponent;
import com.github.johantiden.dwarfactory.components.PositionComponent;
import com.github.johantiden.dwarfactory.components.SpeedComponent;
import com.github.johantiden.dwarfactory.components.VisualComponent;
import com.github.johantiden.dwarfactory.components.VisualComponentFromSpeed;

public class RenderSystem extends EntitySystem {
    private ImmutableArray<Entity> staticTextureEntities;
    private ImmutableArray<Entity> dynamicTextureEntities;

    private final SpriteBatch batch;

    private final Entity cameraEntity;
    private final PositionComponent lastCameraPosition = new PositionComponent(0, 0);
    private final AngleComponent lastCameraAngle = new AngleComponent(0);
    private final Camera camera;


    private final ComponentMapper<AngleComponent> am = ComponentMapper.getFor(AngleComponent.class);
    private final ComponentMapper<PositionComponent> positionComponentMapper = ComponentMapper.getFor(PositionComponent.class);
    private final ComponentMapper<SpeedComponent> sm = ComponentMapper.getFor(SpeedComponent.class);
    private final ComponentMapper<VisualComponent> vm = ComponentMapper.getFor(VisualComponent.class);
    private final ComponentMapper<VisualComponentFromSpeed> visualComponentFromSpeedComponentMapper = ComponentMapper.getFor(VisualComponentFromSpeed.class);

    public RenderSystem(Camera camera, Entity cameraEntity) {


        this.cameraEntity = cameraEntity;
        batch = new SpriteBatch();
        this.camera = camera;
    }

    @Override
    public void addedToEngine(Engine engine) {
        staticTextureEntities = engine.getEntitiesFor(Family.all(PositionComponent.class, VisualComponent.class).get());
        dynamicTextureEntities = engine.getEntitiesFor(Family.all(PositionComponent.class, SpeedComponent.class, VisualComponentFromSpeed.class).get());
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

        AngleComponent cameraAngle = am.get(cameraEntity);
        float cameraDr = cameraAngle.angle - lastCameraAngle.angle;
        lastCameraAngle.angle = cameraAngle.angle;

        camera.translate(cameraDx, cameraDy, 0);
        camera.rotate(cameraDr, 0, 0, 1);
        camera.update();

        batch.begin();
        batch.setProjectionMatrix(camera.combined);

        for (Entity entity : staticTextureEntities) {
            PositionComponent position = positionComponentMapper.get(entity);
            VisualComponent visual = vm.get(entity);

            batch.draw(visual.getTexture(), position.x, position.y, 32, 32);
        }

        for (Entity entity : dynamicTextureEntities) {
            PositionComponent position = positionComponentMapper.get(entity);
            VisualComponentFromSpeed visual = visualComponentFromSpeedComponentMapper.get(entity);
            SpeedComponent speedComponent = sm.get(entity);

            TextureRegion texture = visual.getSpriteSheet(speedComponent);
            batch.draw(texture, position.x-texture.getRegionWidth()/2, position.y-texture.getRegionHeight()/2, 32, 32);
        }

        batch.end();
    }

}

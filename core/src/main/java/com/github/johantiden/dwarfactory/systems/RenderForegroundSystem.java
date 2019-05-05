package com.github.johantiden.dwarfactory.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.github.johantiden.dwarfactory.components.AccelerationComponent;
import com.github.johantiden.dwarfactory.components.ControlComponent;
import com.github.johantiden.dwarfactory.components.ForceContext;
import com.github.johantiden.dwarfactory.components.ForcesComponent;
import com.github.johantiden.dwarfactory.components.ItemConsumerComponent;
import com.github.johantiden.dwarfactory.components.ItemProducerComponent;
import com.github.johantiden.dwarfactory.components.Job;
import com.github.johantiden.dwarfactory.components.PositionComponent;
import com.github.johantiden.dwarfactory.components.SizeComponent;
import com.github.johantiden.dwarfactory.components.SpeedComponent;
import com.github.johantiden.dwarfactory.components.TaskComponent;
import com.github.johantiden.dwarfactory.components.VisualComponent;
import com.github.johantiden.dwarfactory.game.entities.RenderContext;
import com.github.johantiden.dwarfactory.game.entities.factory.Factory;
import com.github.johantiden.dwarfactory.struct.ImmutableVector2Int;

import java.util.List;
import java.util.Optional;

import static com.github.johantiden.dwarfactory.util.FontUtil.font;

public class RenderForegroundSystem extends EntitySystem {
    private static final boolean DRAW_DEBUG = true;
    public static final int FONT_SIZE = 32;
    private ImmutableVector2Int mouseScreenCoordinates;
    private final Texture mouseTileTexture;

    private ImmutableArray<Entity> entitites;

    private final SpriteBatch spriteBatch;
    private final SpriteBatch unprojectedSpriteBatch;
    private final ShapeRenderer shapeRenderer;
    private final ShapeRenderer debugShapeRenderer;
    private final SpriteBatch debugSpriteBatch;

    private final Camera camera;

    private final ComponentMapper<PositionComponent> positionManager = ComponentMapper.getFor(PositionComponent.class);
    private final ComponentMapper<SpeedComponent> speedManager = ComponentMapper.getFor(SpeedComponent.class);
    private final ComponentMapper<ControlComponent> controlManager = ComponentMapper.getFor(ControlComponent.class);
    private final ComponentMapper<TaskComponent> taskManager = ComponentMapper.getFor(TaskComponent.class);
    private final ComponentMapper<VisualComponent> visualManager = ComponentMapper.getFor(VisualComponent.class);
    private final ComponentMapper<SizeComponent> sizeManager = ComponentMapper.getFor(SizeComponent.class);
    private final ComponentMapper<AccelerationComponent> accelerationManager = ComponentMapper.getFor(AccelerationComponent.class);
    private final ComponentMapper<ForcesComponent> forcesManager = ComponentMapper.getFor(ForcesComponent.class);
    private final ComponentMapper<ItemProducerComponent> itemProducerManager = ComponentMapper.getFor(ItemProducerComponent.class);
    private final ComponentMapper<ItemConsumerComponent> itemConsumerManger = ComponentMapper.getFor(ItemConsumerComponent.class);

    public RenderForegroundSystem(Camera camera) {
        this.spriteBatch = new SpriteBatch();
        this.debugSpriteBatch = new SpriteBatch();
        this.unprojectedSpriteBatch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer(100);
        this.debugShapeRenderer = new ShapeRenderer(100);
        this.camera = camera;
        this.mouseTileTexture = new Texture(Gdx.files.internal("selection_overlay.png"), true);
    }

    public void onMouseMoved(ImmutableVector2Int screenCoordinates) {
        this.mouseScreenCoordinates = screenCoordinates;
    }

    @Override
    public void addedToEngine(Engine engine) {
        entitites = engine.getEntitiesFor(Family.all(PositionComponent.class, VisualComponent.class, SizeComponent.class).get());
    }

    @Override
    public void removedFromEngine (Engine engine) {
        entitites = null;
    }

    @Override
    public void update(float deltaTime) {
        drawForeground();

    }

    private void drawForeground() {


        renderSprites();
        renderUnprojected();
        renderShapes();

//        if (mouseScreenCoordinates != null) {
//            TileCoordinate mouseTilePosition = CoordinateUtil.screenToTile(mouseScreenCoordinates, camera);
//            spriteBatch.draw(mouseTileTexture,
//                    TILE_SIZE * mouseTilePosition.x,
//                    TILE_SIZE * mouseTilePosition.y,
//                    TILE_SIZE,
//                    TILE_SIZE);
//        }

        if (DRAW_DEBUG) {
            debugRender();
        }
    }

    private void renderSprites() {
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        for (Entity entity : entitites) {
            PositionComponent position = positionManager.get(entity);
            SizeComponent size = sizeManager.get(entity);
            VisualComponent visual = visualManager.get(entity);
            SpeedComponent speed = speedManager.has(entity) ? speedManager.get(entity) : null;
            TaskComponent task = taskManager.has(entity) ? taskManager.get(entity) : null;

            visual.renderSprites(spriteBatch, new RenderContext(speed, position, size, task));
        }

        spriteBatch.end();
    }

    private void renderUnprojected() {
        unprojectedSpriteBatch.begin();

        for (Entity entity : entitites) {
            PositionComponent position = positionManager.get(entity);
            SizeComponent size = sizeManager.get(entity);
            VisualComponent visual = visualManager.get(entity);
            SpeedComponent speed = speedManager.has(entity) ? speedManager.get(entity) : null;
            TaskComponent task = taskManager.has(entity) ? taskManager.get(entity) : null;

            visual.renderSpritesUnprojected(unprojectedSpriteBatch, new RenderContext(speed, position, size, task));
        }

        unprojectedSpriteBatch.end();
    }

    private void renderShapes() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();

        for (Entity entity : entitites) {
            PositionComponent position = positionManager.get(entity);
            SizeComponent size = sizeManager.get(entity);
            VisualComponent visual = visualManager.get(entity);
            SpeedComponent speed = speedManager.has(entity) ? speedManager.get(entity) : null;
            TaskComponent task = taskManager.has(entity) ? taskManager.get(entity) : null;

            visual.renderShapes(shapeRenderer, new RenderContext(speed, position, size, task));
        }

        shapeRenderer.end();
    }

    private void debugRenderEntities() {
        for (Entity entity : entitites) {
            PositionComponent position = positionManager.get(entity);
            SizeComponent size = sizeManager.get(entity);
            VisualComponent visual = visualManager.get(entity);
            SpeedComponent speed = speedManager.has(entity) ? speedManager.get(entity) : null;
            TaskComponent task = taskManager.has(entity) ? taskManager.get(entity) : null;

            visual.debugRenderShapes(debugShapeRenderer, new RenderContext(speed, position, size, task));
        }
    }


    private void debugRender() {

        debugShapeRenderer.setProjectionMatrix(camera.combined);
        debugShapeRenderer.setAutoShapeType(true);
        debugShapeRenderer.begin();

        debugRenderEntities();
        debugRenderItemInput();
        debugRenderItemOutput();
        debugRenderJobs();

//        debugDrawCoordinateLines();
//        debugDrawBoundingBoxes();
//        debugDrawSpeed();
//        debugDrawForces();
//        debugDrawAcceleration();

        debugShapeRenderer.end();
    }

    private void debugRenderJobs() {
        debugShapeRenderer.setColor(0, 0, 1, 1);
        debugShapeRenderer.set(ShapeRenderer.ShapeType.Line);

        for (Entity entity : entitites) {
            if (controlManager.has(entity) && positionManager.has(entity)) {
                ControlComponent controlComponent = controlManager.get(entity);
                PositionComponent position = positionManager.get(entity);

                Vector2 sourcePosition = position;
                for (Job job : controlComponent.getJobQueue()) {
                    Optional<Vector2> targetPosition = job.getTargetPosition();
                    if (targetPosition.isPresent()) {
                        debugShapeRenderer.line(
                                sourcePosition.x, sourcePosition.y,
                                targetPosition.get().x, targetPosition.get().y
                        );
                        sourcePosition = targetPosition.get();
                    }
                }
            }
        }
    }

    private void debugRenderItemInput() {
        BitmapFont font = font(FONT_SIZE);
        debugSpriteBatch.setProjectionMatrix(camera.combined);
        debugSpriteBatch.begin();
        font.setColor(1, 0, 0, 1);

        for (Entity entity : entitites) {
            if (positionManager.has(entity) &&
                    sizeManager.has(entity) &&
                    itemConsumerManger.has(entity)) {
                PositionComponent position = positionManager.get(entity);
                SizeComponent size = sizeManager.get(entity);
                ItemConsumerComponent itemConsumer = itemConsumerManger.get(entity);

                String s = itemConsumer.getBag().toString();
                font.draw(debugSpriteBatch, s, position.x - size.x/2, position.y - size.y/2);
            }
        }

        debugSpriteBatch.end();
    }

    private void debugRenderItemOutput() {
        BitmapFont font = font(FONT_SIZE);
        debugSpriteBatch.setProjectionMatrix(camera.combined);
        debugSpriteBatch.begin();
        font.setColor(0, 1, 0, 1);

        for (Entity entity : entitites) {
            if (positionManager.has(entity) &&
                    sizeManager.has(entity) &&
                    itemProducerManager.has(entity)) {
                PositionComponent position = positionManager.get(entity);
                SizeComponent size = sizeManager.get(entity);
                ItemProducerComponent itemProducerComponent = itemProducerManager.get(entity);

                String s = itemProducerComponent.getBag().toString();
                font.draw(debugSpriteBatch, s, position.x, position.y - size.y/2);
            }
        }

        debugSpriteBatch.end();
    }

    private void debugDrawSpeed() {
        debugShapeRenderer.setColor(1, 1, 0, 1);
        debugShapeRenderer.set(ShapeRenderer.ShapeType.Line);

        for (Entity entity : entitites) {
            if (speedManager.has(entity)) {
                PositionComponent position = positionManager.get(entity);
                SpeedComponent speed = speedManager.get(entity);

                Vector2 point = position.cpy().add(speed.cpy());
                debugShapeRenderer.line(
                        position.x, position.y,
                        point.x, point.y
                );
            }
        }
    }

    private void debugDrawAcceleration() {
        debugShapeRenderer.setColor(0, 1, 0, 1);
        debugShapeRenderer.set(ShapeRenderer.ShapeType.Line);

        for (Entity entity : entitites) {
            if (accelerationManager.has(entity)) {
                PositionComponent position = positionManager.get(entity);
                AccelerationComponent acceleration = accelerationManager.get(entity);

                Vector2 point = position.cpy().add(acceleration.cpy());
                debugShapeRenderer.line(
                        position.x, position.y,
                        point.x, point.y
                );
            }
        }
    }

    private void debugDrawForces() {
        debugShapeRenderer.setColor(0, 0, 1, 1);
        debugShapeRenderer.set(ShapeRenderer.ShapeType.Line);

        for (Entity entity : entitites) {
            if (forcesManager.has(entity)) {
                PositionComponent position = positionManager.get(entity);
                ForcesComponent forces = forcesManager.get(entity);
                SpeedComponent speed = speedManager.get(entity);

                List<Vector2> forcesList = forces.getForces(new ForceContext(speed));
                for (Vector2 force : forcesList) {
                    Vector2 point = position.cpy().add(force.cpy());
                    debugShapeRenderer.line(
                            position.x, position.y,
                            point.x, point.y
                    );
                }

            }
        }
    }

    private void debugDrawBoundingBoxes() {
        debugShapeRenderer.setColor(0, 1, 0, 1);
        debugShapeRenderer.set(ShapeRenderer.ShapeType.Line);

        for (Entity entity : entitites) {
            PositionComponent position = positionManager.get(entity);
            SizeComponent size = sizeManager.get(entity);

            debugShapeRenderer.rect(
                    position.x-size.x /2,
                    position.y-size.y/2,
                    size.x,
                    size.y);
        }
    }

    private void debugDrawCoordinateLines() {
        int strokeWidth = 10;
        debugShapeRenderer.setColor(1, 0, 0, 1);
        drawThickLine(0, 0, 1000, 0, 1, strokeWidth);
        debugShapeRenderer.setColor(0, 1, 0, 1);
        drawThickLine(0, 0, 0, 1000, strokeWidth, 1);
    }

    private void drawThickLine(float x, float y, float x2, float y2, float strokeWidthX, float strokeWidthY) {
        debugShapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        debugShapeRenderer.rect(x-strokeWidthX/2f, y-strokeWidthY/2f, x2-x+strokeWidthX, y2-y+strokeWidthY);
    }

}

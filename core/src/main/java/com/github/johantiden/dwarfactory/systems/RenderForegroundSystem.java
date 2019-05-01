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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.github.johantiden.dwarfactory.components.AccelerationComponent;
import com.github.johantiden.dwarfactory.components.PositionComponent;
import com.github.johantiden.dwarfactory.components.SizeComponent;
import com.github.johantiden.dwarfactory.components.SpeedComponent;
import com.github.johantiden.dwarfactory.components.TaskComponent;
import com.github.johantiden.dwarfactory.components.VisualComponent;
import com.github.johantiden.dwarfactory.game.TileCoordinate;
import com.github.johantiden.dwarfactory.game.entities.RenderContext;
import com.github.johantiden.dwarfactory.math.ImmutableVector2Int;
import com.github.johantiden.dwarfactory.util.CoordinateUtil;

import static com.github.johantiden.dwarfactory.game.BackgroundTile.TILE_SIZE;

public class RenderForegroundSystem extends EntitySystem {
    private ImmutableVector2Int mouseScreenCoordinates;
    private final Texture mouseTileTexture;

    private ImmutableArray<Entity> entitites;

    private final SpriteBatch spriteBatch;
    private final ShapeRenderer shapeRenderer;
    private final ShapeRenderer debugShapeRenderer;

    private final Camera camera;

    private final ComponentMapper<PositionComponent> positionManager = ComponentMapper.getFor(PositionComponent.class);
    private final ComponentMapper<SpeedComponent> speedManager = ComponentMapper.getFor(SpeedComponent.class);
    private final ComponentMapper<TaskComponent> taskManager = ComponentMapper.getFor(TaskComponent.class);
    private final ComponentMapper<VisualComponent> visualManager = ComponentMapper.getFor(VisualComponent.class);
    private final ComponentMapper<SizeComponent> sizeManager = ComponentMapper.getFor(SizeComponent.class);
    private final ComponentMapper<AccelerationComponent> accelerationManager = ComponentMapper.getFor(AccelerationComponent.class);

    public RenderForegroundSystem(Camera camera) {
        this.spriteBatch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer(100);
        this.debugShapeRenderer = new ShapeRenderer(100);
        this.camera = camera;
        mouseTileTexture = new Texture(Gdx.files.internal("selection_overlay.png"), true);
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
    }

    @Override
    public void update(float deltaTime) {
        drawForeground();
        drawDebug();
    }

    private void drawForeground() {
        spriteBatch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();

        for (Entity entity : entitites) {
            PositionComponent position = positionManager.get(entity);
            SizeComponent size = sizeManager.get(entity);
            VisualComponent visual = visualManager.get(entity);
            SpeedComponent speed = speedManager.has(entity) ? speedManager.get(entity) : null;
            TaskComponent task = taskManager.has(entity) ? taskManager.get(entity) : null;

            visual.draw(new RenderContext(spriteBatch, shapeRenderer, debugShapeRenderer, speed, position, size, task));
        }

        if (mouseScreenCoordinates != null) {
            TileCoordinate mouseTilePosition = CoordinateUtil.screenToTile(mouseScreenCoordinates, camera);
            spriteBatch.draw(mouseTileTexture,
                    TILE_SIZE * mouseTilePosition.x,
                    TILE_SIZE * mouseTilePosition.y,
                    TILE_SIZE,
                    TILE_SIZE);
        }

        shapeRenderer.end();
        spriteBatch.end();
    }

    private void drawDebug() {

        debugShapeRenderer.setProjectionMatrix(camera.combined);
        debugShapeRenderer.setAutoShapeType(true);
        debugShapeRenderer.begin();

        debugDrawCoordinateLines();
//        debugDrawBoundingBoxes();
        debugDrawSpeed();
        debugDrawAcceleration();

        debugShapeRenderer.end();
    }

    private void debugDrawSpeed() {
        debugShapeRenderer.setColor(1, 1, 0, 1);
        debugShapeRenderer.set(ShapeRenderer.ShapeType.Line);

        for (Entity entity : entitites) {
            if (speedManager.has(entity)) {
                PositionComponent position = positionManager.get(entity);
                SpeedComponent speed = speedManager.get(entity);

                Vector2 point = position.cpy().add(speed.cpy().scl(1));
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

                Vector2 point = position.cpy().add(acceleration.cpy().scl(1));
                debugShapeRenderer.line(
                        position.x, position.y,
                        point.x, point.y
                );
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

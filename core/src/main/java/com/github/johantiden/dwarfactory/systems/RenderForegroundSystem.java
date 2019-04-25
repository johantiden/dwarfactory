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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.johantiden.dwarfactory.components.PositionComponent;
import com.github.johantiden.dwarfactory.components.SizeComponent;
import com.github.johantiden.dwarfactory.components.SpeedComponent;
import com.github.johantiden.dwarfactory.components.VisualComponent;
import com.github.johantiden.dwarfactory.game.TileCoordinate;
import com.github.johantiden.dwarfactory.math.ImmutableVector2Int;
import com.github.johantiden.dwarfactory.util.CoordinateUtil;

import static com.github.johantiden.dwarfactory.game.BackgroundTile.TILE_SIZE;

public class RenderForegroundSystem extends EntitySystem {
    private ImmutableVector2Int mouseScreenCoordinates;
    private final Texture mouseTileTexture;

    private ImmutableArray<Entity> entitites;

    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;

    private final Camera camera;

    private final ComponentMapper<PositionComponent> positionComponentMapper = ComponentMapper.getFor(PositionComponent.class);
    private final ComponentMapper<SpeedComponent> sm = ComponentMapper.getFor(SpeedComponent.class);
    private final ComponentMapper<VisualComponent> vm = ComponentMapper.getFor(VisualComponent.class);
    private final ComponentMapper<SizeComponent> sizeManager = ComponentMapper.getFor(SizeComponent.class);

    public RenderForegroundSystem(Camera camera) {
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer(100);
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
        batch.begin();
        batch.setProjectionMatrix(camera.combined);

        for (Entity entity : entitites) {
            PositionComponent position = positionComponentMapper.get(entity);
            SizeComponent size = sizeManager.get(entity);
            VisualComponent visual = vm.get(entity);
            TextureRegion texture = getTexture(entity, visual);

            batch.draw(texture, position.x-size.x/2, position.y-size.y/2, size.x, size.y);
        }

        if (mouseScreenCoordinates != null) {
            TileCoordinate mouseTilePosition = CoordinateUtil.screenToTile(mouseScreenCoordinates, camera);
            batch.draw(mouseTileTexture,
                    TILE_SIZE * mouseTilePosition.x,
                    TILE_SIZE * mouseTilePosition.y,
                    TILE_SIZE,
                    TILE_SIZE);
        }


        batch.end();
    }

    private void drawDebug() {

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        int strokeWidth = 10;

        shapeRenderer.setColor(1, 0, 0, 1);
        drawThickLine(0, 0, 1000, 0, 1, strokeWidth);
        shapeRenderer.setColor(0, 1, 0, 1);
        drawThickLine(0, 0, 0, 1000, strokeWidth, 1);

        shapeRenderer.setColor(0, 1, 0, 1);

        for (Entity entity : entitites) {
            PositionComponent position = positionComponentMapper.get(entity);
            SizeComponent size = sizeManager.get(entity);
            shapeRenderer.set(ShapeRenderer.ShapeType.Line);

            shapeRenderer.circle(position.x, position.y, size.x/2);
            shapeRenderer.rect(
                    position.x-size.x /2,
                    position.y-size.y/2,
                    size.x,
                    size.y);
        }

        shapeRenderer.end();
    }

    private void drawThickLine(float x, float y, float x2, float y2, float strokeWidthX, float strokeWidthY) {
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(x-strokeWidthX/2f, y-strokeWidthY/2f, x2-x+strokeWidthX, y2-y+strokeWidthY);
    }

    private TextureRegion getTexture(Entity entity, VisualComponent visual) {
        TextureRegion texture;
        if (sm.has(entity)) {
            SpeedComponent speed = sm.get(entity);
            texture = visual.getTexture(speed);
        } else {
            texture = visual.getTexture(null);
        }
        return texture;
    }

}

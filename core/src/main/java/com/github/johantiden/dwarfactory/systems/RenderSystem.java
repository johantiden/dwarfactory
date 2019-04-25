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
import com.badlogic.gdx.math.Rectangle;
import com.github.johantiden.dwarfactory.Dwarfactory;
import com.github.johantiden.dwarfactory.components.AngleComponent;
import com.github.johantiden.dwarfactory.components.PositionComponent;
import com.github.johantiden.dwarfactory.components.SpeedComponent;
import com.github.johantiden.dwarfactory.components.VisualComponent;
import com.github.johantiden.dwarfactory.game.TileCoordinate;
import com.github.johantiden.dwarfactory.game.World;
import com.github.johantiden.dwarfactory.math.ImmutableRectangleInt;
import com.github.johantiden.dwarfactory.math.ImmutableVector2Int;
import com.github.johantiden.dwarfactory.util.CoordinateUtil;

import java.util.Map;

import static com.github.johantiden.dwarfactory.game.BackgroundTile.TILE_SIZE;

public class RenderSystem extends EntitySystem {
    private static final float TILE_BUILDING_INSET = 5;
    public static final float FACTORY_SIZE = TILE_SIZE * 3 - TILE_BUILDING_INSET * 2;
    private ImmutableVector2Int mouseScreenCoordinates;
    private final Texture mouseTileTexture;
    private Map<Integer, Texture> map;
    private final TextureRegion factoryTexture;

    private ImmutableArray<Entity> entitites;

    private final SpriteBatch batch;
    private final SpriteBatch backgroundBatch;
    private final ShapeRenderer shapeRenderer;

    private final Entity cameraEntity;
    private final PositionComponent lastCameraPosition = new PositionComponent(0, 0);
    private final AngleComponent lastCameraAngle = new AngleComponent(0);
    private final Camera camera;
    private final World world;

    private final ComponentMapper<AngleComponent> am = ComponentMapper.getFor(AngleComponent.class);
    private final ComponentMapper<PositionComponent> positionComponentMapper = ComponentMapper.getFor(PositionComponent.class);
    private final ComponentMapper<SpeedComponent> sm = ComponentMapper.getFor(SpeedComponent.class);
    private final ComponentMapper<VisualComponent> vm = ComponentMapper.getFor(VisualComponent.class);

    public RenderSystem(Camera camera, Entity cameraEntity, World world) {

        this.cameraEntity = cameraEntity;
        this.world = world;
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer(100);
        this.camera = camera;
        mouseTileTexture = new Texture(Gdx.files.internal("selection_overlay.png"), true);

        this.map = map;

        factoryTexture = new TextureRegion(
                new Texture(Gdx.files.internal("buildings.png"), true),
                0, 1304, 128, 128);
        backgroundBatch = new SpriteBatch();


    }

    public void onMouseMoved(ImmutableVector2Int screenCoordinates) {
        this.mouseScreenCoordinates = screenCoordinates;
    }

    @Override
    public void addedToEngine(Engine engine) {
        entitites = engine.getEntitiesFor(Family.all(PositionComponent.class, VisualComponent.class).get());
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

        drawBackground();
        drawForeground();
        drawDebug();
    }




    private void drawForeground() {
        batch.begin();
        batch.setProjectionMatrix(camera.combined);

        int width = 32;
        int height = 32;

        for (Entity entity : entitites) {
            PositionComponent position = positionComponentMapper.get(entity);
            VisualComponent visual = vm.get(entity);
            TextureRegion texture = getTexture(entity, visual);

            batch.draw(texture, position.x-width/2, position.y-height/2, width, height);
        }

        batch.end();
    }


    private void drawBackground() {
        backgroundBatch.begin();
        backgroundBatch.setProjectionMatrix(camera.combined);

        Rectangle clip = getClipInWorld();
        world.ensureWorldIsLargeEnoughToRender(clip);
        world.forEachBackgroundTile(clip, tile -> {
            backgroundBatch.draw(tile.textureRegion,
                    TILE_SIZE * tile.position.x,
                    TILE_SIZE * tile.position.y,
                    TILE_SIZE,
                    TILE_SIZE);
        });

        backgroundBatch.draw(factoryTexture,
                3 * TILE_SIZE +TILE_BUILDING_INSET,
                7 * TILE_SIZE +TILE_BUILDING_INSET,
                FACTORY_SIZE,
                FACTORY_SIZE);

        backgroundBatch.draw(factoryTexture,
                7 * TILE_SIZE +TILE_BUILDING_INSET,
                4 * TILE_SIZE +TILE_BUILDING_INSET,
                FACTORY_SIZE,
                FACTORY_SIZE);


        if (mouseScreenCoordinates != null) {
            TileCoordinate mouseTilePosition = CoordinateUtil.screenToTile(mouseScreenCoordinates, camera);
            backgroundBatch.draw(mouseTileTexture,
                    TILE_SIZE * mouseTilePosition.x,
                    TILE_SIZE * mouseTilePosition.y,
                    TILE_SIZE,
                    TILE_SIZE);
        }

        backgroundBatch.end();

    }

    private Rectangle getClipInWorld() {
        return CoordinateUtil.screenToWorld(new ImmutableRectangleInt(0, 0, Dwarfactory.VIEWPORT_WIDTH, Dwarfactory.VIEWPORT_HEIGHT), camera);


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

        shapeRenderer.circle((3+1.5f)*TILE_SIZE, (7+1.5f)*TILE_SIZE, 20);
        shapeRenderer.circle((7+1.5f)*TILE_SIZE, (4+1.5f)*TILE_SIZE, 20);

//        if (mouseCoordinatesInWorld != null) {
//            shapeRenderer.set(ShapeRenderer.ShapeType.Line);
//            shapeRenderer.circle(mouseCoordinatesInWorld.x, mouseCoordinatesInWorld.y, 20);
//        }

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

package com.github.johantiden.dwarfactory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.github.czyzby.kiwi.util.tuple.immutable.Pair;
import com.github.johantiden.dwarfactory.components.AccelerationComponent;
import com.github.johantiden.dwarfactory.components.AngleComponent;
import com.github.johantiden.dwarfactory.components.AngularSpeedComponent;
import com.github.johantiden.dwarfactory.components.SpeedComponent;
import com.github.johantiden.dwarfactory.components.PositionComponent;
import com.github.johantiden.dwarfactory.components.VisualComponent;
import com.github.johantiden.dwarfactory.systems.AccelerationSystem;
import com.github.johantiden.dwarfactory.systems.AngularMovementSystem;
import com.github.johantiden.dwarfactory.systems.CameraControlSystem;
import com.github.johantiden.dwarfactory.systems.MovementSystem;
import com.github.johantiden.dwarfactory.systems.RenderSystem;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.badlogic.gdx.math.MathUtils.random;

public class Dwarfactory extends ApplicationAdapter {

    public static final int VIEWPORT_WIDTH = 2880;
    public static final int VIEWPORT_HEIGHT = 1620;
    public static final int TILE_SIZE = 100;
    public static final int NUM_BOIS = 10;

    private SpriteBatch debugBatch;
    private SpriteBatch backgroundBatch;

    private BitmapFont font;
    private PooledEngine engine;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private List<Texture> tileTextures = new ArrayList<>();
    private Map<Integer, Texture> map;
    private TextureRegion factoryTexture;
    private static final float TILE_BUILDING_INSET = 5;
    public static final float FACTORY_SIZE = TILE_SIZE * 3 - TILE_BUILDING_INSET * 2;
    private Vector2 mouseCoordinatesInWorld;
    private Texture overlayTexture;

    private static BitmapFont getFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("PlayfairDisplay-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 48;
        BitmapFont font12 = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose(); // don't forget to dispose to avoid memory leaks!
        return font12;
    }

    @Override
	public void create () {
        Random random = new SecureRandom();

        Texture boiTexture = new Texture(Gdx.files.internal("simple_10x10_character.png"), true);
        tileTextures.add(new Texture(Gdx.files.internal("tile_001.jpg"), true));
        tileTextures.add(new Texture(Gdx.files.internal("tile_002.jpg"), true));
        tileTextures.add(new Texture(Gdx.files.internal("tile_003.jpg"), true));
        tileTextures.add(new Texture(Gdx.files.internal("tile_004.jpg"), true));

        overlayTexture = new Texture(Gdx.files.internal("selection_overlay.png"), true);

        map = createMap(tileTextures);

        factoryTexture = new TextureRegion(
                new Texture(Gdx.files.internal("buildings.png"), true),
                0, 1304, 128, 128);


        boiTexture.setFilter(Texture.TextureFilter.MipMapNearestNearest, Texture.TextureFilter.MipMapNearestNearest);

		debugBatch = new SpriteBatch();
		backgroundBatch = new SpriteBatch();
		font = getFont();
        shapeRenderer = new ShapeRenderer(100);

        engine = new PooledEngine();

        camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        camera.position.set(0, 0, 0);
        camera.update();
        Entity cameraEntity = engine.createEntity();
        cameraEntity.add(new PositionComponent(0, 0));
        cameraEntity.add(new SpeedComponent(0, 0, 500));
        cameraEntity.add(new AngleComponent(0));
        cameraEntity.add(new AngularSpeedComponent(0));
        cameraEntity.add(new AccelerationComponent(0, 0));
        engine.addEntity(cameraEntity);
        engine.addSystem(new CameraControlSystem(camera, cameraEntity));
        engine.addSystem(new AccelerationSystem());
        engine.addSystem(new MovementSystem());
        engine.addSystem(new AngularMovementSystem());
        engine.addSystem(new RenderSystem(camera, cameraEntity));

        TextureRegion boiRegion = new TextureRegion(boiTexture);
        for (int i = 0; i < NUM_BOIS; i++) {
            Entity box = engine.createEntity();
            box.add(new PositionComponent(random(VIEWPORT_WIDTH), random(VIEWPORT_HEIGHT)));
            box.add(new SpeedComponent(random.nextFloat()*30-15, random.nextFloat()*30-15));
            box.add(new VisualComponent(boiRegion));
            engine.addEntity(box);
        }

        MyInputProcessor inputProcessor = new MyInputProcessor(camera, this::onMouseMoved);
        Gdx.input.setInputProcessor(inputProcessor);

    }

    private void onMouseMoved(Vector2 worldCoordinates) {
        this.mouseCoordinatesInWorld = worldCoordinates;
    }

    private static final int mapWidth = 100;
    private static final int mapHeight = 100;
    private static Map<Integer, Texture> createMap(List<Texture> tileTextures) {
        Map<Integer, Texture> map = new HashMap<>();
        for (int i = 0; i < mapWidth * mapHeight; i++) {
            Texture texture = tileTextures.get(random.nextInt(tileTextures.size()));
            map.put(i, texture);
        }
        return map;
    }

    public static void log(String string) {
        System.out.println(string);
    }

	@Override
	public void render () {
        int fps = Gdx.graphics.getFramesPerSecond();

		Gdx.gl.glClearColor(0.3f, 0.3f, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawBackground();

        engine.update(Gdx.graphics.getDeltaTime());

        drawDebug(fps);
    }

    private void drawBackground() {
        backgroundBatch.begin();
        backgroundBatch.setProjectionMatrix(camera.combined);


        float xOffset = -mapWidth/2f*TILE_SIZE;
        float yOffset = -mapHeight/2f*TILE_SIZE;
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                Texture texture = map.get(y * mapWidth + x);
                if (texture == null) {
                    throw new RuntimeException();
                }

                backgroundBatch.draw(texture, TILE_SIZE * x + xOffset, TILE_SIZE * y + yOffset, TILE_SIZE, TILE_SIZE);
            }
        }

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


        if (mouseCoordinatesInWorld != null) {
            Pair<Integer, Integer> mouseCoordinatesInTileSpace = findTileForCoordinates(mouseCoordinatesInWorld);
            backgroundBatch.draw(overlayTexture, TILE_SIZE * mouseCoordinatesInTileSpace.getFirst(), TILE_SIZE * mouseCoordinatesInTileSpace.getSecond(), TILE_SIZE, TILE_SIZE);
        }

        backgroundBatch.end();

    }

    private static Pair<Integer, Integer> findTileForCoordinates(Vector2 worldCoordinates) {
        return new Pair<>(
                Math.floorDiv((int) worldCoordinates.x, TILE_SIZE),
                Math.floorDiv((int) worldCoordinates.y, TILE_SIZE));
    }

    private void drawDebug(int fps) {

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.line(0, 0, 1000, 0);
        shapeRenderer.setColor(0, 1, 0, 1);
        shapeRenderer.line(0, 0, 0, 1000);

        shapeRenderer.setColor(0, 1, 0, 1);

        shapeRenderer.circle((3+1.5f)*TILE_SIZE, (7+1.5f)*TILE_SIZE, 20);
        shapeRenderer.circle((7+1.5f)*TILE_SIZE, (4+1.5f)*TILE_SIZE, 20);

//        if (mouseCoordinatesInWorld != null) {
//            shapeRenderer.set(ShapeRenderer.ShapeType.Line);
//            shapeRenderer.circle(mouseCoordinatesInWorld.x, mouseCoordinatesInWorld.y, 20);
//        }

        shapeRenderer.end();

        debugBatch.begin();
        font.draw(debugBatch, "" + fps, 10, 50);
        debugBatch.end();
    }

    @Override
	public void dispose () {
		debugBatch.dispose();
	}
}

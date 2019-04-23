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
import com.github.johantiden.dwarfactory.components.AngleComponent;
import com.github.johantiden.dwarfactory.components.AngularSpeedComponent;
import com.github.johantiden.dwarfactory.components.SpeedComponent;
import com.github.johantiden.dwarfactory.components.PositionComponent;
import com.github.johantiden.dwarfactory.components.VisualComponent;
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

    private SpriteBatch debugBatch;
    private SpriteBatch backgroundBatch;

    private BitmapFont font;
    private PooledEngine engine;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private List<Texture> tileTextures = new ArrayList<>();
    private Map<Integer, Texture> map;
    private TextureRegion factoryTexture;
    private static final float TILE_BUILDING_INSET = 10;

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
        cameraEntity.add(new SpeedComponent(0, 0));
        cameraEntity.add(new AngleComponent(0));
        cameraEntity.add(new AngularSpeedComponent(0));
        engine.addEntity(cameraEntity);
        engine.addSystem(new CameraControlSystem(camera, cameraEntity));
        engine.addSystem(new RenderSystem(camera, cameraEntity));
        engine.addSystem(new MovementSystem());
        engine.addSystem(new AngularMovementSystem());

        TextureRegion boiRegion = new TextureRegion(boiTexture);
        for (int i = 0; i < 100; i++) {
            Entity box = engine.createEntity();
            box.add(new PositionComponent(random(VIEWPORT_WIDTH), random(VIEWPORT_HEIGHT)));
            box.add(new SpeedComponent(random.nextFloat()*30-15, random.nextFloat()*30-15));
            box.add(new VisualComponent(boiRegion));
            engine.addEntity(box);
        }

        MyInputProcessor inputProcessor = new MyInputProcessor(camera);
        Gdx.input.setInputProcessor(inputProcessor);

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

        backgroundBatch.draw(factoryTexture, TILE_SIZE*3+TILE_BUILDING_INSET, TILE_SIZE*7+TILE_BUILDING_INSET, TILE_SIZE-TILE_BUILDING_INSET*2, TILE_SIZE-TILE_BUILDING_INSET*2);
        backgroundBatch.draw(factoryTexture, TILE_SIZE*6+TILE_BUILDING_INSET, TILE_SIZE*4+TILE_BUILDING_INSET, TILE_SIZE-TILE_BUILDING_INSET*2, TILE_SIZE-TILE_BUILDING_INSET*2);

        backgroundBatch.end();
    }

    private void drawDebug(int fps) {

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.line(0, 0, 1000, 0);
        shapeRenderer.setColor(0, 1, 0, 1);
        shapeRenderer.line(0, 0, 0, 1000);

        shapeRenderer.setColor(0, 1, 0, 1);

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

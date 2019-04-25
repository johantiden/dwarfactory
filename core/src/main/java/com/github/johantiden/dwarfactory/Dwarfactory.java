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
import com.github.johantiden.dwarfactory.components.AccelerationComponent;
import com.github.johantiden.dwarfactory.components.AngleComponent;
import com.github.johantiden.dwarfactory.components.AngularSpeedComponent;
import com.github.johantiden.dwarfactory.components.SpeedComponent;
import com.github.johantiden.dwarfactory.components.PositionComponent;
import com.github.johantiden.dwarfactory.components.VisualComponent;
import com.github.johantiden.dwarfactory.game.World;
import com.github.johantiden.dwarfactory.math.ImmutableRectangleInt;
import com.github.johantiden.dwarfactory.systems.AccelerationSystem;
import com.github.johantiden.dwarfactory.systems.AngularMovementSystem;
import com.github.johantiden.dwarfactory.systems.CameraControlSystem;
import com.github.johantiden.dwarfactory.systems.MovementSystem;
import com.github.johantiden.dwarfactory.systems.RenderSystem;
import com.github.johantiden.dwarfactory.util.TextureUtil;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.badlogic.gdx.math.MathUtils.random;

public class Dwarfactory extends ApplicationAdapter {

    public static final int VIEWPORT_WIDTH = 2880;
    public static final int VIEWPORT_HEIGHT = 1620;
    public static final int NUM_BOIS = 10;

    private SpriteBatch debugBatch;

    private BitmapFont font;
    private PooledEngine engine;
    private final List<TextureRegion> tileTextures = new ArrayList<>();

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

        tileTextures.add(new TextureRegion(TextureUtil.loadTextureWithMipMap("tile_001.jpg")));
        tileTextures.add(new TextureRegion(TextureUtil.loadTextureWithMipMap("tile_002.jpg")));
        tileTextures.add(new TextureRegion(TextureUtil.loadTextureWithMipMap("tile_003.jpg")));
        tileTextures.add(new TextureRegion(TextureUtil.loadTextureWithMipMap("tile_004.jpg")));

		debugBatch = new SpriteBatch();
		font = getFont();

        engine = new PooledEngine();

        OrthographicCamera camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        camera.position.set(0, 0, 0);
        camera.setToOrtho(true);
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
        RenderSystem renderSystem = new RenderSystem(camera, cameraEntity, new World(tileTextures));
        engine.addSystem(renderSystem);

        Texture boiTexture = TextureUtil.loadTextureWithMipMap("simple_10x10_character.png");
        for (int i = 0; i < NUM_BOIS; i++) {
            createBoi(random, boiTexture);
        }

        MyInputProcessor inputProcessor = new MyInputProcessor(camera, renderSystem::onMouseMoved);
        Gdx.input.setInputProcessor(inputProcessor);

    }

    private void createBoi(Random random, Texture boiTexture) {
        float x = random((float)VIEWPORT_WIDTH);
        float y = random((float)VIEWPORT_HEIGHT);
        float speedX = random.nextFloat() * 30 - 15;
        float speedY = random.nextFloat() * 30 - 15;

        Entity boi = engine.createEntity();
        boi.add(new PositionComponent(x, y));
        boi.add(new SpeedComponent(speedX, speedY));
        boi.add(VisualComponent.create4Angles(
                boiTexture,
                new ImmutableRectangleInt(10, 20, 10, 10),
                new ImmutableRectangleInt(10, 0, 10, 10),
                new ImmutableRectangleInt(20, 10, -10, 10),
                new ImmutableRectangleInt(10, 10, 10, 10)
        ));
        engine.addEntity(boi);
    }



    public static void log(String string) {
        System.out.println(string);
    }

	@Override
	public void render () {
        int fps = Gdx.graphics.getFramesPerSecond();

		Gdx.gl.glClearColor(0.3f, 0.3f, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        engine.update(Gdx.graphics.getDeltaTime());

        drawDebug(fps);
    }


    private void drawDebug(int fps) {

        debugBatch.begin();
        font.draw(debugBatch, "" + fps, 10, 50);
        debugBatch.end();
    }

    @Override
	public void dispose () {
		debugBatch.dispose();
	}
}

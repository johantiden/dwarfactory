package com.github.johantiden.dwarfactory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.github.johantiden.dwarfactory.components.AccelerationComponent;
import com.github.johantiden.dwarfactory.components.AngleComponent;
import com.github.johantiden.dwarfactory.components.AngularSpeedComponent;
import com.github.johantiden.dwarfactory.components.PositionComponent;
import com.github.johantiden.dwarfactory.components.SizeComponent;
import com.github.johantiden.dwarfactory.components.SpeedComponent;
import com.github.johantiden.dwarfactory.components.VisualComponent;
import com.github.johantiden.dwarfactory.game.TileCoordinate;
import com.github.johantiden.dwarfactory.game.World;
import com.github.johantiden.dwarfactory.game.assets.Assets;
import com.github.johantiden.dwarfactory.systems.AccelerationSystem;
import com.github.johantiden.dwarfactory.systems.AngularMovementSystem;
import com.github.johantiden.dwarfactory.systems.CameraControlSystem;
import com.github.johantiden.dwarfactory.systems.CameraUpdateSystem;
import com.github.johantiden.dwarfactory.systems.MovementSystem;
import com.github.johantiden.dwarfactory.systems.RenderBackgroundSystem;
import com.github.johantiden.dwarfactory.systems.RenderForegroundSystem;
import com.github.johantiden.dwarfactory.systems.RenderHudSystem;
import com.github.johantiden.dwarfactory.util.CoordinateUtil;

import java.security.SecureRandom;
import java.util.Random;

import static com.badlogic.gdx.math.MathUtils.random;
import static com.github.johantiden.dwarfactory.game.BackgroundTile.TILE_SIZE;

public class Dwarfactory extends ApplicationAdapter {

    public static final int VIEWPORT_WIDTH = 2880;
    public static final int VIEWPORT_HEIGHT = 1620;

    private SpriteBatch debugBatch;

    private PooledEngine engine;

    @Override
	public void create () {
        Random random = new SecureRandom();

		debugBatch = new SpriteBatch();

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
        engine.addSystem(new CameraUpdateSystem(camera, cameraEntity));
        World world = new World(Assets.Tiles.TILES);
        engine.addSystem(new RenderBackgroundSystem(camera, world));
        RenderForegroundSystem renderForegroundSystem = new RenderForegroundSystem(camera);
        engine.addSystem(renderForegroundSystem);
        RenderHudSystem renderHudSystem = new RenderHudSystem(camera);
        engine.addSystem(renderHudSystem);

        MyInputProcessor inputProcessor = new MyInputProcessor(camera, screenCoordinates -> {
            renderForegroundSystem.onMouseMoved(screenCoordinates);
            renderHudSystem.onMouseMoved(screenCoordinates);
        });
        Gdx.input.setInputProcessor(inputProcessor);

        createFactories();
        createBois(random);
    }

    private void createFactories() {
        createFactory(new TileCoordinate(3,7));
        createFactory(new TileCoordinate(7,4));
    }

    private void createBois(Random random) {
        for (int i = 0; i < 10; i++) {
            createBoi(random);
        }
    }


    private void createBoi(Random random) {
        float x = random((float)VIEWPORT_WIDTH);
        float y = random((float)VIEWPORT_HEIGHT);
        float speedX = random.nextFloat() * 30 - 15;
        float speedY = random.nextFloat() * 30 - 15;

        Entity boi = engine.createEntity();
        boi.add(new PositionComponent(x, y));
        boi.add(new SpeedComponent(speedX, speedY));
        boi.add(VisualComponent.create4Angles(
                Assets.Boi.DOWN,
                Assets.Boi.UP,
                Assets.Boi.LEFT,
                Assets.Boi.RIGHT));
        boi.add(new SizeComponent(32, 32));
        engine.addEntity(boi);
    }

    private void createFactory(TileCoordinate position) {
        float tileBuildingInset = 5f;
        float factorySize = TILE_SIZE * 3 - tileBuildingInset * 2;

        Vector2 centerInWorld = CoordinateUtil.tileCenterToWorld(position);

        Entity boi = engine.createEntity();
        boi.add(new PositionComponent(centerInWorld.x, centerInWorld.y));
        boi.add(new SizeComponent(factorySize, factorySize));
        boi.add(VisualComponent.createStatic(Assets.FACTORY));
        engine.addEntity(boi);
    }

    public static void log(String string) {
        System.out.println(string);
    }

	@Override
	public void render () {

        Gdx.gl.glClearColor(0.3f, 0.3f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        engine.update(Gdx.graphics.getDeltaTime());
    }

    @Override
	public void dispose () {
		debugBatch.dispose();
	}
}

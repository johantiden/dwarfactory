package com.github.johantiden.dwarfactory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.github.johantiden.dwarfactory.components.AccelerationComponent;
import com.github.johantiden.dwarfactory.components.PositionComponent;
import com.github.johantiden.dwarfactory.components.SpeedComponent;
import com.github.johantiden.dwarfactory.game.TileCoordinate;
import com.github.johantiden.dwarfactory.game.entities.factory.Factory;
import com.github.johantiden.dwarfactory.game.entities.factory.House;
import com.github.johantiden.dwarfactory.game.entities.factory.Recipes;
import com.github.johantiden.dwarfactory.game.input.MouseInputController;
import com.github.johantiden.dwarfactory.game.world.World;
import com.github.johantiden.dwarfactory.systems.CameraControlSystem;
import com.github.johantiden.dwarfactory.systems.ControlSystem;
import com.github.johantiden.dwarfactory.systems.RenderBackgroundSystem;
import com.github.johantiden.dwarfactory.systems.RenderForegroundSystem;
import com.github.johantiden.dwarfactory.systems.RenderHudSystem;
import com.github.johantiden.dwarfactory.systems.SpeedFromControlSystem;
import com.github.johantiden.dwarfactory.systems.TaskSystem;
import com.github.johantiden.dwarfactory.systems.physics.AccelerationSystem;
import com.github.johantiden.dwarfactory.systems.physics.ForceSystem;
import com.github.johantiden.dwarfactory.systems.physics.MovementSystem;

public class Dwarfactory extends ApplicationAdapter {

    public static final int VIEWPORT_WIDTH = 2880;
    public static final int VIEWPORT_HEIGHT = 1620;

    private PooledEngine engine;
    private World world;

    @Override
	public void create () {
        world = new World();

        engine = new PooledEngine();

        OrthographicCamera camera = createCamera();

        engine.addSystem(new ForceSystem());
        engine.addSystem(new AccelerationSystem());
        engine.addSystem(new MovementSystem());

        engine.addSystem(new RenderBackgroundSystem(camera, world));
        RenderHudSystem renderHudSystem = new RenderHudSystem(camera);
        engine.addSystem(renderHudSystem);

        MouseInputController mouseInputController = new MouseInputController(engine, camera, renderHudSystem);
        RenderForegroundSystem renderForegroundSystem = new RenderForegroundSystem(camera, mouseInputController);
        engine.addSystem(renderForegroundSystem);
        engine.addSystem(new ControlSystem());
        engine.addSystem(new SpeedFromControlSystem());
        engine.addSystem(new TaskSystem());

        MyInputProcessor inputProcessor = new MyInputProcessor(camera, mouseInputController);
        Gdx.input.setInputProcessor(inputProcessor);

        createGameEntities();
    }

    private OrthographicCamera createCamera() {
        OrthographicCamera camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        camera.position.set(0, 0, 0);
        camera.update();
        Entity cameraEntity = engine.createEntity();
        cameraEntity.add(new PositionComponent(0, 0));
        cameraEntity.add(new SpeedComponent(0, 0, 500));
        cameraEntity.add(new AccelerationComponent(0, 0));
        engine.addEntity(cameraEntity);
        engine.addSystem(new CameraControlSystem(camera, cameraEntity));
        return camera;
    }

    private void createGameEntities() {
        Factory.createFactory(new TileCoordinate(3,7), engine, 0.1f, Recipes.APPLE_GARDEN);
        Factory.createFactory(new TileCoordinate(3,10), engine, 0.5f, Recipes.APPLE_GARDEN);
        Factory.createFactory(new TileCoordinate(3,13), engine, 0.9f, Recipes.APPLE_GARDEN);


        Factory.createFactory(new TileCoordinate(7,4), engine, 0, Recipes.APPLE_JUICER);
        Factory.createFactory(new TileCoordinate(7,1), engine, 0, Recipes.APPLE_JUICER);

        Factory.createFactory(new TileCoordinate(17, 1), engine, 0, Recipes.SELL_APPLE_JUICE);

        House.createHouse(new TileCoordinate(6,7), engine, 1);
        House.createHouse(new TileCoordinate(6,8), engine, 1);
        House.createHouse(new TileCoordinate(6,9), engine, 1);
        House.createHouse(new TileCoordinate(15,2), engine, 1);
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
    public void dispose() {
    }
}

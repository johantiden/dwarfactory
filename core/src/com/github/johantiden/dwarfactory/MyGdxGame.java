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
import com.badlogic.gdx.math.MathUtils;
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
import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {

    public static final int VIEWPORT_WIDTH = 2880;
    public static final int VIEWPORT_HEIGHT = 1620;
    public static final int TILE_SIZE = 100;

    private SpriteBatch debugBatch;

    private BitmapFont font;
    private PooledEngine engine;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

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

        Texture crateTexture = new Texture(Gdx.files.internal("crate.jpg"), true);
        crateTexture.setFilter(Texture.TextureFilter.MipMapNearestNearest, Texture.TextureFilter.MipMapNearestNearest);

		debugBatch = new SpriteBatch();
		font = getFont();
        shapeRenderer = new ShapeRenderer(100);

        engine = new PooledEngine();

        camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        camera.position.set(VIEWPORT_WIDTH/2,VIEWPORT_HEIGHT/2, 0);
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

        TextureRegion coinRegion = new TextureRegion(crateTexture);

        for (int i = 0; i < 100; i++) {
            Entity box = engine.createEntity();
            box.add(new PositionComponent(MathUtils.random(VIEWPORT_WIDTH), MathUtils.random(VIEWPORT_HEIGHT)));
            box.add(new SpeedComponent(random.nextFloat()*30-15, random.nextFloat()*30-15));
            box.add(new VisualComponent(coinRegion));
            engine.addEntity(box);
        }

        MyInputProcessor inputProcessor = new MyInputProcessor(camera);
        Gdx.input.setInputProcessor(inputProcessor);

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

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.line(0, 0, 1000, 0);
        shapeRenderer.setColor(0, 1, 0, 1);
        shapeRenderer.line(0, 0, 0, 1000);

        shapeRenderer.setColor(0, 1, 0, 1);

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                shapeRenderer.rect(TILE_SIZE*x, TILE_SIZE*y, TILE_SIZE, TILE_SIZE);
            }
        }

        shapeRenderer.end();


        debugBatch.begin();
        font.draw(debugBatch, "" + fps, 10, 100);
		debugBatch.end();
    }
	
	@Override
	public void dispose () {
		debugBatch.dispose();
	}
}

package com.github.johantiden.dwarfactory.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.github.johantiden.dwarfactory.Dwarfactory;
import com.github.johantiden.dwarfactory.game.World;
import com.github.johantiden.dwarfactory.math.ImmutableRectangleInt;
import com.github.johantiden.dwarfactory.util.CoordinateUtil;

import static com.github.johantiden.dwarfactory.game.BackgroundTile.TILE_SIZE;

public class BackgroundRenderSystem extends EntitySystem {
    private final SpriteBatch backgroundBatch;
    private final Camera camera;
    private final World world;

    public BackgroundRenderSystem(Camera camera, World world) {
        this.world = world;
        this.camera = camera;

        backgroundBatch = new SpriteBatch();
    }

    @Override
    public void addedToEngine(Engine engine) {
    }

    @Override
    public void removedFromEngine (Engine engine) {
    }

    @Override
    public void update(float deltaTime) {
        drawBackground();
    }

    private void drawBackground() {
        backgroundBatch.begin();
        backgroundBatch.setProjectionMatrix(camera.combined);

        Rectangle clip = getClipInWorld();
        world.ensureWorldIsLargeEnoughToRender(clip);
        world.forEachBackgroundTile(clip, tile -> backgroundBatch.draw(tile.textureRegion,
                TILE_SIZE * tile.position.x,
                TILE_SIZE * tile.position.y,
                TILE_SIZE,
                TILE_SIZE));

        backgroundBatch.end();
    }

    private Rectangle getClipInWorld() {
        return CoordinateUtil.screenToWorld(new ImmutableRectangleInt(0, 0, Dwarfactory.VIEWPORT_WIDTH, Dwarfactory.VIEWPORT_HEIGHT), camera);
    }
}

package com.github.johantiden.dwarfactory.game.input;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.github.johantiden.dwarfactory.game.TileCoordinate;
import com.github.johantiden.dwarfactory.game.assets.Assets;
import com.github.johantiden.dwarfactory.game.entities.BuildingFactory;
import com.github.johantiden.dwarfactory.game.entities.factory.Factory;
import com.github.johantiden.dwarfactory.game.entities.factory.House;
import com.github.johantiden.dwarfactory.game.entities.factory.Recipe;
import com.github.johantiden.dwarfactory.struct.ImmutableVector2Int;
import com.github.johantiden.dwarfactory.systems.RenderHudSystem;
import com.github.johantiden.dwarfactory.util.CoordinateUtil;

import static com.github.johantiden.dwarfactory.game.BackgroundTile.TILE_SIZE;

public class MouseInputController {

    private final TextureRegion mouseTileTexture = Assets.MOUSE_TILE_OVERLAY;
    private ImmutableVector2Int mouseScreenCoordinates;

    private final PooledEngine pooledEngine;
    private final Camera camera;
    private final RenderHudSystem renderHudSystem;

    private BuildingFactory currentlyPlacing;

    public MouseInputController(PooledEngine pooledEngine, Camera camera, RenderHudSystem renderHudSystem) {
        this.pooledEngine = pooledEngine;
        this.camera = camera;
        this.renderHudSystem = renderHudSystem;
    }

    public void startPlacingFactory(Recipe recipe) {
        currentlyPlacing = Factory.factoryFactory(pooledEngine, recipe);
    }

    public void startPlacingHouse(int numBois) {
        currentlyPlacing = House.houseFactory(pooledEngine, numBois);
    }

    public void onMouseMoved(ImmutableVector2Int screenCoordinates) {
        this.mouseScreenCoordinates = screenCoordinates;
        renderHudSystem.onMouseMoved(screenCoordinates);

    }

    public void finishPlacing(ImmutableVector2Int screenCoordinates) {
        this.mouseScreenCoordinates = screenCoordinates;
        if (currentlyPlacing != null) {
            currentlyPlacing.createAt(getMouseTilePosition());
        }
    }

    public void render(SpriteBatch spriteBatch) {
        if (mouseScreenCoordinates != null) {
            TileCoordinate mouseTilePosition = getMouseTilePosition();
            Vector2 tileCenterToWorld = CoordinateUtil.tileCenterToWorld(mouseTilePosition);

            if (currentlyPlacing == null) {
                renderMouseOverlay(spriteBatch, mouseTilePosition);
            } else {
                currentlyPlacing.render(spriteBatch, tileCenterToWorld);
            }
        }
    }

    private TileCoordinate getMouseTilePosition() {
        return CoordinateUtil.screenToTile(mouseScreenCoordinates, camera);
    }


    private void renderMouseOverlay(SpriteBatch spriteBatch, TileCoordinate mouseTilePosition) {
        if (mouseScreenCoordinates != null) {
            spriteBatch.draw(mouseTileTexture,
                    TILE_SIZE * mouseTilePosition.x,
                    TILE_SIZE * mouseTilePosition.y,
                    TILE_SIZE,
                    TILE_SIZE);
        }
    }

    public void cancel() {
        currentlyPlacing = null;
    }
}

package com.github.johantiden.dwarfactory.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.github.johantiden.dwarfactory.game.TileCoordinate;

public interface BuildingFactory {
    void createAt(TileCoordinate tileCoordinate);

    void render(SpriteBatch spriteBatch, Vector2 worldCoordinateCenterTile);
}

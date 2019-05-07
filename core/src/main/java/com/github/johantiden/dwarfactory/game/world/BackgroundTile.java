package com.github.johantiden.dwarfactory.game.world;

import com.github.johantiden.dwarfactory.game.TileCoordinate;

public class BackgroundTile {

    public final TileCoordinate position;
    public final TileType tileType;


    public BackgroundTile(TileCoordinate position, TileType tileType) {
        this.position = position;
        this.tileType = tileType;
    }
}

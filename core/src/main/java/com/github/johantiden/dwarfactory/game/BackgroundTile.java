package com.github.johantiden.dwarfactory.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Objects;

public class BackgroundTile {
    public static final int TILE_SIZE = 100;

    public final TileCoordinate position;
    public final TextureRegion textureRegion;

    public BackgroundTile(TileCoordinate position, TextureRegion textureRegion) {
        this.position = Objects.requireNonNull(position);
        this.textureRegion = Objects.requireNonNull(textureRegion);
    }

}

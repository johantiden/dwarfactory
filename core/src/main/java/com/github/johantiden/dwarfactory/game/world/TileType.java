package com.github.johantiden.dwarfactory.game.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.github.johantiden.dwarfactory.util.TextureUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.badlogic.gdx.math.MathUtils.random;
import static com.github.johantiden.dwarfactory.game.world.TileFunctionalType.GRASS;
import static com.github.johantiden.dwarfactory.game.world.TileFunctionalType.WATER;

public enum TileType {
    LAND_GRASS_1(GRASS, Color.GREEN, TextureUtil.loadTextureRegionWithMipMap("background/tile_grass_01.jpg")),
    LAND_GRASS_2(GRASS, Color.GREEN.cpy().lerp(Color.YELLOW, Constants.LERP_FACTOR), TextureUtil.loadTextureRegionWithMipMap("background/tile_grass_02.jpg")),
    LAND_GRASS_3(GRASS, Color.GREEN.cpy().lerp(Color.BLACK, Constants.LERP_FACTOR), TextureUtil.loadTextureRegionWithMipMap("background/tile_grass_03.jpg")),
//    LAND_MUD_1(GRASS, Color.BROWN, TextureUtil.loadTextureRegionWithMipMap("background/tile_mud_01.jpg")),
//    LAND_MUD_2(GRASS, Color.BROWN.cpy().lerp(Color.YELLOW, Constants.LERP_FACTOR), TextureUtil.loadTextureRegionWithMipMap("background/tile_mud_02.jpg")),
    LAND_WATER_1(WATER, new Color(0.3f, 0.3f, 1, 1), TextureUtil.loadTextureRegionWithMipMap("background/tile_water_01.jpg")),
    ;

    public final TileFunctionalType tileFunctionalType;
    public final TextureRegion textureRegion;
    public final Color color;

    TileType(TileFunctionalType tileFunctionalType, Color color, TextureRegion textureRegion) {
        this.tileFunctionalType = tileFunctionalType;
        this.textureRegion = textureRegion;
        this.color = color;
    }

    public static TileType randomTileType() {
        TileType[] allTileTypes = TileType.values();
        return allTileTypes[(random.nextInt(allTileTypes.length))];
    }

    public static TileType randomMatchingFunctional(TileFunctionalType functionalType) {
        List<TileType> matching = Arrays.stream(TileType.values())
                .filter(tileType -> tileType.tileFunctionalType == functionalType)
                .collect(Collectors.toList());
        return matching.get(random.nextInt(matching.size()));
    }

    private static class Constants {
        public static final float LERP_FACTOR = 0.05f;
    }
}

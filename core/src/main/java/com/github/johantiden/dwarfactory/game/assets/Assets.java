package com.github.johantiden.dwarfactory.game.assets;

import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.github.johantiden.dwarfactory.struct.ImmutableRectangleInt;
import com.github.johantiden.dwarfactory.util.TextureUtil;

public class Assets {
    public static final TextureRegion FACTORY = new TextureRegion(
            TextureUtil.loadTextureWithMipMap("buildings_transparency.png"),
            0, 1304, 128, 128);
    public static final TextureRegion HOUSE = new TextureRegion(
            TextureUtil.loadTextureWithMipMap("buildings_transparency.png"),
            416, 208, 96, 96);

    public static final TextureRegion BOX = new TextureRegion(
            TextureUtil.loadTextureWithMipMap("crate.jpg"));

    public static final float TILE_BUILDING_INSET = 5f;

    private static TextureRegion getRegion(Texture spriteSheet, ImmutableRectangleInt rectangle) {
        return new TextureRegion(spriteSheet, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    public static class Boi {
        private static final Texture boiSpriteSheet = TextureUtil.loadTextureWithMipMap("simple_10x10_character.png");

        public static final TextureRegion UP = getRegion(boiSpriteSheet, new ImmutableRectangleInt(10, 20, 10, 10));
        public static final TextureRegion DOWN = getRegion(boiSpriteSheet, new ImmutableRectangleInt(10, 0, 10, 10));
        public static final TextureRegion LEFT = getRegion(boiSpriteSheet, new ImmutableRectangleInt(20, 10, -10, 10));
        public static final TextureRegion RIGHT = getRegion(boiSpriteSheet, new ImmutableRectangleInt(10, 10, 10, 10));
    }



    public static class Tiles {
        public static final ImmutableArray<TextureRegion> TILES;

        static {
            Array<TextureRegion> array = new Array<>();

            array.add(new TextureRegion(TextureUtil.loadTextureWithMipMap("tile_001.jpg")));
            array.add(new TextureRegion(TextureUtil.loadTextureWithMipMap("tile_002.jpg")));
            array.add(new TextureRegion(TextureUtil.loadTextureWithMipMap("tile_003.jpg")));
            array.add(new TextureRegion(TextureUtil.loadTextureWithMipMap("tile_004.jpg")));

            TILES = new ImmutableArray<>(array);
        }
    }
}

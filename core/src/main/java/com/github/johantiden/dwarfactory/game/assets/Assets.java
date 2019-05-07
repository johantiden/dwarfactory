package com.github.johantiden.dwarfactory.game.assets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.github.johantiden.dwarfactory.struct.ImmutableRectangleInt;
import com.github.johantiden.dwarfactory.util.JLists;
import com.github.johantiden.dwarfactory.util.TextureUtil;

import java.util.List;

public class Assets {
    public static final TextureRegion FACTORY = new TextureRegion(
            TextureUtil.loadTextureWithMipMap("buildings_transparency.png"),
            0, 1304, 128, 128);
    public static final TextureRegion FACTORY_GREEN = new TextureRegion(
            TextureUtil.loadTextureWithMipMap("buildings_transparency_green.png"),
            0, 1304, 128, 128);
    public static final TextureRegion HOUSE = new TextureRegion(
            TextureUtil.loadTextureWithMipMap("buildings_transparency.png"),
            416, 208, 96, 96);

    public static final TextureRegion HOUSE_GREEN = new TextureRegion(
            TextureUtil.loadTextureWithMipMap("buildings_transparency_green.png"),
            416, 208, 96, 96);

    public static final TextureRegion BOX = new TextureRegion(
            TextureUtil.loadTextureWithMipMap("crate.jpg"));

    public static final TextureRegion MOUSE_TILE_OVERLAY = new TextureRegion(TextureUtil.loadTextureWithMipMap("selection_overlay.png"));

    public static final float TILE_BUILDING_INSET = 5f;

    private static TextureRegion getRegion(Texture spriteSheet, ImmutableRectangleInt rectangle) {
        return new TextureRegion(spriteSheet, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    public static class Boi {

        //        public static final TextureRegion UP = getRegion(boiSpriteSheet, new ImmutableRectangleInt(10, 20, 10, 10));
//        public static final TextureRegion DOWN = getRegion(boiSpriteSheet, new ImmutableRectangleInt(10, 0, 10, 10));
//        public static final TextureRegion LEFT = getRegion(boiSpriteSheet, new ImmutableRectangleInt(20, 10, -10, 10));
//        public static final TextureRegion RIGHT = getRegion(boiSpriteSheet, new ImmutableRectangleInt(10, 10, 10, 10));
        public static final TextureRegion UP = new TextureRegion(TextureUtil.loadTextureWithMipMap("male/Walk (1).png"));
        public static final TextureRegion DOWN = new TextureRegion(TextureUtil.loadTextureWithMipMap("male/Walk (1).png"));
        public static final List<TextureRegion> LEFT = JLists.newArrayList(
                new TextureRegion(TextureUtil.loadTextureWithMipMap("male/Walk (1).png"), 429, 0, -430, 519),
                new TextureRegion(TextureUtil.loadTextureWithMipMap("male/Walk (2).png"), 429, 0, -430, 519),
                new TextureRegion(TextureUtil.loadTextureWithMipMap("male/Walk (3).png"), 429, 0, -430, 519),
                new TextureRegion(TextureUtil.loadTextureWithMipMap("male/Walk (4).png"), 429, 0, -430, 519),
                new TextureRegion(TextureUtil.loadTextureWithMipMap("male/Walk (5).png"), 429, 0, -430, 519),
                new TextureRegion(TextureUtil.loadTextureWithMipMap("male/Walk (6).png"), 429, 0, -430, 519),
                new TextureRegion(TextureUtil.loadTextureWithMipMap("male/Walk (7).png"), 429, 0, -430, 519),
                new TextureRegion(TextureUtil.loadTextureWithMipMap("male/Walk (8).png"), 429, 0, -430, 519),
                new TextureRegion(TextureUtil.loadTextureWithMipMap("male/Walk (9).png"), 429, 0, -430, 519),
                new TextureRegion(TextureUtil.loadTextureWithMipMap("male/Walk (10).png"), 429, 0, -430, 519));
        public static final List<TextureRegion> RIGHT = JLists.newArrayList(
                new TextureRegion(TextureUtil.loadTextureWithMipMap("male/Walk (1).png")),
                new TextureRegion(TextureUtil.loadTextureWithMipMap("male/Walk (2).png")),
                new TextureRegion(TextureUtil.loadTextureWithMipMap("male/Walk (3).png")),
                new TextureRegion(TextureUtil.loadTextureWithMipMap("male/Walk (4).png")),
                new TextureRegion(TextureUtil.loadTextureWithMipMap("male/Walk (5).png")),
                new TextureRegion(TextureUtil.loadTextureWithMipMap("male/Walk (6).png")),
                new TextureRegion(TextureUtil.loadTextureWithMipMap("male/Walk (7).png")),
                new TextureRegion(TextureUtil.loadTextureWithMipMap("male/Walk (8).png")),
                new TextureRegion(TextureUtil.loadTextureWithMipMap("male/Walk (9).png")),
                new TextureRegion(TextureUtil.loadTextureWithMipMap("male/Walk (10).png")));
    }
}

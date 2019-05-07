package com.github.johantiden.dwarfactory.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureUtil {
    public static Texture loadTextureWithMipMap(String assetsPath) {
        Texture texture = new Texture(Gdx.files.internal(assetsPath), true);
        texture.setFilter(Texture.TextureFilter.MipMapNearestNearest, Texture.TextureFilter.MipMapNearestNearest);
        return texture;
    }

    public static TextureRegion loadTextureRegionWithMipMap(String assetsPath) {
        return new TextureRegion(loadTextureWithMipMap(assetsPath));
    }
}

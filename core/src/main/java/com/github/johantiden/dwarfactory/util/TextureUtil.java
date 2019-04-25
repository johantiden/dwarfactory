package com.github.johantiden.dwarfactory.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class TextureUtil {
    public static Texture loadTextureWithMipMap(String assetsPath) {
        Texture texture = new Texture(Gdx.files.internal(assetsPath), true);
        texture.setFilter(Texture.TextureFilter.MipMapNearestNearest, Texture.TextureFilter.MipMapNearestNearest);
        return texture;
    }
}

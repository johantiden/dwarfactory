package com.github.johantiden.dwarfactory.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.util.HashMap;
import java.util.Map;

public class FontUtil {
    private static Map<Integer, BitmapFont> cache = new HashMap<>();

    public static BitmapFont font(int size) {
        cache.computeIfAbsent(size, FontUtil::createFont);
        return cache.get(size);
    }

    private static BitmapFont createFont(int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("PlayfairDisplay-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.borderColor = new Color(0,0,0,1);
        parameter.borderWidth = 1;
        parameter.size = size;
        BitmapFont font12 = generator.generateFont(parameter);
        generator.dispose(); // don't forget to dispose to avoid memory leaks!
        return font12;
    }
}

package com.github.johantiden.dwarfactory.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class FontUtil {
    public static BitmapFont getFont(int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("PlayfairDisplay-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        BitmapFont font12 = generator.generateFont(parameter);
        generator.dispose(); // don't forget to dispose to avoid memory leaks!
        return font12;
    }
}

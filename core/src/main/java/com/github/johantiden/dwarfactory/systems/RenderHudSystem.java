package com.github.johantiden.dwarfactory.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.github.johantiden.dwarfactory.math.ImmutableVector2Int;
import com.github.johantiden.dwarfactory.util.CoordinateUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RenderHudSystem extends EntitySystem {
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final Camera camera;

    private ImmutableVector2Int mouseScreenCoordinates;

    public RenderHudSystem(Camera camera) {
        this.camera = camera;
        batch = new SpriteBatch();
        font = getFont();
    }

    private static BitmapFont getFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("PlayfairDisplay-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        BitmapFont font12 = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose(); // don't forget to dispose to avoid memory leaks!
        return font12;
    }

    @Override
    public void update(float deltaTime) {
        drawDebug();
    }

    private void drawDebug() {
        int fps = Gdx.graphics.getFramesPerSecond();

        List<String> debugLines = new ArrayList<>();

        debugLines.add("fps: " + fps);
        debugLines.addAll(getMouseCoordinatesInDifferentSpaces());

        Collections.reverse(debugLines);
        batch.begin();
        int rowHeight = 50;
        for (int i = 0; i < debugLines.size(); i++) {
            font.draw(batch, debugLines.get(i), 10, rowHeight*(i+1));
        }
        batch.end();
    }

    public void onMouseMoved(ImmutableVector2Int screenCoordinates) {
        this.mouseScreenCoordinates = screenCoordinates;
    }

    private List<String> getMouseCoordinatesInDifferentSpaces() {
        ArrayList<String> strings = new ArrayList<>();
        if (mouseScreenCoordinates == null) {
            strings.add("");
            strings.add("");
            strings.add("");
        } else {
            strings.add("mouse.screen: "+ mouseScreenCoordinates.toString());
            strings.add("mouse.world: " + CoordinateUtil.screenToWorld(mouseScreenCoordinates, camera).toString());
            strings.add("mouse.tile: " + CoordinateUtil.screenToTile(mouseScreenCoordinates, camera).toString());
        }
        return strings;
    }


}

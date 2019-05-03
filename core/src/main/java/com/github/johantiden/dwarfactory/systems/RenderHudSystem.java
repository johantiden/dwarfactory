package com.github.johantiden.dwarfactory.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.github.johantiden.dwarfactory.Dwarfactory;
import com.github.johantiden.dwarfactory.struct.FifoWithLimit;
import com.github.johantiden.dwarfactory.struct.ImmutableVector2Int;
import com.github.johantiden.dwarfactory.util.CoordinateUtil;
import com.github.johantiden.dwarfactory.util.FontUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RenderHudSystem extends EntitySystem {
    private final SpriteBatch batch;
    private final BitmapFont font20 = FontUtil.getFont(20);
    private final BitmapFont font32 = FontUtil.getFont(32);
    private final Camera camera;

    private ImmutableVector2Int mouseScreenCoordinates;

    private static final FifoWithLimit<String> log = new FifoWithLimit<>(15);
    public static final int ROW_HEIGHT_20 = 20;

    public static void log(String message) {
        log.add(message);
    }

    public RenderHudSystem(Camera camera) {
        this.camera = camera;
        batch = new SpriteBatch();
    }

    @Override
    public void update(float deltaTime) {
        drawDebug(deltaTime);
        drawLog();
    }

    private void drawLog() {
        int i = 0;
        batch.begin();

        for (String message : log) {
            font20.draw(batch, message, 10, Dwarfactory.VIEWPORT_HEIGHT-ROW_HEIGHT_20*i);
            i++;
        }
        batch.end();

    }

    private void drawDebug(float deltaTime) {
        int fps = Gdx.graphics.getFramesPerSecond();

        List<String> debugLines = new ArrayList<>();

        debugLines.add("fps: " + fps);
        debugLines.addAll(getMouseCoordinatesInDifferentSpaces());
        debugLines.add("deltaTime: " + deltaTime);

        Collections.reverse(debugLines);
        batch.begin();
        for (int i = 0; i < debugLines.size(); i++) {
            font20.draw(batch, debugLines.get(i), 10, ROW_HEIGHT_20 *(i+1));
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

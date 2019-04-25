package com.github.johantiden.dwarfactory;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.github.johantiden.dwarfactory.util.CoordinateUtil;

import java.util.function.Consumer;

class MyInputProcessor extends InputAdapter {
    private static final float ZOOM_SPEED = 10;
    private final OrthographicCamera camera;
    private final Consumer<Vector2> onMouseMoved;

    public MyInputProcessor(OrthographicCamera camera, Consumer<Vector2> onMouseMoved) {
        this.camera = camera;
        this.onMouseMoved = onMouseMoved;
    }

    @Override
    public boolean scrolled(int amount) {
        camera.zoom *= 1+amount/100f*ZOOM_SPEED;
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Vector2 world = CoordinateUtil.screenToWorld(screenX, screenY, camera);
        onMouseMoved.accept(world);
        return false;
    }

}

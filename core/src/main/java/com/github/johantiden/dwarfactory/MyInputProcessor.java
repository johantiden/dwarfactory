package com.github.johantiden.dwarfactory;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.github.johantiden.dwarfactory.math.ImmutableVector2Int;

import java.util.function.Consumer;

class MyInputProcessor extends InputAdapter {
    private static final float ZOOM_SPEED = 10;
    private final OrthographicCamera camera;
    private final Consumer<ImmutableVector2Int> onMouseMoved;

    public MyInputProcessor(OrthographicCamera camera, Consumer<ImmutableVector2Int> onMouseMoved) {
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
        onMouseMoved.accept(new ImmutableVector2Int(screenX, screenY));
        return false;
    }

}

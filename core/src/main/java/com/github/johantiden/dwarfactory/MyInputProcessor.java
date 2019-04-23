package com.github.johantiden.dwarfactory;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;

class MyInputProcessor extends InputAdapter {
    private static final float ZOOM_SPEED = 5;
    private final OrthographicCamera camera;

    public MyInputProcessor(OrthographicCamera camera) {
        this.camera = camera;
    }

    @Override
    public boolean scrolled(int amount) {
        camera.zoom *= 1+amount/100f*ZOOM_SPEED;
        return false;
    }
}

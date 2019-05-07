package com.github.johantiden.dwarfactory;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.github.johantiden.dwarfactory.game.entities.factory.Recipes;
import com.github.johantiden.dwarfactory.game.input.MouseInputController;
import com.github.johantiden.dwarfactory.struct.ImmutableVector2Int;

class MyInputProcessor extends InputAdapter {
    private static final float ZOOM_SPEED = 10;
    private final OrthographicCamera camera;
    private final MouseInputController mouseInputController;

    public MyInputProcessor(OrthographicCamera camera, MouseInputController mouseInputController) {
        this.camera = camera;
        this.mouseInputController = mouseInputController;
    }

    @Override
    public boolean scrolled(int amount) {
        camera.zoom *= 1+amount/100f*ZOOM_SPEED;
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mouseInputController.onMouseMoved(new ImmutableVector2Int(screenX, screenY));
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        mouseInputController.onMouseMoved(new ImmutableVector2Int(screenX, screenY));
        mouseInputController.finishPlacing(new ImmutableVector2Int(screenX, screenY));
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        mouseInputController.onMouseMoved(new ImmutableVector2Int(screenX, screenY));
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.F) {
            mouseInputController.startPlacingFactory(Recipes.APPLE_GARDEN);
        }
        if (keycode == Input.Keys.G) {
            mouseInputController.startPlacingFactory(Recipes.APPLE_JUICER);
        }
        if (keycode == Input.Keys.H) {
            mouseInputController.startPlacingFactory(Recipes.SELL_APPLE_JUICE);
        }
        if (keycode == Input.Keys.NUM_1) {
            mouseInputController.startPlacingHouse(1);
        }
        if (keycode == Input.Keys.NUM_2) {
            mouseInputController.startPlacingHouse(2);
        }

        if (keycode == Input.Keys.ESCAPE) {
            mouseInputController.cancel();
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }
}

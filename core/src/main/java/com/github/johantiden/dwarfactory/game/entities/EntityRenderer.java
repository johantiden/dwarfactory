package com.github.johantiden.dwarfactory.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface EntityRenderer {

    default void renderSprites(SpriteBatch spriteBatch, RenderContext renderContext) {
    }

    default void renderShapes(ShapeRenderer shapeRenderer, RenderContext renderContext) {
    }

    default void renderSpritesUnprojected(SpriteBatch screenBatch, RenderContext renderContext) {
    }

    default void debugRenderShapes(ShapeRenderer debugShapeRenderer, RenderContext renderContext) {
    }

}

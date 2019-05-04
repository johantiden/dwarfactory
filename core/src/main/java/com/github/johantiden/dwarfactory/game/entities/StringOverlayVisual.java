package com.github.johantiden.dwarfactory.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.github.johantiden.dwarfactory.components.VisualComponent;
import com.github.johantiden.dwarfactory.util.FontUtil;

import java.util.Optional;
import java.util.function.Supplier;

public class StringOverlayVisual {

    public static VisualComponent create(Color color, int size, Vector2 offset, Supplier<Optional<String>> valueSupplier) {
        return new VisualComponent(
                new EntityRenderer() {
                    @Override
                    public void renderSprites(SpriteBatch screenBatch, RenderContext renderContext) {
                        valueSupplier.get()
                                .ifPresent(string -> draw(screenBatch, renderContext, string, FontUtil.font(size), color, offset));
                    }
                });
    }

    private static void draw(SpriteBatch screenBatch, RenderContext renderContext, String string, BitmapFont font, Color color, Vector2 offset) {
        font.setColor(color);
        font.draw(screenBatch, string, renderContext.position.x + offset.x, renderContext.position.y + offset.y);
    }

}

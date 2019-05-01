package com.github.johantiden.dwarfactory.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.github.johantiden.dwarfactory.components.VisualComponent;
import com.github.johantiden.dwarfactory.util.FontUtil;

import java.util.Optional;
import java.util.function.Supplier;

public class StringOverlayVisual {

    public static VisualComponent create(Color color, int size, Vector2 offset, Supplier<Optional<String>> valueSupplier) {
        final BitmapFont font = FontUtil.getFont(size);
        font.setColor(color);

        return new VisualComponent(
             renderContext -> valueSupplier.get()
                     .ifPresent(string ->
                             font.draw(renderContext.spriteBatch, string,
                                     renderContext.position.x + offset.x,
                                     renderContext.position.y + offset.y))
        );
    }

}

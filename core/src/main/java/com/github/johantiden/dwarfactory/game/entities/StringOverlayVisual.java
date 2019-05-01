package com.github.johantiden.dwarfactory.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.github.johantiden.dwarfactory.components.VisualComponent;
import com.github.johantiden.dwarfactory.util.FontUtil;

import java.util.Optional;
import java.util.function.Supplier;

public class StringOverlayVisual {

    public static VisualComponent create(Color color, Supplier<Optional<String>> valueSupplier) {
        final BitmapFont font = FontUtil.getFont(32);
        font.setColor(color);

        return new VisualComponent(
             renderContext -> valueSupplier.get()
                     .ifPresent(string ->
                             font.draw(renderContext.batch, string, renderContext.position.x, renderContext.position.y))
        );
    }

}

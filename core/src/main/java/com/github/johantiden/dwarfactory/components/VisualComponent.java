package com.github.johantiden.dwarfactory.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.github.johantiden.dwarfactory.game.entities.RenderContext;

import java.util.function.Consumer;

public class VisualComponent implements Component {

    private final Consumer<RenderContext> impl;

    public VisualComponent(Consumer<RenderContext> impl) {
        this.impl = impl;
    }

    public static VisualComponent createStatic(TextureRegion texture) {
        return new VisualComponent(renderContext -> drawSimple(renderContext, texture));
    }

    public static VisualComponent blend2(VisualComponent under, VisualComponent over) {
        return new VisualComponent(
                renderContext -> {
                    under.draw(renderContext);
                    over.draw(renderContext);
                }
        );
    }

    public static VisualComponent create4Angles(TextureRegion down,
                                                TextureRegion up,
                                                TextureRegion left,
                                                TextureRegion right) {
        return new VisualComponent(
                renderContext -> {
                    TextureRegion textureRegion = chooseTextureFromAngle(renderContext.speed,
                            down, up, left, right);
                    drawSimple(renderContext, textureRegion);
                });
    }

    public static TextureRegion chooseTextureFromAngle(
            Vector2 vector,
            TextureRegion down,
            TextureRegion up,
            TextureRegion left,
            TextureRegion right) {
        double angle = vector.angle();

        if (angle >= 225 && angle <= 315) {
            return down;
        }

        if (angle >= 45 && angle <= 135) {
            return up;
        }

        if (angle >= 135 && angle <= 225) {
            return left;
        }

        return right;

    }

    private static void drawSimple(RenderContext renderContext, TextureRegion textureRegion) {
        float width = renderContext.size.x;
        float height = renderContext.size.y;
        float x = renderContext.position.x - width / 2;
        float y = renderContext.position.y - height / 2;
        renderContext.batch.draw(textureRegion, x, y, width, height);
    }

    public void draw(RenderContext renderContext) {
        impl.accept(renderContext);
    }
}
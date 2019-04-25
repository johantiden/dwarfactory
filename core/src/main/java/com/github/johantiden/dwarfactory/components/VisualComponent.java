package com.github.johantiden.dwarfactory.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.function.Function;

public class VisualComponent implements Component {

    private final Function<SpeedComponent, TextureRegion> impl;

    private VisualComponent(Function<SpeedComponent, TextureRegion> impl) {
        this.impl = impl;
    }

    public static VisualComponent createStatic(TextureRegion texture) {
        return new VisualComponent(speedComponent -> texture);
    }

    public static VisualComponent create4Angles(TextureRegion down,
                                                TextureRegion up,
                                                TextureRegion left,
                                                TextureRegion right) {
        return new VisualComponent(speed -> {
            double angle = (double) new Vector2(speed.x, speed.y).angle();

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
        });
    }


    public TextureRegion getTexture(SpeedComponent speedComponent) {
        return impl.apply(speedComponent);
    }
}
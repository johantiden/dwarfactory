package com.github.johantiden.dwarfactory.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.github.johantiden.dwarfactory.math.RectangleInt;

public class VisualComponentFromSpeed implements Component {
    private final Texture spriteSheet;

    public VisualComponentFromSpeed(Texture spriteSheet) {
        this.spriteSheet = spriteSheet;
    }

    public TextureRegion getSpriteSheet(SpeedComponent speedComponent) {
        return getRegion(getRectangleInSpriteSheet(speedComponent));
    }

    private TextureRegion getRegion(RectangleInt rectangle) {
        return new TextureRegion(spriteSheet, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    protected RectangleInt getRectangleInSpriteSheet(SpeedComponent speed) {
        double angle = (double) new Vector2(speed.x, speed.y).angle();

        if (angle >= 225 && angle <= 315) {
            // down
            return new RectangleInt(10, 20, 10, 10);
        }

        if (angle >= 45 && angle <= 135) {
            // up
            return new RectangleInt( 10, 0, 10, 10);
        }

        if (angle >= 135 && angle <= 225) {
            // left, flip the image
            return new RectangleInt(20, 10, -10, 10);
        }

        return new RectangleInt(10, 10, 10, 10);
    }
}

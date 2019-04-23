package com.github.johantiden.dwarfactory.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class SpeedComponent implements Component {
    public final Vector2 speed;
    public float maxSpeed;

    public SpeedComponent(Vector2 speed, float maxSpeed) {
        this.speed = speed;
        this.maxSpeed = maxSpeed;
    }

    public SpeedComponent(float x, float y, float maxSpeed) {
        this(new Vector2(x, y), maxSpeed);
    }

    public SpeedComponent(float x, float y) {
        this(new Vector2(x, y), Float.MAX_VALUE);
    }
}

package com.github.johantiden.dwarfactory.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class AccelerationComponent implements Component {
    public final Vector2 acceleration;

    public AccelerationComponent(Vector2 acceleration) {
        this.acceleration = acceleration;
    }

    public AccelerationComponent(float x, float y) {
        this(new Vector2(x, y));
    }
}

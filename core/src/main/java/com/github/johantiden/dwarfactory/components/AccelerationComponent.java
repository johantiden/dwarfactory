package com.github.johantiden.dwarfactory.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class AccelerationComponent extends Vector2 implements Component {

    public AccelerationComponent() {
    }

    public AccelerationComponent(float x, float y) {
        super(x, y);
    }

    public AccelerationComponent(Vector2 v) {
        super(v);
    }
}

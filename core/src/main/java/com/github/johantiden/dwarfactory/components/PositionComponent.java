package com.github.johantiden.dwarfactory.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class PositionComponent extends Vector2 implements Component {
    public PositionComponent() {
    }

    public PositionComponent(float x, float y) {
        super(x, y);
    }

    public PositionComponent(Vector2 v) {
        super(v);
    }
}

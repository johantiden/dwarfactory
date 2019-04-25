package com.github.johantiden.dwarfactory.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class SizeComponent extends Vector2 implements Component {
    public SizeComponent() {
    }

    public SizeComponent(float x, float y) {
        super(x, y);
    }

    public SizeComponent(Vector2 v) {
        super(v);
    }
}

package com.github.johantiden.dwarfactory.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class SpeedComponent extends Vector2 implements Component {
    private static final long serialVersionUID = -1505706454756700406L;
    public float maxSpeed;

    public SpeedComponent(float x, float y, float maxSpeed) {
        super(x, y);
        this.maxSpeed = maxSpeed;
    }

    public SpeedComponent(float x, float y) {
        this(x, y, Float.MAX_VALUE);
    }
}

package com.github.johantiden.dwarfactory.systems.physics;

import com.badlogic.gdx.math.Vector2;
import com.github.johantiden.dwarfactory.components.ForceContext;

public class DragForce implements Force {

    public static final float DRAG_FACTOR = 0.01f;

    @Override
    public Vector2 getForce(ForceContext forceContext) {
        return forceContext.speedComponent.cpy()
                .scl(-forceContext.speedComponent.len()).scl(DRAG_FACTOR);
    }
}

package com.github.johantiden.dwarfactory.systems.physics;

import com.badlogic.gdx.math.Vector2;
import com.github.johantiden.dwarfactory.components.ForceContext;

@FunctionalInterface
public interface Force {
    Vector2 getForce(ForceContext forceContext);
}

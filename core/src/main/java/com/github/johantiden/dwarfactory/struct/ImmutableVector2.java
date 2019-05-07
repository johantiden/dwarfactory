package com.github.johantiden.dwarfactory.struct;

public class ImmutableVector2 {
    public final float x;
    public final float y;

    public ImmutableVector2(float x, float y) {this.x = x;
        this.y = y;
    }

    public float distanceSquaredTo(ImmutableVector2 other) {
        return (x-other.x)*(x-other.x) + (y-other.y)*(y-other.y);
    }
}

package com.github.johantiden.dwarfactory.struct;

import jdk.nashorn.internal.ir.annotations.Immutable;

@Immutable
public class ImmutableRectangleInt {
    public final int x;
    public final int y;
    public final int width;
    public final int height;

    private final ImmutableVector2Int topLeft;
    private final ImmutableVector2Int bottomRight;

    public ImmutableRectangleInt(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        topLeft = new ImmutableVector2Int(x, y);
        bottomRight = new ImmutableVector2Int(x+width, y+height);
    }

    public int getBottom() {
        return y + height;
    }

    public int getRight() {
        return x + width;
    }

    public boolean contains(ImmutableRectangleInt that) {
        return
                x <= that.x &&
                y <= that.y &&
                getRight() >= that.getRight() &&
                getBottom() >= that.getBottom();
    }

    public boolean contains(ImmutableVector2 that) {
        return
                x <= that.x &&
                        y <= that.y &&
                        getRight() >= that.x &&
                        getBottom() >= that.x;
    }

    public ImmutableVector2Int getTopLeft() {
        return topLeft;
    }

    public ImmutableVector2Int getBottomRight() {
        return bottomRight;
    }

}

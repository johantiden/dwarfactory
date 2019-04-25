package com.github.johantiden.dwarfactory.math;

import java.util.Objects;

public final class ImmutableVector2Int {
    public final int x;
    public final int y;

    public ImmutableVector2Int(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        ImmutableVector2Int that = (ImmutableVector2Int) o;
        return x == that.x &&
                y == that.y;
    }

    @Override
    public int hashCode() {

        return Objects.hash(x, y);
    }
}

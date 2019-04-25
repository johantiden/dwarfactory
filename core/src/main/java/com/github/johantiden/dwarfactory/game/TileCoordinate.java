package com.github.johantiden.dwarfactory.game;

import java.util.Objects;

public class TileCoordinate {
    public static final int TILE_SIZE = 100;

    public final int x;
    public final int y;

    public TileCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        TileCoordinate that = (TileCoordinate) o;
        return x == that.x &&
                y == that.y;
    }

    @Override
    public int hashCode() {

        return Objects.hash(x, y);
    }
}

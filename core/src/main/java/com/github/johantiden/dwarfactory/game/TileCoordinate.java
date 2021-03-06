package com.github.johantiden.dwarfactory.game;

import com.badlogic.gdx.math.Vector2;

import java.util.Objects;

public class TileCoordinate {

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

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public TileCoordinate north() {
        return new TileCoordinate(x, y+1);
    }

    public TileCoordinate west() {
        return new TileCoordinate(x-1, y);
    }

    public TileCoordinate east() {
        return new TileCoordinate(x+1, y);
    }

    public TileCoordinate south() {
        return new TileCoordinate(x, y-1);
    }

    public Vector2 asVector() {
        return new Vector2(x, y);
    }
}

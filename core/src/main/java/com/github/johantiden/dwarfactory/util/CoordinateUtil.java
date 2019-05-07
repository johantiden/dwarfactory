package com.github.johantiden.dwarfactory.util;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.github.johantiden.dwarfactory.game.TileCoordinate;
import com.github.johantiden.dwarfactory.struct.ImmutableRectangleInt;
import com.github.johantiden.dwarfactory.struct.ImmutableVector2;
import com.github.johantiden.dwarfactory.struct.ImmutableVector2Int;

import static com.github.johantiden.dwarfactory.game.world.World.TILE_SIZE;

public class CoordinateUtil {

    public static Vector2 screenToWorld(ImmutableVector2Int screenPoint, Camera camera) {
        Vector3 unprojected = camera.unproject(new Vector3(screenPoint.x, screenPoint.y, 0));
        return new Vector2(unprojected.x, unprojected.y);
    }

    public static ImmutableVector2 worldToScreen(Vector2 worldPoint, Camera camera) {
        Vector3 projected = camera.project(new Vector3(worldPoint.x, worldPoint.y, 0));
        return new ImmutableVector2(projected.x, projected.y);
    }

    public static Rectangle screenToWorld(ImmutableRectangleInt screenRectangle, Camera camera) {
        ImmutableVector2Int topLeftScreen = screenRectangle.getTopLeft();
        ImmutableVector2Int bottomRightScreen = screenRectangle.getBottomRight();

        Vector2 topLeftWorld = screenToWorld(topLeftScreen, camera);
        Vector2 bottomRightWorld = screenToWorld(bottomRightScreen, camera);

        return new Rectangle(
            topLeftWorld.x,
            topLeftWorld.y,
            bottomRightWorld.x-topLeftWorld.x,
            bottomRightWorld.y-topLeftWorld.y
        );
    }

    public static TileCoordinate worldToTile(Vector2 worldPoint) {
        return new TileCoordinate(
                worldToTile(worldPoint.x),
                worldToTile(worldPoint.y));
    }

    public static int worldToTile(float coordinate) {
        return Math.floorDiv((int)coordinate, TILE_SIZE);
    }

    public static ImmutableRectangleInt worldToTile(Rectangle worldRectangle) {
        int x = worldToTile(worldRectangle.x);
        int y = worldToTile(worldRectangle.y);
        int width = worldToTile(worldRectangle.width);
        int height = worldToTile(worldRectangle.height);

        return new ImmutableRectangleInt(x, y, width, height);
    }

    public static TileCoordinate screenToTile(ImmutableVector2Int screenPoint, Camera camera) {
        return worldToTile(screenToWorld(screenPoint, camera));
    }

    public static Vector2 tileCenterToWorld(TileCoordinate position) {
        return new Vector2(
                (position.x+0.5f)*TILE_SIZE,
                (position.y+0.5f)*TILE_SIZE);
    }
}

package com.github.johantiden.dwarfactory.util;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.github.johantiden.dwarfactory.game.TileCoordinate;
import com.github.johantiden.dwarfactory.math.ImmutableRectangleInt;
import com.github.johantiden.dwarfactory.math.ImmutableVector2Int;

import static com.github.johantiden.dwarfactory.game.BackgroundTile.TILE_SIZE;

public class CoordinateUtil {

    public static Vector2 screenToWorld(int screenX, int screenY, Camera camera) {
        Vector3 unprojected = camera.unproject(new Vector3(screenX, screenY, 0));
        return new Vector2(unprojected.x, unprojected.y);
    }

    public static Rectangle screenToWorld(ImmutableRectangleInt screenRectangle, Camera camera) {
        ImmutableVector2Int topLeftScreen = screenRectangle.getTopLeft();
        ImmutableVector2Int bottomRightScreen = screenRectangle.getBottomRight();

        Vector2 topLeftWorld = screenToWorld(topLeftScreen.x, topLeftScreen.y, camera);
        Vector2 bottomRightWorld = screenToWorld(bottomRightScreen.x, bottomRightScreen.y, camera);

        return new Rectangle(
            topLeftWorld.x,
            topLeftWorld.y,
            bottomRightWorld.x-topLeftWorld.x,
            bottomRightWorld.y-topLeftWorld.y
        );
    }

    public static TileCoordinate worldCoordinatesToTileCoordinates(Vector2 worldPoint) {
        return new TileCoordinate(
                worldCoordinateToTileCoordinate(worldPoint.x),
                worldCoordinateToTileCoordinate(worldPoint.y));
    }

    public static int worldCoordinateToTileCoordinate(float coordinate) {
        return Math.floorDiv((int)coordinate, TILE_SIZE);
    }

    public static ImmutableRectangleInt worldToTile(Rectangle worldRectangle) {
        int x = worldCoordinateToTileCoordinate(worldRectangle.x);
        int y = worldCoordinateToTileCoordinate(worldRectangle.y);
        int width = worldCoordinateToTileCoordinate(worldRectangle.width);
        int height = worldCoordinateToTileCoordinate(worldRectangle.height);

        return new ImmutableRectangleInt(x, y, width, height);
    }

}

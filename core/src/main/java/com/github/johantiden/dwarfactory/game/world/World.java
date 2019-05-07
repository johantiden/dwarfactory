package com.github.johantiden.dwarfactory.game.world;

import com.badlogic.gdx.math.Rectangle;
import com.github.johantiden.dwarfactory.game.TileCoordinate;
import com.github.johantiden.dwarfactory.struct.ImmutableRectangleInt;
import com.github.johantiden.dwarfactory.struct.ImmutableVector2;
import com.github.johantiden.dwarfactory.util.CoordinateUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class World {
    public static final int TILE_SIZE = 100;
    private final VoronoiWorld voronoiWorld = new VoronoiWorld();

    private final Map<TileCoordinate, TileType> map;
    private ImmutableRectangleInt currentMapBounds = new ImmutableRectangleInt(0, 0, 0, 0);

    private final Object sync = new Object();

    public World() {
        map = new HashMap<>();
    }

    public void forEachBackgroundTile(Rectangle clip, Consumer<BackgroundTile> consumer) {
        forEachTile(clip, tileCoordinate -> {
            TileType tileType = map.get(tileCoordinate);
            consumer.accept(new BackgroundTile(tileCoordinate, tileType));
        });
    }

    public void forEachTile(Rectangle worldClip, Consumer<TileCoordinate> consumer) {
        ImmutableRectangleInt clipTiles = CoordinateUtil.worldToTile(worldClip);
        forEachTile(clipTiles, consumer);
    }

    public void forEachTile(ImmutableRectangleInt tilesClip, Consumer<TileCoordinate> consumer) {
        for (int y = tilesClip.y; y <= tilesClip.getBottom()+1; y++) {
            for (int x = tilesClip.x; x <= tilesClip.getRight()+1; x++) {
                TileCoordinate tile = new TileCoordinate(x, y);
                consumer.accept(tile);
            }
        }
    }


    public final void ensureWorldIsLargeEnoughToRender(Rectangle clip) {

        ImmutableRectangleInt clipTiles = CoordinateUtil.worldToTile(clip);
        synchronized (sync) {
            if (!currentMapBounds.contains(clipTiles)) {
                forEachTile(clipTiles, tile -> map.computeIfAbsent(tile, tileCoordinate ->
                {
                    TileFunctionalType functionalType = voronoiWorld.findClosest(new ImmutableVector2(tileCoordinate.x, tileCoordinate.y));
                    TileType tileType = TileType.randomMatchingFunctional(functionalType);
                    return tileType;
                }));
            }
            currentMapBounds = clipTiles;
        }
    }

}

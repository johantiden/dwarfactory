package com.github.johantiden.dwarfactory.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.github.johantiden.dwarfactory.game.assets.Assets;
import com.github.johantiden.dwarfactory.struct.ImmutableRectangleInt;
import com.github.johantiden.dwarfactory.util.CoordinateUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.badlogic.gdx.math.MathUtils.random;

public class World {
    private final Map<TileCoordinate, BackgroundTile> map;
    private ImmutableRectangleInt currentMapBounds = new ImmutableRectangleInt(0, 0, 0, 0);

    private final Object sync = new Object();

    public World() {
        map = new HashMap<>();
    }

    private static TextureRegion randomTexture() {
        return Assets.Tiles.TILES.get(random.nextInt(Assets.Tiles.TILES.size()));
    }

    public void forEachBackgroundTile(Rectangle clip, Consumer<BackgroundTile> consumer) {
        forEachTile(clip, tile -> {
            BackgroundTile backgroundTile = map.get(tile);
            consumer.accept(backgroundTile);
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
                Function<TileCoordinate, BackgroundTile> tileFactory = tileFactory();

                forEachTile(clipTiles, tile -> map.computeIfAbsent(tile, tileFactory));
            }
            currentMapBounds = clipTiles;
        }
    }

    private static Function<TileCoordinate, BackgroundTile> tileFactory() {
        return tileCoordinate -> {
            TextureRegion textureRegion = randomTexture();
            return new BackgroundTile(tileCoordinate, textureRegion);
        };
    }
}

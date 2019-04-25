package com.github.johantiden.dwarfactory.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.github.johantiden.dwarfactory.Dwarfactory;
import com.github.johantiden.dwarfactory.math.ImmutableRectangleInt;
import com.github.johantiden.dwarfactory.util.CoordinateUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.badlogic.gdx.math.MathUtils.random;

public class World {
    private final Map<TileCoordinate, BackgroundTile> map;
    private ImmutableRectangleInt currentMapBounds = new ImmutableRectangleInt(0, 0, 0, 0);

    public static final Rectangle INITIAL_CLIP_WORLD = new Rectangle(
            -Dwarfactory.VIEWPORT_WIDTH,
            -Dwarfactory.VIEWPORT_HEIGHT,
            Dwarfactory.VIEWPORT_WIDTH*2,
            Dwarfactory.VIEWPORT_HEIGHT*2);

    private final List<TextureRegion> tileTextures;
    private Object sync = new Object();

    public World(List<TextureRegion> tileTextures) {
        this.tileTextures = tileTextures;
        map = new HashMap<>();
        ensureWorldIsLargeEnoughToRender(INITIAL_CLIP_WORLD);
    }

    private static TextureRegion randomTexture(List<TextureRegion> tileTextures) {
        return tileTextures.get(random.nextInt(tileTextures.size()));
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

    private Function<TileCoordinate, BackgroundTile> tileFactory() {
        return tileCoordinate -> {
            TextureRegion textureRegion = randomTexture(tileTextures);
            return new BackgroundTile(tileCoordinate, textureRegion);
        };
    }
}

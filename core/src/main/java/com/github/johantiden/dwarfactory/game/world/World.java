package com.github.johantiden.dwarfactory.game.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.github.czyzby.kiwi.util.tuple.immutable.Pair;
import com.github.johantiden.dwarfactory.game.TileCoordinate;
import com.github.johantiden.dwarfactory.struct.ImmutableRectangleInt;
import com.github.johantiden.dwarfactory.util.CoordinateUtil;
import com.github.johantiden.dwarfactory.util.JLists;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.github.johantiden.dwarfactory.game.world.TileFunctionalType.GRASS;
import static com.github.johantiden.dwarfactory.game.world.TileFunctionalType.WATER;
import static java.lang.Math.PI;
import static java.lang.Math.random;

public class World {
    public static final int TILE_SIZE = 100;
    private final VoronoiWorld voronoiWorld = new VoronoiWorld();

    private final Map<TileCoordinate, TileType> map;
    private ImmutableRectangleInt currentMapBounds = new ImmutableRectangleInt(0, 0, 0, 0);

    private final Object sync = new Object();

    public World() {
        map = new HashMap<>();

        populate();
    }

    private void populate() {
//        for (int y = -100; y < 100; y++) {
//            for (int x = -100; x < 100; x++) {
//                TileFunctionalType tileFunctionalType = voronoiWorld.findClosest(new ImmutableVector2(x, y));
//                map.put(new TileCoordinate(x, y), TileType.randomMatchingFunctional(tileFunctionalType));
//            }
//        }
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
                forEachTile(clipTiles, tile -> map.computeIfAbsent(tile, tileCoordinate -> {
//                    TileFunctionalType functionalType = voronoiWorld.findClosest(new ImmutableVector2(tileCoordinate.x, tileCoordinate.y));
                    TileFunctionalType functionalType = pseudoRandom(tileCoordinate);
                    TileType tileType = TileType.randomMatchingFunctional(functionalType);
                    return tileType;
                }));

                currentMapBounds = clipTiles;

                Automata automata = new Automata(map);
                for (int i = 0; i < 10; i++) {
                    automata.iterate();
                }
            }

        }
    }



    private TileFunctionalType pseudoRandom(TileCoordinate coordinate) {
        Vector2 positionVector = coordinate.asVector();

        int baseFrequency = 1000;
        double waterRatio = 0.4;
        List<Pair<TileFunctionalType, List<SineWave>>> waves = JLists.newArrayList(
            new Pair<>(GRASS, JLists.newArrayList(
                    new SineWave(random()*2*PI, random()/ baseFrequency, random()*2*PI, random()),
//                    new SineWave(random()*2*PI, random()/ baseFrequency, random()*2*PI, random()),
//                    new SineWave(random()*2*PI, random()/ baseFrequency, random()*2*PI, random()),
                    new SineWave(random()*2*PI, random()/ baseFrequency, random()*2*PI, random())
            )),
            new Pair<>(WATER, JLists.newArrayList(
                    new SineWave(random()*2*PI, random()/baseFrequency, random()*2*PI, random()* waterRatio),
                    new SineWave(random()*2*PI, random()/baseFrequency, random()*2*PI, random()* waterRatio)
            ))
        );

        return waves.stream()
                .max(Comparator.comparing(
                        pair -> pair.getSecond().stream()
                             .mapToDouble(sineWave -> sineWave.compute(positionVector))
                                .max()
                                .getAsDouble()
                ))
                .get()
                .getFirst();
    }



    private static class SineWave {
        private final double startOffset;
        private final double frequency;
        private final double amplitude;
        private final double angle;

        private final Vector2 angularFrequency;
        private SineWave(double startOffset, double frequency, double angle, double amplitude) {
            this.startOffset = startOffset;
            this.frequency = frequency;
            this.angle = angle;
            this.amplitude = amplitude;
            angularFrequency = new Vector2((float)Math.cos(angle), (float)Math.sin(angle)).scl((float) frequency);
        }

        private double compute(Vector2 vector) {

            float dotProduct = angularFrequency.dot(vector);

            return Math.sin(dotProduct+ startOffset) * amplitude;
//            return (Math.cos(vector.x*frequency* Math.sin(angle) + startOffset) * amplitude) +
//                    (Math.sin(vector.y*frequency* Math.cos(angle) + startOffset) * amplitude);

        }
    }
}

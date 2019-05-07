package com.github.johantiden.dwarfactory.game.world;

import com.github.johantiden.dwarfactory.struct.ImmutableRectangleInt;
import com.github.johantiden.dwarfactory.struct.ImmutableVector2;
import javafx.util.Pair;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class VoronoiWorld {

    public static final int NUM_POINTS = 1000;
    public static final int MAX_WORLD_SIZE = 1000;
    private final List<Pair<ImmutableVector2, TileFunctionalType>> voronoiCentroids = new ArrayList<>();

    private final Random random = new SecureRandom();

    public static final int STARTER_ZONE_SIZE = 100;
    private final ImmutableRectangleInt STARTER_ZONE = new ImmutableRectangleInt(-STARTER_ZONE_SIZE/2, -STARTER_ZONE_SIZE/2, STARTER_ZONE_SIZE, STARTER_ZONE_SIZE);

    public VoronoiWorld() {
        for (int i = 0; i < NUM_POINTS; i++) {
            float x = random.nextFloat()*MAX_WORLD_SIZE - (MAX_WORLD_SIZE/2);
            float y = random.nextFloat()*MAX_WORLD_SIZE - (MAX_WORLD_SIZE/2);

            ImmutableVector2 position = new ImmutableVector2(x, y);
            TileFunctionalType type = TileFunctionalType.randomTileType();
            if (STARTER_ZONE.contains(position)) {
                while (type == TileFunctionalType.WATER) {
                    type = TileFunctionalType.randomTileType();
                }
            }
            voronoiCentroids.add(new Pair<>(position, type));
        }
    }

    public TileFunctionalType findClosest(ImmutableVector2 worldPosition) {
        return voronoiCentroids.stream()
                .min(closestTo(worldPosition))
                .get()
                .getValue();
    }

    private static Comparator<Pair<ImmutableVector2, TileFunctionalType>> closestTo(ImmutableVector2 worldPosition) {
        return Comparator.comparing(p -> p.getKey().distanceSquaredTo(worldPosition));
    }
}

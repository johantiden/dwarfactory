package com.github.johantiden.dwarfactory.game.world;

import static com.badlogic.gdx.math.MathUtils.random;

public enum TileFunctionalType {
    GRASS,
//    MUD,
    WATER,
    ;

    public static TileFunctionalType randomTileType() {
        TileFunctionalType[] values = TileFunctionalType.values();
        return values[(random.nextInt(values.length))];
    }

}

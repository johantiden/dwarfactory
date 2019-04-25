package com.github.johantiden.dwarfactory.game.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.github.johantiden.dwarfactory.components.PositionComponent;
import com.github.johantiden.dwarfactory.components.SizeComponent;
import com.github.johantiden.dwarfactory.components.VisualComponent;
import com.github.johantiden.dwarfactory.game.TileCoordinate;
import com.github.johantiden.dwarfactory.game.assets.Assets;
import com.github.johantiden.dwarfactory.util.CoordinateUtil;

import static com.github.johantiden.dwarfactory.game.BackgroundTile.TILE_SIZE;

public class Factory {
    public static void createFactory(TileCoordinate position, PooledEngine engine) {
        float tileBuildingInset = 5f;
        float factorySize = TILE_SIZE * 3 - tileBuildingInset * 2;

        Vector2 centerInWorld = CoordinateUtil.tileCenterToWorld(position);

        Entity boi = engine.createEntity();
        boi.add(new PositionComponent(centerInWorld.x, centerInWorld.y));
        boi.add(new SizeComponent(factorySize, factorySize));
        boi.add(VisualComponent.createStatic(Assets.FACTORY));
        engine.addEntity(boi);
    }
}

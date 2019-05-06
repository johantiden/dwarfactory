package com.github.johantiden.dwarfactory.game.entities.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.github.johantiden.dwarfactory.components.ItemConsumerComponent;
import com.github.johantiden.dwarfactory.components.ItemProducerComponent;
import com.github.johantiden.dwarfactory.components.PositionComponent;
import com.github.johantiden.dwarfactory.components.Priority;
import com.github.johantiden.dwarfactory.components.SizeComponent;
import com.github.johantiden.dwarfactory.components.VisualComponent;
import com.github.johantiden.dwarfactory.game.TileCoordinate;
import com.github.johantiden.dwarfactory.game.assets.Assets;
import com.github.johantiden.dwarfactory.game.entities.Bag;
import com.github.johantiden.dwarfactory.game.entities.Boi;
import com.github.johantiden.dwarfactory.game.entities.BuildingFactory;
import com.github.johantiden.dwarfactory.util.CoordinateUtil;

import static com.github.johantiden.dwarfactory.game.BackgroundTile.TILE_SIZE;

public class House {
    private final Entity entity;
    public static final float SIZE = TILE_SIZE - Assets.TILE_BUILDING_INSET * 2;

    public House(Entity entity) {this.entity = entity;}


    public static BuildingFactory houseFactory(PooledEngine pooledEngine, int numBois) {
        TextureRegion textureRegion = Assets.HOUSE_GREEN;

        return new BuildingFactory() {
            @Override
            public void createAt(TileCoordinate tileCoordinate) {
                createHouse(tileCoordinate, pooledEngine, numBois);
            }

            @Override
            public void render(SpriteBatch spriteBatch, Vector2 worldCoordinateCenterTile) {
                float width = SIZE;
                float height = SIZE;
                float x = worldCoordinateCenterTile.x - width / 2;
                float y = worldCoordinateCenterTile.y - height / 2;
                spriteBatch.draw(textureRegion, x, y, width, height);
            }
        };
    }

    public static void createHouse(TileCoordinate position, PooledEngine engine, double numBois) {
        Entity entity = engine.createEntity();

        Vector2 centerInWorld = CoordinateUtil.tileCenterToWorld(position);

        entity.add(new PositionComponent(centerInWorld.x, centerInWorld.y));
        entity.add(new SizeComponent(SIZE, SIZE));
        entity.add(VisualComponent.createStatic(Assets.HOUSE));
        Bag bag = new Bag(type -> true);
        entity.add(ItemConsumerComponent.createWithSharedBag(entity, bag, Priority.LOW));
        entity.add(ItemProducerComponent.createWithSharedBag(entity, bag, Priority.HIGH));
        engine.addEntity(entity);

        House house = new House(entity);

        for (int i = 0; i < numBois; i++) {
            Boi.createBoi(centerInWorld.x, centerInWorld.y, 0, 0, engine, house);
        }
    }

    public Entity getEntity() {
        return entity;
    }
}

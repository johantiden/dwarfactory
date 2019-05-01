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

public class Factory implements ItemReceiver<Apple>, ItemProducer<Apple> {
    private final Entity entity;

    private final ItemStack<Apple> outputStack = new AppleStack(100);
    private final ItemStack<Apple> inputStack = new AppleStack(0);

    public Factory(Entity entity) {
        this.entity = entity;
    }

    public static Factory createFactory(TileCoordinate position, PooledEngine engine) {
        float tileBuildingInset = 5f;
        float factorySize = TILE_SIZE * 3 - tileBuildingInset * 2;

        Vector2 centerInWorld = CoordinateUtil.tileCenterToWorld(position);

        Entity entity = engine.createEntity();
        entity.add(new PositionComponent(centerInWorld.x, centerInWorld.y));
        entity.add(new SizeComponent(factorySize, factorySize));
        entity.add(VisualComponent.createStatic(Assets.FACTORY));
        engine.addEntity(entity);
        return new Factory(entity);
    }

    @Override
    public ItemStack<Apple> drain(int amount) {
        return outputStack.tryDrain(amount);
    }

    @Override
    public void accept(ItemStack<Apple> itemStack) {
        inputStack.takeAllFrom(itemStack);
    }

    @Override
    public Entity getEntity() {
        return entity;
    }
}

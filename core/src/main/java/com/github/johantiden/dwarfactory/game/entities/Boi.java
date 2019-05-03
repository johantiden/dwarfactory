package com.github.johantiden.dwarfactory.game.entities;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.github.johantiden.dwarfactory.components.ControlComponent;
import com.github.johantiden.dwarfactory.components.ItemConsumerComponent;
import com.github.johantiden.dwarfactory.components.Job;
import com.github.johantiden.dwarfactory.components.PositionComponent;
import com.github.johantiden.dwarfactory.components.SizeComponent;
import com.github.johantiden.dwarfactory.components.SpeedComponent;
import com.github.johantiden.dwarfactory.components.VisualComponent;
import com.github.johantiden.dwarfactory.game.assets.Assets;
import com.github.johantiden.dwarfactory.game.entities.factory.House;

import java.util.Collection;
import java.util.Optional;

public class Boi {
    public static final int RANGE_SQUARED = 100;

    static final int MAX_CARRY = 5;
    public static final int SIZE = 50;
    public static final int MAX_SPEED = 100;

    final Entity boiEntity;
    final House house;
    ItemStack carrying = null;

    private final ComponentMapper<ItemConsumerComponent> itemConsumerMapper = ComponentMapper.getFor(ItemConsumerComponent.class);

    public Boi(Entity boiEntity, House house) {
        this.boiEntity = boiEntity;
        this.house = house;
    }

    public static void createBoi(float x, float y, float speedX, float speedY, PooledEngine engine, House house) {

        Entity entity = engine.createEntity();
        Boi boi = new Boi(entity, house);

        entity.add(new PositionComponent(x, y));
        entity.add(new SpeedComponent(speedX, speedY, MAX_SPEED));
        ControlComponent control = new ControlComponent(boi::selectNewJob);
        entity.add(control);

        VisualComponent mainVisual = VisualComponent.create2AnglesAnimated(
                Assets.Boi.LEFT,
                Assets.Boi.RIGHT);
        VisualComponent carryVisual = StringOverlayVisual.create(new Color(1,0,0,1), SIZE, new Vector2(SIZE*2/5, -SIZE*2/5), () -> boi.tryGetCarrying().map(carrying -> String.valueOf(carrying.getAmount())));
        entity.add(VisualComponent.blend(mainVisual, carryVisual));
        entity.add(new SizeComponent(SIZE, SIZE));
        engine.addEntity(entity);
    }

    private Collection<Job> selectNewJob(SelectJobContext selectJobContext) {
        return new BoiJobSelector(selectJobContext, this).get();
    }

    private Optional<ItemStack> tryGetCarrying() {
        return Optional.ofNullable(carrying);
    }

    private ItemConsumerComponent getHomeConsumer() {
        return itemConsumerMapper.get(house.getEntity());
    }

}

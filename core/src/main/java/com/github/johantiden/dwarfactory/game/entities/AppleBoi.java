package com.github.johantiden.dwarfactory.game.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.github.johantiden.dwarfactory.Dwarfactory;
import com.github.johantiden.dwarfactory.components.AccelerationComponent;
import com.github.johantiden.dwarfactory.components.ControlComponent;
import com.github.johantiden.dwarfactory.components.PositionComponent;
import com.github.johantiden.dwarfactory.components.SizeComponent;
import com.github.johantiden.dwarfactory.components.SpeedComponent;
import com.github.johantiden.dwarfactory.components.VisualComponent;
import com.github.johantiden.dwarfactory.game.assets.Assets;
import com.github.johantiden.dwarfactory.game.entities.factory.ItemProducer;
import com.github.johantiden.dwarfactory.game.entities.factory.ItemReceiver;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

import static com.badlogic.gdx.math.MathUtils.random;

public class AppleBoi {

    private static final int MAX_CARRY = 1;

    private final Entity entity;
    private ItemStack<Apple> carrying = null;

    private final ItemReceiver<Apple> targetHack;
    private final ItemProducer<Apple> sourceHack;

    public AppleBoi(Entity entity, ItemReceiver<Apple> targetHack, ItemProducer<Apple> sourceHack) {
        this.entity = entity;
        this.targetHack = targetHack;
        this.sourceHack = sourceHack;
    }

    public static AppleBoi createBoi(float x, float y, float speedX, float speedY, PooledEngine engine, ItemReceiver<Apple> targetHack, ItemProducer<Apple> sourceHack) {

        Entity boiEntity = engine.createEntity();
        AppleBoi appleBoi = new AppleBoi(boiEntity, targetHack, sourceHack);

        boiEntity.add(new PositionComponent(x, y));
        boiEntity.add(new SpeedComponent(speedX, speedY, 100));
        boiEntity.add(new AccelerationComponent(0, 0));
        VisualComponent mainVisual = VisualComponent.create4Angles(
                Assets.Boi.DOWN,
                Assets.Boi.UP,
                Assets.Boi.LEFT,
                Assets.Boi.RIGHT);
        VisualComponent carryVisual = StringOverlayVisual.create(new Color(1,0,0,1), 32, new Vector2(30, 30), () -> appleBoi.tryGetCarrying().map(carrying -> String.valueOf(carrying.getAmount())));
        boiEntity.add(VisualComponent.blend(mainVisual, carryVisual));
        boiEntity.add(new SizeComponent(32, 32));
        boiEntity.add(new ControlComponent(appleBoi.newMyJobSelector()));
        return appleBoi;
    }

    private Optional<ItemStack<Apple>> tryGetCarrying() {
        return Optional.ofNullable(carrying);
    }

    public Supplier<ControlComponent.Job> newMyJobSelector() {
        return new MyJobSelector();
    }

    public static void createRandomBoi(PooledEngine engine, ItemReceiver<Apple> targetHack, ItemProducer<Apple> sourceHack) {
        Random random = new SecureRandom();

        float x = random((float)Dwarfactory.VIEWPORT_WIDTH);
        float y = random((float)Dwarfactory.VIEWPORT_HEIGHT);
        float speedX = random.nextFloat() * 30 - 15;
        float speedY = random.nextFloat() * 30 - 15;

        AppleBoi appleBoi = createBoi(x, y, speedX, speedY, engine, targetHack, sourceHack);
        engine.addEntity(appleBoi.entity);
    }

    private class MyJobSelector implements Supplier<ControlComponent.Job> {

        @Override
        public ControlComponent.Job get() {
            if (carrying != null) {
                return deliveryJob();
            } else {
                return returnHomeJob();
            }
        }

        private ControlComponent.Job returnHomeJob() {
            return new ControlComponent.Job(
                 sourceHack.getEntity(),
                 createPickUpJob()
            );
        }

        private Runnable createPickUpJob() {
            return () -> {
                ItemStack<Apple> newItemStack = sourceHack.drain(MAX_CARRY);
                if (newItemStack.getAmount() > 0) {
                    carrying = newItemStack;
                } else {
                    carrying = null;
                }
            };
        }

        private ControlComponent.Job deliveryJob() {
            return new ControlComponent.Job(
                    targetHack.getEntity(),
                    createFinishDelivery()
            );
        }

        private Runnable createFinishDelivery() {
            return () -> {
                targetHack.accept(carrying);
                carrying = null;
            };
        }

    }
}

package com.github.johantiden.dwarfactory.game.entities;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.github.johantiden.dwarfactory.components.AccelerationComponent;
import com.github.johantiden.dwarfactory.components.ControlComponent;
import com.github.johantiden.dwarfactory.components.ForceContext;
import com.github.johantiden.dwarfactory.components.ForcesComponent;
import com.github.johantiden.dwarfactory.components.ItemProducerComponent;
import com.github.johantiden.dwarfactory.components.ItemConsumerComponent;
import com.github.johantiden.dwarfactory.components.Job;
import com.github.johantiden.dwarfactory.components.PositionComponent;
import com.github.johantiden.dwarfactory.components.SizeComponent;
import com.github.johantiden.dwarfactory.components.SpeedComponent;
import com.github.johantiden.dwarfactory.components.VisualComponent;
import com.github.johantiden.dwarfactory.game.assets.Assets;
import com.github.johantiden.dwarfactory.systems.physics.DragForce;
import com.github.johantiden.dwarfactory.util.JLists;

import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.github.johantiden.dwarfactory.game.BackgroundTile.TILE_SIZE;

public class Boi {
    public static final int RANGE_SQUARED = TILE_SIZE*TILE_SIZE/2/2;

    private static final int MAX_CARRY = 2;
    public static final int SIZE = 32;
    public static final SecureRandom RANDOM = new SecureRandom();

    private final Entity entity;
    private ItemStack carrying = null;

    private final ItemProducerComponent sourceHack;
    private final ComponentMapper<PositionComponent> positionMapper = ComponentMapper.getFor(PositionComponent.class);

    public Boi(Entity entity, ItemProducerComponent sourceHack) {
        this.entity = entity;
        this.sourceHack = sourceHack;
    }

    public static void createBoi(float x, float y, float speedX, float speedY, PooledEngine engine, ItemProducerComponent sourceHack) {

        Entity entity = engine.createEntity();
        Boi boi = new Boi(entity, sourceHack);

        entity.add(new PositionComponent(x, y));
        entity.add(new SpeedComponent(speedX, speedY, 100));
        entity.add(new AccelerationComponent(0, 0));
        ControlComponent control = new ControlComponent(boi::selectNewJob);
        entity.add(control);

        entity.add(new ForcesComponent(JLists.newArrayList(
                new DragForce(),
                control.asForce())));
        VisualComponent mainVisual = VisualComponent.create4Angles(
                Assets.Boi.DOWN,
                Assets.Boi.UP,
                Assets.Boi.LEFT,
                Assets.Boi.RIGHT);
        VisualComponent carryVisual = StringOverlayVisual.create(new Color(1,0,0,1), SIZE, new Vector2(SIZE*2/5, -SIZE*2/5), () -> boi.tryGetCarrying().map(carrying -> String.valueOf(carrying.getAmount())));
        entity.add(VisualComponent.blend(mainVisual, carryVisual));
        entity.add(new SizeComponent(SIZE, SIZE));
        engine.addEntity(entity);
    }

    private Job selectNewJob(SelectJobContext selectJobContext) {
        return new MyJobSelector(selectJobContext).get();
    }

    private Optional<ItemStack> tryGetCarrying() {
        return Optional.ofNullable(carrying);
    }

    private class MyJobSelector implements Supplier<Job> {

        private final SelectJobContext selectJobContext;

        public MyJobSelector(SelectJobContext selectJobContext) {

            this.selectJobContext = selectJobContext;
        }

        @Override
        public Job get() {
            if (carrying != null) {
                Optional<ItemConsumerComponent> maybeNewReceiver = chooseTarget(selectJobContext);
                return maybeNewReceiver
                        .map(this::deliveryJob)
                        .orElseGet(() -> {
                            // no one wants my stuff :( go home and wait
                            if (!isHome()) {
                                return goHomeJob();
                            } else {
                                return waitJob();
                            }
                        });

            } else if (!isHome()){
                return goHomeJob();
            } else {
                return pickupJob();
            }
        }

        private Job waitJob() {
            return new Job() {
                @Override
                public boolean canFinishJob() {
                    return true;
                }

                @Override
                public Vector2 getWantedAcceleration(ForceContext forceContext) {
                    return brake(forceContext);
                }
            };
        }

        private Optional<ItemConsumerComponent> chooseTarget(SelectJobContext selectJobContext) {
            List<ItemConsumerComponent> eligibleReceivers = JLists.stream(selectJobContext.allItemConsumers)
                    .filter(itemConsumer -> itemConsumer.wants(carrying.itemType))
                    .collect(Collectors.toList());

            if (eligibleReceivers.isEmpty()) {
                return Optional.empty();
            }
            ItemConsumerComponent receiver = eligibleReceivers.get(RANDOM.nextInt(eligibleReceivers.size()));
            return Optional.of(receiver);
        }

        private Job pickupJob() {
            return new Job() {

                @Override
                public void finish() {
                    sourceHack.getAvailableOutput().stream()
                            .findAny()
                            .ifPresent(outputStack -> {
                                ItemStack newItemStack = sourceHack.drain(outputStack.itemType, MAX_CARRY);
                                if (newItemStack.getAmount() > 0) {
                                    carrying = newItemStack;
                                } else {
                                    carrying = null;
                                }
                            });

                }

                @Override
                public Vector2 getWantedAcceleration(ForceContext forceContext) {
                    return brake(forceContext);
                }

                @Override
                public boolean canFinishJob() {
                    boolean isInRange = isInRange(Boi.this.sourceHack.hack_getEntity());
                    return isInRange && !sourceHack.getAvailableOutput().isEmpty();
                }

                @Override
                public boolean isJobFailed() {
                    boolean isInRange = isInRange(Boi.this.sourceHack.hack_getEntity());
                    return !isInRange;
                }
            };
        }

        private Vector2 brake(ForceContext forceContext) {
            // brakes
            return forceContext.speedComponent.cpy().scl(-0.5f);
        }

        private boolean isInRange(Entity target) {
            float distanceSquared = getDistanceSquared(target, Boi.this.entity);
            return distanceSquared < RANGE_SQUARED;
        }


        private Job goHomeJob() {
            return new Job() {

                @Override
                public boolean canFinishJob() {
                    return isInRange(sourceHack.hack_getEntity());
                }

                @Override
                public Vector2 getWantedAcceleration(ForceContext forceContext) {
                    return getAccelerationTowards(getPosition(sourceHack.hack_getEntity()), getPosition(entity));
                }

                @Override
                public boolean isJobFailed() {
                    return false;
                }
            };
        }

        private boolean isHome() {
            return isInRange(sourceHack.hack_getEntity());
        }

        private Job deliveryJob(ItemConsumerComponent target) {
            return new Job() {

                @Override
                public boolean canFinishJob() {
                    return isInRange(target.hack_getEntity());
                }

                @Override
                public Vector2 getWantedAcceleration(ForceContext forceContext) {
                    return getAccelerationTowards(getPosition(target.hack_getEntity()), getPosition(entity));
                }

                @Override
                public void finish() {
                    target.accept(carrying);
                    carrying = null;
                }
            };
        }
    }

    private float getDistanceSquared(Entity a, Entity b) {
        PositionComponent homePosition = getPosition(a);
        PositionComponent myPosition = getPosition(b);
        return getDistanceSquared(homePosition, myPosition);
    }

    private static float getDistanceSquared(PositionComponent a, PositionComponent b) {
        return a.cpy().sub(b).len2();
    }

    private PositionComponent getPosition(Entity entity) {
        return positionMapper.get(entity);
    }

    private static Vector2 getAccelerationTowards(PositionComponent target, PositionComponent position) {
        Objects.requireNonNull(target);
        Objects.requireNonNull(position);

        Vector2 targetDirectionVector = target.cpy()
                .sub(position)
                .setLength(1);

        float accelerationFactor = 100;
        return targetDirectionVector.scl(accelerationFactor);
    }
}

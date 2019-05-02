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
import com.github.johantiden.dwarfactory.components.ItemConsumerComponent;
import com.github.johantiden.dwarfactory.components.ItemProducerComponent;
import com.github.johantiden.dwarfactory.components.Job;
import com.github.johantiden.dwarfactory.components.PositionComponent;
import com.github.johantiden.dwarfactory.components.SizeComponent;
import com.github.johantiden.dwarfactory.components.SpeedComponent;
import com.github.johantiden.dwarfactory.components.VisualComponent;
import com.github.johantiden.dwarfactory.game.assets.Assets;
import com.github.johantiden.dwarfactory.game.entities.factory.House;
import com.github.johantiden.dwarfactory.systems.physics.DragForce;
import com.github.johantiden.dwarfactory.util.JLists;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.github.johantiden.dwarfactory.game.BackgroundTile.TILE_SIZE;

public class Boi {
    public static final int RANGE_SQUARED = TILE_SIZE*TILE_SIZE/2/2;

    private static final int MAX_CARRY = 5;
    public static final int SIZE = 32;
    public static final int MAX_SPEED = 200;
    public static final float MOVE_FORCE = 1000;

    private final Entity boiEntity;
    private final House house;
    private ItemStack carrying = null;

    private final ComponentMapper<PositionComponent> positionMapper = ComponentMapper.getFor(PositionComponent.class);
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
                Optional<ItemConsumerComponent> maybeNewConsumer = chooseTarget(selectJobContext);
                return maybeNewConsumer
                        .map(this::deliveryJob)
                        .orElseGet(() -> {
                            // no one wants my stuff :( go home and wait
                            return goHomeAndDropResourcesThenWait();
                        });

            } else {
                Optional<ItemProducerComponent> maybeNewProducer = chooseSource(selectJobContext);
                return maybeNewProducer
                        .map(this::pickupJob)
                        .orElseGet(() -> {
                            // nothing to pick up.
                            return goHomeAndDropResourcesThenWait();
                        });
            }
        }

        private Job pickupJob(ItemProducerComponent source) {
            return new Job() {

                @Override
                public void finish() {
                    ImmutableItemStack biggestStack = source.getBiggestStack();
                    ItemStack newItemStack = source.output(biggestStack.itemType, MAX_CARRY);
                    if (newItemStack.getAmount() > 0) {
                        carrying = newItemStack;
                    } else {
                        carrying = null;
                    }
                }

                @Override
                public boolean canFinishJob() {
                    boolean isInRange = isInRange(source.hack_getEntity());
                    return isInRange && !source.getAvailableOutput().isEmpty();
                }

                @Override
                public Vector2 getWantedAcceleration(ForceContext forceContext) {
                    return getAccelerationTowards(getPosition(source.hack_getEntity()), getPosition(boiEntity));
                }

                @Override
                public boolean isJobFailed() {
                    boolean isInRange = isInRange(source.hack_getEntity());
                    return !isInRange;
                }
            };
        }

        private Job goHomeAndDropResourcesThenWait() {
            if (carrying != null) {
                return deliveryJob(getHomeConsumer());
            }

            if (!isHome()) {
                return goHomeJob();
            }

            return waitJob();
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

            eligibleReceivers.sort(
                    Comparator.comparing(ItemConsumerComponent::getPriority)
                            .thenComparing(p -> getPosition(p.hack_getEntity()), sortByProximityTo(house.getEntity())));

            return Optional.of(eligibleReceivers.get(0));
        }

        private Optional<ItemProducerComponent> chooseSource(SelectJobContext selectJobContext) {
            List<ItemProducerComponent> eligibleProducers = JLists.stream(selectJobContext.allItemProducers)
                    .filter(itemProducer -> !itemProducer.getAvailableOutput().isEmpty())
                    .collect(Collectors.toList());

            if (eligibleProducers.isEmpty()) {
                return Optional.empty();
            }

            eligibleProducers.sort(
                    Comparator.comparing(ItemProducerComponent::getPriority)
//                            .thenComparing(Comparator.<ItemProducerComponent, Integer>comparing(p -> Math.min(p.getBiggestStack().getAmount(), MAX_CARRY)).reversed())
                            .thenComparing(Comparator.<ItemProducerComponent, Boolean>comparing(itemProducerComponent -> itemProducerComponent.hasFullOutput()).reversed())

                            .thenComparing(p -> getPosition(p.hack_getEntity()), sortByProximityTo(boiEntity)));
//                            .thenComparing(p -> getPosition(p.hack_getEntity()), sortByProximityTo(house.getEntity())));


            return Optional.of(eligibleProducers.get(0));
        }


        private Comparator<PositionComponent> sortByProximityTo(Entity homeEntity) {
            return (a, b) -> {
                PositionComponent homePosition = getPosition(homeEntity);
                return Float.compare(
                        homePosition.cpy().sub(a).len(),
                        homePosition.cpy().sub(b).len()
                        );
            };
        }

        private Vector2 brake(ForceContext forceContext) {
            // brakes
            return forceContext.speedComponent.cpy().scl(-0.5f);
        }

        private boolean isInRange(Entity target) {
            float distanceSquared = getDistanceSquared(target, Boi.this.boiEntity);
            return distanceSquared < RANGE_SQUARED;
        }


        private Job goHomeJob() {
            return new Job() {

                @Override
                public boolean canFinishJob() {
                    return isInRange(house.getEntity());
                }

                @Override
                public Vector2 getWantedAcceleration(ForceContext forceContext) {
                    return getAccelerationTowards(getPosition(house.getEntity()), getPosition(boiEntity));
                }

                @Override
                public boolean isJobFailed() {
                    return false;
                }
            };
        }

        private boolean isHome() {
            return isInRange(house.getEntity());
        }

        private Job deliveryJob(ItemConsumerComponent target) {
            Objects.requireNonNull(target);
            return new Job() {

                @Override
                public boolean canFinishJob() {
                    return isInRange(target.hack_getEntity());
                }

                @Override
                public Vector2 getWantedAcceleration(ForceContext forceContext) {
                    return getAccelerationTowards(getPosition(target.hack_getEntity()), getPosition(boiEntity));
                }

                @Override
                public void finish() {
                    target.input(carrying);
                    if (carrying.getAmount() == 0) {
                        carrying = null;
                    }
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

    private ItemConsumerComponent getHomeConsumer() {
        return itemConsumerMapper.get(house.getEntity());
    }

    private static Vector2 getAccelerationTowards(PositionComponent target, PositionComponent position) {
        Objects.requireNonNull(target);
        Objects.requireNonNull(position);

        Vector2 targetDirectionVector = target.cpy()
                .sub(position)
                .setLength(1);

        return targetDirectionVector.scl(MOVE_FORCE);
    }
}

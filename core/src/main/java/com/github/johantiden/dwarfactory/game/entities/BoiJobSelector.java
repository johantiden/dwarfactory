package com.github.johantiden.dwarfactory.game.entities;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.github.johantiden.dwarfactory.components.ForceContext;
import com.github.johantiden.dwarfactory.components.ItemConsumerComponent;
import com.github.johantiden.dwarfactory.components.ItemProducerComponent;
import com.github.johantiden.dwarfactory.components.Job;
import com.github.johantiden.dwarfactory.components.PositionComponent;
import com.github.johantiden.dwarfactory.game.entities.factory.ItemType;
import com.github.johantiden.dwarfactory.util.JLists;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.github.johantiden.dwarfactory.game.entities.Boi.MAX_SPEED;

public class BoiJobSelector implements Supplier<Job> {

    private final SelectJobContext selectJobContext;
    private final Boi boi;

    private final ComponentMapper<PositionComponent> positionMapper = ComponentMapper.getFor(PositionComponent.class);

    public BoiJobSelector(SelectJobContext selectJobContext, Boi boi) {
        this.selectJobContext = selectJobContext;
        this.boi = boi;
    }

    private static Vector2 getSpeedTowards(PositionComponent target, PositionComponent position) {
        Objects.requireNonNull(target);
        Objects.requireNonNull(position);

        Vector2 targetDirectionVector = target.cpy()
                .sub(position)
                .setLength(1);

        return targetDirectionVector.scl(MAX_SPEED);
    }

    @Override
    public Job get() {
        if (boi.carrying != null) {
            Optional<ItemConsumerComponent> maybeNewConsumer = chooseTarget(selectJobContext, false);
            return maybeNewConsumer
                    .map(this::deliveryJob)
                    .orElseGet(() -> {
                        // no one wants my stuff :( go home and wait
                        return dropResourcesThenGoHomeAndWait();
                    });

        } else {
            Optional<ItemProducerComponent> maybeNewProducer = chooseSource(selectJobContext);
            return maybeNewProducer
                    .map(this::pickupJob)
                    .orElseGet(() -> {
                        // nothing to pick up.
                        return dropResourcesThenGoHomeAndWait();
                    });
        }
    }

    private Job pickupJob(ItemProducerComponent source) {
        return new Job() {

            @Override
            public void finish() {
                ImmutableItemStack biggestStack = source.getBiggestStack()
                        .orElseThrow(() -> new IllegalStateException("There are no stacks to pickup from!"));
                ItemStack newItemStack = source.output(biggestStack.itemType, Boi.MAX_CARRY);
                if (newItemStack.getAmount() > 0) {
                    boi.carrying = newItemStack;
                } else {
                    boi.carrying = null;
                }
            }

            @Override
            public boolean canFinishJob() {
                boolean isInRange = isInRange(source.hack_getEntity());
                return isInRange && !source.getAvailableOutput().isEmpty();
            }

            @Override
            public Vector2 getWantedSpeed() {
                return getSpeedTowards(getPosition(source.hack_getEntity()), getPosition(boi.boiEntity));
            }


            @Override
            public boolean isJobFailed() {
                boolean isInRange = isInRange(source.hack_getEntity());
                return !isInRange;
            }
        };
    }

    private PositionComponent getPosition(Entity entity) {
        return positionMapper.get(entity);
    }

    private Job dropResourcesThenGoHomeAndWait() {
        if (boi.carrying != null) {
            Optional<ItemConsumerComponent> backupConsumer = chooseTarget(selectJobContext, true);
            if (backupConsumer.isPresent()) {
                return deliveryJob(backupConsumer.get());
            }
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
            public Vector2 getWantedSpeed() {
                return new Vector2(0, 0);
            }
        };
    }

    private Optional<ItemConsumerComponent> chooseTarget(SelectJobContext selectJobContext, boolean anyPriority) {
        Predicate<ItemConsumerComponent> wants = itemConsumerComponent -> itemConsumerComponent.wants(boi.carrying.itemType);
        Predicate<ItemConsumerComponent> filter = anyPriority ? consumer -> consumer.canFitSome(boi.carrying.itemType) : wants;

        List<ItemConsumerComponent> eligibleReceivers = getFilteredConsumers(selectJobContext, filter);

        if (eligibleReceivers.isEmpty()) {
            return Optional.empty();
        }

        eligibleReceivers.sort(
                Comparator.comparing(ItemConsumerComponent::getPriority)
                        .thenComparing(Comparator.comparing(ItemConsumerComponent::hasEmptyInput).reversed())
                        .thenComparing(p -> getPosition(p.hack_getEntity()), sortByProximityTo(boi.boiEntity)));

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
                Comparator.comparing(anyoneWantsMyBiggestStack(selectJobContext, ItemConsumerComponent::wants)).reversed()
                        .thenComparing(ItemProducerComponent::getPriority)
//                            .thenComparing(Comparator.<ItemProducerComponent, Integer>comparing(p -> Math.min(p.getBiggestStack().getAmount(), MAX_CARRY)).reversed())
//                            .thenComparing(Comparator.<ItemProducerComponent, Boolean>comparing(ItemProducerComponent::hasFullOutput).reversed())

//                            .thenComparing(p -> getPosition(p.hack_getEntity()), sortByProximityTo(boiEntity)));
                        .thenComparing(p -> getPosition(p.hack_getEntity()), sortByProximityTo(boi.house.getEntity())));


        return Optional.of(eligibleProducers.get(0));
    }

    private Function<ItemProducerComponent, Boolean> anyoneWantsMyBiggestStack(SelectJobContext selectJobContext, BiPredicate<ItemConsumerComponent, ItemType> predicate) {
        return itemProducerComponent ->
                itemProducerComponent.getBiggestStack()
                        .map(biggestStack -> {
                            List<ItemConsumerComponent> eligibleReceivers = getFilteredConsumers(selectJobContext, itemConsumer -> predicate.test(itemConsumer, biggestStack.itemType));
                            return !eligibleReceivers.isEmpty();
                        })
                        .orElse(false);
    }

    private List<ItemConsumerComponent> getFilteredConsumers(SelectJobContext selectJobContext, Predicate<ItemConsumerComponent> filter) {
        return JLists.stream(selectJobContext.allItemConsumers)
                .filter(filter)
                .collect(Collectors.toList());
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
        float distanceSquared = getDistanceSquared(target, boi.boiEntity);
        return distanceSquared < Boi.RANGE_SQUARED;
    }

    private static float getDistanceSquared(PositionComponent a, PositionComponent b) {
        return a.cpy().sub(b).len2();
    }

    private float getDistanceSquared(Entity a, Entity b) {
        PositionComponent homePosition = getPosition(a);
        PositionComponent myPosition = getPosition(b);
        return getDistanceSquared(homePosition, myPosition);
    }

    private Job goHomeJob() {
        return new Job() {

            @Override
            public boolean canFinishJob() {
                return isInRange(boi.house.getEntity());
            }

            @Override
            public Vector2 getWantedSpeed() {
                return getSpeedTowards(getPosition(boi.house.getEntity()), getPosition(boi.boiEntity));
            }

            @Override
            public boolean isJobFailed() {
                return false;
            }
        };
    }

    private boolean isHome() {
        return isInRange(boi.house.getEntity());
    }

    private Job deliveryJob(ItemConsumerComponent target) {
        Objects.requireNonNull(target);
        return new Job() {

            @Override
            public boolean canFinishJob() {
                return isInRange(target.hack_getEntity());
            }

            @Override
            public Vector2 getWantedSpeed() {
                return getSpeedTowards(getPosition(target.hack_getEntity()), getPosition(boi.boiEntity));
            }

            @Override
            public void finish() {
                target.input(boi.carrying);
                if (boi.carrying.getAmount() == 0) {
                    boi.carrying = null;
                }
            }
        };
    }



}

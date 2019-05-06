package com.github.johantiden.dwarfactory.game.entities;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.github.czyzby.kiwi.util.tuple.immutable.Triple;
import com.github.johantiden.dwarfactory.components.ItemConsumerComponent;
import com.github.johantiden.dwarfactory.components.ItemProducerComponent;
import com.github.johantiden.dwarfactory.components.Job;
import com.github.johantiden.dwarfactory.components.PositionComponent;
import com.github.johantiden.dwarfactory.game.entities.factory.ItemType;
import com.github.johantiden.dwarfactory.systems.RenderHudSystem;
import com.github.johantiden.dwarfactory.util.JLists;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.github.johantiden.dwarfactory.game.entities.Boi.MAX_CARRY;
import static com.github.johantiden.dwarfactory.game.entities.Boi.MAX_SPEED;

public class BoiJobSelector {
    public static final int IDLE_TIME = 5000;

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

    public Collection<Job> get() {
        if (boi.carrying != null) {
            Optional<ItemConsumerComponent> maybeNewConsumer = chooseTarget(selectJobContext, boi.carrying.itemType, false);
            return maybeNewConsumer
                    .map(target -> JLists.newArrayList((Job)new DeliveryJob(target, boi.carrying)))
                    .orElseGet(() -> {
                        // no one wants my stuff :( go home and wait
                        return dropResourcesThenGoHomeAndWait();
                    });

        } else {
            Optional<Triple<ItemProducerComponent, ItemConsumerComponent, ItemStack>> maybeNewProducer = choosePickupAndDelivery(selectJobContext);
            return maybeNewProducer
                    .map(plan -> {
                        ItemProducerComponent producer = plan.getFirst();
                        ItemConsumerComponent consumer = plan.getSecond();
                        ItemStack mutableDeliveryStack = plan.getThird();
                        DeliveryJob deliveryJob = new DeliveryJob(consumer, mutableDeliveryStack);
                        PickupJob pickupJob = new PickupJob(producer, mutableDeliveryStack);
                        return JLists.newArrayList(pickupJob, deliveryJob);
                    })
                    .orElseGet(() -> {
                        // nothing to pick up.
                        return dropResourcesThenGoHomeAndWait();
                    });
        }
    }

    private PositionComponent getPosition(Entity entity) {
        return positionMapper.get(entity);
    }

    private List<Job> dropResourcesThenGoHomeAndWait() {
        if (boi.carrying != null) {
            Optional<ItemConsumerComponent> backupConsumer = chooseTarget(selectJobContext, boi.carrying.itemType, true);
            if (backupConsumer.isPresent()) {
                ItemConsumerComponent target = backupConsumer.get();
                Objects.requireNonNull(target);
                return JLists.newArrayList(new DeliveryJob(target, boi.carrying));
            }
        }

        long currentTimeMillis = System.currentTimeMillis();
        if (!isHome()) {
            return JLists.newArrayList(new GoHomeJob(currentTimeMillis), new IdleJob(currentTimeMillis));
        } else {
            return JLists.newArrayList((Job) new IdleJob(currentTimeMillis));
        }

    }


    private Optional<ItemConsumerComponent> chooseTarget(SelectJobContext selectJobContext, ItemType itemType, boolean anyPriority) {
        Predicate<ItemConsumerComponent> wants = itemConsumerComponent -> itemConsumerComponent.wants(itemType);
        Predicate<ItemConsumerComponent> filter = anyPriority ? consumer -> consumer.canFitSome(itemType) : wants;

        List<ItemConsumerComponent> eligibleReceivers = getFilteredConsumers(selectJobContext.allItemConsumers, filter);

        if (eligibleReceivers.isEmpty()) {
            return Optional.empty();
        }

        eligibleReceivers.sort(
                Comparator.comparing(ItemConsumerComponent::getPriority)
                        .thenComparing(Comparator.comparing(ItemConsumerComponent::hasEmptyInput).reversed())
                        .thenComparing(p -> getPosition(p.hack_getEntity()), sortByProximityTo(boi.boiEntity)));

        return Optional.of(eligibleReceivers.get(0));
    }

    public static ImmutableBag getAvailableOutput(ItemProducerComponent itemProducer) {
        return itemProducer.getOutputStacks()
                .filterByValue(stack -> {
                    ItemType itemType = stack.itemType;
                    int dibsSum = itemProducer.getBag().getDibsSum(itemType);
                    return stack.getAmount() + dibsSum > 0;
                });
    }

    private Optional<Triple<ItemProducerComponent, ItemConsumerComponent, ItemStack>> choosePickupAndDelivery(SelectJobContext selectJobContext) {
        List<ItemProducerComponent> eligibleProducers = JLists.stream(selectJobContext.allItemProducers)
                .filter(itemProducer -> !getAvailableOutput(itemProducer).isEmpty())
                .filter(anyoneWantsMyBiggestStack(selectJobContext, ItemConsumerComponent::wants))
                .collect(Collectors.toList());

        if (eligibleProducers.isEmpty()) {
            return Optional.empty();
        }

        eligibleProducers.sort(
                Comparator.comparing(ItemProducerComponent::getPriority)
//                            .thenComparing(Comparator.<ItemProducerComponent, Integer>comparing(p -> Math.min(p.getBiggestStack().getAmount(), MAX_CARRY)).reversed())
//                            .thenComparing(Comparator.<ItemProducerComponent, Boolean>comparing(ItemProducerComponent::hasFullOutput).reversed())

//                            .thenComparing(p -> getPosition(p.hack_getEntity()), sortByProximityTo(boiEntity)));
                        .thenComparing(p -> getPosition(p.hack_getEntity()), sortByProximityTo(boi.house.getEntity())));

        ItemProducerComponent itemProducerComponent = eligibleProducers.get(0);
        ImmutableItemStack targetStack = itemProducerComponent.getBiggestStack().get();
        ItemConsumerComponent itemConsumerComponent = chooseTarget(selectJobContext, targetStack.itemType, false)
                .orElseThrow(() -> new IllegalStateException("No one wants my stack but we just filtered on that :("));
        return Optional.of(new Triple<>(itemProducerComponent, itemConsumerComponent, new ItemStack(targetStack.itemType, MAX_CARRY)));
    }

    // TODO: We should choose here, which item stack to pickup. Return Optional
    private static Predicate<ItemProducerComponent> anyoneWantsMyBiggestStack(SelectJobContext selectJobContext, BiPredicate<ItemConsumerComponent, ItemType> predicate) {
        return itemProducerComponent ->
                itemProducerComponent.getBiggestStack()
                        .map(biggestStack -> {
                            List<ItemConsumerComponent> eligibleReceivers = getFilteredConsumers(selectJobContext.allItemConsumers, itemConsumer -> predicate.test(itemConsumer, biggestStack.itemType));
                            return !eligibleReceivers.isEmpty();
                        })
                        .orElse(false);
    }

    public static List<ItemConsumerComponent> getFilteredConsumers(Iterable<ItemConsumerComponent> allItemConsumers, Predicate<ItemConsumerComponent> filter) {
        return JLists.stream(allItemConsumers)
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

    private boolean isHome() {
        return isInRange(boi.house.getEntity());
    }

    private static class IdleJob implements Job {
        private final long startTimeMillis;

        private IdleJob(long startTimeMillis) {
            this.startTimeMillis = startTimeMillis;
        }

        @Override
        public boolean canFinishJob() {
            return System.currentTimeMillis() - startTimeMillis > IDLE_TIME;
        }

        @Override
        public Vector2 getWantedSpeed() {
            return new Vector2(0, 0);
        }

        @Override
        public Optional<Vector2> getTargetPosition() {
            return Optional.empty();
        }
    }


    private class DeliveryJob implements Job {

        private final ItemConsumerComponent target;
        private final ItemStack dibsItemStack; // The amount in here may change due to jobs before this one in the job queue.

        public DeliveryJob(ItemConsumerComponent target, ItemStack mutableDeliveryStack) {
            this.target = target;
            this.dibsItemStack = mutableDeliveryStack;
            target.getBag().addDibs(mutableDeliveryStack);
        }

        @Override
        public boolean canFinishJob() {
            return isInRange(target.hack_getEntity());
        }

        @Override
        public Vector2 getWantedSpeed() {
            return getSpeedTowards(getPosition(target.hack_getEntity()), getPosition(boi.boiEntity));
        }

        @Override
        public Optional<Vector2> getTargetPosition() {
            return Optional.of(getPosition(target.hack_getEntity()));
        }

        @Override
        public void finish() {
            target.input(boi.carrying);
            if (boi.carrying.getAmount() == 0) {
                boi.carrying = null;
            }
            target.getBag().removeDibs(dibsItemStack);
        }
    }

    private class GoHomeJob implements Job {
        private final long startTimeMillis;

        private GoHomeJob(long startTimeMillis) {
            this.startTimeMillis = startTimeMillis;
        }

        @Override
        public boolean canFinishJob() {
            return isHome() || System.currentTimeMillis() - startTimeMillis > IDLE_TIME;
        }

        @Override
        public Vector2 getWantedSpeed() {
            return getSpeedTowards(getHomePosition(), getPosition(boi.boiEntity));
        }

        @Override
        public boolean isJobFailed() {
            return false;
        }

        @Override
        public Optional<Vector2> getTargetPosition() {
            return Optional.of(getHomePosition());
        }
    }

    private PositionComponent getHomePosition() {
        return getPosition(boi.house.getEntity());
    }

    private class PickupJob implements Job {

        private final ItemProducerComponent target;
        private final ItemStack deliveryItemStack;
        private final ItemStack pickupItemStackNegative;

        public PickupJob(ItemProducerComponent target, ItemStack deliveryItemStack) {
            this.deliveryItemStack = deliveryItemStack;
            this.pickupItemStackNegative = deliveryItemStack.negate();
            RenderHudSystem.log("Starting PickupJob");
            this.target = target;
            target.getBag().addDibs(pickupItemStackNegative);
        }

        @Override
        public void finish() {
            ItemStack newItemStack = target.output(pickupItemStackNegative.itemType, MAX_CARRY);
            if (newItemStack.getAmount() > 0) {
                boi.carrying = newItemStack;
                RenderHudSystem.log("Finished PickupJob");
            } else {
                RenderHudSystem.log("Finished PickupJob but didn't pick up anything!");
                boi.carrying = null;
            }

            target.getBag().removeDibs(pickupItemStackNegative);
            deliveryItemStack.setAmount(newItemStack.getAmount()); // We used to promise to deliver MAX_CARRY but now we know the exact amount.
        }

        @Override
        public boolean canFinishJob() {
            boolean isInRange = isInRange(target.hack_getEntity());
            return isInRange && !target.getBag().getStack(pickupItemStackNegative.itemType).isEmpty();
        }

        @Override
        public Vector2 getWantedSpeed() {
            return getSpeedTowards(getPosition(target.hack_getEntity()), getPosition(boi.boiEntity));
        }


        @Override
        public boolean isJobFailed() {
            return target.getBiggestStack()
                    .map(biggestStack -> biggestStack.getAmount() == 0)
                    .orElse(true);
        }

        @Override
        public Optional<Vector2> getTargetPosition() {
            return Optional.of(getPosition(target.hack_getEntity()));
        }

        @Override
        public void fail() {
            target.getBag().removeDibs(pickupItemStackNegative);
        }
    }
}

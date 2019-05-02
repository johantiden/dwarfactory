package com.github.johantiden.dwarfactory.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.github.johantiden.dwarfactory.components.ControlComponent;
import com.github.johantiden.dwarfactory.components.ItemConsumerComponent;
import com.github.johantiden.dwarfactory.components.ItemProducerComponent;
import com.github.johantiden.dwarfactory.game.entities.SelectJobContext;
import com.github.johantiden.dwarfactory.util.JLists;

public class ControlSystem extends EntitySystem {

    private final ComponentMapper<ControlComponent> controlMapper = ComponentMapper.getFor(ControlComponent.class);
    private final ComponentMapper<ItemConsumerComponent> itemConsumerMapper = ComponentMapper.getFor(ItemConsumerComponent.class);
    private final ComponentMapper<ItemProducerComponent> itemProducerMapper = ComponentMapper.getFor(ItemProducerComponent.class);

    private ImmutableArray<Entity> entities;
    private ImmutableArray<Entity> itemConsumers;
    private ImmutableArray<Entity> itemProducers;

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(ControlComponent.class).get());
        itemConsumers = engine.getEntitiesFor(Family.all(ItemConsumerComponent.class).get());
        itemProducers = engine.getEntitiesFor(Family.all(ItemProducerComponent.class).get());
    }

    @Override
    public void removedFromEngine (Engine engine) {
        entities = new ImmutableArray<>(new Array<>());
        itemConsumers = new ImmutableArray<>(new Array<>());
    }

    @Override
    public void update(float deltaTime) {

        for (Entity entity : entities) {
            ControlComponent control = controlMapper.get(entity);

            if (control.hasJob()) {
                if (control.isJobFailed()) {
                    control.fail();
                }
            }
            if (control.hasJob()) {
                if (control.canFinishJob()) {
                    control.finishJob();
                }
            }
            if (!control.hasJob()) {
                ImmutableArray<ItemConsumerComponent> itemConsumerComponents = JLists.map(itemConsumerMapper::get, itemConsumers);
                ImmutableArray<ItemProducerComponent> allItemProducers = JLists.map(itemProducerMapper::get, itemProducers);
                control.trySelectNewJob(new SelectJobContext(itemConsumerComponents, allItemProducers));
            }
        }
    }
}

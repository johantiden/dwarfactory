package com.github.johantiden.dwarfactory.game.entities.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.github.johantiden.dwarfactory.components.PositionComponent;
import com.github.johantiden.dwarfactory.components.SizeComponent;
import com.github.johantiden.dwarfactory.components.TaskComponent;
import com.github.johantiden.dwarfactory.components.VisualComponent;
import com.github.johantiden.dwarfactory.game.TileCoordinate;
import com.github.johantiden.dwarfactory.game.assets.Assets;
import com.github.johantiden.dwarfactory.game.entities.Apple;
import com.github.johantiden.dwarfactory.game.entities.AppleStack;
import com.github.johantiden.dwarfactory.game.entities.ItemStack;
import com.github.johantiden.dwarfactory.game.entities.StringOverlayVisual;
import com.github.johantiden.dwarfactory.util.CoordinateUtil;

import java.util.Optional;

import static com.github.johantiden.dwarfactory.game.BackgroundTile.TILE_SIZE;

public class Factory implements ItemReceiver<Apple>, ItemProducer<Apple> {
    public static final int INPUT_COST_PER_TASK = 1;

    private final Entity entity;

    private final ItemStack<Apple> outputStack;
    private final ItemStack<Apple> inputStack;

    public Factory(Entity entity, int initialOutput, int initialInput) {
        this.entity = entity;
        this.outputStack = new AppleStack(initialOutput);
        this.inputStack = new AppleStack(initialInput);
    }

    public static Factory createFactory(TileCoordinate position, PooledEngine engine, int initialOutput, int initialInput) {
        Entity entity = engine.createEntity();
        Factory factory = new Factory(entity, initialOutput, initialInput);

        float tileBuildingInset = 5f;
        float factorySize = TILE_SIZE - tileBuildingInset * 2;

        Vector2 centerInWorld = CoordinateUtil.tileCenterToWorld(position);

        entity.add(new PositionComponent(centerInWorld.x, centerInWorld.y));
        entity.add(new SizeComponent(factorySize, factorySize));
        VisualComponent mainVisual = VisualComponent.createStatic(Assets.FACTORY);
        VisualComponent fullVisual = VisualComponent.blend(
                mainVisual,
                visualizeProgress(),
                visualizeOutput(factory),
                visualizeInput(factory));
        entity.add(fullVisual);
        entity.add(createTask(factory));
        engine.addEntity(entity);
        return factory;
    }

    private static TaskComponent createTask(Factory factory) {
        return new TaskComponent(
                3f,
                () -> {
                    factory.outputStack.add(1);
                    factory.inputStack.sub(INPUT_COST_PER_TASK);
                },
                () -> factory.inputStack.getAmount() >= INPUT_COST_PER_TASK
        );
    }

    private static VisualComponent visualizeProgress() {
        int progressHeight = 20;

        return new VisualComponent(
                renderContext -> {
                    SizeComponent size = renderContext.size;
                    PositionComponent position = renderContext.position;
                    TaskComponent task = renderContext.task;
                    float progress = task.getProgressRatio();
                    if (task.canRun()) {
                        renderContext.shapeRenderer.setColor(0, 1, 0, 1);
                    } else {
                        renderContext.shapeRenderer.setColor(1, 0, 0, 1);
                    }

                    if (progress > 0.001) {
                        renderContext.shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
                        renderContext.shapeRenderer.rect(
                                position.x - size.x / 2,
                                position.y + size.y /2 - progressHeight,
                                progress * size.x,
                                progressHeight
                        );
                    }
                }
        );
    }

    private static VisualComponent visualizeOutput(Factory factory) {
        return visualizeStack(factory.outputStack, new Color(0, 1, 0, 1), new Vector2(0, -TILE_SIZE/2));
    }

    private static VisualComponent visualizeInput(Factory factory) {
        return visualizeStack(factory.inputStack, new Color(0, 1, 1, 1), new Vector2(0, TILE_SIZE/2));
    }

    private static VisualComponent visualizeStack(ItemStack<Apple> itemStack, Color color, Vector2 offset) {
        return StringOverlayVisual.create(color, 50,
                offset, () -> {
                    if (itemStack.getAmount() > 0) {
                        return Optional.of(String.valueOf(itemStack.getAmount()));
                    } else {
                        return Optional.empty();
                    }
                });
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

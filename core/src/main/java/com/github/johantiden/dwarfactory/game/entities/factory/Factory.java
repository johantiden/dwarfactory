package com.github.johantiden.dwarfactory.game.entities.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.github.johantiden.dwarfactory.components.ItemConsumerComponent;
import com.github.johantiden.dwarfactory.components.ItemProducerComponent;
import com.github.johantiden.dwarfactory.components.PositionComponent;
import com.github.johantiden.dwarfactory.components.Priority;
import com.github.johantiden.dwarfactory.components.SizeComponent;
import com.github.johantiden.dwarfactory.components.TaskComponent;
import com.github.johantiden.dwarfactory.components.VisualComponent;
import com.github.johantiden.dwarfactory.game.TileCoordinate;
import com.github.johantiden.dwarfactory.game.assets.Assets;
import com.github.johantiden.dwarfactory.game.entities.EntityRenderer;
import com.github.johantiden.dwarfactory.game.entities.ImmutableItemStack;
import com.github.johantiden.dwarfactory.game.entities.RenderContext;
import com.github.johantiden.dwarfactory.util.CoordinateUtil;

import java.util.List;

import static com.github.johantiden.dwarfactory.game.BackgroundTile.TILE_SIZE;

public class Factory {
    public static final int FONT_SIZE = 30;

    private final Recipe recipe;

    public Factory(Recipe recipe) {
        this.recipe = recipe;
    }

    public static void createFactory(TileCoordinate position, PooledEngine engine, float initialProgress, Recipe recipe) {
        Entity entity = engine.createEntity();
        Factory factory = new Factory(recipe);

        float factorySize = TILE_SIZE*3 - Assets.TILE_BUILDING_INSET * 2;

        Vector2 centerInWorld = CoordinateUtil.tileCenterToWorld(position);

        entity.add(new PositionComponent(centerInWorld.x, centerInWorld.y));
        entity.add(new SizeComponent(factorySize, factorySize));
        VisualComponent mainVisual = VisualComponent.createStatic(Assets.FACTORY);

        entity.add(createTask(factory, initialProgress));
        List<ItemType> resultTypes = recipe.getResultTypes();
        ItemProducerComponent itemProducerComponent = ItemProducerComponent.createFiltered(entity, resultTypes, Priority.NORMAL);
        entity.add(itemProducerComponent);

        List<ItemType> ingredientTypes = recipe.getIngredientTypes();
        ItemConsumerComponent itemConsumerComponent = ItemConsumerComponent.createFiltered(entity, ingredientTypes, Priority.NORMAL);
        entity.add(itemConsumerComponent);

        VisualComponent fullVisual = VisualComponent.blend(
                mainVisual,
                visualizeProgress());
        entity.add(fullVisual);
        engine.addEntity(entity);
    }

    private static TaskComponent createTask(Factory factory, float initialProgress) {
        return new TaskComponent(
                factory.recipe.time,
                initialProgress,
                taskContext -> {
                    for (ImmutableItemStack result : factory.recipe.result) {
                        taskContext.itemProducer.input(result);
                    }

                    for (ImmutableItemStack ingredient : factory.recipe.ingredients) {
                        taskContext.itemConsumer.output(ingredient);
                    }
                },
                taskContext -> factory.recipe.ingredients.stream()
                        .allMatch(ingredient ->
                                taskContext.itemConsumer.getSnapshot(ingredient.itemType).getAmount() >= ingredient.getAmount())
                    &&
                        factory.recipe.result.stream()
                            .allMatch(taskContext.itemProducer::canFitFully)
        );
    }

    private static VisualComponent visualizeProgress() {
        int progressHeight = 20;

        return new VisualComponent(
                new EntityRenderer() {
                    @Override
                    public void renderShapes(ShapeRenderer shapeRenderer, RenderContext renderContext) {
                        SizeComponent size = renderContext.size;
                        PositionComponent position = renderContext.position;
                        TaskComponent task = renderContext.task;
                        float progress = task.getProgressRatio();
                        shapeRenderer.setColor(0, 1, 0, 1);

                        if (progress > 0.001) {
                            shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
                            shapeRenderer.rect(
                                    position.x - size.x / 2,
                                    position.y + size.y / 2 - progressHeight,
                                    progress * size.x,
                                    progressHeight
                            );
                        }
                    }
                });
    }

}

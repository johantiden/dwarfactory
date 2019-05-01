package com.github.johantiden.dwarfactory.game.entities.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.github.johantiden.dwarfactory.components.ItemProducerComponent;
import com.github.johantiden.dwarfactory.components.ItemConsumerComponent;
import com.github.johantiden.dwarfactory.components.PositionComponent;
import com.github.johantiden.dwarfactory.components.SizeComponent;
import com.github.johantiden.dwarfactory.components.TaskComponent;
import com.github.johantiden.dwarfactory.components.VisualComponent;
import com.github.johantiden.dwarfactory.game.TileCoordinate;
import com.github.johantiden.dwarfactory.game.assets.Assets;
import com.github.johantiden.dwarfactory.game.entities.Boi;
import com.github.johantiden.dwarfactory.game.entities.EntityRenderer;
import com.github.johantiden.dwarfactory.game.entities.ImmutableItemStack;
import com.github.johantiden.dwarfactory.game.entities.ItemStack;
import com.github.johantiden.dwarfactory.game.entities.RenderContext;
import com.github.johantiden.dwarfactory.game.entities.StringOverlayVisual;
import com.github.johantiden.dwarfactory.game.entities.VisualEmpty;
import com.github.johantiden.dwarfactory.util.CoordinateUtil;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.github.johantiden.dwarfactory.game.BackgroundTile.TILE_SIZE;

public class Factory {
    public static final int FONT_SIZE = 30;

    private final Recipe recipe;

    public Factory(Recipe recipe) {
        this.recipe = recipe;
    }

    public static void createFactory(TileCoordinate position, PooledEngine engine, int initialOutput, int initialInput, Recipe recipe, double numBois) {
        Entity entity = engine.createEntity();
        Factory factory = new Factory(recipe);

        float tileBuildingInset = 5f;
        float factorySize = TILE_SIZE - tileBuildingInset * 2;

        Vector2 centerInWorld = CoordinateUtil.tileCenterToWorld(position);

        entity.add(new PositionComponent(centerInWorld.x, centerInWorld.y));
        entity.add(new SizeComponent(factorySize, factorySize));
        VisualComponent mainVisual = VisualComponent.createStatic(Assets.FACTORY);

        entity.add(createTask(factory));
        List<ItemType> resultTypes = recipe.getResultTypes();
        ItemProducerComponent itemProducerComponent = new ItemProducerComponent(entity, resultTypes);
        entity.add(itemProducerComponent);

        List<ItemType> ingredientTypes = recipe.getIngredientTypes();
        ItemConsumerComponent itemConsumerComponent = new ItemConsumerComponent(entity, ingredientTypes);
        entity.add(itemConsumerComponent);

        VisualComponent fullVisual = VisualComponent.blend(
                mainVisual,
                visualizeProgress(),
                visualizeStacks(itemProducerComponent.getAvailableOutput(), new Color(0, 1, 0, 1), new Vector2(0, -TILE_SIZE / 2 + FONT_SIZE)),
                visualizeStacks(itemConsumerComponent.getCurrentBuffer(), new Color(0, 1, 1, 1), new Vector2(-TILE_SIZE / 2, -TILE_SIZE / 2 + FONT_SIZE)));
        entity.add(fullVisual);
        engine.addEntity(entity);

        for (int i = 0; i < numBois; i++) {
            Boi.createBoi(centerInWorld.x, centerInWorld.y, 0, 0, engine, itemProducerComponent);
        }
    }

    private static TaskComponent createTask(Factory factory) {
        return new TaskComponent(
                factory.recipe.time,
                taskContext -> {
                    for (ImmutableItemStack result : factory.recipe.result) {
                        taskContext.itemProducer.add(result);
                    }

                    for (ImmutableItemStack ingredient : factory.recipe.ingredients) {
                        taskContext.itemConsumerComponent.sub(ingredient);
                    }
                },
                taskContext -> factory.recipe.ingredients.stream()
                        .allMatch(ingredient ->
                                taskContext.itemConsumerComponent.get(ingredient.itemType).getAmount() >= ingredient.getAmount())
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

    private static VisualComponent visualizeStacks(Collection<ItemStack> itemStacks, Color color, Vector2 offset) {
        return itemStacks.stream()
                .filter(itemStack -> itemStack.getAmount() > 0)
                .map(itemStack -> StringOverlayVisual.create(color, FONT_SIZE,
                        offset, () -> Optional.of(String.valueOf(itemStack.getAmount()))))
                .reduce((a, b) -> VisualComponent.blend(a, b))
                .orElse(VisualEmpty.create());
    }

}

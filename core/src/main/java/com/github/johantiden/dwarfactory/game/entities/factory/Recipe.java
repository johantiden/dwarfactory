package com.github.johantiden.dwarfactory.game.entities.factory;

import com.github.johantiden.dwarfactory.game.entities.ImmutableItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class Recipe {

    public final List<ImmutableItemStack> ingredients;
    public final List<ImmutableItemStack> result;
    public final float time;

    public Recipe(List<ImmutableItemStack> ingredients, List<ImmutableItemStack> result, float time) {
        this.ingredients = ingredients;
        this.result = result;
        this.time = time;
    }

    public List<ItemType> getResultTypes() {
        return result.stream()
                .map(ImmutableItemStack::getItemType)
                .collect(Collectors.toList());
    }

    public List<ItemType> getIngredientTypes() {
        return ingredients.stream()
                .map(ImmutableItemStack::getItemType)
                .collect(Collectors.toList());
    }
}

package com.github.johantiden.dwarfactory.game.entities;

public class AppleStack extends ItemStack<Apple> {
    public AppleStack(int amount) {super(amount);}

    @Override
    public ItemStack<Apple> createNew(int amount) {
        return new AppleStack(amount);
    }
}

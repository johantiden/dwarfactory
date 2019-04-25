package com.github.johantiden.dwarfactory.game.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.github.johantiden.dwarfactory.Dwarfactory;
import com.github.johantiden.dwarfactory.components.PositionComponent;
import com.github.johantiden.dwarfactory.components.SizeComponent;
import com.github.johantiden.dwarfactory.components.SpeedComponent;
import com.github.johantiden.dwarfactory.components.VisualComponent;
import com.github.johantiden.dwarfactory.game.assets.Assets;

import java.security.SecureRandom;
import java.util.Random;

import static com.badlogic.gdx.math.MathUtils.random;

public class Boi {
    public static Entity createBoi(float x, float y, float speedX, float speedY, PooledEngine engine) {
        Entity boi = engine.createEntity();
        boi.add(new PositionComponent(x, y));
        boi.add(new SpeedComponent(speedX, speedY));
        boi.add(VisualComponent.create4Angles(
                Assets.Boi.DOWN,
                Assets.Boi.UP,
                Assets.Boi.LEFT,
                Assets.Boi.RIGHT));
        boi.add(new SizeComponent(32, 32));
        return boi;
    }


    public static void createRandomBoi(PooledEngine engine) {
        Random random = new SecureRandom();

        float x = random((float)Dwarfactory.VIEWPORT_WIDTH);
        float y = random((float)Dwarfactory.VIEWPORT_HEIGHT);
        float speedX = random.nextFloat() * 30 - 15;
        float speedY = random.nextFloat() * 30 - 15;

        Entity boi = Boi.createBoi(x, y, speedX, speedY, engine);
        engine.addEntity(boi);
    }

    public static void createRandomBois(int numBois, PooledEngine engine) {
        for (int i = 0; i < numBois; i++) {
            createRandomBoi(engine);
        }
    }
}

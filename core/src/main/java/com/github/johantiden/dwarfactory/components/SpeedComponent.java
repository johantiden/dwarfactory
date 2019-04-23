package com.github.johantiden.dwarfactory.components;

import com.badlogic.ashley.core.Component;

public class SpeedComponent implements Component {
    public float speedX;
    public float speedY;

    public SpeedComponent(float speedX, float speedY) {
        this.speedX = speedX;
        this.speedY = speedY;
    }
}

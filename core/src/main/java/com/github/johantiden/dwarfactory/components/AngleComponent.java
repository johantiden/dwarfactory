package com.github.johantiden.dwarfactory.components;

import com.badlogic.ashley.core.Component;

public class AngleComponent implements Component {
    public float angle;

    public AngleComponent(float angle) {
        this.angle = angle;
    }
}

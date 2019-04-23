package com.github.johantiden.dwarfactory.components;

import com.badlogic.ashley.core.Component;

public class AngularSpeedComponent implements Component {
    public float angularSpeed;

    public AngularSpeedComponent(float angularSpeed) {
        this.angularSpeed = angularSpeed;
    }
}

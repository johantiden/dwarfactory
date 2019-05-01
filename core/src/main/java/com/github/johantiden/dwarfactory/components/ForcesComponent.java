package com.github.johantiden.dwarfactory.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.github.johantiden.dwarfactory.systems.physics.Force;

import java.util.List;
import java.util.stream.Collectors;

public class ForcesComponent implements Component {

    private final List<Force> forces;

    public ForcesComponent(List<Force> forces) {
        this.forces = forces;
    }

    public List<Vector2> getForces(ForceContext forceContext) {
        return forces.stream()
                .map(f -> f.getForce(forceContext))
                .collect(Collectors.toList());
    }
}

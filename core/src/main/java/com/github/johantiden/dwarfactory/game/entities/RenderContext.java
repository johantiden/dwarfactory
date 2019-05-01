package com.github.johantiden.dwarfactory.game.entities;

import com.github.johantiden.dwarfactory.components.PositionComponent;
import com.github.johantiden.dwarfactory.components.SizeComponent;
import com.github.johantiden.dwarfactory.components.SpeedComponent;
import com.github.johantiden.dwarfactory.components.TaskComponent;

public class RenderContext {
    public final SpeedComponent speed;
    public final PositionComponent position;
    public final SizeComponent size;
    public final TaskComponent task;

    public RenderContext(SpeedComponent speed, PositionComponent position, SizeComponent size, TaskComponent task) {
        this.speed = speed;
        this.position = position;
        this.size = size;
        this.task = task;
    }
}

package com.github.johantiden.dwarfactory.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.github.johantiden.dwarfactory.components.PositionComponent;
import com.github.johantiden.dwarfactory.components.SizeComponent;
import com.github.johantiden.dwarfactory.components.SpeedComponent;

public class RenderContext {
    public final SpriteBatch batch;
    public final SpeedComponent speed;
    public final PositionComponent position;
    public final SizeComponent size;

    public RenderContext(SpriteBatch batch, SpeedComponent speed, PositionComponent position, SizeComponent size) {
        this.batch = batch;
        this.speed = speed;
        this.position = position;
        this.size = size;
    }
}

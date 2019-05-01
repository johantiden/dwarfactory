package com.github.johantiden.dwarfactory.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.johantiden.dwarfactory.components.PositionComponent;
import com.github.johantiden.dwarfactory.components.SizeComponent;
import com.github.johantiden.dwarfactory.components.SpeedComponent;
import com.github.johantiden.dwarfactory.components.TaskComponent;

public class RenderContext {
    public final SpriteBatch spriteBatch;
    public final ShapeRenderer shapeRenderer;
    public final ShapeRenderer debugShapeRenderer;
    public final SpeedComponent speed;
    public final PositionComponent position;
    public final SizeComponent size;
    public final TaskComponent task;

    public RenderContext(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, ShapeRenderer debugShapeRenderer, SpeedComponent speed, PositionComponent position, SizeComponent size, TaskComponent task) {
        this.spriteBatch = spriteBatch;
        this.shapeRenderer = shapeRenderer;
        this.debugShapeRenderer = debugShapeRenderer;
        this.speed = speed;
        this.position = position;
        this.size = size;
        this.task = task;
    }
}

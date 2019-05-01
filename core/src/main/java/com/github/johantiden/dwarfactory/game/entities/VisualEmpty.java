package com.github.johantiden.dwarfactory.game.entities;

import com.github.johantiden.dwarfactory.components.VisualComponent;

public class VisualEmpty {
    public static VisualComponent create() {
        return new VisualComponent(
                new EntityRenderer() {
                }
        );
    }
}

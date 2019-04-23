package com.github.johantiden.dwarfactory.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class VisualComponent implements Component {
    public TextureRegion region;

    public VisualComponent (TextureRegion region) {
        this.region = region;
    }
}
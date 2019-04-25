package com.github.johantiden.dwarfactory.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class VisualComponent implements Component {
    private final TextureRegion textureRegion;

    public VisualComponent(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    public VisualComponent(Texture texture) {
        textureRegion = new TextureRegion(texture);
    }

    public TextureRegion getTexture() {
        return textureRegion;
    }
}
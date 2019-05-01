package com.github.johantiden.dwarfactory.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.github.johantiden.dwarfactory.game.entities.EntityRenderer;
import com.github.johantiden.dwarfactory.game.entities.RenderContext;

import java.util.Arrays;

public final class VisualComponent implements Component {

    private final EntityRenderer renderer;

    public VisualComponent(EntityRenderer renderer) {
        this.renderer = renderer;
    }

    public static VisualComponent createStatic(TextureRegion texture) {
        return new VisualComponent(new EntityRenderer() {
            @Override
            public void renderSprites(SpriteBatch spriteBatch, RenderContext renderContext) {
                drawSimple(spriteBatch, renderContext, texture);
            }
        });
    }

    public static VisualComponent blend(VisualComponent... visualComponents) {
        return new VisualComponent(new EntityRenderer() {
            @Override
            public void renderSprites(SpriteBatch spriteBatch, RenderContext renderContext) {
                Arrays.stream(visualComponents)
                        .forEach(visualComponent -> visualComponent.renderSprites(spriteBatch, renderContext));
            }

            @Override
            public void renderShapes(ShapeRenderer shapeRenderer, RenderContext renderContext) {
                Arrays.stream(visualComponents)
                        .forEach(visualComponent -> visualComponent.renderShapes(shapeRenderer, renderContext));
            }

            @Override
            public void renderSpritesUnprojected(SpriteBatch screenBatch, RenderContext renderContext) {
                Arrays.stream(visualComponents)
                        .forEach(visualComponent -> visualComponent.renderSpritesUnprojected(screenBatch, renderContext));
            }

            @Override
            public void debugRenderShapes(ShapeRenderer debugShapeRenderer, RenderContext renderContext) {
                Arrays.stream(visualComponents)
                        .forEach(visualComponent -> visualComponent.debugRenderShapes(debugShapeRenderer, renderContext));
            }
        });
    }

    public static VisualComponent create4Angles(
            TextureRegion down,
            TextureRegion up,
            TextureRegion left,
            TextureRegion right) {
        return new VisualComponent(
                new EntityRenderer() {
                    @Override
                    public void renderSprites(SpriteBatch spriteBatch, RenderContext renderContext) {
                        TextureRegion textureRegion = chooseTextureFromAngle(renderContext.speed,
                                down, up, left, right);
                        drawSimple(spriteBatch, renderContext, textureRegion);
                    }
                }
        );
    }

    public static TextureRegion chooseTextureFromAngle(
            Vector2 vector,
            TextureRegion down,
            TextureRegion up,
            TextureRegion left,
            TextureRegion right) {
        double angle = vector.angle();

        if (angle >= 225 && angle <= 315) {
            return up;
        }

        if (angle >= 45 && angle <= 135) {
            return down;
        }

        if (angle >= 135 && angle <= 225) {
            return left;
        }

        return right;

    }

    private static void drawSimple(SpriteBatch spriteBatch, RenderContext renderContext, TextureRegion textureRegion) {
        float width = renderContext.size.x;
        float height = renderContext.size.y;
        float x = renderContext.position.x - width / 2;
        float y = renderContext.position.y - height / 2;
        spriteBatch.draw(textureRegion, x, y, width, height);
    }

    /// These delegates are here instead of inheritance since inheritance of Components is forbidden
    public void renderSprites(SpriteBatch spriteBatch, RenderContext renderContext) {
        renderer.renderSprites(spriteBatch, renderContext);
    }

    /// These delegates are here instead of inheritance since inheritance of Components is forbidden
    public void renderShapes(ShapeRenderer shapeRenderer, RenderContext renderContext) {
        renderer.renderShapes(shapeRenderer, renderContext);
    }

    /// These delegates are here instead of inheritance since inheritance of Components is forbidden
    public void renderSpritesUnprojected(SpriteBatch screenBatch, RenderContext renderContext) {
        renderer.renderSpritesUnprojected(screenBatch, renderContext);
    }

    /// These delegates are here instead of inheritance since inheritance of Components is forbidden
    public void debugRenderShapes(ShapeRenderer debugShapeRenderer, RenderContext renderContext) {
        renderer.debugRenderShapes(debugShapeRenderer, renderContext);
    }
}
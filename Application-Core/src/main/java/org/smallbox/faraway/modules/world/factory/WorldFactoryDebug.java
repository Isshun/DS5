package org.smallbox.faraway.modules.world.factory;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseLayer;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameLayerInit;

@GameObject
@GameLayer(level = 999, visible = true)
public class WorldFactoryDebug extends BaseLayer {

    private Pixmap pixmap;
    private int offset;

    @OnGameLayerInit
    private void init() {
        pixmap = new Pixmap(50 * 10, 100 * 10, Pixmap.Format.RGBA8888);
    }

    public void drawPixel(int x, int y, int color) {
        pixmap.drawPixel(offset + x, y, color);
    }

    public void next() {
        offset += 50;
    }

    public Pixmap getPixmap() {
        return pixmap;
    }

    @Override
    protected void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        Sprite sprite = new Sprite(new Texture(pixmap));
        sprite.setPosition(100, 0);
        sprite.setScale(1);
        renderer.drawUI(sprite);
    }
}

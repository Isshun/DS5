package org.smallbox.faraway.game.world.factory;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import org.smallbox.faraway.client.asset.AssetManager;
import org.smallbox.faraway.client.layer.BaseLayer;
import org.smallbox.faraway.client.layer.GameLayer;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnDestroy;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnInit;

@GameObject
@GameLayer(level = 999, visible = true)
public class WorldFactoryDebug extends BaseLayer {
    @Inject private AssetManager assetManager;

    private Texture texture;
    private Pixmap pixmap;
    private int offset;

    @OnInit
    private void init() {
        pixmap = assetManager.createPixmap(50 * 10, 100 * 10, Pixmap.Format.RGBA8888);
    }

    @OnDestroy
    private void gameStop() {
        pixmap.dispose();
    }

    public void drawPixel(int x, int y, int color) {
        pixmap.drawPixel(offset + x, y, color);
    }

    public void next() {
        if (texture != null) {
            texture.dispose();
        }

        offset += 50;
        texture = new Texture(pixmap);
    }

    public Pixmap getPixmap() {
        return pixmap;
    }

    @Override
    protected void onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
//        renderer.draw(texture, 100, 0);
    }
}

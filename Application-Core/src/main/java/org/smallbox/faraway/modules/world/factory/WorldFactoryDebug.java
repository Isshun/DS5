package org.smallbox.faraway.modules.world.factory;

import com.badlogic.gdx.graphics.Pixmap;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;

@ApplicationObject
public class WorldFactoryDebug {

    private Pixmap pixmap;
    private int offset;

    @OnInit
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
}

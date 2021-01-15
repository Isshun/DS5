package org.smallbox.faraway.client;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.util.log.Log;

import java.util.function.Consumer;

@ApplicationObject
public class AssetManager extends com.badlogic.gdx.assets.AssetManager {

    public <T> T lazyLoad(String key, Class<T> cls) {
        if (!contains(key)) {
            load(key, cls);
            finishLoading();
            Log.warning("Lazy load: " + key);
        }

        return get(key);
    }

    public void temporaryPixmap(Texture texture, Consumer<Pixmap> pixmapConsumer) {
        texture.getTextureData().prepare();
        Pixmap pixmap = texture.getTextureData().consumePixmap();
        pixmapConsumer.accept(pixmap);
        pixmap.dispose();
        texture.dispose();
    }

    public void temporaryPixmap(int width, int height, Pixmap.Format format, Consumer<Pixmap> pixmapConsumer) {
        Pixmap pixmap = new Pixmap(width, height, format);
        pixmapConsumer.accept(pixmap);
        pixmap.dispose();
    }

}

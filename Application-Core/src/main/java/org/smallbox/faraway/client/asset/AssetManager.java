package org.smallbox.faraway.client.asset;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.util.log.Log;

import java.util.UUID;
import java.util.function.Consumer;

@ApplicationObject
public class AssetManager extends com.badlogic.gdx.assets.AssetManager {

    public <T> T lazyLoad(String key, Class<T> cls) {
        return lazyLoad(key, cls, null);
    }

    public <T> T lazyLoad(String key, Class<T> cls, Consumer<T> initializationSupplier) {
        if (!contains(key)) {
            load(key, cls);
            finishLoading();
            if (initializationSupplier != null) {
                initializationSupplier.accept(get(key));
            }
            Log.warning("Lazy load: " + key);
        }

        return get(key);
    }

    public void temporaryPixmap(String absolutePath, Consumer<Pixmap> pixmapConsumer) {
        Log.debug("Create temporary pixmap");

        Texture texture = lazyLoad(absolutePath, Texture.class);
        texture.getTextureData().prepare();
        Pixmap pixmap = texture.getTextureData().consumePixmap();
        texture.getTextureData().disposePixmap();
        pixmapConsumer.accept(pixmap);
        pixmap.dispose();
    }

    public Texture createTextureFromPixmap(int width, int height, Pixmap.Format format, Consumer<Pixmap> pixmapConsumer) {
        Pixmap pixmap = new Pixmap(width, height, format);
        pixmapConsumer.accept(pixmap);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        addAsset(UUID.randomUUID().toString(), Texture.class, texture);
        return texture;
    }

    public Texture createTextureFromPixmap(String key, int width, int height, Pixmap.Format format, Consumer<Pixmap> pixmapConsumer) {
        Pixmap pixmap = new Pixmap(width, height, format);
        pixmapConsumer.accept(pixmap);
        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();
        addAsset(key, Texture.class, texture);
        return texture;
    }

    public Pixmap createPixmapFromTexture(String absolutePath) {
        Texture texture = lazyLoad(absolutePath, Texture.class);
        texture.getTextureData().prepare();
        Pixmap pixmap = texture.getTextureData().consumePixmap();
        texture.getTextureData().disposePixmap();
        texture.dispose();
        addAsset(UUID.randomUUID().toString(), Pixmap.class, pixmap);
        return pixmap;
    }

    public Pixmap createPixmap(int width, int height, Pixmap.Format format) {
        Pixmap pixmap = new Pixmap(width, height, format);
        addAsset(UUID.randomUUID().toString(), Pixmap.class, pixmap);
        return pixmap;
    }
}

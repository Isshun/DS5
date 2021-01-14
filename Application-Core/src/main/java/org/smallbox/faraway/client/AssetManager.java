package org.smallbox.faraway.client;

import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.util.log.Log;

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

}

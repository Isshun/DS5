package org.smallbox.faraway.module.extra;

import org.smallbox.faraway.core.engine.module.ModuleObserver;
import org.smallbox.faraway.core.module.world.model.NetworkItem;

/**
 * Created by Alex on 19/07/2016.
 */
public interface NetworkModuleObserver extends ModuleObserver {
    void onAddNetworkObject(NetworkItem networkObject);
    void onRemoveNetworkObject(NetworkItem networkObject);
}

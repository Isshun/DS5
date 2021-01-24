package org.smallbox.faraway.module.extra;

import org.smallbox.faraway.core.engine.module.ModuleObserver;
import org.smallbox.faraway.core.world.model.NetworkItem;

public interface NetworkModuleObserver extends ModuleObserver {
    void onAddNetworkObject(NetworkItem networkObject);
    void onRemoveNetworkObject(NetworkItem networkObject);
}

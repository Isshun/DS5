package org.smallbox.faraway.module.extra;

import org.smallbox.faraway.core.engine.module.ModuleObserver;
import org.smallbox.faraway.core.game.module.world.model.NetworkObjectModel;

/**
 * Created by Alex on 19/07/2016.
 */
public interface NetworkModuleObserver extends ModuleObserver {
    void onAddNetworkObject(NetworkObjectModel networkObject);
    void onRemoveNetworkObject(NetworkObjectModel networkObject);
}

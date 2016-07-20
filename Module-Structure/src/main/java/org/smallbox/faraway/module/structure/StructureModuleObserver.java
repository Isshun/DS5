package org.smallbox.faraway.module.structure;

import org.smallbox.faraway.core.engine.module.ModuleObserver;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.StructureModel;

/**
 * Created by Alex on 19/07/2016.
 */
public interface StructureModuleObserver extends ModuleObserver {
    void onAddStructure(StructureModel structure);
    void onRemoveStructure(ParcelModel parcel, StructureModel structure);
    void onStructureComplete(StructureModel structure);
}

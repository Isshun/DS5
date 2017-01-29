package org.smallbox.faraway.modules.structure;

import org.smallbox.faraway.core.engine.module.ModuleObserver;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.world.model.StructureItem;

/**
 * Created by Alex on 19/07/2016.
 */
public interface StructureModuleObserver extends ModuleObserver {
    default void onAddStructure(StructureItem structure) {}
    default void onRemoveStructure(ParcelModel parcel, StructureItem structure) {}
    default void onStructureComplete(StructureItem structure) {}

    default void onSelectStructure(StructureItem structure) {}
    default void onDeselectStructure(StructureItem lastStructure) {}
}

package org.smallbox.faraway.modules.structure;

import org.smallbox.faraway.core.engine.module.ModuleObserver;
import org.smallbox.faraway.core.module.world.model.Parcel;
import org.smallbox.faraway.core.module.world.model.StructureItem;

public interface StructureModuleObserver extends ModuleObserver {
    default void onAddStructure(StructureItem structure) {}
    default void onRemoveStructure(Parcel parcel, StructureItem structure) {}
    default void onStructureComplete(StructureItem structure) {}

    default void onSelectStructure(StructureItem structure) {}
    default void onDeselectStructure(StructureItem lastStructure) {}
}

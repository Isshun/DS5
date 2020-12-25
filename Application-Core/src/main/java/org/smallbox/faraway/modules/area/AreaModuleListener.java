package org.smallbox.faraway.modules.area;

import org.smallbox.faraway.core.module.world.model.ParcelModel;

public interface AreaModuleListener {
    void onRemoveParcel(ParcelModel parcel);
    boolean hasArea(ParcelModel parcel);
    void select(ParcelModel parcel);
}

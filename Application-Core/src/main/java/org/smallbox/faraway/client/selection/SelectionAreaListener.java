package org.smallbox.faraway.client.selection;

import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.area.AreaModel;

public interface SelectionAreaListener {
    void onSelectParcel(ParcelModel parcel);
    void onSelectArea(AreaModel area);
}

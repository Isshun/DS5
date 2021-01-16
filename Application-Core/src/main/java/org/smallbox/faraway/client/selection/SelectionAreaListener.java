package org.smallbox.faraway.client.selection;

import org.smallbox.faraway.core.module.world.model.Parcel;
import org.smallbox.faraway.modules.area.AreaModel;

public interface SelectionAreaListener {
    void onSelectParcel(Parcel parcel);
    void onSelectArea(AreaModel area);
}

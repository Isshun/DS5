package org.smallbox.faraway.client.selection;

import org.smallbox.faraway.game.area.AreaModel;
import org.smallbox.faraway.game.world.Parcel;

public interface SelectionAreaListener {
    void onSelectParcel(Parcel parcel);
    void onSelectArea(AreaModel area);
}

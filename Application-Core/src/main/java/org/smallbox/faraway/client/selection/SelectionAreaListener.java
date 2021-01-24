package org.smallbox.faraway.client.selection;

import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.area.AreaModel;

public interface SelectionAreaListener {
    void onSelectParcel(Parcel parcel);
    void onSelectArea(AreaModel area);
}

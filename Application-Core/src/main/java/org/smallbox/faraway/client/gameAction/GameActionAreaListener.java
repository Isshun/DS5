package org.smallbox.faraway.client.gameAction;

import org.smallbox.faraway.core.module.world.model.Parcel;

public interface GameActionAreaListener {
    void removeArea(Parcel parcel);
//    void selectArea(ParcelModel parcel);
    boolean hasArea(Parcel parcel);
}

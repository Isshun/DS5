package org.smallbox.faraway.modules.area;

import org.smallbox.faraway.core.module.world.model.ParcelModel;

public interface GameActionAreaListener {
    void removeArea(ParcelModel parcel);
    void selectArea(ParcelModel parcel);
    boolean hasArea(ParcelModel parcel);
}

package org.smallbox.faraway.client.gameAction;

import org.smallbox.faraway.game.world.Parcel;

public interface GameActionAreaListener {
    void removeArea(Parcel parcel);
//    void selectArea(ParcelModel parcel);
    boolean hasArea(Parcel parcel);
}

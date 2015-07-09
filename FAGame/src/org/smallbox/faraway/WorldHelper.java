package org.smallbox.faraway;

import org.smallbox.faraway.game.model.item.ParcelModel;

/**
 * Created by Alex on 09/07/2015.
 */
public class WorldHelper {
    public static boolean isSurroundedByRock(ParcelModel parcel) {
        if (parcel._neighbors[0] != null && (parcel._neighbors[0].getResource() == null || !parcel._neighbors[0].getResource().isRock())) return false;
        if (parcel._neighbors[1] != null && (parcel._neighbors[1].getResource() == null || !parcel._neighbors[1].getResource().isRock())) return false;
        if (parcel._neighbors[2] != null && (parcel._neighbors[2].getResource() == null || !parcel._neighbors[2].getResource().isRock())) return false;
        if (parcel._neighbors[3] != null && (parcel._neighbors[3].getResource() == null || !parcel._neighbors[3].getResource().isRock())) return false;
        return true;
    }
}

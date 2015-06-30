package org.smallbox.faraway.util;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.WorldManager;
import org.smallbox.faraway.game.model.item.ParcelModel;

/**
 * Created by Alex on 29/06/2015.
 */
public class MapUtils {

    public static boolean isSurroundedByBlocked(ParcelModel toParcel) {
        return isSurroundedByBlocked(toParcel.getX(), toParcel.getY());
    }

    public static boolean isSurroundedByBlocked(int x, int y) {
        WorldManager worldManager = Game.getWorldManager();

        if (!worldManager.isBlocked(x + 1, y)) return false;
        if (!worldManager.isBlocked(x - 1, y)) return false;
        if (!worldManager.isBlocked(x, y + 1)) return false;
        if (!worldManager.isBlocked(x, y - 1)) return false;

        return true;
    }

}

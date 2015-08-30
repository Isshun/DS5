package org.smallbox.faraway.engine.lua;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.item.ParcelModel;

/**
 * Created by Alex on 20/06/2015.
 */
public class LuaMapModel {
    public LuaValue getDropLocation() {
        ParcelModel[][][] parcels = Game.getWorldManager().getParcels();
        int width = Game.getInstance().getInfo().worldWidth;
        int height = Game.getInstance().getInfo().worldHeight;

        int startX = (int) (Math.random() * width);
        int startY = (int) (Math.random() * height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                ParcelModel parcel = parcels[(x + startX) % width][(y + startY) % height][0];
                if (parcel.getRoom() != null && !parcel.getRoom().isExterior()) {
                    break;
                }
                if (parcel.getStructure() != null && parcel.getStructure().isSolid()) {
                    break;
                }
                if (parcel.getResource() != null && parcel.getResource().isSolid()) {
                    break;
                }
                if (parcel.getItem() != null) {
                    break;
                }
                if (parcel.getConsumable() != null) {
                    break;
                }

                // Return free space
                LuaValue ret = new LuaTable();
                ret.set("x", LuaValue.valueOf(parcel.x));
                ret.set("y", LuaValue.valueOf(parcel.y));
                return ret;
            }
        }
        return null;
    }
}

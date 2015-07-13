package org.smallbox.faraway;

import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.item.*;

/**
 * Created by Alex on 09/07/2015.
 */
public class WorldHelper {
    public static ParcelModel[][][]     _parcels;
    private static int                  _width;
    private static int                  _height;

    public static void init(ParcelModel[][][] parcels) {
        _parcels = parcels;
        _width = _parcels.length;
        _height = _parcels[0].length;
    }

    public static ItemModel         getItem(int x, int y) { return getItem(x, y, 0); }
    public static ItemModel         getItem(int x, int y, int z) { return inMapBounds(x, y) ? _parcels[x][y][z].getItem() : null; }
    public static ConsumableModel   getConsumable(int x, int y) { return getConsumable(x, y, 0); }
    public static ConsumableModel   getConsumable(int x, int y, int z) { return inMapBounds(x, y) ? _parcels[x][y][z].getConsumable() : null; }
    public static StructureModel    getStructure(int x, int y) { return getStructure(x, y, 0); }
    public static StructureModel    getStructure(int x, int y, int z) { return inMapBounds(x, y) ? _parcels[x][y][z].getStructure() : null; }
    public static ResourceModel     getResource(int x, int y) { return getResource(x, y, 0); }
    public static ResourceModel     getResource(int x, int y, int z) { return inMapBounds(x, y) ? _parcels[x][y][z].getResource() : null; }

    public static boolean isSurroundedByRock(ParcelModel parcel) {
        if (parcel._neighbors[0] != null && (parcel._neighbors[0].getResource() == null || !parcel._neighbors[0].getResource().isRock())) return false;
        if (parcel._neighbors[1] != null && (parcel._neighbors[1].getResource() == null || !parcel._neighbors[1].getResource().isRock())) return false;
        if (parcel._neighbors[2] != null && (parcel._neighbors[2].getResource() == null || !parcel._neighbors[2].getResource().isRock())) return false;
        if (parcel._neighbors[3] != null && (parcel._neighbors[3].getResource() == null || !parcel._neighbors[3].getResource().isRock())) return false;
        return true;
    }

    /**
     * Search for area free to receive a ConsumableItem
     *
     * @param itemInfo
     * @param x
     * @param y
     * @return nearest free area
     */
    public static ParcelModel getNearestFreeArea(ItemInfo itemInfo, int x, int y, int quantity) {
        if (itemInfo.isConsumable) {
            for (int d = 0; d < 8; d++) {
                for (int i = 0; i <= d; i++) {
                    if (areaFreeForConsumable(x + i, y + d, itemInfo, quantity)) return _parcels[x + i][y + d][0];
                    if (areaFreeForConsumable(x + i, y - d, itemInfo, quantity)) return _parcels[x + i][y - d][0];
                    if (areaFreeForConsumable(x + d, y + i, itemInfo, quantity)) return _parcels[x + d][y + i][0];
                    if (areaFreeForConsumable(x - d, y + i, itemInfo, quantity)) return _parcels[x - d][y + i][0];

                    if (areaFreeForConsumable(x - i, y + d, itemInfo, quantity)) return _parcels[x - i][y + d][0];
                    if (areaFreeForConsumable(x - i, y - d, itemInfo, quantity)) return _parcels[x - i][y - d][0];
                    if (areaFreeForConsumable(x + d, y - i, itemInfo, quantity)) return _parcels[x + d][y - i][0];
                    if (areaFreeForConsumable(x - d, y - i, itemInfo, quantity)) return _parcels[x - d][y - i][0];
                }
            }
        }
        return null;
    }

    /**
     * Check if current area is free for consumable
     *
     * @param x
     * @param y
     * @param info
     * @return
     */
    private static boolean areaFreeForConsumable(int x, int y, ItemInfo info, int quantity) {
        if (inMapBounds(x, y)) {
            ParcelModel area = _parcels[x][y][0];
            if (area.getStructure() == null || area.getStructure().isFloor()) {
                return area.getItem() == null && (area.getConsumable() == null || (area.getConsumable().getInfo() == info && area.getConsumable().getQuantity() + quantity <= Math.max(GameData.config.storageMaxQuantity, area.getConsumable().getInfo().stack)));
            }
        }
        return false;
    }

    /**
     * Check if position is in map bounds
     *
     * @param x
     * @param y
     * @return true if position in bounds
     */
    public static boolean inMapBounds(int x, int y) {
        return !(x < 0 || y < 0 || x >= _width || y >= _height);
    }

    public static ParcelModel getNearestFreeSpace(int x, int y, boolean acceptInterior, boolean acceptExterior) {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < i; j++) {
                // Top
                if (isFreeSpace(x + j, y + i, acceptInterior, acceptExterior)) return _parcels[x + j][y + i][0];
                if (isFreeSpace(x - j, y + i, acceptInterior, acceptExterior)) return _parcels[x - j][y + i][0];

                // Bottom
                if (isFreeSpace(x + j, y - i, acceptInterior, acceptExterior)) return _parcels[x + j][y - i][0];
                if (isFreeSpace(x - j, y - i, acceptInterior, acceptExterior)) return _parcels[x - j][y - i][0];

                // Right
                if (isFreeSpace(x + i, y + j, acceptInterior, acceptExterior)) return _parcels[x + i][y + j][0];
                if (isFreeSpace(x + i, y - j, acceptInterior, acceptExterior)) return _parcels[x + i][y + j][0];

                // Left
                if (isFreeSpace(x - i, y + j, acceptInterior, acceptExterior)) return _parcels[x - i][y + j][0];
                if (isFreeSpace(x - i, y - j, acceptInterior, acceptExterior)) return _parcels[x - i][y - j][0];
            }
        }
        return null;
    }

    private static boolean isFreeSpace(int x, int y, boolean acceptInterior, boolean acceptExterior) {
        if (!WorldHelper.inMapBounds(x, y)) {
            return false;
        }
        if (!acceptInterior && _parcels[x][y][0].getRoom() != null && !_parcels[x][y][0].getRoom().isExterior()) {
            return false;
        }
        if (!acceptExterior && (_parcels[x][y][0].getRoom() == null || _parcels[x][y][0].getRoom().isExterior())) {
            return false;
        }
        if (_parcels[x][y][0].getStructure() != null && _parcels[x][y][0].getStructure().isSolid()) {
            return false;
        }
        if (_parcels[x][y][0].getResource() != null) {
            return false;
        }
        if (_parcels[x][y][0].getItem() != null) {
            return false;
        }
        if (_parcels[x][y][0].getConsumable() != null) {
            return false;
        }
        return true;
    }

    public static ParcelModel getRandomFreeSpace(boolean acceptInterior, boolean acceptExterior) {
        int startX = (int) (Math.random() * _width);
        int startY = (int) (Math.random() * _height);
        for (int i = 0; i < _width; i++) {
            for (int j = 0; j < _height; j++) {
                if (isFreeSpace((startX + i) % _width, (startY + j) % _height, acceptInterior, acceptExterior)) {
                    return _parcels[(startX + i) % _width][(startY + j) % _height][0];
                }
            }
        }
        return null;
    }

    public static boolean isBlocked(int x, int y) {
        if (inMapBounds(x, y)) {
            return _parcels[x][y][0] != null && _parcels[x][y][0].isBlocked();
        }
        return true;
    }


    public static boolean isSurroundedByBlocked(ParcelModel toParcel) {
        return isSurroundedByBlocked(toParcel.x, toParcel.y);
    }

    public static boolean isSurroundedByBlocked(int x, int y) {
        if (!isBlocked(x + 1, y)) return false;
        if (!isBlocked(x - 1, y)) return false;
        if (!isBlocked(x, y + 1)) return false;
        if (!isBlocked(x, y - 1)) return false;
        return true;
    }

    public static ParcelModel getParcel(int x, int y) {
        if (inMapBounds(x, y)) {
            return _parcels[x][y][0];
        }
        return null;
    }

    public static int getApproxDistance(ParcelModel p1, ParcelModel p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }
}

package org.smallbox.faraway.core.game.helper;

import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.module.world.model.*;
import org.smallbox.faraway.core.module.java.ModuleHelper;

/**
 * Created by Alex on 09/07/2015.
 */
public class WorldHelper {
    public static int                  currentFloor;
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

        if (parcel._neighbors[4] != null && (parcel._neighbors[4].getResource() == null || !parcel._neighbors[4].getResource().isRock())) return false;
        if (parcel._neighbors[5] != null && (parcel._neighbors[5].getResource() == null || !parcel._neighbors[5].getResource().isRock())) return false;
        if (parcel._neighbors[6] != null && (parcel._neighbors[6].getResource() == null || !parcel._neighbors[6].getResource().isRock())) return false;
        if (parcel._neighbors[7] != null && (parcel._neighbors[7].getResource() == null || !parcel._neighbors[7].getResource().isRock())) return false;

        return true;
    }

    /**
     * Search for model free to receive a ConsumableItem
     *
     * @param parcel
     * @param itemInfo
     * @return nearest free model
     */
    public static ParcelModel getNearestFreeArea(ParcelModel parcel, ItemInfo itemInfo, int quantity) {
        if (parcel != null && itemInfo.isConsumable) {
            int x = parcel.x;
            int y = parcel.y;
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
     * Check if current model is free for consumable
     *
     * @param x
     * @param y
     * @param info
     * @return
     */
    public static boolean areaFreeForConsumable(int x, int y, ItemInfo info, int quantity) {
        if (!inMapBounds(x, y)) {
            return false;
        }

        ParcelModel parcel = _parcels[x][y][0];
        if (parcel.getStructure() != null && !parcel.getStructure().isWalkable()) {
            return false;
        }

        if (parcel.getResource() != null && parcel.getResource().isSolid()) {
            return false;
        }

        if (parcel.getItem() != null && !parcel.getItem().isStorageParcel(parcel)) {
            return false;
        }

        if (parcel.getConsumable() != null && (parcel.getConsumable().getInfo() != info || parcel.getConsumable().getQuantity() + quantity > Math.max(GameData.config.storageMaxQuantity, parcel.getConsumable().getInfo().stack))) {
            return false;
        }

        return true;
    }

    /**
     * Check if position is in old bounds
     *
     * @param x
     * @param y
     * @return true if position in bounds
     */
    public static boolean inMapBounds(int x, int y) {
        return !(x < 0 || y < 0 || x >= _width || y >= _height);
    }

    public static boolean inMapBounds(int x, int y, int z) {
        return !(x < 0 || y < 0 || x >= _width || y >= _height);
    }

    public static ParcelModel getNearestFreeParcel(int x, int y, boolean acceptInterior, boolean acceptExterior) {
        return getNearestFreeParcel(WorldHelper.getParcel(x, y), acceptInterior, acceptExterior);
    }

    public static ParcelModel getNearestFreeParcel(ParcelModel parcel, boolean acceptInterior, boolean acceptExterior) {
        int x = parcel.x;
        int y = parcel.y;
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

    public static ParcelModel getNearestWalkable(int x, int y, boolean acceptInterior, boolean acceptExterior) {
        return getNearestWalkable(x, y, acceptInterior, acceptExterior, 0, 20);
    }
    public static ParcelModel getNearestWalkable(int x, int y, boolean acceptInterior, boolean acceptExterior, int minDistance, int maxDistance) {
        for (int i = minDistance; i <= maxDistance; i++) {
            for (int j = 0; j < i; j++) {
                // Top
                if (isWalkableParcel(x + j, y + i, acceptInterior, acceptExterior)) return _parcels[x + j][y + i][0];
                if (isWalkableParcel(x - j, y + i, acceptInterior, acceptExterior)) return _parcels[x - j][y + i][0];

                // Bottom
                if (isWalkableParcel(x + j, y - i, acceptInterior, acceptExterior)) return _parcels[x + j][y - i][0];
                if (isWalkableParcel(x - j, y - i, acceptInterior, acceptExterior)) return _parcels[x - j][y - i][0];

                // Right
                if (isWalkableParcel(x + i, y + j, acceptInterior, acceptExterior)) return _parcels[x + i][y + j][0];
                if (isWalkableParcel(x + i, y - j, acceptInterior, acceptExterior)) return _parcels[x + i][y + j][0];

                // Left
                if (isWalkableParcel(x - i, y + j, acceptInterior, acceptExterior)) return _parcels[x - i][y + j][0];
                if (isWalkableParcel(x - i, y - j, acceptInterior, acceptExterior)) return _parcels[x - i][y - j][0];
            }
        }
        return null;
    }

    private static boolean isWalkableParcel(int x, int y, boolean acceptInterior, boolean acceptExterior) {
        if (!WorldHelper.inMapBounds(x, y)) {
            return false;
        }
        if (!acceptInterior && _parcels[x][y][0].getRoom() != null && !_parcels[x][y][0].getRoom().isExterior()) {
            return false;
        }
        if (!acceptExterior && (_parcels[x][y][0].getRoom() == null || _parcels[x][y][0].getRoom().isExterior())) {
            return false;
        }
        if (_parcels[x][y][0].getStructure() != null && !_parcels[x][y][0].getStructure().isWalkable()) {
            return false;
        }
        if (_parcels[x][y][0].getResource() != null && !_parcels[x][y][0].getResource().isWalkable()) {
            return false;
        }
//        if (_parcels[x][y][0].getItem() != null) {
//            return false;
//        }
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
            return _parcels[x][y][0] != null && !_parcels[x][y][0].isWalkable();
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
            return _parcels[x][y][currentFloor];
        }
        return null;
    }

    public static ParcelModel getParcel(int x, int y, int z) {
        if (inMapBounds(x, y, z)) {
            return _parcels[x][y][z];
        }
        return null;
    }

    public static int getApproxDistance(ParcelModel p1, ParcelModel p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }

    public static ParcelModel getNearest(int x, int y, boolean allowExterior, boolean allowInterior, boolean allowCharacter, boolean allowStructure, boolean allowItem, boolean allowConsumable, boolean allowResource) {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < i; j++) {
                // Top
                if (checkParcel(x + j, y + i, allowExterior, allowInterior, allowCharacter, allowStructure, allowItem, allowConsumable, allowResource)) return _parcels[x + j][y + i][0];
                if (checkParcel(x - j, y + i, allowExterior, allowInterior, allowCharacter, allowStructure, allowItem, allowConsumable, allowResource)) return _parcels[x - j][y + i][0];

                // Bottom
                if (checkParcel(x + j, y - i, allowExterior, allowInterior, allowCharacter, allowStructure, allowItem, allowConsumable, allowResource)) return _parcels[x + j][y - i][0];
                if (checkParcel(x - j, y - i, allowExterior, allowInterior, allowCharacter, allowStructure, allowItem, allowConsumable, allowResource)) return _parcels[x - j][y - i][0];

                // Right
                if (checkParcel(x + i, y + j, allowExterior, allowInterior, allowCharacter, allowStructure, allowItem, allowConsumable, allowResource)) return _parcels[x + i][y + j][0];
                if (checkParcel(x + i, y - j, allowExterior, allowInterior, allowCharacter, allowStructure, allowItem, allowConsumable, allowResource)) return _parcels[x + i][y + j][0];

                // Left
                if (checkParcel(x - i, y + j, allowExterior, allowInterior, allowCharacter, allowStructure, allowItem, allowConsumable, allowResource)) return _parcels[x - i][y + j][0];
                if (checkParcel(x - i, y - j, allowExterior, allowInterior, allowCharacter, allowStructure, allowItem, allowConsumable, allowResource)) return _parcels[x - i][y - j][0];
            }
        }
        return null;
    }

    private static boolean checkParcel(int x, int y, boolean allowExterior, boolean allowInterior, boolean allowCharacter, boolean allowStructure, boolean allowItem, boolean allowConsumable, boolean allowResource) {
        if (!WorldHelper.inMapBounds(x, y)) {
            return false;
        }
        if (!allowInterior && _parcels[x][y][0].getRoom() != null && !_parcels[x][y][0].getRoom().isExterior()) {
            return false;
        }
        if (!allowExterior && (_parcels[x][y][0].getRoom() == null || _parcels[x][y][0].getRoom().isExterior())) {
            return false;
        }
        if (!allowStructure && _parcels[x][y][0].getStructure() != null && _parcels[x][y][0].getStructure().isSolid()) {
            return false;
        }
        if (!allowResource && _parcels[x][y][0].getResource() != null) {
            return false;
        }
        if (!allowItem && _parcels[x][y][0].getItem() != null) {
            return false;
        }
        if (!allowConsumable && _parcels[x][y][0].getConsumable() != null) {
            return false;
        }
        if (!allowCharacter && ModuleHelper.getCharacterModule().countCharacterAtPos(x, y) > 0) {
            return false;
        }
        return true;
    }

}

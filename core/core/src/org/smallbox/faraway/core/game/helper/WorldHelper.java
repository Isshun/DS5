package org.smallbox.faraway.core.game.helper;

import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.character.model.PathModel;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.StructureModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.game.module.world.model.resource.PlantModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;

/**
 * Created by Alex on 09/07/2015.
 */
public class WorldHelper {
    public static int                   _currentFloor = 9;
    public static ParcelModel[][][]     _parcels;
    private static int                  _width;
    private static int                  _height;
    private static int                  _floors;

    public static void init(ParcelModel[][][] parcels) {
        _parcels = parcels;
        _width = _parcels.length;
        _height = _parcels[_currentFloor].length;
        _floors = _parcels[_currentFloor][_currentFloor].length;
    }

    public static ItemModel         getItem(int x, int y) { return getItem(x, y, 0); }
    public static ItemModel         getItem(int x, int y, int z) { return inMapBounds(x, y) ? _parcels[x][y][z].getItem() : null; }
    public static ConsumableModel   getConsumable(int x, int y) { return getConsumable(x, y, 0); }
    public static ConsumableModel   getConsumable(int x, int y, int z) { return inMapBounds(x, y) ? _parcels[x][y][z].getConsumable() : null; }
    public static StructureModel    getStructure(int x, int y) { return getStructure(x, y, 0); }
    public static StructureModel    getStructure(int x, int y, int z) { return inMapBounds(x, y) ? _parcels[x][y][z].getStructure() : null; }
    public static PlantModel getResource(int x, int y) { return getResource(x, y, 0); }
    public static PlantModel getResource(int x, int y, int z) { return inMapBounds(x, y) ? _parcels[x][y][z].getPlant() : null; }
    public static ItemInfo          getResourceInfo(int x, int y, int z) { return inMapBounds(x, y) && _parcels[x][y][z].getPlant() != null ? _parcels[x][y][z].getPlant().getInfo() : null; }
    public static ItemInfo          getRockInfo(int x, int y, int z) { return inMapBounds(x, y) ? _parcels[x][y][z].getRockInfo() : null; }

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
                    if (areaFreeForConsumable(x + i, y + d, itemInfo, quantity)) return _parcels[x + i][y + d][_currentFloor];
                    if (areaFreeForConsumable(x + i, y - d, itemInfo, quantity)) return _parcels[x + i][y - d][_currentFloor];
                    if (areaFreeForConsumable(x + d, y + i, itemInfo, quantity)) return _parcels[x + d][y + i][_currentFloor];
                    if (areaFreeForConsumable(x - d, y + i, itemInfo, quantity)) return _parcels[x - d][y + i][_currentFloor];

                    if (areaFreeForConsumable(x - i, y + d, itemInfo, quantity)) return _parcels[x - i][y + d][_currentFloor];
                    if (areaFreeForConsumable(x - i, y - d, itemInfo, quantity)) return _parcels[x - i][y - d][_currentFloor];
                    if (areaFreeForConsumable(x + d, y - i, itemInfo, quantity)) return _parcels[x + d][y - i][_currentFloor];
                    if (areaFreeForConsumable(x - d, y - i, itemInfo, quantity)) return _parcels[x - d][y - i][_currentFloor];
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

        ParcelModel parcel = _parcels[x][y][_currentFloor];
        if (parcel.getStructure() != null && !parcel.getStructure().isWalkable()) {
            return false;
        }

        if (parcel.hasPlant() && parcel.getPlant().isSolid()) {
            return false;
        }

        if (parcel.getItem() != null && !parcel.getItem().isStorageParcel(parcel)) {
            return false;
        }

        if (parcel.getConsumable() != null && (parcel.getConsumable().getInfo() != info || parcel.getConsumable().getQuantity() + quantity > Math.max(Data.config.storageMaxQuantity, parcel.getConsumable().getInfo().stack))) {
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
        return !(x < 0 || y < 0 || z < 0 || x >= _width || y >= _height || z >= _floors);
    }

    public static ParcelModel getNearestFreeParcel(int x, int y, boolean acceptInterior, boolean acceptExterior) {
        return getNearestFreeParcel(WorldHelper.getParcel(x, y), acceptInterior, acceptExterior);
    }

    // TODO: Use spiral pattern
    public static ParcelModel getNearestFreeParcel(ParcelModel parcel, boolean acceptInterior, boolean acceptExterior) {
        int x = parcel.x;
        int y = parcel.y;
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < i; j++) {
                // Top
                if (isFreeSpace(x + j, y + i, acceptInterior, acceptExterior)) return _parcels[x + j][y + i][_currentFloor];
                if (isFreeSpace(x - j, y + i, acceptInterior, acceptExterior)) return _parcels[x - j][y + i][_currentFloor];

                // Bottom
                if (isFreeSpace(x + j, y - i, acceptInterior, acceptExterior)) return _parcels[x + j][y - i][_currentFloor];
                if (isFreeSpace(x - j, y - i, acceptInterior, acceptExterior)) return _parcels[x - j][y - i][_currentFloor];

                // Right
                if (isFreeSpace(x + i, y + j, acceptInterior, acceptExterior)) return _parcels[x + i][y + j][_currentFloor];
                if (isFreeSpace(x + i, y - j, acceptInterior, acceptExterior)) return _parcels[x + i][y + j][_currentFloor];

                // Left
                if (isFreeSpace(x - i, y + j, acceptInterior, acceptExterior)) return _parcels[x - i][y + j][_currentFloor];
                if (isFreeSpace(x - i, y - j, acceptInterior, acceptExterior)) return _parcels[x - i][y - j][_currentFloor];
            }
        }
        return null;
    }

    private static boolean isFreeSpace(int x, int y, boolean acceptInterior, boolean acceptExterior) {
        if (!WorldHelper.inMapBounds(x, y)) {
            return false;
        }
        if (!acceptInterior && _parcels[x][y][_currentFloor].getRoom() != null && !_parcels[x][y][_currentFloor].getRoom().isExterior()) {
            return false;
        }
        if (!acceptExterior && (_parcels[x][y][_currentFloor].getRoom() == null || _parcels[x][y][_currentFloor].getRoom().isExterior())) {
            return false;
        }
        if (_parcels[x][y][_currentFloor].getStructure() != null && _parcels[x][y][_currentFloor].getStructure().isSolid()) {
            return false;
        }
        if (_parcels[x][y][_currentFloor].hasPlant()) {
            return false;
        }
        if (_parcels[x][y][_currentFloor].getItem() != null) {
            return false;
        }
        if (_parcels[x][y][_currentFloor].getConsumable() != null) {
            return false;
        }
        return true;
    }

    public static ParcelModel getNearestWalkable(ParcelModel parcel, int minDistance, int maxDistance) {
        return getNearestWalkable(parcel.x, parcel.y, true, true, minDistance, maxDistance);
    }

    public static ParcelModel getNearestWalkable(int x, int y, boolean acceptInterior, boolean acceptExterior) {
        return getNearestWalkable(x, y, acceptInterior, acceptExterior, 0, 20);
    }

    public static ParcelModel getNearestWalkable(int x, int y, boolean acceptInterior, boolean acceptExterior, int minDistance, int maxDistance) {
        for (int i = minDistance; i <= maxDistance; i++) {
            for (int j = 0; j < i; j++) {
                // Top
                if (isWalkableParcel(x + j, y + i, acceptInterior, acceptExterior)) return _parcels[x + j][y + i][_currentFloor];
                if (isWalkableParcel(x - j, y + i, acceptInterior, acceptExterior)) return _parcels[x - j][y + i][_currentFloor];

                // Bottom
                if (isWalkableParcel(x + j, y - i, acceptInterior, acceptExterior)) return _parcels[x + j][y - i][_currentFloor];
                if (isWalkableParcel(x - j, y - i, acceptInterior, acceptExterior)) return _parcels[x - j][y - i][_currentFloor];

                // Right
                if (isWalkableParcel(x + i, y + j, acceptInterior, acceptExterior)) return _parcels[x + i][y + j][_currentFloor];
                if (isWalkableParcel(x + i, y - j, acceptInterior, acceptExterior)) return _parcels[x + i][y + j][_currentFloor];

                // Left
                if (isWalkableParcel(x - i, y + j, acceptInterior, acceptExterior)) return _parcels[x - i][y + j][_currentFloor];
                if (isWalkableParcel(x - i, y - j, acceptInterior, acceptExterior)) return _parcels[x - i][y - j][_currentFloor];
            }
        }
        return null;
    }

    private static boolean isWalkableParcel(int x, int y, boolean acceptInterior, boolean acceptExterior) {
        if (!WorldHelper.inMapBounds(x, y)) {
            return false;
        }
        if (!acceptInterior && _parcels[x][y][_currentFloor].getRoom() != null && !_parcels[x][y][_currentFloor].getRoom().isExterior()) {
            return false;
        }
        if (!acceptExterior && (_parcels[x][y][_currentFloor].getRoom() == null || _parcels[x][y][_currentFloor].getRoom().isExterior())) {
            return false;
        }
        if (_parcels[x][y][_currentFloor].getStructure() != null && !_parcels[x][y][_currentFloor].getStructure().isWalkable()) {
            return false;
        }
        if (_parcels[x][y][_currentFloor].hasPlant() && !_parcels[x][y][_currentFloor].getPlant().isWalkable()) {
            return false;
        }
//        if (_parcels[x][y][_currentFloor].getItem() != null) {
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
                    return _parcels[(startX + i) % _width][(startY + j) % _height][_currentFloor];
                }
            }
        }
        return null;
    }

    public static boolean isBlocked(int x, int y) {
        if (inMapBounds(x, y)) {
            return _parcels[x][y][_currentFloor] != null && !_parcels[x][y][_currentFloor].isWalkable();
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
            return _parcels[x][y][_currentFloor];
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

    public static int getDistance(ParcelModel p1, ParcelModel p2) {
        PathModel path = PathManager.getInstance().getPath(p1, p2, true, false);
        if (path != null) {
            return path.getLength();
        }
        return -1;
    }

    public static ParcelModel getNearest(int x, int y, boolean allowExterior, boolean allowInterior, boolean allowCharacter, boolean allowStructure, boolean allowItem, boolean allowConsumable, boolean allowResource) {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < i; j++) {
                // Top
                if (checkParcel(x + j, y + i, allowExterior, allowInterior, allowCharacter, allowStructure, allowItem, allowConsumable, allowResource)) return _parcels[x + j][y + i][_currentFloor];
                if (checkParcel(x - j, y + i, allowExterior, allowInterior, allowCharacter, allowStructure, allowItem, allowConsumable, allowResource)) return _parcels[x - j][y + i][_currentFloor];

                // Bottom
                if (checkParcel(x + j, y - i, allowExterior, allowInterior, allowCharacter, allowStructure, allowItem, allowConsumable, allowResource)) return _parcels[x + j][y - i][_currentFloor];
                if (checkParcel(x - j, y - i, allowExterior, allowInterior, allowCharacter, allowStructure, allowItem, allowConsumable, allowResource)) return _parcels[x - j][y - i][_currentFloor];

                // Right
                if (checkParcel(x + i, y + j, allowExterior, allowInterior, allowCharacter, allowStructure, allowItem, allowConsumable, allowResource)) return _parcels[x + i][y + j][_currentFloor];
                if (checkParcel(x + i, y - j, allowExterior, allowInterior, allowCharacter, allowStructure, allowItem, allowConsumable, allowResource)) return _parcels[x + i][y + j][_currentFloor];

                // Left
                if (checkParcel(x - i, y + j, allowExterior, allowInterior, allowCharacter, allowStructure, allowItem, allowConsumable, allowResource)) return _parcels[x - i][y + j][_currentFloor];
                if (checkParcel(x - i, y - j, allowExterior, allowInterior, allowCharacter, allowStructure, allowItem, allowConsumable, allowResource)) return _parcels[x - i][y - j][_currentFloor];
            }
        }
        return null;
    }

    private static boolean checkParcel(int x, int y, boolean allowExterior, boolean allowInterior, boolean allowCharacter, boolean allowStructure, boolean allowItem, boolean allowConsumable, boolean allowResource) {
        if (!WorldHelper.inMapBounds(x, y)) {
            return false;
        }
        if (!allowInterior && _parcels[x][y][_currentFloor].getRoom() != null && !_parcels[x][y][_currentFloor].getRoom().isExterior()) {
            return false;
        }
        if (!allowExterior && (_parcels[x][y][_currentFloor].getRoom() == null || _parcels[x][y][_currentFloor].getRoom().isExterior())) {
            return false;
        }
        if (!allowStructure && _parcels[x][y][_currentFloor].getStructure() != null && _parcels[x][y][_currentFloor].getStructure().isSolid()) {
            return false;
        }
        if (!allowResource && _parcels[x][y][_currentFloor].hasPlant()) {
            return false;
        }
        if (!allowItem && _parcels[x][y][_currentFloor].getItem() != null) {
            return false;
        }
        if (!allowConsumable && _parcels[x][y][_currentFloor].getConsumable() != null) {
            return false;
        }
        if (!allowCharacter && ModuleHelper.getCharacterModule().countCharacterAtPos(x, y) > 0) {
            return false;
        }
        return true;
    }
}
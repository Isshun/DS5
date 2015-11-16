package org.smallbox.faraway.core.game.helper;

import com.badlogic.gdx.math.MathUtils;
import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.GameInfo;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.character.model.PathModel;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.PlantModel;
import org.smallbox.faraway.core.game.module.world.model.StructureModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;

/**
 * Created by Alex on 09/07/2015.
 */
public class WorldHelper {
    public static int                   _currentFloor;
    public static ParcelModel[][][]     _parcels;
    private static int                  _width;
    private static int                  _height;
    private static int                  _floors;

    public static void init(GameInfo gameInfo, ParcelModel[][][]parcels) {
        _currentFloor = gameInfo.worldFloors - 1;
        _parcels = parcels;
        _width = gameInfo.worldWidth;
        _height = gameInfo.worldHeight;
        _floors = gameInfo.worldFloors;
    }

    public static ItemModel         getItem(int x, int y) { return getItem(x, y, 0); }
    public static ItemModel         getItem(int x, int y, int z) { return inMapBounds(x, y) ? _parcels[x][y][z].getItem() : null; }
    public static ConsumableModel   getConsumable(int x, int y) { return getConsumable(x, y, 0); }
    public static ConsumableModel   getConsumable(int x, int y, int z) { return inMapBounds(x, y) ? _parcels[x][y][z].getConsumable() : null; }
    public static StructureModel    getStructure(int x, int y) { return getStructure(x, y, 0); }
    public static StructureModel    getStructure(int x, int y, int z) { return inMapBounds(x, y, z) ? _parcels[x][y][z].getStructure() : null; }
    public static PlantModel        getResource(int x, int y) { return getResource(x, y, 0); }
    public static PlantModel        getResource(int x, int y, int z) { return inMapBounds(x, y) ? _parcels[x][y][z].getPlant() : null; }
    public static ItemInfo          getPlantInfo(int x, int y, int z) { return inMapBounds(x, y) && _parcels[x][y][z].getPlant() != null ? _parcels[x][y][z].getPlant().getInfo() : null; }
    public static ItemInfo          getGroundInfo(int x, int y, int z) { return inMapBounds(x, y) ? _parcels[x][y][z].getGroundInfo() : null; }
    public static ItemInfo          getRockInfo(int x, int y, int z) { return inMapBounds(x, y) ? _parcels[x][y][z].getRockInfo() : null; }
    public static ItemInfo          getStructureInfo(int x, int y, int z) { return inMapBounds(x, y) ? _parcels[x][y][z].getStructureInfo() : null; }
    public static boolean           hasGround(int x, int y, int z) { return inMapBounds(x, y, z) && _parcels[x][y][z].getGroundInfo() != null; }
    public static boolean           hasRock(int x, int y, int z) { return inMapBounds(x, y, z) && _parcels[x][y][z].getRockInfo() != null; }
    public static boolean           hasWall(int x, int y, int z) { return inMapBounds(x, y, z) && _parcels[x][y][z].getStructure() != null && _parcels[x][y][z].getStructure().getInfo().isWall; }
    public static boolean           hasPlant(int x, int y, int z) { return inMapBounds(x, y, z) && _parcels[x][y][z].getPlant() != null; }
    public static boolean           hasStructure(int x, int y, int z) { return inMapBounds(x, y, z) && _parcels[x][y][z].getStructure() != null; }

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
            int z = parcel.z;
            for (int d = 0; d < 8; d++) {
                for (int i = 0; i <= d; i++) {
                    if (areaFreeForConsumable(x + i, y + d, z, itemInfo, quantity)) return _parcels[x + i][y + d][z];
                    if (areaFreeForConsumable(x + i, y - d, z, itemInfo, quantity)) return _parcels[x + i][y - d][z];
                    if (areaFreeForConsumable(x + d, y + i, z, itemInfo, quantity)) return _parcels[x + d][y + i][z];
                    if (areaFreeForConsumable(x - d, y + i, z, itemInfo, quantity)) return _parcels[x - d][y + i][z];

                    if (areaFreeForConsumable(x - i, y + d, z, itemInfo, quantity)) return _parcels[x - i][y + d][z];
                    if (areaFreeForConsumable(x - i, y - d, z, itemInfo, quantity)) return _parcels[x - i][y - d][z];
                    if (areaFreeForConsumable(x + d, y - i, z, itemInfo, quantity)) return _parcels[x + d][y - i][z];
                    if (areaFreeForConsumable(x - d, y - i, z, itemInfo, quantity)) return _parcels[x - d][y - i][z];
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
    public static boolean areaFreeForConsumable(int x, int y, int z, ItemInfo info, int quantity) {
        if (!inMapBounds(x, y, z)) {
            return false;
        }

        ParcelModel parcel = _parcels[x][y][z];
        if (parcel.getStructure() != null && !parcel.getStructure().isWalkable()) {
            return false;
        }

        if (parcel.hasPlant()) {
            return false;
        }

        if (parcel.hasRock()) {
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
        int z = parcel.z;
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < i; j++) {
                // Top
                if (isFreeSpace(x + j, y + i, z, acceptInterior, acceptExterior)) return _parcels[x + j][y + i][z];
                if (isFreeSpace(x - j, y + i, z, acceptInterior, acceptExterior)) return _parcels[x - j][y + i][z];

                // Bottom
                if (isFreeSpace(x + j, y - i, z, acceptInterior, acceptExterior)) return _parcels[x + j][y - i][z];
                if (isFreeSpace(x - j, y - i, z, acceptInterior, acceptExterior)) return _parcels[x - j][y - i][z];

                // Right
                if (isFreeSpace(x + i, y + j, z, acceptInterior, acceptExterior)) return _parcels[x + i][y + j][z];
                if (isFreeSpace(x + i, y - j, z, acceptInterior, acceptExterior)) return _parcels[x + i][y + j][z];

                // Left
                if (isFreeSpace(x - i, y + j, z, acceptInterior, acceptExterior)) return _parcels[x - i][y + j][z];
                if (isFreeSpace(x - i, y - j, z, acceptInterior, acceptExterior)) return _parcels[x - i][y - j][z];
            }
        }
        return null;
    }

    private static boolean isFreeSpace(int x, int y, int z, boolean acceptInterior, boolean acceptExterior) {
        if (!WorldHelper.inMapBounds(x, y)) {
            return false;
        }
        if (!acceptInterior && _parcels[x][y][z].getRoom() != null && !_parcels[x][y][z].getRoom().isExterior()) {
            return false;
        }
        if (!acceptExterior && (_parcels[x][y][z].getRoom() == null || _parcels[x][y][z].getRoom().isExterior())) {
            return false;
        }
        if (_parcels[x][y][z].hasRock()) {
            return false;
        }
        if (_parcels[x][y][z].hasStructure() && !_parcels[x][y][z].getStructure().isWalkable()) {
            return false;
        }
        if (_parcels[x][y][z].hasPlant()) {
            return false;
        }
        if (_parcels[x][y][z].hasItem()) {
            return false;
        }
        if (_parcels[x][y][z].hasConsumable()) {
            return false;
        }
        return true;
    }

    public static ParcelModel getNearestWalkable(ParcelModel parcel, int minDistance, int maxDistance) {
        int x = parcel.x;
        int y = parcel.y;
        int z = parcel.z;
        for (int i = minDistance; i <= maxDistance; i++) {
            for (int j = 0; j < i; j++) {
                // Top
                if (isWalkableParcel(x + j, y + i, z)) return _parcels[x + j][y + i][z];
                if (isWalkableParcel(x - j, y + i, z)) return _parcels[x - j][y + i][z];

                // Bottom
                if (isWalkableParcel(x + j, y - i, z)) return _parcels[x + j][y - i][z];
                if (isWalkableParcel(x - j, y - i, z)) return _parcels[x - j][y - i][z];

                // Right
                if (isWalkableParcel(x + i, y + j, z)) return _parcels[x + i][y + j][z];
                if (isWalkableParcel(x + i, y - j, z)) return _parcels[x + i][y + j][z];

                // Left
                if (isWalkableParcel(x - i, y + j, z)) return _parcels[x - i][y + j][z];
                if (isWalkableParcel(x - i, y - j, z)) return _parcels[x - i][y - j][z];
            }
        }
        return null;
    }

    private static boolean isWalkableParcel(int x, int y, int z) {
        return WorldHelper.inMapBounds(x, y) && _parcels[x][y][z].isWalkable();
    }

    public static ParcelModel getRandomFreeSpace(boolean acceptInterior, boolean acceptExterior) {
        int startX = MathUtils.random(0, _width - 1);
        int startY = MathUtils.random(0, _height - 1);
        for (int i = 0; i < _width; i++) {
            for (int j = 0; j < _height; j++) {
                if (isFreeSpace((startX + i) % _width, (startY + j) % _height, _currentFloor, acceptInterior, acceptExterior)) {
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

    public static int getCurrentFloor() {
        return _currentFloor;
    }
}
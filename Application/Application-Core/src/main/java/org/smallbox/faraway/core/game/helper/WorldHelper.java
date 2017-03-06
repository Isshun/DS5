package org.smallbox.faraway.core.game.helper;

import com.badlogic.gdx.math.MathUtils;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.game.GameInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.world.model.StructureItem;
import org.smallbox.faraway.modules.character.model.PathModel;
import org.smallbox.faraway.modules.flora.model.PlantItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 09/07/2015.
 */
public class WorldHelper {
    private static ParcelModel[][][]    _parcels;
    private static int                  _currentFloor;
    private static int                  _groundFloor;
    private static int                  _width;
    private static int                  _height;
    private static int                  _floors;

    public static void init(GameInfo gameInfo, ParcelModel[][][]parcels) {
        _groundFloor = gameInfo.groundFloor;
        _currentFloor = gameInfo.worldFloors - 1;
        _parcels = parcels;
        _width = gameInfo.worldWidth;
        _height = gameInfo.worldHeight;
        _floors = gameInfo.worldFloors;
    }

    public static ConsumableItem    getConsumable(int x, int y, int z) { return inMapBounds(x, y, z) ? _parcels[x][y][z].getItem(ConsumableItem.class) : null; }
    public static StructureItem     getStructure(int x, int y, int z) { return inMapBounds(x, y, z) ? _parcels[x][y][z].getItem(StructureItem.class) : null; }
    public static PlantItem getResource(int x, int y, int z) { return inMapBounds(x, y, z) ? _parcels[x][y][z].getPlant() : null; }
    public static ItemInfo          getPlantInfo(int x, int y, int z) { return inMapBounds(x, y, z) && _parcels[x][y][z].getPlant() != null ? _parcels[x][y][z].getPlant().getInfo() : null; }
    public static ItemInfo          getGroundInfo(int x, int y, int z) { return inMapBounds(x, y, z) ? _parcels[x][y][z].getGroundInfo() : null; }
    public static int               getCurrentFloor() { return _currentFloor; }
    public static int               getGroundFloor() { return _groundFloor; }

    public static void              setCurrentFloor(int currentFloor) { _currentFloor = currentFloor; }

    public static boolean           hasGround(int x, int y, int z) { return inMapBounds(x, y, z) && _parcels[x][y][z].getGroundInfo() != null; }
    public static boolean           hasRock(int x, int y, int z) { return inMapBounds(x, y, z) && _parcels[x][y][z].getRockInfo() != null; }
    public static boolean           hasWall(int x, int y, int z) { return inMapBounds(x, y, z) && _parcels[x][y][z].hasItem(StructureItem.class) && _parcels[x][y][z].getItem(StructureItem.class).getInfo().isWall; }
    public static boolean           hasDoor(int x, int y, int z) { return inMapBounds(x, y, z) && _parcels[x][y][z].hasItem(StructureItem.class) && _parcels[x][y][z].getItem(StructureItem.class).getInfo().isDoor; }
    public static boolean           hasPlant(int x, int y, int z) { return inMapBounds(x, y, z) && _parcels[x][y][z].getPlant() != null; }
    public static boolean           hasStructure(int x, int y, int z) { return inMapBounds(x, y, z) && _parcels[x][y][z].hasItem(StructureItem.class); }
    public static boolean           hasWallOrDoor(int x, int y, int z) { return inMapBounds(x, y, z) && _parcels[x][y][z].hasItem(StructureItem.class) && hasWallOrDoor(_parcels[x][y][z]); }
    public static boolean           hasLiquid(int x, int y, int z) { return inMapBounds(x, y, z) && _parcels[x][y][z].hasLiquid(); }

    public static boolean           hasStructure(ParcelModel parcel) { return parcel.hasItem(StructureItem.class); }
    public static boolean           hasWallOrDoor(ParcelModel parcel) { return parcel.hasItem(StructureItem.class) && (parcel.getItem(StructureItem.class).getInfo().isWall || parcel.getItem(StructureItem.class).getInfo().isDoor); }
    public static boolean           hasWall(ParcelModel parcel) { return parcel.hasItem(StructureItem.class) && parcel.getItem(StructureItem.class).getInfo().isWall; }
    public static boolean           hasDoor(ParcelModel parcel) { return parcel.hasItem(StructureItem.class) && parcel.getItem(StructureItem.class).getInfo().isDoor; }
    public static boolean           hasFloor(ParcelModel parcel) { return parcel.hasItem(StructureItem.class) && parcel.getItem(StructureItem.class).getInfo().isFloor; }

    /**
     * Search for org.smallbox.faraway.core.module.room.model free to receive a ConsumableItem
     *
     * @param parcel
     * @param itemInfo
     * @return nearest free org.smallbox.faraway.core.module.room.model
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
     * Check if current org.smallbox.faraway.core.module.room.model is free for consumable
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
        if (parcel.hasItem(StructureItem.class) && !parcel.getItem(StructureItem.class).isWalkable()) {
            return false;
        }

        if (parcel.hasPlant()) {
            return false;
        }

        if (!parcel.hasGround() || !parcel.getGroundInfo().isWalkable) {
            return false;
        }

        if (parcel.hasRock()) {
            return false;
        }

        // TODO
//        if (parcel.getItem() != null && !parcel.getItem().isStorageParcel(parcel)) {
//            return false;
//        }

        ConsumableItem consumable = parcel.getItem(ConsumableItem.class);
        if (consumable != null && (consumable.getInfo() != info || consumable.getFreeQuantity() + quantity > Math.max(Application.APPLICATION_CONFIG.game.storageMaxQuantity, consumable.getInfo().stack))) {
            return false;
        }

        return true;
    }

    /**
     * Check if position is in world bounds
     *
     * @param x
     * @param y
     * @param z
     * @return true if position in bounds
     */
    public static boolean inMapBounds(int x, int y, int z) {
        return !(x < 0 || y < 0 || z < 0 || x >= _width || y >= _height || z >= _floors);
    }

    public static ParcelModel getNearestFreeParcel(int x, int y, int z, boolean acceptInterior, boolean acceptExterior) {
        return getNearestFreeParcel(WorldHelper.getParcel(x, y, z), acceptInterior, acceptExterior);
    }

    public static ParcelModel getNearestFreeParcel(ParcelModel parcel, ItemInfo itemInfo, int quantity) {
        int x = parcel.x;
        int y = parcel.y;
        int z = parcel.z;
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < i; j++) {
                // Top
                if (parcelAcceptConsumable(x + j, y + i, z, itemInfo, quantity)) return _parcels[x + j][y + i][z];
                if (parcelAcceptConsumable(x - j, y + i, z, itemInfo, quantity)) return _parcels[x - j][y + i][z];

                // Bottom
                if (parcelAcceptConsumable(x + j, y - i, z, itemInfo, quantity)) return _parcels[x + j][y - i][z];
                if (parcelAcceptConsumable(x - j, y - i, z, itemInfo, quantity)) return _parcels[x - j][y - i][z];

                // Right
                if (parcelAcceptConsumable(x + i, y + j, z, itemInfo, quantity)) return _parcels[x + i][y + j][z];
                if (parcelAcceptConsumable(x + i, y - j, z, itemInfo, quantity)) return _parcels[x + i][y + j][z];

                // Left
                if (parcelAcceptConsumable(x - i, y + j, z, itemInfo, quantity)) return _parcels[x - i][y + j][z];
                if (parcelAcceptConsumable(x - i, y - j, z, itemInfo, quantity)) return _parcels[x - i][y - j][z];
            }
        }
        return null;
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

    private static boolean parcelAcceptConsumable(int x, int y, int z, ItemInfo itemInfo, int quantity) {
        if (!WorldHelper.inMapBounds(x, y, z)) {
            return false;
        }

        return _parcels[x][y][z].accept(itemInfo, quantity);
    }

    private static boolean isFreeSpace(int x, int y, int z, boolean acceptInterior, boolean acceptExterior) {
        if (!WorldHelper.inMapBounds(x, y, z)) {
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
        if (_parcels[x][y][z].hasItem(StructureItem.class) && !_parcels[x][y][z].getItem(StructureItem.class).isWalkable()) {
            return false;
        }
        if (_parcels[x][y][z].hasPlant()) {
            return false;
        }
        // TODO
        //        if (_parcels[x][y][z].hasItem()) {
//            return false;
//        }

        if (_parcels[x][y][z].hasItem(ConsumableItem.class)) {
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
        return WorldHelper.inMapBounds(x, y, z) && _parcels[x][y][z].isWalkable();
    }

    public static ParcelModel getRandomFreeSpace(int floor, boolean acceptInterior, boolean acceptExterior) {
        int startX = MathUtils.random(0, _width - 1);
        int startY = MathUtils.random(0, _height - 1);
        for (int i = 0; i < _width; i++) {
            for (int j = 0; j < _height; j++) {
                if (isFreeSpace((startX + i) % _width, (startY + j) % _height, floor, acceptInterior, acceptExterior)) {
                    return _parcels[(startX + i) % _width][(startY + j) % _height][floor];
                }
            }
        }
        return null;
    }

    public static boolean isBlocked(int x, int y, int z) {
        if (inMapBounds(x, y, z)) {
            return _parcels[x][y][z] != null && !_parcels[x][y][z].isWalkable();
        }
        return true;
    }


    public static boolean isSurroundedByBlocked(ParcelModel toParcel) {
        return isSurroundedByBlocked(toParcel.x, toParcel.y, toParcel.z);
    }

    public static boolean isSurroundedByBlocked(int x, int y, int z) {
        if (!isBlocked(x + 1, y, z)) return false;
        if (!isBlocked(x - 1, y, z)) return false;
        if (!isBlocked(x, y + 1, z)) return false;
        if (!isBlocked(x, y - 1, z)) return false;
        if (!isBlocked(x, y, z + 1)) return false;
        if (!isBlocked(x, y, z - 1)) return false;
        return true;
    }

    public static ParcelModel getParcel(int x, int y, int z) {
        if (inMapBounds(x, y, z)) {
            return _parcels[x][y][z];
        }
        return null;
    }

    public static ParcelModel getParcelOffset(ParcelModel parcel, int offsetX, int offsetY) {
        if (inMapBounds(parcel.x + offsetX, parcel.y + offsetY, parcel.z)) {
            return _parcels[parcel.x + offsetX][parcel.y + offsetY][parcel.z];
        }
        return null;
    }

    public static int getApproxDistance(ParcelModel p1, ParcelModel p2) {
        if (p1 == null) {
            throw new GameException(WorldHelper.class, "P1 cannot be null");
        }

        if (p2 == null) {
            throw new GameException(WorldHelper.class, "P2 cannot be null");
        }

        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }

    public static int getDistance(ParcelModel p1, ParcelModel p2) {
        PathModel path = Application.pathManager.getPath(p1, p2, true, false);
        if (path != null) {
            return path.getLength();
        }
        return -1;
    }

    public static List<ParcelModel> getParcelInRect(int x1, int y1, int x2, int y2, int z) {
        List<ParcelModel> parcelList = new ArrayList<>();

        int fromMapX = Math.min(x1, x2);
        int fromMapY = Math.min(y1, y2);
        int toMapX = Math.max(x1, x2);
        int toMapY = Math.max(y1, y2);

        for (int mapX = fromMapX; mapX <= toMapX; mapX++) {
            for (int mapY = fromMapY; mapY <= toMapY; mapY++) {
                ParcelModel parcel = WorldHelper.getParcel(mapX, mapY, z);
                if (parcel != null) {
                    parcelList.add(parcel);
                }
            }
        }

        return parcelList;
    }

    public enum SearchStrategy { FREE }

    public interface SearchArroundCallback {
        boolean check(ParcelModel parcel);
    }

    public static ParcelModel searchAround2(ParcelModel originParcel, int maxDistance, SearchArroundCallback callback) {
        for (int distance = 0; distance <= maxDistance; distance++) {
            for (int x = originParcel.x - distance; x <= originParcel.x + distance; x++) {
                for (int y = originParcel.y - distance; y <= originParcel.y + distance; y++) {
                    ParcelModel parcel = getParcel(x, y, originParcel.z);
                    if (parcel != null && callback.check(parcel)) {
                        return parcel;
                    }
                }
            }
        }
        return null;
    }

    public static ParcelModel searchAround(ParcelModel originParcel, int maxDistance, SearchStrategy... strategies) {
        for (int distance = 0; distance <= maxDistance; distance++) {
            for (int x = originParcel.x - distance; x <= originParcel.x + distance; x++) {
                for (int y = originParcel.y - distance; y <= originParcel.y + distance; y++) {
                    ParcelModel parcel = getParcel(x, y, originParcel.z);
                    if (parcel != null && searchAroundMatch(parcel, strategies)) {
                        return parcel;
                    }
                }
            }
        }
        return null;
    }

    private static boolean searchAroundMatch(ParcelModel parcel, SearchStrategy... strategies) {
        for (SearchStrategy strategy: strategies) {
            if (strategy == SearchStrategy.FREE && !parcel.isWalkable()) {
                return false;
            }
        }
        return true;
    }

    public interface GetParcelCallback {
        boolean onParcel(ParcelModel parcel);
    }

    /**
     * Parcours en spirale
     *
     * @param parcel    position initiale
     * @param callback  condition d'arret
     */
    public static ParcelModel move(ParcelModel parcel, GetParcelCallback callback) {
        return move(parcel.x, parcel.y, parcel.z, callback);
    }

    /**
     * Parcours en spirale
     *
     * @param x0        position X initiale
     * @param y0        position Y initiale
     * @param z0        position Z initiale
     * @param callback  condition d'arret
     */
    public static ParcelModel move(int x0, int y0, int z0, GetParcelCallback callback) {
        // directions possibles: G=(-1,0) H=(0,-1) D=(1,0) B=(0,1)
        int[] dx = new int[]{1, 0, -1, 0};
        int[] dy = new int[]{0, 1, 0, -1};
        int dirIndex = 0;
        int distanceMax = 50;

        // distance parcourue
        int distance = 0;

        // nombre de pas a faire
        int stepToDo = 1;

        // position courante
        int x = x0, y = y0;
        System.out.println("Initial position: " + x + "," + y);

        while (true) {

            // a faire 2 fois avec le meme nombre de pas (gauche+haut) ou (droite+bas)
            for (int i = 0; i < 2; i++) {

                // déplacement du nombre de pas
                for (int j = 0; j < stepToDo; j++) {

                    // condition de sortie
                    ParcelModel parcel = getParcel(x, y, z0);
                    if (parcel != null && callback.onParcel(parcel)) {
                        return parcel;
                    }

                    // condition de sortie
                    distance++;
                    if (distance > distanceMax) {
                        return null;
                    }

                    // déplacement
                    x += dx[dirIndex];
                    y += dy[dirIndex];

                    System.out.println("Current position: " + x + "," + y + " distance=" + distance);
                }

                // tourne a droite
                dirIndex = (dirIndex + 1) % 4;
            }

            // incrementer le nombre de pas a faire
            stepToDo++;
        }

    }
}
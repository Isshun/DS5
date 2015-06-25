
package org.smallbox.faraway.game.manager;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.GameObserver;
import org.smallbox.faraway.game.model.item.ItemModel;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.item.ResourceModel;
import org.smallbox.faraway.game.model.item.StructureModel;
import org.smallbox.faraway.game.model.room.NeighborModel;
import org.smallbox.faraway.game.model.room.RoomModel;
import org.smallbox.faraway.game.model.room.RoomModel.RoomType;
import org.smallbox.faraway.ui.AreaModel;
import org.smallbox.faraway.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomManager extends BaseManager implements GameObserver {
    private static final int    UPDATE_INTERVAL = 10;

    private List<RoomModel>		_roomList;
    private int                 _width;
    private int                 _height;
    private ParcelModel[][][]   _parcels;
    private double[][]          _oxygenLevels;
    private boolean             _needRefresh;

    @Override
    protected void onCreate() {
        int width = Game.getWorldManager().getWidth();
        int height = Game.getWorldManager().getHeight();

        if (width == 0 || height == 0) {
            throw new RuntimeException("Cannot create RoomManager with 0 sized world map");
        }

        double oxygenLevel = Game.getInstance().getPlanet().getOxygen();
        _roomList = new ArrayList<>();
        _oxygenLevels = new double[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                _oxygenLevels[x][y] = oxygenLevel;
            }
        }
    }

    public RoomModel getRoom(int x, int y) {
        for (RoomModel room: _roomList) {
            if (room.containsParcel(x, y)) {
                return room;
            }
        }
        return null;
    }

    public List<RoomModel> getRoomList() { return _roomList; }

    protected void onUpdate(int tick) {
        if (_needRefresh) {
            refreshRooms();
        }
        if (tick % UPDATE_INTERVAL == 0) {
            _roomList.forEach(RoomModel::update);
        }
    }

    public void makeRooms() {
        _width = Game.getWorldManager().getWidth();
        _height = Game.getWorldManager().getHeight();
        _parcels = Game.getWorldManager().getParcels();

        for (int x = 0; x < _width; x++) {
            for (int y = 0; y < _height; y++) {
                _parcels[x][y][0].setRoom(null);
                _parcels[x][y][0].tmpData = 0;
            }
        }
        _roomList.clear();

        boolean newRoomFound;
        do {
            newRoomFound = false;
            for (int x = 0; x < _width; x++) {
                for (int y = 0; y < _height; y++) {
                    if (!Thread.currentThread().isInterrupted()) {
                        ParcelModel parcel = _parcels[x][y][0];
                        if (parcel.getRoom() != null) {
                            continue;
                        }
                        if (parcel.getStructure() != null && (parcel.getStructure().isSolid() || parcel.getStructure().isDoor())) {
                            continue;
                        }
                        if (parcel.getResource() != null && parcel.getResource().isSolid()) {
                            continue;
                        }

                        newRoomFound = true;
                        RoomModel room = new RoomModel(RoomType.NONE);
                        room.setExterior(false);
                        room.addParcel(parcel);
                        parcel.setRoom(room);
                        _roomList.add(room);
                        exploreRoom(room);
                        checkRoof(room);
                        addSpecialsItems(room);
                        room.setAutoName(autoName(room));
                    }
                }
            }
        } while (newRoomFound);

        Log.info("[RoomManager] " + _roomList.size() + " rooms found");
    }

    private void addSpecialsItems(RoomModel room) {
        for (ParcelModel parcel: room.getParcels()) {
            ItemModel item = parcel.getItem();
            if (item != null && item.getInfo().effects != null) {
                // Add heat item
                if (item.getInfo().effects.heatPotency > 0) {
                    room.getHeatItems().add(item);
                }
                // Add cold item
                if (item.getInfo().effects.coldPotency > 0) {
                    room.getHeatItems().add(item);
                }
                // Add oxygen item
                if (item.getInfo().effects.oxygen > 0) {
                    room.getOxygenItems().add(item);
                }
            }
        }
    }

    private String autoName(RoomModel room) {

        // Check item
        ItemModel bed = null;
        AreaModel storageArea = null;
        int nbBed = 0;
        int bestCount = 0;
        String bestCategory = null;
        Map<String, Integer>    itemCategory = new HashMap<>();

        for (ParcelModel parcel: room.getParcels()) {
            ItemModel item = parcel.getItem();
            if (item != null) {
                String category = item.getInfo().category;
                int itemCategoryCount = itemCategory.containsKey(category) ? itemCategory.get(category) + 1 : 1;
                itemCategory.put(category, itemCategoryCount);
                if (bestCount < itemCategoryCount) {
                    bestCount = itemCategoryCount;
                    bestCategory = category;
                }
                if (item.isBed()) {
                    bed = item;
                    nbBed++;
                }
            }
            if (parcel.getArea() != null) {
                storageArea = parcel.getArea();
            }
        }

        if ("kitchen".equals(bestCategory)) {
            return "Kitchen";
        }

        if ("quarter".equals(bestCategory)) {
            if (nbBed == 1 && bed.getOwner() != null) {
                bed.getOwner().setQuarter(room);
                room.setOwner(bed.getOwner());
                return bed.getOwner().getName() + "'s quarter";
            }
            return "Common quarter";
        }

        if (storageArea != null) {
            return "Storage room";
        }

        return "Room";
    }

    private void checkRoof(RoomModel room) {
        Log.info("[RoomManager] Check roof: " + room.getName());
        for (ParcelModel parcel: room.getParcels()) {
            boolean isUnsupported = true;
            for (int i = 0; i < 6; i++) {
                if (checkAreaCanSupportRoof(parcel.getX() + i, parcel.getY())) isUnsupported = false;
                if (checkAreaCanSupportRoof(parcel.getX() - i, parcel.getY())) isUnsupported = false;
                if (checkAreaCanSupportRoof(parcel.getX(), parcel.getY() + i)) isUnsupported = false;
                if (checkAreaCanSupportRoof(parcel.getX(), parcel.getY() - i)) isUnsupported = false;
            }
            if (isUnsupported) {
                room.setExterior(true);
                return;
            }
        }
    }

    private void exploreRoom(RoomModel room) {
        List<ParcelModel> openList = new ArrayList<>();
        boolean parcelFound = true;
        for (int i = 1; parcelFound; i++) {
            parcelFound = false;
            for (ParcelModel parcel : room.getParcels()) {
                if (parcel.tmpData == i - 1) {
                    int x = parcel.getX();
                    int y = parcel.getY();
                    if (addToRoomIfFree(room, openList, i, x + 1, y)) { parcelFound = true; }
                    if (addToRoomIfFree(room, openList, i, x - 1, y)) { parcelFound = true; }
                    if (addToRoomIfFree(room, openList, i, x, y + 1)) { parcelFound = true; }
                    if (addToRoomIfFree(room, openList, i, x, y - 1)) { parcelFound = true; }
                }
            }
            room.addParcels(openList);
            openList.clear();
        }
    }

    private boolean addToRoomIfFree(RoomModel room, List<ParcelModel> openList, int i, int x, int y) {
        if (x < 0 || x >= _width || y < 0 || y >= _height || _parcels[x][y][0] == null) {
            return false;
        }

        if (_parcels[x][y][0].getRoom() != null) {
            return false;
        }

        if (_parcels[x][y][0].getStructure() != null && _parcels[x][y][0].getStructure().isCloseRoom()) {
            return false;
        }

        if (_parcels[x][y][0].getResource() != null && _parcels[x][y][0].getResource().isCloseRoom()) {
            return false;
        }

        _parcels[x][y][0].tmpData = i;
        _parcels[x][y][0].setRoom(room);
        openList.add(_parcels[x][y][0]);
        return true;
    }

    private boolean checkAreaCanSupportRoof(int x, int y) {
        if (x >= 0 && x < _width && y >= 0 && y < _height && _parcels[x][y][0] != null) {
            return _parcels[x][y][0].canSupportRoof();
        }
        return false;
    }

    public void refreshRooms() {
        _needRefresh = false;

        Log.info("RoomManager: refresh");
        long time = System.currentTimeMillis();

        // Store o2 levels
        for (RoomModel room: _roomList) {
            for (ParcelModel parcel: room.getParcels()) {
                if (parcel != null) {
                    _oxygenLevels[parcel.getX()][parcel.getY()] = room.getOxygen();
                }
            }
        }
        _roomList.clear();
        makeRooms();
        makeNeighborhood();

        // Restore o2 levels
        if (!Thread.currentThread().isInterrupted()) {
            for (RoomModel room : _roomList) {
                double oxygen = 0;
                for (ParcelModel parcel : room.getParcels()) {
                    if (parcel != null) {
                        oxygen += _oxygenLevels[parcel.getX()][parcel.getY()];
                    }
                }
                room.setOxygen(oxygen / room.getParcels().size());
            }
        }

        Log.info("RoomManager: refresh done " + (System.currentTimeMillis() - time));
    }

    private void makeNeighborhood() {
        WorldManager manager = Game.getWorldManager();

        for (RoomModel room: _roomList) {
            // Init neighborhood
            Map<RoomModel, NeighborModel> neighborhood = new HashMap<>();
            for (RoomModel neighbor: _roomList) {
                if (neighbor != room) {
                    neighborhood.put(neighbor, new NeighborModel(neighbor));
                }
            }

            for (ParcelModel parcel: room.getParcels()) {
                checkAndAddNeighbor(manager, neighborhood, room, parcel, -1, 0);
                checkAndAddNeighbor(manager, neighborhood, room, parcel, +1, 0);
                checkAndAddNeighbor(manager, neighborhood, room, parcel, 0, -1);
                checkAndAddNeighbor(manager, neighborhood, room, parcel, 0, +1);
            }

            // Compute sealing
            for (NeighborModel neighbor: neighborhood.values()) {
                boolean isOpen = false;
                neighbor.sealing = 0;
                for (ParcelModel parcel : neighbor.parcels) {
                    neighbor.sealing += parcel.getSealing();
                    if (parcel.getSealing() == 0) {
                        isOpen = true;
                    }
                }
                neighbor.sealing = isOpen ? 0.1 : neighbor.sealing / neighbor.parcels.size();
            }

            room.setNeighborhoods(new ArrayList<>(neighborhood.values()));
        }
    }

    private void checkAndAddNeighbor(WorldManager manager, Map<RoomModel, NeighborModel> neighborhood, RoomModel room, ParcelModel parcel, int offsetX, int offsetY) {
        ParcelModel p1 = manager.getParcel(parcel.getX() + offsetX, parcel.getY() + offsetY);
        if (p1 != null && (p1.getStructure() != null && !p1.getStructure().isFloor() || p1.getResource() != null && p1.getResource().isRock())) {
            ParcelModel p2 = manager.getParcel(p1.getX() + offsetX, p1.getY() + offsetY);
            if (p2 != null && p2.getRoom() != null && p2.getRoom() != room) {
                neighborhood.get(p2.getRoom()).parcels.add(p1);
            }
        }
    }

    @Override
    public void onAddItem(ItemModel item){
        if (item.isLight() && item.getParcel() != null && item.getParcel().getRoom() != null) {
            int lightValue = 0;
            for (ParcelModel area: item.getParcel().getRoom().getParcels()) {
                if (area != null && area.getItem() != null && area.getItem().isLight()) {
                    lightValue += area.getItem().getInfo().light;
                }
            }
            item.getParcel().getRoom().setLight(lightValue);
        }
    }

    @Override
    public void onAddStructure(StructureModel structure){
        if (!structure.isFloor()) {
            _needRefresh = true;
        }
    }

    @Override
    public void onRemoveStructure(StructureModel structure) {
        if (!structure.isFloor()) {
            _needRefresh = true;
        }
    }

    @Override
    public void onRemoveResource(ResourceModel resource){
        if (resource.isRock()) {
            _needRefresh = true;
        }
    }

    public int getLight(int x, int y) {
        RoomModel room = getRoom(x, y);
        return room != null ? room.getLight() : -1;
    }
}

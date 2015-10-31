
package org.smallbox.faraway.core.game.module.room;

import com.badlogic.gdx.ai.pfa.Connection;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.module.area.model.AreaModel;
import org.smallbox.faraway.core.game.module.room.model.NeighborModel;
import org.smallbox.faraway.core.game.module.room.model.RoomModel;
import org.smallbox.faraway.core.game.module.room.model.RoomModel.RoomType;
import org.smallbox.faraway.core.game.module.world.WorldModule;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.resource.ResourceModel;
import org.smallbox.faraway.core.game.module.world.model.StructureModel;
import org.smallbox.faraway.core.module.GameModule;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.AsyncTask;

import java.util.*;
import java.util.stream.Collectors;

public class RoomModule extends GameModule implements GameObserver {
    private RoomModel                   _worldRoom;
    private final List<RoomModel>       _roomList = new ArrayList<>();
    private double[][]                  _oxygenLevels;
    private boolean                     _needRefresh;
    private List<ParcelModel>           _roomlessParcels;
    private AsyncTask<List<RoomModel>>  _task;

    @Override
    protected void onLoaded() {
        _updateInterval = 10;
        _worldRoom = new RoomModel(RoomType.WORLD);
        _worldRoom.setExterior(true);

        int width = Game.getInstance().getInfo().worldWidth;
        int height = Game.getInstance().getInfo().worldHeight;

        if (width == 0 || height == 0) {
            throw new RuntimeException("Cannot onCreate RoomModule with 0 sized world old");
        }

        double oxygenLevel = Game.getInstance().getPlanet().getOxygen();
        _roomlessParcels = new ArrayList<>();
        _oxygenLevels = new double[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                _oxygenLevels[x][y] = oxygenLevel;
            }
        }
    }

    @Override
    protected boolean loadOnStart() {
        return GameData.config.manager.room;
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

        if (_task != null && _task.isComplete()) {
            _task.complete();
            _task = null;
        }
    }
//
//    public void makeRooms(List<RoomModel> rooms, ParcelModel[][][] parcels) {
//        for (int x = 0; x < parcels.length; x++) {
//            for (int y = 0; y < parcels[x].length; y++) {
//                parcels[x][y][0].setRoom(null);
//                parcels[x][y][0].tmpData = 0;
//            }
//        }
//
//        boolean newRoomFound;
//        do {
//            newRoomFound = false;
//            for (int x = 0; x < parcels.length; x++) {
//                for (int y = 0; y < parcels[x].length; y++) {
//                    if (!Thread.currentThread().isInterrupted()) {
//                        ParcelModel parcel = parcels[x][y][0];
//                        if (parcel.getRoom() != null) {
//                            continue;
//                        }
//                        if (parcel.getStructure() != null && (parcel.getStructure().isSolid() || parcel.getStructure().isDoor())) {
//                            continue;
//                        }
//                        if (parcel.getResource() != null && parcel.getResource().isSolid()) {
//                            continue;
//                        }
//
//                        newRoomFound = true;
//                        RoomModel room = new RoomModel(RoomType.NONE);
//                        room.setExterior(false);
//                        room.addParcel(parcel);
//                        parcel.setRoom(room);
//                        rooms.add(room);
//                        exploreRoom(parcels, room);
//                        checkRoof(parcels, room);
//                        addSpecialsItems(room);
//                        if (room.isExterior()) {
//                            room.getParcels().forEach(p -> p.setExterior(true));
//                        }
//                        room.setAutoName(autoName(room));
//                    }
//                }
//            }
//        } while (newRoomFound);
//
//        printInfo("[RoomModule] " + _roomList.size() + " rooms found");
//    }

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

        // Bedroom
        if (nbBed >= 1) {
            if (nbBed == 1 && bed.getOwner() != null) {
                bed.getOwner().setQuarter(room);
                room.setOwner(bed.getOwner());
                return bed.getOwner().getName() + "'s quarter";
            }
            return "Common bedroom";
        }

        // Kitchen
        if (itemCategory.containsKey("kitchen")) {
            return "Kitchen";
        }

        // Common room
        if (itemCategory.containsKey("amusement")) {
            return "Common room";
        }

        // Storage
        if (storageArea != null) {
            return "Storage room";
        }

        return "Room";
    }

    private void checkRoof(ParcelModel[][][] parcels, RoomModel room) {
        printInfo("[RoomModule] Check roof: " + room.getName());
        for (ParcelModel parcel: room.getParcels()) {
            boolean isUnsupported = true;
            for (int i = 0; i < 6; i++) {
                if (checkAreaCanSupportRoof(parcels, parcel.x + i, parcel.y)) isUnsupported = false;
                if (checkAreaCanSupportRoof(parcels, parcel.x - i, parcel.y)) isUnsupported = false;
                if (checkAreaCanSupportRoof(parcels, parcel.x, parcel.y + i)) isUnsupported = false;
                if (checkAreaCanSupportRoof(parcels, parcel.x, parcel.y - i)) isUnsupported = false;
            }
            if (isUnsupported) {
                printInfo("[RoomModule] roof collapse");
                room.setExterior(true);
                return;
            }
        }
        printInfo("[RoomModule] roof ok");
        room.setExterior(false);
    }

    private void exploreRoom(ParcelModel[][][] parcels, RoomModel room) {
        List<ParcelModel> openList = new ArrayList<>();
        boolean parcelFound = true;
        for (int i = 1; parcelFound; i++) {
            parcelFound = false;
            for (ParcelModel parcel : room.getParcels()) {
                if (parcel.tmpData == i - 1) {
                    int x = parcel.x;
                    int y = parcel.y;
                    if (addToRoomIfFree(parcels, room, openList, i, x + 1, y)) { parcelFound = true; }
                    if (addToRoomIfFree(parcels, room, openList, i, x - 1, y)) { parcelFound = true; }
                    if (addToRoomIfFree(parcels, room, openList, i, x, y + 1)) { parcelFound = true; }
                    if (addToRoomIfFree(parcels, room, openList, i, x, y - 1)) { parcelFound = true; }
                }
            }
            room.addParcels(openList);
            openList.clear();
        }
    }

    private boolean addToRoomIfFree(ParcelModel[][][] parcels, RoomModel room, List<ParcelModel> openList, int i, int x, int y) {
        if (x < 0 || x >= parcels.length || y < 0 || y >= parcels[0].length || parcels[x][y][0] == null) {
            return false;
        }

        if (parcels[x][y][0].getRoom() != null) {
            return false;
        }

        if (parcels[x][y][0].getStructure() != null && parcels[x][y][0].getStructure().isCloseRoom()) {
            return false;
        }

        if (parcels[x][y][0].getResource() != null && parcels[x][y][0].getResource().isCloseRoom()) {
            return false;
        }

        parcels[x][y][0].tmpData = i;
        parcels[x][y][0].setRoom(room);
        openList.add(parcels[x][y][0]);
        return true;
    }

    private boolean checkAreaCanSupportRoof(ParcelModel[][][] parcels, int x, int y) {
        if (x >= 0 && x < parcels.length && y >= 0 && y < parcels[0].length && parcels[x][y][0] != null) {
            return parcels[x][y][0].canSupportRoof();
        }
        return false;
    }

    public void refreshRooms() {
        _needRefresh = false;

        printInfo("RoomModule: refresh");
        long time = System.currentTimeMillis();

//        _todo.forEach(room -> {
//            ParcelModel baseParcel = room.getBaseParcel();
//            if (room.getType() != RoomType.WORLD) {
//                room.getParcels().forEach(parcel -> parcel.setRoom(null));
//                _freeParcel.addAll(room.getParcels());
//                room.getParcels().clear();
//                inspect(room, baseParcel);
//                checkRoof(ModuleHelper.getWorldModule().getParcels(), room);
//            }
//        });
//        _todo.clear();
//
//        while (!_freeParcel.isEmpty()) {
//            RoomModel room = new RoomModel(RoomType.NONE);
//            inspect(room, _freeParcel.iterator().next());
//            checkRoof(ModuleHelper.getWorldModule().getParcels(), room);
//            _roomList.add(room);
//        }

        _roomList.forEach(room -> {
            ParcelModel parcel = room.getBaseParcel();
            room.getParcels().forEach(p -> p.setRoom(null));
            room.getParcels().clear();
            inspect(room, parcel);
        });

        List<RoomModel> newRooms = new ArrayList<>();
        ModuleHelper.getWorldModule().getParcelList().forEach(parcel -> {
            if (parcel.getRoom() == null && parcel.isRoomOpen()) {
                RoomModel room = new RoomModel(RoomType.NONE);
                inspect(room, parcel);
                checkRoof(ModuleHelper.getWorldModule().getParcels(), room);
                newRooms.add(room);
            }
        });

        synchronized (_roomList) {
            _roomList.addAll(newRooms);
            _roomList.removeAll(_roomList.stream().filter(room -> room.getSize() == 0).collect(Collectors.toList()));
        }

//        _roomList.forEach(room -> {
//            if (!room.isExterior()) {
//                System.out.println("Room info: " + room.getId());
//                room.getParcels().forEach(p -> System.out.println(p.x + "x" + p.y));
//            }
//        });

//        RoomModel room = new RoomModel(RoomType.NONE);
//        ParcelModel parcel = WorldHelper.getParcel(17, 11);
//        inspect(room, parcel);

//
//        _task = new AsyncTask<List<RoomModel>>() {
//            @Override
//            public void onStart() {
//                // Store o2 levels
//                ParcelModel[][][] parcels = ModuleHelper.getWorldModule().getParcels();
//                for (int x = 0; x < parcels.length; x++) {
//                    for (int y = 0; y < parcels[x].length; y++) {
//                        _oxygenLevels[x][x] = parcels[x][y][0].getOxygen();
//                    }
//                }
//
//                // Clear rooms
//                ModuleHelper.getWorldModule().getParcelList().forEach(parcel -> {
//                    parcel.setRoom(null);
//                    parcel.tmpData = 0;
//                });
//            }
//
//            @Override
//            public List<RoomModel> onBackground() {
//                List<RoomModel> rooms = new ArrayList<>();
//                makeRooms(rooms, ModuleHelper.getWorldModule().getParcels());
//                makeNeighborhood(rooms);
//                return rooms;
//            }
//
//            @Override
//            public void onComplete(List<RoomModel> rooms) {
//                long time = System.currentTimeMillis();
//
//                _roomList = rooms;
//
//                // Restore o2 levels
//                if (!Thread.currentThread().isInterrupted()) {
//                    for (RoomModel room : _roomList) {
//                        double oxygen = 0;
//                        for (ParcelModel parcel : room.getParcels()) {
//                            if (parcel != null) {
//                                oxygen += _oxygenLevels[parcel.x][parcel.y];
//                            }
//                        }
//                        room.setOxygen(oxygen / room.getParcels().size());
//                        printInfo("Set room oxygen: " + oxygen / room.getParcels().size());
//                    }
//                }
//
//                _roomlessParcels = ModuleHelper.getWorldModule().getParcelList().stream().filter(parcel -> parcel.getRoom() == null).collect(Collectors.toList());
//
//                printNotice("complete done in " + (System.currentTimeMillis() - time));
//            }
//        };
//
//        _task.start();

        printInfo("RoomModule: refresh done " + (System.currentTimeMillis() - time));
    }

    private void inspect(RoomModel room, ParcelModel parcel) {
        if (parcel.getConnections() != null && parcel.isRoomOpen()) {
            parcel.setRoom(room);
            room.setBaseParcel(parcel);
            room.getParcels().add(parcel);
            for (int i = parcel.getConnections().size - 1; i >= 0; i--) {
                Connection<ParcelModel> connection = parcel.getConnections().get(i);
                if (!room.getParcels().contains(connection.getToNode()) && connection.getToNode().isRoomOpen()) {
                    inspect(room, connection.getToNode());
                }
            }
        }
    }

    private void makeNeighborhood(List<RoomModel> rooms) {
        WorldModule manager = ModuleHelper.getWorldModule();

        for (RoomModel room: rooms) {
            // Init neighborhood
            Map<RoomModel, NeighborModel> neighborhood = new HashMap<>();
            for (RoomModel neighbor: rooms) {
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
            List<NeighborModel> neighborList = new ArrayList<>();
            for (NeighborModel neighbor: neighborhood.values()) {
                if (!neighbor.parcels.isEmpty()) {
                    boolean isOpen = false;
                    neighbor.sealing = 0;
                    for (ParcelModel parcel : neighbor.parcels) {
                        neighbor.sealing += parcel.getSealing();
                        if (parcel.getSealing() == 0) {
                            isOpen = true;
                        }
                    }
                    neighbor.sealing = isOpen ? 0.1 : neighbor.sealing / neighbor.parcels.size();
                    neighborList.add(neighbor);
                }
            }

            room.setNeighborhoods(neighborList);
        }
    }

    private void checkAndAddNeighbor(WorldModule manager, Map<RoomModel, NeighborModel> neighborhood, RoomModel room, ParcelModel parcel, int offsetX, int offsetY) {
        ParcelModel p1 = manager.getParcel(parcel.x + offsetX, parcel.y + offsetY);
        if (p1 != null && (p1.getStructure() != null && !p1.getStructure().isFloor() || p1.getResource() != null && p1.getResource().isRock())) {
            ParcelModel p2 = manager.getParcel(p1.x + offsetX, p1.y + offsetY);
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

    private void refreshAround(ParcelModel parcel) {
        _needRefresh = true;

//        int x = parcel.x;
//        int y = parcel.y;
//
//        parcel.setRoom(null);
//        if (parcel.isRoomOpen()) {
//            _freeParcel.add(parcel);
//        }
//
//        ParcelModel rightParcel = WorldHelper.getParcel(x + 1, y);
//        if (rightParcel != null && rightParcel.getRoom() != null) {
//            _todo.add(rightParcel.getRoom());
//        }
//
//        ParcelModel leftParcel = WorldHelper.getParcel(x - 1, y);
//        if (leftParcel != null && leftParcel.getRoom() != null) {
//            _todo.add(leftParcel.getRoom());
//        }
//
//        ParcelModel topParcel = WorldHelper.getParcel(x, y + 1);
//        if (topParcel != null && topParcel.getRoom() != null) {
//            _todo.add(topParcel.getRoom());
//        }
//
//        ParcelModel bottomParcel = WorldHelper.getParcel(x, y - 1);
//        if (bottomParcel != null && bottomParcel.getRoom() != null) {
//            _todo.add(bottomParcel.getRoom());
//        }
    }

    @Override
    public void onAddStructure(StructureModel structure){
        refreshAround(structure.getParcel());
//        _needRefresh = true;
//        structure.getParcel().setRoom(null);
//        if (!structure.isFloor()) {
//            _needRefresh = true;
//        }
    }

    @Override
    public void onRemoveStructure(StructureModel structure) {
        refreshAround(structure.getParcel());
    }

    @Override
    public void onAddResource(ResourceModel resource){
        refreshAround(resource.getParcel());
//        _needRefresh = true;
//
//        resource.getParcel().setRoom(null);
////        if (!structure.isFloor()) {
////            _needRefresh = true;
////        }
    }

    @Override
    public void onRemoveResource(ResourceModel resource){
        refreshAround(resource.getParcel());
//        _needRefresh = true;
//        resource.getParcel().setRoom(null);
//        if (resource.isRock()) {
//            _needRefresh = true;
//        }
    }

    @Override
    public void onStartGame() {
        refreshRooms();
    }

    public int getLight(int x, int y) {
        RoomModel room = getRoom(x, y);
        return room != null ? room.getLight() : -1;
    }

    public List<ParcelModel> getRoomlessParcels() {
        return _roomlessParcels;
    }
}

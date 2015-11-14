package org.smallbox.faraway.core.game.module.room;

import com.badlogic.gdx.ai.pfa.Connection;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.room.model.NeighborModel;
import org.smallbox.faraway.core.game.module.room.model.RoomModel;
import org.smallbox.faraway.core.game.module.world.WorldModule;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.StructureModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.module.GameModule;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.AsyncTask;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class RoomModule extends GameModule implements GameObserver {
    private static final int                ROOF_MAX_DISTANCE = 6;

    private final Collection<RoomModel>     _rooms = new ConcurrentLinkedQueue<>();
    private boolean                         _needRefresh;
    private AsyncTask<List<RoomModel>>      _task;
    private boolean[]                       _refresh;
    private HashSet<ParcelModel>            _closeList = new HashSet<>();

    public boolean      hasOwnThread() { return true; }

    @Override
    protected void onLoaded(Game game) {
        _updateInterval = 10;
        _needRefresh = true;
        _refresh = new boolean[game.getInfo().worldFloors];
        for (int floor = 0; floor < _refresh.length; floor++) {
            _refresh[floor] = true;
        }

        int width = Game.getInstance().getInfo().worldWidth;
        int height = Game.getInstance().getInfo().worldHeight;

        if (width == 0 || height == 0) {
            throw new RuntimeException("Cannot onCreate RoomModule with 0 sized world old");
        }
    }

    @Override
    protected boolean loadOnStart() {
        return Data.config.manager.room;
    }

    public Collection<RoomModel> getRooms() { return _rooms; }

    protected void onUpdate(int tick) {
        if (_needRefresh) {
            _needRefresh = false;
            for (int floor = 0; floor < _refresh.length; floor++) {
                if (_refresh[floor]) {
                    _refresh[floor] = false;
                    refreshRooms(floor);
                }
            }
        }

        if (_task != null && _task.isComplete()) {
            _task.complete();
            _task = null;
        }
    }

    public void refreshRooms(int floor) {
        System.out.println("RoomModule: refresh floor " + floor);
        long time = System.currentTimeMillis();

        ParcelModel[][][] parcels = ModuleHelper.getWorldModule().getParcels();
        int width = Game.getInstance().getInfo().worldWidth;
        int height = Game.getInstance().getInfo().worldHeight;

        // Clean floor parcel
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                parcels[x][y][floor].setRoom(null);
            }
        }

        // Remove all room for this floor
        _rooms.removeIf(room -> room.getFloor() == floor);

        // Make new rooms on free parcels
        _closeList.clear();
        List<RoomModel> newRooms = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                ParcelModel parcel = parcels[x][y][floor];
                if (!_closeList.contains(parcel) && parcel.isRoomOpen()) {
                    System.out.println("Create new room for parcel " + x + "x" + y);
                    RoomModel room = new RoomModel(RoomModel.RoomType.NONE, floor, parcel);
                    explore(room, parcel, _closeList);
                    checkRoof(ModuleHelper.getWorldModule().getParcels(), room);
                    newRooms.add(room);
                }
            }
        }

        // Make neighborhood for new rooms
        newRooms.forEach(room -> makeNeighborhood(newRooms, room));

        // Add new rooms to list
        _rooms.addAll(newRooms);

        System.out.println("Room list: " + _rooms.size());
        System.out.println("RoomModule: refresh done " + (System.currentTimeMillis() - time));
    }

    private void explore(RoomModel room, ParcelModel parcel, Set<ParcelModel> closeList) {
        double temperature = 0;
        double light = 0;
        double oxygen = 0;
        int nbParcelInRoom = 0;

        Queue<ParcelModel> openList = new ArrayDeque<>(Collections.singleton(parcel));
        while ((parcel = openList.poll()) != null) {
            if (parcel.getConnections() != null && parcel.isRoomOpen()) {

                // Keep old room info
                if (parcel.getRoom() != null) {
                    temperature += parcel.getRoom().getTemperature();
                    oxygen += parcel.getRoom().getOxygen();
                    light += parcel.getRoom().getLight();
                    nbParcelInRoom++;
                }

                parcel.setRoom(room);
                room.getParcels().add(parcel);
                for (Connection<ParcelModel> connection: parcel.getConnections()) {
                    ParcelModel toParcel = connection.getToNode();
                    if (!closeList.contains(toParcel)) {
                        closeList.add(connection.getToNode());
                        if (toParcel.isRoomOpen()) {
                            openList.add(connection.getToNode());
                        }
                    }
                }
            }
        }

        room.setTemperature(temperature / nbParcelInRoom);
        room.setLight(light / nbParcelInRoom);
        room.setOxygen(oxygen / nbParcelInRoom);
    }

    private void checkRoof(ParcelModel[][][] parcels, RoomModel room) {
        printInfo("[RoomModule] Check roof: " + room.getName());
        for (ParcelModel parcel: room.getParcels()) {
            boolean isUnsupported = true;
            for (int i = 0; i < ROOF_MAX_DISTANCE; i++) {
                if (checkAreaCanSupportRoof(parcels, parcel.x + i, parcel.y, parcel.z)) isUnsupported = false;
                if (checkAreaCanSupportRoof(parcels, parcel.x - i, parcel.y, parcel.z)) isUnsupported = false;
                if (checkAreaCanSupportRoof(parcels, parcel.x, parcel.y + i, parcel.z)) isUnsupported = false;
                if (checkAreaCanSupportRoof(parcels, parcel.x, parcel.y - i, parcel.z)) isUnsupported = false;
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

    private boolean checkAreaCanSupportRoof(ParcelModel[][][] parcels, int x, int y, int z) {
        if (x >= 0 && x < parcels.length && y >= 0 && y < parcels[0].length && parcels[x][y][z] != null) {
            return parcels[x][y][z].canSupportRoof();
        }
        return false;
    }

    private void makeNeighborhood(List<RoomModel> roomList, RoomModel room) {
        WorldModule manager = ModuleHelper.getWorldModule();

        // Create neighborhood model form each room
        Map<RoomModel, NeighborModel> neighborhood = roomList.stream()
                .filter(r -> r != room && r.getFloor() == room.getFloor())
                .map(NeighborModel::new)
                .collect(Collectors.toMap(n -> n._room, n -> n));

        // Get all neighbor parcels
        for (ParcelModel parcel: room.getParcels()) {
            checkAndAddNeighbor(manager, neighborhood, room, parcel, -1, 0);
            checkAndAddNeighbor(manager, neighborhood, room, parcel, +1, 0);
            checkAndAddNeighbor(manager, neighborhood, room, parcel, 0, -1);
            checkAndAddNeighbor(manager, neighborhood, room, parcel, 0, +1);
        }

        // Add neighbors to room
        room.setNeighborhoods(neighborhood.values().stream().filter(n -> !n.isEmpty()).collect(Collectors.toList()));

        // Compute sealing
        for (NeighborModel neighbor: room.getNeighbors()) {
            neighbor._borderValue = 0;
            for (ParcelModel parcel : neighbor._parcels) {
                neighbor._borderValue += parcel.getSealValue();
            }
            neighbor._borderValue = neighbor._borderValue / neighbor._parcels.size();
        }
    }

    private void checkAndAddNeighbor(WorldModule manager, Map<RoomModel, NeighborModel> neighborhood, RoomModel room, ParcelModel parcel, int offsetX, int offsetY) {
        ParcelModel p1 = manager.getParcel(parcel.x + offsetX, parcel.y + offsetY);
        if (p1 != null && p1.getRoom() == null) {
            ParcelModel p2 = manager.getParcel(p1.x + offsetX, p1.y + offsetY);
            if (p2 != null && p2.getRoom() != null && p2.getRoom() != room) {
                neighborhood.get(p2.getRoom())._parcels.add(p1);
            }
        }
    }

    private void plantRefresh(int floor) {
        _refresh[floor] = true;
        _needRefresh = true;
    }

//    private void refreshParcel(ParcelModel parcel) {
//        System.out.println("Create new room for parcel " + parcel);
//        RoomModel room = new RoomModel(RoomModel.RoomType.NONE, parcel.z, parcel);
//        explore(room, parcel);
//        checkRoof(ModuleHelper.getWorldModule().getParcels(), room);
//        _rooms.add(room);
//
//        // Remove empty rooms
//        _rooms.removeIf(RoomModel::isEmpty);
//    }
//
//    private void refreshAround(ParcelModel parcel) {
//        ParcelModel t = WorldHelper.getParcel(parcel.x, parcel.y + 1, parcel.z);
//        ParcelModel r = WorldHelper.getParcel(parcel.x + 1, parcel.y, parcel.z);
//        ParcelModel b = WorldHelper.getParcel(parcel.x, parcel.y - 1, parcel.z);
//        ParcelModel l = WorldHelper.getParcel(parcel.x - 1, parcel.y, parcel.z);
//
//        RoomModel lastRoom = null;
//        if (t != null && t.isRoomOpen()) { refreshParcel(t); lastRoom = t.getRoom(); }
//        if (r != null && r.isRoomOpen() && r.getRoom() != lastRoom) { refreshParcel(r); lastRoom = r.getRoom(); }
//        if (b != null && b.isRoomOpen() && b.getRoom() != lastRoom) { refreshParcel(b); lastRoom = b.getRoom(); }
//        if (l != null && l.isRoomOpen() && l.getRoom() != lastRoom) { refreshParcel(l); }
//    }

    @Override
    public void onAddItem(ItemModel item){
    }

    @Override
    public void onRemoveItem(ItemModel item){
    }

    @Override
    public void onStructureComplete(StructureModel structure) {
        plantRefresh(structure.getParcel().z);
    }

    @Override
    public void onAddStructure(StructureModel structure) {
        if (structure.isComplete()) {
            plantRefresh(structure.getParcel().z);
        }
    }

    @Override
    public void onRemoveStructure(StructureModel structure) {
        if (structure.isComplete()) {
            plantRefresh(structure.getParcel().z);
        }
    }

    @Override
    public void onRemoveRock(ParcelModel parcel){
        plantRefresh(parcel.z);
    }
}

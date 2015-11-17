package org.smallbox.faraway.core.game.module.room;

import com.badlogic.gdx.ai.pfa.Connection;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.room.model.NeighborModel;
import org.smallbox.faraway.core.game.module.room.model.RoomModel;
import org.smallbox.faraway.core.game.module.world.OxygenModule;
import org.smallbox.faraway.core.game.module.world.WeatherModule;
import org.smallbox.faraway.core.game.module.world.WorldModule;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.StructureModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
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
    private WeatherModule                   _weatherModule;
    private OxygenModule                    _oxygenModule;

    public boolean      hasOwnThread() { return true; }

    @Override
    protected void onLoaded(Game game) {
        _weatherModule = (WeatherModule)ModuleManager.getInstance().getModule(WeatherModule.class);
        _oxygenModule = (OxygenModule)ModuleManager.getInstance().getModule(OxygenModule.class);
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
                    refreshRooms(floor);
                }
            }
            for (int floor = 0; floor < _refresh.length; floor++) {
                if (_refresh[floor]) {
                    final int f = floor;
                    _rooms.stream().filter(room -> room.getFloor() >= f - 1 && room.getFloor() <= f + 1).forEach(room -> makeNeighborhood(_rooms, room));
                }
            }
            for (int floor = 0; floor < _refresh.length; floor++) {
                _refresh[floor] = false;
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

        // Clean floor parcel
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (parcels[x][y][floor].getRoom() != null && !newRooms.contains(parcels[x][y][floor].getRoom())) {
                    parcels[x][y][floor].setRoom(null);
                }
            }
        }

        // Add new rooms to list
        _rooms.addAll(newRooms);

        System.out.println("Room list: " + _rooms.size());
        System.out.println("RoomModule: refresh done " + (System.currentTimeMillis() - time));
    }

    private void explore(RoomModel room, ParcelModel parcel, Set<ParcelModel> closeList) {
        double temperature = 0;
        double light = 0;
        double oxygen = 0;
        int nbParcel = 0;

        Queue<ParcelModel> openList = new ArrayDeque<>(Collections.singleton(parcel));
        while ((parcel = openList.poll()) != null) {
            if (parcel.getConnections() != null && parcel.isRoomOpen()) {
                // Keep old room info
                temperature += parcel.getRoom() != null ? parcel.getRoom().getTemperature() : _weatherModule.getTemperature();
                oxygen += parcel.getRoom() != null ? parcel.getRoom().getOxygen() : _oxygenModule.getOxygen();
                light += parcel.getRoom() != null ? parcel.getRoom().getLight() : _weatherModule.getLight();
                nbParcel++;

                parcel.setRoom(room);
                room.getParcels().add(parcel);
                for (Connection<ParcelModel> connection: parcel.getConnections()) {
                    ParcelModel toParcel = connection.getToNode();
                    if (parcel.z == toParcel.z && !closeList.contains(toParcel)) {
                        closeList.add(connection.getToNode());
                        if (toParcel.isRoomOpen()) {
                            openList.add(connection.getToNode());
                        }
                    }
                }
            }
        }

        room.setTemperature(temperature / nbParcel);
        room.setLight(light / nbParcel);
        room.setOxygen(oxygen / nbParcel);
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

    private void makeNeighborhood(Collection<RoomModel> roomList, RoomModel room) {
        WorldModule manager = ModuleHelper.getWorldModule();

        // Create neighborhood model form each room
        Map<RoomModel, NeighborModel> neighborhood = roomList.stream()
                .filter(r -> r != room && r.getFloor() >= room.getFloor() - 1 && r.getFloor() <= room.getFloor() + 1)
                .map(NeighborModel::new)
                .collect(Collectors.toMap(n -> n._room, n -> n));

        // Get all neighbor parcels
        for (ParcelModel parcel: room.getParcels()) {
            checkAndAddNeighbor(manager, neighborhood, room, parcel, -1, 0);
            checkAndAddNeighbor(manager, neighborhood, room, parcel, +1, 0);
            checkAndAddNeighbor(manager, neighborhood, room, parcel, 0, -1);
            checkAndAddNeighbor(manager, neighborhood, room, parcel, 0, +1);
            checkAndAddNeighborFloor(manager, neighborhood, room, parcel);
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
        ParcelModel p1 = manager.getParcel(parcel.x + offsetX, parcel.y + offsetY, parcel.z);
        if (p1 != null && p1.getRoom() == null) {
            ParcelModel p2 = manager.getParcel(p1.x + offsetX, p1.y + offsetY, p1.z);
            if (p2 != null && p2.getRoom() != null && p2.getRoom() != room) {
                neighborhood.get(p2.getRoom())._parcels.add(p1);
            }
        }
    }

    private void checkAndAddNeighborFloor(WorldModule manager, Map<RoomModel, NeighborModel> neighborhood, RoomModel room, ParcelModel parcel) {
        ParcelModel parcelUp = manager.getParcel(parcel.x, parcel.y, parcel.z + 1);
        if (parcelUp != null && parcelUp.hasRoom() && (!parcelUp.hasGround() || parcelUp.getGroundInfo().isLinkDown)) {
            neighborhood.get(parcelUp.getRoom())._parcels.add(parcel);
        }
        ParcelModel parcelDown = manager.getParcel(parcel.x, parcel.y, parcel.z - 1);
        if (parcelDown != null && parcelDown.hasRoom() && (!parcel.hasGround() || parcel.getGroundInfo().isLinkDown)) {
            neighborhood.get(parcelDown.getRoom())._parcels.add(parcel);
        }
    }

    private void plantRefresh(int floor) {
        _refresh[floor] = true;
        _needRefresh = true;
    }

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
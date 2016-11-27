package org.smallbox.faraway.module.room;

import com.badlogic.gdx.ai.pfa.Connection;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.module.room.model.RoomConnectionModel;
import org.smallbox.faraway.core.game.module.room.model.RoomModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.StructureModel;
import org.smallbox.faraway.core.util.AsyncTask;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.module.structure.StructureModule;
import org.smallbox.faraway.module.structure.StructureModuleObserver;
import org.smallbox.faraway.module.world.WorldModule;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class RoomModule extends GameModule implements GameObserver {
    @BindModule
    private StructureModule _structureModule;

    @BindModule
    private WorldModule _world;

    private static final int                                    ROOF_MAX_DISTANCE = 6;

    private final List<RoomModel>                               _exteriorRooms = new ArrayList<>();
    private final Collection<RoomModel>                         _rooms = new ConcurrentLinkedQueue<>();
    private boolean                                             _needRefresh;
    private AsyncTask<List<RoomModel>>                          _task;
    private boolean[]                                           _refresh;
    private HashSet<ParcelModel>                                _closeList = new HashSet<>();

    public boolean runOnMainThread() { return false; }

    @Override
    protected void onGameCreate(Game game) {
        _structureModule.addObserver(new StructureModuleObserver() {
            @Override
            public void onAddStructure(StructureModel structure) {
                if (structure.isComplete()) {
                    plantRefresh(structure.getParcel().z);
                }
            }

            @Override
            public void onRemoveStructure(ParcelModel parcel, StructureModel structure) {
                if (structure.isComplete()) {
                    plantRefresh(structure.getParcel().z);
                }
            }

            @Override
            public void onStructureComplete(StructureModel structure) {
            }
        });
    }

    @Override
    public void onGameStart(Game game) {
        _updateInterval = 10;

        _needRefresh = true;
        _refresh = new boolean[game.getInfo().worldFloors];
        for (int floor = 0; floor < game.getInfo().worldFloors; floor++) {
            _refresh[floor] = true;
        }

        _exteriorRooms.clear();
        for (int floor = 0; floor < game.getInfo().worldFloors; floor++) {
            _exteriorRooms.add(new RoomModel(RoomModel.RoomType.NONE, floor, null));
        }
    }

    public Collection<RoomModel> getRooms() { return _rooms; }

    protected void onGameUpdate(Game game, int tick) {
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
        Log.info("org.smallbox.faraway.module.room.RoomModule: refresh floor " + floor);
        long time = System.currentTimeMillis();

        ParcelModel[][][] parcels = _world.getParcels();
        int width = Application.gameManager.getGame().getInfo().worldWidth;
        int height = Application.gameManager.getGame().getInfo().worldHeight;

        // Remove all room for this floor
        _rooms.removeIf(room -> room.getFloor() == floor);

        // Make new rooms on free parcels
        _closeList.clear();
        List<RoomModel> newRooms = new ArrayList<>();
        RoomModel exteriorRoom = _exteriorRooms.get(floor);
        exteriorRoom.setExterior(true);
        newRooms.add(exteriorRoom);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                ParcelModel parcel = parcels[x][y][floor];
                if (!_closeList.contains(parcel)) {
                    if (parcel.isRoomOpen()) {
//                        Log.info("Create new room for parcel " + x + "x" + y);
                        RoomModel room = new RoomModel(RoomModel.RoomType.NONE, floor, parcel);
                        explore(room, exteriorRoom, parcel, _closeList);
                        checkRoof(room);
                        newRooms.add(room);
                    } else {
                        parcel.setRoom(exteriorRoom);
                        exteriorRoom.getParcels().add(parcel);
                    }
                }
            }
        }

        // Add new rooms to list
        _rooms.addAll(newRooms);

        Log.info("Room list: " + _rooms.size());
        Log.info("org.smallbox.faraway.module.room.RoomModule: refresh done " + (System.currentTimeMillis() - time));
    }

    private void explore(RoomModel room, RoomModel exteriorRoom, ParcelModel parcel, Set<ParcelModel> closeList) {
//        double temperature = 0;
//        double light = 0;
//        double oxygen = 0;
        int nbParcel = 0;

        Queue<ParcelModel> openList = new ArrayDeque<>(Collections.singleton(parcel));
        while ((parcel = openList.poll()) != null) {
            if (parcel.getConnections() != null && parcel.isRoomOpen()) {
                // Keep old room info
//                temperature += parcel.getRoom() != null ? parcel.getRoom().getTemperature() : _weatherModule.getTemperature(parcel.z);
//                oxygen += parcel.getRoom() != null ? parcel.getRoom().getOxygen() : _oxygenModule.getOxygen();
//                light += parcel.getRoom() != null ? parcel.getRoom().getLight() : _weatherModule.getLight();
                nbParcel++;

                parcel.setRoom(room);
                room.getParcels().add(parcel);
                for (Connection<ParcelModel> connection: parcel.getConnections()) {
                    ParcelModel toParcel = connection.getToNode();
                    if (parcel.z == toParcel.z && !closeList.contains(toParcel)) {
                        closeList.add(connection.getToNode());
                        if (toParcel.isRoomOpen()) {
                            openList.add(connection.getToNode());
                        } else {
                            connection.getToNode().setRoom(exteriorRoom);
                            exteriorRoom.getParcels().add(connection.getToNode());
                        }
                    }
                }
            }
        }

//        room.setTemperature(temperature / nbParcel);
//        room.setLight(light / nbParcel);
//        room.setOxygen(oxygen / nbParcel);
    }

    private void checkRoof(RoomModel room) {
        printInfo("[org.smallbox.faraway.module.room.RoomModule] Check roof: " + room.getName());
        for (ParcelModel parcel: room.getParcels()) {
            boolean isRoofSupported = false;
            for (int x = parcel.x - ROOF_MAX_DISTANCE; x <= parcel.x + ROOF_MAX_DISTANCE; x++) {
                for (int y = parcel.y - ROOF_MAX_DISTANCE; y <= parcel.y + ROOF_MAX_DISTANCE; y++) {
                    ParcelModel targetParcel = WorldHelper.getParcel(parcel.x + x, parcel.y + y, parcel.z);
                    if (targetParcel == null || targetParcel.canSupportRoof()) {
                        isRoofSupported = true;
                    }
                }
            }
            if (!isRoofSupported) {
                printInfo("[org.smallbox.faraway.module.room.RoomModule] roof collapse");
                room.setExterior(true);
                return;
            }
        }
        printInfo("[org.smallbox.faraway.module.room.RoomModule] roof ok");
        room.setExterior(false);
    }

    private void makeNeighborhood(Collection<RoomModel> roomList, RoomModel room) {
        // Create neighborhood org.smallbox.faraway.core.game.module.room.model form each room
        Map<RoomModel, RoomConnectionModel> neighborhood = roomList.stream()
                .filter(r -> r != room && r.getFloor() >= room.getFloor() - 1 && r.getFloor() <= room.getFloor() + 1)
                .map(RoomConnectionModel::new)
                .collect(Collectors.toMap(n -> n._room, n -> n));
        RoomModel exteriorRoom = _exteriorRooms.get(room.getFloor());
        RoomConnectionModel exteriorConnection = new RoomConnectionModel(exteriorRoom);

        // Get all neighbor parcels
        for (ParcelModel parcel: room.getParcels()) {
            checkAndAddNeighbor(_world, neighborhood, room, exteriorRoom, exteriorConnection, parcel, -1, 0);
            checkAndAddNeighbor(_world, neighborhood, room, exteriorRoom, exteriorConnection, parcel, +1, 0);
            checkAndAddNeighbor(_world, neighborhood, room, exteriorRoom, exteriorConnection, parcel, 0, -1);
            checkAndAddNeighbor(_world, neighborhood, room, exteriorRoom, exteriorConnection, parcel, 0, +1);
            checkAndAddNeighborFloor(_world, neighborhood, room, parcel);
        }
        neighborhood.put(null, exteriorConnection);

        // Add neighbors to room
        room.setConnections(neighborhood.values().stream().filter(connection -> !connection.isEmpty()).collect(Collectors.toList()));

        // Compute permeability
        for (RoomConnectionModel roomConnection: room.getConnections()) {
            int roomFloor = room.getFloor();
            roomConnection._permeability = 0;
            for (ParcelModel parcel : roomConnection._parcels) {
                if (parcel.z == roomFloor + 1) {
                    roomConnection._permeability += parcel.getFloorPermeability();
                } else if (parcel.z == roomFloor - 1) {
                    roomConnection._permeability += parcel.getCeilPermeability();
                } else {
                    roomConnection._permeability += parcel.getPermeability();
                }
            }
            roomConnection._permeability = roomConnection._permeability / roomConnection._parcels.size();
        }
    }

    private void checkAndAddNeighbor(WorldModule manager, Map<RoomModel, RoomConnectionModel> neighborhood, RoomModel room, RoomModel exteriorRoom, RoomConnectionModel exteriorConnection, ParcelModel parcel, int offsetX, int offsetY) {
        ParcelModel p1 = manager.getParcel(parcel.x + offsetX, parcel.y + offsetY, parcel.z);
        if (p1 != null && (p1.getRoom() == null || p1.getRoom() == exteriorRoom)) {
            ParcelModel p2 = manager.getParcel(p1.x + offsetX, p1.y + offsetY, p1.z);
            if (p2 != null) {
                if (p2.getRoom() == exteriorRoom) {
                    exteriorConnection.addParcel(parcel);
                } else if (p2.getRoom() != null && p2.getRoom() != room) {
                    neighborhood.get(p2.getRoom()).addParcel(p1);
                }
            }
        }
    }

    private void checkAndAddNeighborFloor(WorldModule manager, Map<RoomModel, RoomConnectionModel> neighborhood, RoomModel room, ParcelModel parcel) {
        ParcelModel parcelUp = manager.getParcel(parcel.x, parcel.y, parcel.z + 1);
        if (parcelUp != null && parcelUp.hasRoom()) {
            neighborhood.get(parcelUp.getRoom())._parcels.add(parcelUp);
        }
        ParcelModel parcelDown = manager.getParcel(parcel.x, parcel.y, parcel.z - 1);
        if (parcelDown != null && parcelDown.hasRoom()) {
            neighborhood.get(parcelDown.getRoom())._parcels.add(parcelDown);
        }
    }

    private void plantRefresh(int floor) {
        _refresh[floor] = true;
        _needRefresh = true;
    }

    // TODO
//    @Override
//    public void onStructureComplete(StructureModel structure) {
//        plantRefresh(structure.getParcel().z);
//    }

    @Override
    public void onRemoveRock(ParcelModel parcel){
        plantRefresh(parcel.z);
    }
}
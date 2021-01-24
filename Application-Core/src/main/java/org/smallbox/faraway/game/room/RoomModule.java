package org.smallbox.faraway.game.room;

import org.smallbox.faraway.util.GameException;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.module.SuperGameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.room.model.*;
import org.smallbox.faraway.game.weather.WeatherModule;
import org.smallbox.faraway.game.world.WorldModule;
import org.smallbox.faraway.util.AsyncTask;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

@GameObject
public class RoomModule extends SuperGameModule {
    @Inject private WorldModule worldModule;
    @Inject private WeatherModule weatherModule;

    private final List<RoomModel>                               _exteriorRooms = new ArrayList<>();
    private final Collection<RoomModel>                         _rooms = new ConcurrentLinkedQueue<>();
    private final Collection<Class<? extends RoomModel>>              _roomClasses = new LinkedBlockingQueue<>();
    private boolean                                             _needRefresh;
    private AsyncTask<List<RoomModel>>                          _task;
    private boolean[]                                           _refresh;
    private final HashSet<Parcel>                                _closeList = new HashSet<>();

    public boolean runOnMainThread() { return false; }

    public void addRoomClass(Class<? extends RoomModel> cls) {
        _roomClasses.add(cls);
    }

    public Collection<Class<? extends RoomModel>> getRoomClasses() {
        return _roomClasses;
    }

    @Override
    public void onGameCreate(Game game) {
        addRoomClass(QuarterRoom.class);
        addRoomClass(SickbayRoom.class);
        addRoomClass(CommonRoom.class);
        addRoomClass(CellRoom.class);
        addRoomClass(TechnicalRoom.class);
    }

    @Override
    public void onGameStart(Game game) {

//        _updateInterval = 10;
//
//        _needRefresh = true;
//        _refresh = new boolean[game.getInfo().worldFloors];
//        for (int floor = 0; floor < game.getInfo().worldFloors; floor++) {
//            _refresh[floor] = true;
//        }
//
//        _exteriorRooms.clear();
//        for (int floor = 0; floor < game.getInfo().worldFloors; floor++) {
//            _exteriorRooms.add(new RoomModel(RoomModel.RoomType.NONE, floor, null));
//        }
    }

    public Collection<RoomModel> getRooms() { return _rooms; }

    @Override
    protected void onModuleUpdate(Game game) {

        // TODO
        _rooms.forEach(room -> room.setTemperature(weatherModule.getTemperature()));
        _rooms.forEach(room -> room.setLight(weatherModule.getLight()));

//        if (_needRefresh) {
//            _needRefresh = false;
//            for (int floor = 0; floor < _refresh.length; floor++) {
//                if (_refresh[floor]) {
//                    refreshRooms(floor);
//                }
//            }
//            for (int floor = 0; floor < _refresh.length; floor++) {
//                if (_refresh[floor]) {
//                    final int f = floor;
//                    _rooms.stream().filter(room -> room.getFloor() >= f - 1 && room.getFloor() <= f + 1).forEach(room -> makeNeighborhood(_rooms, room));
//                }
//            }
//            for (int floor = 0; floor < _refresh.length; floor++) {
//                _refresh[floor] = false;
//            }
//        }
//
//        if (_task != null && _task.isComplete()) {
//            _task.complete();
//            _task = null;
//        }
    }

    public <T extends RoomModel> T addRoom(Class<T> cls, Collection<Parcel> parcels) {
        T existingArea = _rooms.stream()
                .filter(cls::isInstance)
                .filter(area -> area.getParcels().stream().anyMatch(parcels::contains))
                .map(cls::cast)
                .findAny().orElse(null);

        // Add parcel to existing room
        if (existingArea != null) {
            parcels.forEach(existingArea::addParcel);
            return existingArea;
        }

        // Create new room
        try {
            T room = cls.getConstructor().newInstance();
            parcels.forEach(room::addParcel);
            _rooms.add(room);
            return room;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new GameException(RoomModule.class, "Cannot create room: " + cls.getSimpleName());
        }
    }

    public void removeArea(List<Parcel> parcels) {
        _rooms.forEach(room -> parcels.forEach(room::removeParcel));
        _rooms.removeIf(area -> area.getParcels().isEmpty());
    }

    public RoomModel getRoom(Parcel parcel) {
        return _rooms.stream().filter(room -> room.hasParcel(parcel)).findFirst().orElse(null);
    }

//    public void refreshRooms(int floor) {
//        Log.info("org.smallbox.faraway.module.room.RoomModule: onDisplayMultiple floor " + floor);
//        long time = System.currentTimeMillis();
//
//        ParcelModel[][][] parcels = worldModule.getParcelsByType();
//        int width = Application.gameManager.getGame().getInfo().worldWidth;
//        int height = Application.gameManager.getGame().getInfo().worldHeight;
//
//        // Remove all room for this floor
//        _rooms.removeIf(room -> room.getFloor() == floor);
//
//        // Make new rooms on free parcels
//        _closeList.clear();
//        List<RoomModel> newRooms = new ArrayList<>();
//        RoomModel exteriorRoom = _exteriorRooms.get(floor);
//        exteriorRoom.setExterior(true);
//        newRooms.add(exteriorRoom);
//        for (int x = 0; x < width; x++) {
//            for (int y = 0; y < height; y++) {
//                ParcelModel parcel = parcels[x][y][floor];
//                if (!_closeList.contains(parcel)) {
//                    if (parcel.isRoomOpen()) {
////                        Log.info("Create new room for parcel " + x + "x" + y);
//                        RoomModel room = new RoomModel(RoomModel.RoomType.NONE, floor, parcel);
//                        explore(room, exteriorRoom, parcel, _closeList);
//                        checkRoof(room);
//                        newRooms.add(room);
//                    } else {
//                        parcel.setRoom(exteriorRoom);
//                        exteriorRoom.getParcelsByType().add(parcel);
//                    }
//                }
//            }
//        }
//
//        // Add new rooms to list
//        _rooms.addAll(newRooms);
//
//        Log.info("Room list: " + _rooms.size());
//        Log.info("org.smallbox.faraway.module.room.RoomModule: onDisplayMultiple done " + (System.currentTimeMillis() - time));
//    }
//
//    private void explore(RoomModel room, RoomModel exteriorRoom, ParcelModel parcel, Set<ParcelModel> closeList) {
////        double temperature = 0;
////        double light = 0;
////        double oxygen = 0;
//        int nbParcel = 0;
//
//        Queue<ParcelModel> openList = new ArrayDeque<>(Collections.singleton(parcel));
//        while ((parcel = openList.poll()) != null) {
//            if (parcel.getConnections() != null && parcel.isRoomOpen()) {
//                // Keep old room info
////                temperature += parcel.getRoom() != null ? parcel.getRoom().getTemperatureFloor() : _weatherModule.getTemperatureFloor(parcel.z);
////                oxygen += parcel.getRoom() != null ? parcel.getRoom().getOxygen() : _oxygenModule.getOxygen();
////                light += parcel.getRoom() != null ? parcel.getRoom().getLight() : _weatherModule.getLight();
//                nbParcel++;
//
//                parcel.setRoom(room);
//                room.getParcelsByType().add(parcel);
//                for (Connection<ParcelModel> connection: parcel.getConnections()) {
//                    ParcelModel toParcel = connection.getToNode();
//                    if (parcel.z == toParcel.z && !closeList.contains(toParcel)) {
//                        closeList.add(connection.getToNode());
//                        if (toParcel.isRoomOpen()) {
//                            openList.add(connection.getToNode());
//                        } else {
//                            connection.getToNode().setRoom(exteriorRoom);
//                            exteriorRoom.getParcelsByType().add(connection.getToNode());
//                        }
//                    }
//                }
//            }
//        }
//
////        room.setTemperature(temperature / nbParcel);
////        room.setLight(light / nbParcel);
////        room.setOxygen(oxygen / nbParcel);
//    }
//
//    private void checkRoof(RoomModel room) {
//        printInfo("[org.smallbox.faraway.module.room.RoomModule] Check roof: " + room.getName());
//        for (ParcelModel parcel: room.getParcelsByType()) {
//            boolean isRoofSupported = false;
//            for (int x = parcel.x - ROOF_MAX_DISTANCE; x <= parcel.x + ROOF_MAX_DISTANCE; x++) {
//                for (int y = parcel.y - ROOF_MAX_DISTANCE; y <= parcel.y + ROOF_MAX_DISTANCE; y++) {
//                    ParcelModel targetParcel = WorldHelper.getParcel(parcel.x + x, parcel.y + y, parcel.z);
//                    if (targetParcel == null || targetParcel.canSupportRoof()) {
//                        isRoofSupported = true;
//                    }
//                }
//            }
//            if (!isRoofSupported) {
//                printInfo("[org.smallbox.faraway.module.room.RoomModule] roof collapse");
//                room.setExterior(true);
//                return;
//            }
//        }
//        printInfo("[org.smallbox.faraway.module.room.RoomModule] roof ok");
//        room.setExterior(false);
//    }
//
//    private void makeNeighborhood(Collection<RoomModel> roomList, RoomModel room) {
//        // Create neighborhood org.smallbox.faraway.core.module.room.model form each room
//        Map<RoomModel, RoomConnectionModel> neighborhood = roomList.stream()
//                .filter(r -> r != room && r.getFloor() >= room.getFloor() - 1 && r.getFloor() <= room.getFloor() + 1)
//                .map(RoomConnectionModel::new)
//                .collect(Collectors.toMap(n -> n._room, n -> n));
//        RoomModel exteriorRoom = _exteriorRooms.get(room.getFloor());
//        RoomConnectionModel exteriorConnection = new RoomConnectionModel(exteriorRoom);
//
//        // Get all neighbor parcels
//        for (ParcelModel parcel: room.getParcelsByType()) {
//            checkAndAddNeighbor(worldModule, neighborhood, room, exteriorRoom, exteriorConnection, parcel, -1, 0);
//            checkAndAddNeighbor(worldModule, neighborhood, room, exteriorRoom, exteriorConnection, parcel, +1, 0);
//            checkAndAddNeighbor(worldModule, neighborhood, room, exteriorRoom, exteriorConnection, parcel, 0, -1);
//            checkAndAddNeighbor(worldModule, neighborhood, room, exteriorRoom, exteriorConnection, parcel, 0, +1);
//            checkAndAddNeighborFloor(worldModule, neighborhood, room, parcel);
//        }
//        neighborhood.put(null, exteriorConnection);
//
//        // Add neighbors to room
//        room.setConnections(neighborhood.values().stream().filter(connection -> !connection.isEmpty()).collect(Collectors.toList()));
//
//        // Compute permeability
//        for (RoomConnectionModel roomConnection: room.getConnections()) {
//            int roomFloor = room.getFloor();
//            roomConnection._permeability = 0;
//            for (ParcelModel parcel : roomConnection._parcels) {
//                if (parcel.z == roomFloor + 1) {
//                    roomConnection._permeability += parcel.getFloorPermeability();
//                } else if (parcel.z == roomFloor - 1) {
//                    roomConnection._permeability += parcel.getCeilPermeability();
//                } else {
//                    roomConnection._permeability += parcel.getPermeability();
//                }
//            }
//            roomConnection._permeability = roomConnection._permeability / roomConnection._parcels.size();
//        }
//    }
//
//    private void checkAndAddNeighbor(WorldModule manager, Map<RoomModel, RoomConnectionModel> neighborhood, RoomModel room, RoomModel exteriorRoom, RoomConnectionModel exteriorConnection, ParcelModel parcel, int offsetX, int offsetY) {
//        ParcelModel p1 = manager.getParcel(parcel.x + offsetX, parcel.y + offsetY, parcel.z);
//        if (p1 != null && (p1.getRoom() == null || p1.getRoom() == exteriorRoom)) {
//            ParcelModel p2 = manager.getParcel(p1.x + offsetX, p1.y + offsetY, p1.z);
//            if (p2 != null) {
//                if (p2.getRoom() == exteriorRoom) {
//                    exteriorConnection.addParcel(parcel);
//                } else if (p2.getRoom() != null && p2.getRoom() != room) {
//                    neighborhood.get(p2.getRoom()).addParcel(p1);
//                }
//            }
//        }
//    }
//
//    private void checkAndAddNeighborFloor(WorldModule manager, Map<RoomModel, RoomConnectionModel> neighborhood, RoomModel room, ParcelModel parcel) {
//        ParcelModel parcelUp = manager.getParcel(parcel.x, parcel.y, parcel.z + 1);
//        if (parcelUp != null && parcelUp.hasRoom()) {
//            neighborhood.get(parcelUp.getRoom())._parcels.add(parcelUp);
//        }
//        ParcelModel parcelDown = manager.getParcel(parcel.x, parcel.y, parcel.z - 1);
//        if (parcelDown != null && parcelDown.hasRoom()) {
//            neighborhood.get(parcelDown.getRoom())._parcels.add(parcelDown);
//        }
//    }
}
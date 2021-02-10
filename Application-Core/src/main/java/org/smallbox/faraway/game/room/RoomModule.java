package org.smallbox.faraway.game.room;

import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.GenericMapGameModule;
import org.smallbox.faraway.game.item.ItemModule;
import org.smallbox.faraway.game.room.model.*;
import org.smallbox.faraway.game.weather.WeatherModule;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.WorldModule;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

@GameObject
public class RoomModule extends GenericMapGameModule<String, RoomModel> {
    @Inject private WorldModule worldModule;
    @Inject private WeatherModule weatherModule;
    @Inject private ApplicationConfig applicationConfig;
    @Inject private ItemModule itemModule;
    @Inject private RoomExplorer roomExplorer;

    private final Collection<Class<? extends RoomModel>>              _roomClasses = new LinkedBlockingQueue<>();

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
    public void onGameLongUpdate(Game game) {
        roomExplorer.refresh();
    }

    public RoomModel getRoom(Parcel parcel) {
        return getAll().stream().filter(room -> room.hasParcel(parcel)).findFirst().orElse(null);
    }

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
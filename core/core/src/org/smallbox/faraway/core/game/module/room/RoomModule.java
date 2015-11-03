package org.smallbox.faraway.core.game.module.room;

import com.badlogic.gdx.ai.pfa.Connection;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.module.room.model.NeighborModel;
import org.smallbox.faraway.core.game.module.room.model.RoomModel;
import org.smallbox.faraway.core.game.module.room.model.RoomModel.RoomType;
import org.smallbox.faraway.core.game.module.world.WorldModule;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.StructureModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.game.module.world.model.resource.ResourceModel;
import org.smallbox.faraway.core.module.GameModule;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RoomModule extends GameModule implements GameObserver {
    private final List<RoomModel>       _roomList = new ArrayList<>();
    private boolean                     _needRefresh;
    private AsyncTask<List<RoomModel>>  _task;

    @Override
    protected void onLoaded() {
        _updateInterval = 10;

        int width = Game.getInstance().getInfo().worldWidth;
        int height = Game.getInstance().getInfo().worldHeight;

        if (width == 0 || height == 0) {
            throw new RuntimeException("Cannot onCreate RoomModule with 0 sized world old");
        }
    }

    @Override
    protected boolean loadOnStart() {
        return GameData.config.manager.room;
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

    public void refreshRooms() {
        _needRefresh = false;

        printInfo("RoomModule: refresh");
        long time = System.currentTimeMillis();

        // Reconstruct each room
        _roomList.forEach(room -> {
            synchronized (room.getParcels()) {
                ParcelModel parcel = room.getBaseParcel();
                room.getParcels().forEach(p -> p.setRoom(null));
                room.getParcels().clear();
                explore(room, parcel);
                checkRoof(ModuleHelper.getWorldModule().getParcels(), room);
            }
        });

        // Make new rooms on free parcels
        List<RoomModel> newRooms = new ArrayList<>();
        ModuleHelper.getWorldModule().getParcelList().forEach(parcel -> {
            if (parcel.getRoom() == null && parcel.isRoomOpen()) {
                RoomModel room = new RoomModel(RoomType.NONE);
                explore(room, parcel);
                checkRoof(ModuleHelper.getWorldModule().getParcels(), room);
                newRooms.add(room);
            }
        });

        synchronized (_roomList) {
            // Add new rooms to list
            _roomList.addAll(newRooms);

            // Merge all exterior rooms
            RoomModel firstExterior = null;
            for (RoomModel room: _roomList) {
                if (room.isExterior()) {
                    if (firstExterior == null) {
                        firstExterior = room;
                    } else {
                        firstExterior.addParcels(room.getParcels());
                        room.getParcels().clear();
                    }
                }
            }

            // Remove empty rooms
            _roomList.removeIf(RoomModel::isEmpty);
        }

        _roomList.forEach(room -> makeNeighborhood(room));

        printInfo("RoomModule: refresh done " + (System.currentTimeMillis() - time));
    }

    private void explore(RoomModel room, ParcelModel parcel) {
        if (parcel.getConnections() != null && parcel.isRoomOpen()) {
            parcel.setRoom(room);
            room.setBaseParcel(parcel);
            room.getParcels().add(parcel);
            for (int i = parcel.getConnections().size - 1; i >= 0; i--) {
                Connection<ParcelModel> connection = parcel.getConnections().get(i);
                if (!room.getParcels().contains(connection.getToNode()) && connection.getToNode().isRoomOpen()) {
                    explore(room, connection.getToNode());
                }
            }
        }
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

    private boolean checkAreaCanSupportRoof(ParcelModel[][][] parcels, int x, int y) {
        if (x >= 0 && x < parcels.length && y >= 0 && y < parcels[0].length && parcels[x][y][0] != null) {
            return parcels[x][y][0].canSupportRoof();
        }
        return false;
    }

    private void makeNeighborhood(RoomModel room) {
        WorldModule manager = ModuleHelper.getWorldModule();

        // Create neighborhood model form each room
        Map<RoomModel, NeighborModel> neighborhood = _roomList.stream()
                .filter(r -> r != room)
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

    private void refreshAround(ParcelModel parcel) {
        _needRefresh = true;
    }

    @Override
    public void onAddItem(ItemModel item){
    }

    @Override
    public void onAddStructure(StructureModel structure){
        refreshAround(structure.getParcel());
    }

    @Override
    public void onRemoveStructure(StructureModel structure) {
        refreshAround(structure.getParcel());
    }

    @Override
    public void onAddResource(ResourceModel resource){
        refreshAround(resource.getParcel());
    }

    @Override
    public void onRemoveResource(ResourceModel resource){
        refreshAround(resource.getParcel());
    }

    @Override
    public void onGameStart() {
        refreshRooms();
    }
}

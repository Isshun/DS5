
package org.smallbox.faraway.game.manager;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.GameObserver;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.character.base.CharacterRelation;
import org.smallbox.faraway.game.model.character.base.CharacterRelation.Relation;
import org.smallbox.faraway.game.model.item.ItemModel;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.item.ResourceModel;
import org.smallbox.faraway.game.model.item.StructureModel;
import org.smallbox.faraway.game.model.room.NeighborModel;
import org.smallbox.faraway.game.model.room.RoomModel;
import org.smallbox.faraway.game.model.room.RoomModel.RoomType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomManager extends BaseManager implements GameObserver {
    private static final int    UPDATE_INTERVAL = 10;

    private RoomModel[][] 		_rooms;
    private List<RoomModel>		_roomList;
    private int                 _width;
    private int                 _height;
    private ParcelModel[][][]   _parcels;
    private double[][]          _oxygenLevels;

    @Override
    protected void onCreate() {
        int width = Game.getWorldManager().getWidth();
        int height = Game.getWorldManager().getHeight();

        if (width == 0 || height == 0) {
            throw new RuntimeException("Cannot create RoomManager with 0 sized world map");
        }

        double oxygenLevel = Game.getInstance().getPlanet().getOxygen();
        _roomList = new ArrayList<>();
        _rooms = new RoomModel[width][height];
        _oxygenLevels = new double[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                _oxygenLevels[x][y] = oxygenLevel;
            }
        }

        refreshRooms();
    }

    public void add(RoomModel room) {
        if (room == null) {
            Log.error("room cannot be null");
            return;
        }

        if (_roomList.contains(room)) {
            Log.error("room already exists");
            return;
        }

        _roomList.add(room);

        for (ParcelModel area: room.getParcels()) {
            _rooms[area.getX()][area.getY()] = room;
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

    public RoomModel take(CharacterModel character, RoomType type) {
        if (character.getQuarter() != null) {
            return character.getQuarter();
        }

        // Check relations
        List<CharacterRelation> relations = character.getRelations();
        for (CharacterRelation relation: relations) {

            // Check if relation have there own quarters
            RoomModel relationQuarter = relation.getSecond().getQuarter();
            if (relationQuarter != null) {

                // Live in parent's quarters
                if (relation.getRelation() == Relation.PARENT && character.getOld() <= Constant.CHARACTER_LEAVE_HOME_OLD) {
                    character.setQuarter(relationQuarter);
                    relationQuarter.addOccupant(character);
                    return relationQuarter;
                }

                // Live with mate
                if (relation.getRelation() == Relation.MATE && character.getOld() > Constant.CHARACTER_LEAVE_HOME_OLD) {
                    character.setQuarter(relationQuarter);
                    relationQuarter.addOccupant(character);
                    return relationQuarter;
                }

                // Children take the quarters first
                if (relation.getRelation() == Relation.CHILDREN && relation.getSecond().getOld() <= Constant.CHARACTER_LEAVE_HOME_OLD) {
                    character.setQuarter(relationQuarter);
                    relationQuarter.addOccupant(character);
                    return relationQuarter;
                }
            }
        }

        for (RoomModel room: _roomList) {
            if (room.isType(type) && room.getOwner() == null) {
                room.setOwner(character);
                room.addOccupant(character);
                character.setQuarter(room);
                return room;
            }
        }

        return null;
    }

    protected void onUpdate(int tick) {
        if (tick % UPDATE_INTERVAL == 0) {
            _roomList.forEach(RoomModel::update);
        }
    }

    public void makeRooms() {
        _width = Game.getWorldManager().getWidth();
        _height = Game.getWorldManager().getHeight();
        _parcels = Game.getWorldManager().getAreas();

        for (int x = 0; x < _width; x++) {
            for (int y = 0; y < _height; y++) {
                _parcels[x][y][0].setRoom(null);
            }
        }

        boolean newRoomFound;
        do {
            newRoomFound = false;
            for (int x = 0; x < _width; x++) {
                for (int y = 0; y < _height; y++) {
                    ParcelModel area = _parcels[x][y][0];
                    boolean isPassable = true;
                    if (area.getStructure() != null && (area.getStructure().isSolid() || area.getStructure().isDoor())) {
                        isPassable = false;
                    }
                    if (area.getResource() != null && area.getResource().isSolid()) {
                        isPassable = false;
                    }
                    if (area.getRoom() == null && isPassable) {
                        newRoomFound = true;
                        RoomModel room = new RoomModel(RoomType.NONE);
                        room.setExterior(false);
                        _roomList.add(room);
                        try {
                            exploreRoom(room, x, y);
                        } catch (StackOverflowError e) {
                        }
                    }
                }
            }
        } while (newRoomFound);
    }

    private void exploreRoom(RoomModel room, int x, int y) {
        if (x >= 0 && x < _width && y >= 0 && y < _height && _parcels[x][y][0] != null) {
            ParcelModel parcel = _parcels[x][y][0];
            boolean isPassable = true;
            if (parcel.getStructure() != null && (parcel.getStructure().isSolid() || parcel.getStructure().isDoor())) {
                isPassable = false;
            }
            if (parcel.getResource() != null && parcel.getResource().isSolid()) {
                isPassable = false;
            }
            if (parcel.getRoom() == null && isPassable) {
                parcel.setRoom(room);
                room.addParcel(parcel);

                boolean isUnsupported = true;
                for (int i = 0; i < 6; i++) {
                    if (checkAreaCanSupportRoof(x + i, y)) isUnsupported = false;
                    if (checkAreaCanSupportRoof(x - i, y)) isUnsupported = false;
                    if (checkAreaCanSupportRoof(x, y + i)) isUnsupported = false;
                    if (checkAreaCanSupportRoof(x, y - i)) isUnsupported = false;
                }
                if (isUnsupported) room.setExterior(true);

                exploreRoom(room, x - 1, y);
                exploreRoom(room, x + 1, y);
                exploreRoom(room, x, y - 1);
                exploreRoom(room, x, y + 1);
            }
        }
    }

    private boolean checkAreaCanSupportRoof(int x, int y) {
        if (x >= 0 && x < _width && y >= 0 && y < _height && _parcels[x][y][0] != null) {
            return _parcels[x][y][0].canSupportRoof();
        }
        return false;
    }

    private void refreshRooms() {
        Log.info("RoomManager: refresh");

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
        for (RoomModel room: _roomList) {
            double oxygen = 0;
            for (ParcelModel parcel: room.getParcels()) {
                if (parcel != null) {
                    oxygen += _oxygenLevels[parcel.getX()][parcel.getY()];
                }
            }
            room.setOxygen(oxygen / room.getParcels().size());
        }
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
            refreshRooms();
        }
    }

    @Override
    public void onRemoveStructure(StructureModel structure) {
        if (!structure.isFloor()) {
            refreshRooms();
        }
    }

    @Override
    public void onRemoveResource(ResourceModel resource){
        if (resource.isRock()) {
            refreshRooms();
        }
    }

    public int getLight(int x, int y) {
        RoomModel room = getRoom(x, y);
        return room != null ? room.getLight() : -1;
    }
}

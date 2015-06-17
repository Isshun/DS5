
package org.smallbox.faraway.manager;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.GameObserver;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.model.character.base.CharacterModel;
import org.smallbox.faraway.model.character.base.CharacterRelation;
import org.smallbox.faraway.model.character.base.CharacterRelation.Relation;
import org.smallbox.faraway.model.item.ItemModel;
import org.smallbox.faraway.model.item.ParcelModel;
import org.smallbox.faraway.model.item.ResourceModel;
import org.smallbox.faraway.model.item.StructureModel;
import org.smallbox.faraway.model.room.RoomModel;
import org.smallbox.faraway.model.room.RoomModel.RoomType;

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
    private ParcelModel[][][]   _areas;

    public RoomManager() {
        _rooms = new RoomModel[Constant.WORLD_WIDTH][Constant.WORLD_HEIGHT];
        _roomList = new ArrayList<>();
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
        _areas = Game.getWorldManager().getAreas();

        for (int x = 0; x < _width; x++) {
            for (int y = 0; y < _height; y++) {
                _areas[x][y][0].setRoom(null);
            }
        }

        boolean newRoomFound;
        do {
            newRoomFound = false;
            for (int x = 0; x < _width; x++) {
                for (int y = 0; y < _height; y++) {
                    ParcelModel area = _areas[x][y][0];
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
        if (x >= 0 && x < _width && y >= 0 && y < _height && _areas[x][y][0] != null) {
            ParcelModel area = _areas[x][y][0];
            boolean isPassable = true;
            if (area.getStructure() != null && (area.getStructure().isSolid() || area.getStructure().isDoor())) {
                isPassable = false;
            }
            if (area.getResource() != null && area.getResource().isSolid()) {
                isPassable = false;
            }
            if (area.getRoom() == null && isPassable) {
                area.setRoom(room);
                room.addArea(area);

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
        if (x >= 0 && x < _width && y >= 0 && y < _height && _areas[x][y][0] != null) {
            return _areas[x][y][0].canSupportRoof();
        }
        return false;
    }

    @Override
    public void onAddStructure(StructureModel structure){
        refreshRooms();
    }

    private void refreshRooms() {
        _roomList.clear();
        makeRooms();
        makeNeighborhood();
    }

    private void makeNeighborhood() {
        WorldManager manager = Game.getWorldManager();

        for (RoomModel room: _roomList) {
            // Init neighborhood
            Map<RoomModel, Integer> neighborhood = new HashMap<>();
            for (RoomModel neighbor: _roomList) {
                if (neighbor != room) {
                    neighborhood.put(neighbor, 0);
                }
            }

            for (ParcelModel parcel: room.getParcels()) {
                checkAndAddNeighbor(manager, neighborhood, room, parcel, -1, 0);
                checkAndAddNeighbor(manager, neighborhood, room, parcel, +1, 0);
                checkAndAddNeighbor(manager, neighborhood, room, parcel, 0, -1);
                checkAndAddNeighbor(manager, neighborhood, room, parcel, 0, +1);
            }

            room.setNeighborhoods(neighborhood);
        }
    }

    private void checkAndAddNeighbor(WorldManager manager, Map<RoomModel, Integer> neighborhood, RoomModel room, ParcelModel parcel, int offsetX, int offsetY) {
        ParcelModel p1 = manager.getParcel(parcel.getX() + offsetX, parcel.getY() + offsetY);
        if (p1 != null && (p1.getStructure() != null && !p1.getStructure().isFloor() || p1.getResource() != null && p1.getResource().isRock())) {
            ParcelModel p2 = manager.getParcel(p1.getX() + offsetX, p1.getY() + offsetY);
            if (p2 != null && p2.getRoom() != null && p2.getRoom() != room) {
                neighborhood.put(p2.getRoom(), neighborhood.get(p2.getRoom()) + 1);
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
    public void onRemoveStructure(StructureModel structure){
        refreshRooms();
    }

    @Override
    public void onRemoveResource(ResourceModel resource){
        refreshRooms();
    }

    public int getLight(int x, int y) {
        RoomModel room = getRoom(x, y);
        return room != null ? room.getLight() : -1;
    }
}


package org.smallbox.faraway.manager;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.WorldObserver;
import org.smallbox.faraway.engine.renderer.MainRenderer;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.character.CharacterRelation;
import org.smallbox.faraway.model.character.CharacterRelation.Relation;
import org.smallbox.faraway.model.item.*;
import org.smallbox.faraway.model.room.GardenRoom;
import org.smallbox.faraway.model.room.QuarterRoom;
import org.smallbox.faraway.model.room.RoomModel;
import org.smallbox.faraway.model.room.RoomModel.RoomType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoomManager implements WorldObserver {
    private RoomModel[][] 			_rooms;
    private List<RoomModel>			_roomList;
    private RoomModel _currentDiffuseRoom;
    private int                 _width;
    private int                 _height;
    private ParcelModel[][][]     _areas;

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

        for (ParcelModel area: room.getAreas()) {
            _rooms[area.getX()][area.getY()] = room;
        }
    }

    public RoomModel putRoom(int startX, int startY, int fromX, int fromY, int toX, int toY, RoomType type, CharacterModel owner) {
        Log.info("RoomManager: put room from " + fromX + "x" + fromY + " to " + toX + "x" + toY);

        if (type == null) {
            Log.error("RoomManager: cannot put new room with NULL type");
            return null;
        }

        RoomModel existingRoom = null;
        RoomModel tempRoom = null;
        int existingRoomPosX = 0;
        int existingRoomPosY = 0;

        // Check if room already exists on start area
        if (startX > 0 && startY > 0 && startX < Constant.WORLD_WIDTH && startY < Constant.WORLD_HEIGHT && _rooms[startX][startY] != null) {
            existingRoom = _rooms[startX][startY];
            existingRoomPosX = startX;
            existingRoomPosY = startY;
        }

        // Check on others areas
        else {
            for (int x = fromX - 1; x <= toX + 1; x++) {
                for (int y = fromY - 1; y <= toY + 1; y++) {
                    if ((x >= fromX && x <= toX || y >= fromY && y <= toY) && x > 0 && y > 0 && x < Constant.WORLD_WIDTH && y < Constant.WORLD_HEIGHT && _rooms[x][y] != null && _rooms[x][y].getType() == type) {
                        existingRoom = _rooms[x][y];
//						tempRoom = existingRoom;
                        existingRoomPosX = x;
                        existingRoomPosY = y;
                        break;
                    }
                }
            }
        }

        // Create new room if not exist
        if (tempRoom == null) {
            tempRoom = createRoom(type, owner);
        }

        // Set room for each area
        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                if (x >= 0 && y >= 0 && x < Constant.WORLD_WIDTH && y < Constant.WORLD_HEIGHT) {
                    StructureModel struct = ServiceManager.getWorldMap().getStructure(x, y);
                    if (struct == null || struct.roomCanBeSet()) {
                        if (_rooms[x][y] == null || _rooms[x][y].getType() != tempRoom.getType()) {
                            ParcelModel area = ServiceManager.getWorldMap().getParcel(x, y);
                            area.setRoom(tempRoom);
                            tempRoom.addArea(area);
                            _rooms[x][y] = tempRoom;
                            MainRenderer.getInstance().invalidate(x, y);
                        }
                    }
                }
            }
        }

        // If already existing room
        if (existingRoom != null) {
            _currentDiffuseRoom = existingRoom;
            diffuseRoom(tempRoom, existingRoomPosX, existingRoomPosY);
        }

        int leftRoom = Integer.MAX_VALUE;
        while (tempRoom.getAreas().size() != 0 && tempRoom.getAreas().size() < leftRoom) {
            leftRoom = tempRoom.getAreas().size();
            RoomModel newRoom = createRoom(tempRoom.getType(), owner);
            _roomList.add(newRoom);
            ParcelModel area = tempRoom.getAreas().get(0);
            replaceArea(newRoom, tempRoom, area.getX(), area.getY());
            _currentDiffuseRoom = newRoom;
            diffuseRoom(tempRoom, area.getX(), area.getY());
            newRoom.refreshPosition();
        }

        if (type == RoomType.GARDEN) {
            ResourceManager.getInstance().refreshWater();
        }

        // Refresh start position
        tempRoom.refreshPosition();

        return tempRoom;
    }

    private RoomModel createRoom(RoomType type, CharacterModel owner) {
        RoomModel room = null;

        if (type == RoomType.GARDEN) {
            room = new GardenRoom();
        } else if (type == RoomType.QUARTER) {
            room = new QuarterRoom();
        } else {
            room = new RoomModel(type);
        }
        room.setOwner(owner);
        room.refreshPosition();

        return room;
    }

    private void diffuseRoom(RoomModel tempRoom, int x, int y) {
        RoomModel neighboorRoom = getRoom(x+1, y);

        // If neighboorRoom IS NOT tempRoom AND IS NOT _currentDiffuseRoom, it's an old existing room
        // so we replace all room previously set to _currentDiffuseRoom by this new room
        if (neighboorRoom != null && neighboorRoom != tempRoom && neighboorRoom != _currentDiffuseRoom) {
            List<ParcelModel> areasCopy = new ArrayList<ParcelModel>(_currentDiffuseRoom.getAreas());
            for (ParcelModel area: areasCopy) {
                replaceArea(neighboorRoom, _currentDiffuseRoom, area.getX(), area.getY());
            }
            _currentDiffuseRoom = neighboorRoom;
        }

        // If neighboorRoom IS NOT _currentDiffuseRoom, transform to _currentDiffuseRoom
        if (neighboorRoom != null && neighboorRoom != _currentDiffuseRoom) {
            replaceArea(_currentDiffuseRoom, neighboorRoom, x+1, y);
            diffuseRoom(tempRoom, x+1, y);
        }
        neighboorRoom = getRoom(x-1, y);
        if (neighboorRoom != null && neighboorRoom != _currentDiffuseRoom) {
            replaceArea(_currentDiffuseRoom, neighboorRoom, x-1, y);
            diffuseRoom(tempRoom, x-1, y);
        }
        neighboorRoom = getRoom(x, y+1);
        if (neighboorRoom != null && neighboorRoom != _currentDiffuseRoom) {
            replaceArea(_currentDiffuseRoom, neighboorRoom, x, y+1);
            diffuseRoom(tempRoom, x, y+1);
        }
        neighboorRoom = getRoom(x, y-1);
        if (neighboorRoom != null && neighboorRoom != _currentDiffuseRoom) {
            replaceArea(_currentDiffuseRoom, neighboorRoom, x, y-1);
            diffuseRoom(tempRoom, x, y-1);
        }
    }

    private void replaceArea(RoomModel room, RoomModel neighboorRoom, int x, int y) {
        ParcelModel area = ServiceManager.getWorldMap().getParcel(x, y);
        room.addArea(area);
        neighboorRoom.removeArea(area);
        _rooms[x][y] = room;
    }

    private RoomModel getRoom(int x, int y) {
        if (x >= 0 && x < Constant.WORLD_WIDTH && y >= 0 && y < Constant.WORLD_HEIGHT) {
            return _rooms[x][y];
        }
        return null;
    }

    public RoomModel get(int x, int y) {
        if (x >= 0 && y >= 0 && x < Constant.WORLD_WIDTH && y < Constant.WORLD_HEIGHT) {
            return _rooms[x][y];
        }
        return null;
    }

    public RoomModel getNearFreeStorage(int fromX, int fromY) {
        for (int i = 0; i < Constant.WORLD_WIDTH; i++) {
            for (int j = 0; j < Constant.WORLD_HEIGHT; j++) {
                if (hasRoomTypeAtPos(RoomType.STORAGE, fromX + i, fromY + j)) return _rooms[fromX + i][fromY + j];
                if (hasRoomTypeAtPos(RoomType.STORAGE, fromX - i, fromY + j)) return _rooms[fromX - i][fromY + j];
                if (hasRoomTypeAtPos(RoomType.STORAGE, fromX + i, fromY - j)) return _rooms[fromX + i][fromY - j];
                if (hasRoomTypeAtPos(RoomType.STORAGE, fromX - i, fromY - j)) return _rooms[fromX - i][fromY - j];
            }
        }
        return null;
    }

    private boolean hasRoomTypeAtPos(RoomType storage, int x, int y) {
        if (x < 0 || x >= Constant.WORLD_WIDTH || y < 0 || y >= Constant.WORLD_HEIGHT) {
            return false;
        }
        if (_rooms[x][y] == null || _rooms[x][y].getType() != storage) {
            return false;
        }
        return true;
    }

    public void removeRoom(int fromX, int fromY, int toX, int toY) {
        Set<RoomModel> updatedRooms = new HashSet<RoomModel>();
        boolean hasGarden = false;

        // Set room for each area
        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                if (x >= 0 && y >= 0 && x < Constant.WORLD_WIDTH && y < Constant.WORLD_HEIGHT) {
                    if (_rooms[x][y] != null) {
                        updatedRooms.add(_rooms[x][y]);

                        // Remove area from room
                        _rooms[x][y].removeArea(x, y);

                        // Flag garden
                        if (_rooms[x][y].isType(RoomType.GARDEN)) {
                            hasGarden = true;
                        }
                    }
                    _rooms[x][y] = null;
                    MainRenderer.getInstance().invalidate(x, y);
                }
            }
        }

        // Refresh start position
        for (RoomModel room: _roomList) {
            room.refreshPosition();
        }

        // Remove non existing room
        List<RoomModel> toRemove = new ArrayList<RoomModel>();
        for (RoomModel room: _roomList) {
            if (room.getAreas().size() == 0) {
                toRemove.add(room);
            }
        }
        _roomList.removeAll(toRemove);

        // Refresh water if garden was removed
        if (hasGarden) {
            ResourceManager.getInstance().refreshWater();
        }
    }

    public RoomModel[][] getRooms() {
        return _rooms;
    }

    public List<RoomModel> getRoomList() {
        return _roomList;
    }

    public RoomModel getNeerRoom(int x, int y, RoomType type) {
        int bestDistance = Integer.MAX_VALUE;
        RoomModel bestRoom = null;
        for (RoomModel room: _roomList) {
            if (room.isType(type)) {
                int distance = Math.abs(room.getX() - x) + Math.abs(room.getY() - y);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestRoom = room;
                }
            }
        }
        return bestRoom;
    }

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

    public void removeFromRooms(CharacterModel character) {
        for (RoomModel room: _roomList) {
            if (room.getOccupants().contains(character)) {
                room.removeOccupant(character);
            }
        }
    }

    public void update() {
        for (RoomModel room: _roomList) {
            room.update();
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
        _roomList.clear();
        makeRooms();
    }

    @Override
    public void onAddItem(ItemModel item){
        if (item.isLight() && item.getArea() != null && item.getArea().getRoom() != null) {
            int lightValue = 0;
            for (ParcelModel area: item.getArea().getRoom().getAreas()) {
                if (area != null && area.getItem() != null && area.getItem().isLight()) {
                    lightValue += area.getItem().getInfo().light;
                }
            }
            item.getArea().getRoom().setLight(lightValue);
        }
    }

    @Override
    public void onRemoveStructure(StructureModel structure){
        makeRooms();
    }

}

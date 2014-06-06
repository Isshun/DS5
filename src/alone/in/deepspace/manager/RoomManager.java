package alone.in.deepspace.manager;

import java.util.ArrayList;
import java.util.List;

import alone.in.deepspace.engine.renderer.MainRenderer;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.character.CharacterRelation;
import alone.in.deepspace.model.character.CharacterRelation.Relation;
import alone.in.deepspace.model.item.StructureItem;
import alone.in.deepspace.model.item.WorldArea;
import alone.in.deepspace.model.room.GardenRoom;
import alone.in.deepspace.model.room.QuarterRoom;
import alone.in.deepspace.model.room.Room;
import alone.in.deepspace.model.room.Room.Type;
import alone.in.deepspace.util.Constant;
import alone.in.deepspace.util.Log;

public class RoomManager {
	private Room[][] 			_rooms;
	private List<Room>			_roomList;

	public RoomManager() {
		_rooms = new Room[Constant.WORLD_WIDTH][Constant.WORLD_HEIGHT];
		_roomList = new ArrayList<Room>();
	}

	public Room putRoom(int startX, int startY, int fromX, int fromY, int toX, int toY, Type type, Character owner) {
		Log.info("RoomManager: put room from " + fromX + "x" + fromY + " to " + toX + "x" + toY);

		if (type == null) {
			Log.error("RoomManager: cannot put new room with NULL type");
			return null;
		}
		
		Room room = null;
		
		// Check if room already exists on start area
		if (startX > 0 && startY > 0 && startX < Constant.WORLD_WIDTH && startY < Constant.WORLD_HEIGHT && _rooms[startX][startY] != null) {
			room = _rooms[startX][startY];
		}
		
		// Check on others areas
		else {
			for (int x = fromX - 1; x <= toX + 1; x++) {
				for (int y = fromY - 1; y <= toY + 1; y++) {
					if (x > 0 && y > 0 && x < Constant.WORLD_WIDTH && y < Constant.WORLD_HEIGHT && _rooms[x][y] != null && _rooms[x][y].getType() == type) {
						room = _rooms[x][y];
						break;
					}
				}
			}
		}
		
		// Create new room if not exist
		if (room == null) {
			if (type == Type.GARDEN) {
				room = new GardenRoom(fromX, fromY);
			} else if (type == Type.QUARTER) {
				room = new QuarterRoom(fromX, fromY);
			} else {
				room = new Room(type, fromX, fromY);
			}
			room.setOwner(owner);
			_roomList.add(room); 
		}
		
		// Set room for each area
		for (int x = fromX; x <= toX; x++) {
			for (int y = fromY; y <= toY; y++) {
				if (x >= 0 && y >= 0 && x < Constant.WORLD_WIDTH && y < Constant.WORLD_HEIGHT) {
					StructureItem struct = ServiceManager.getWorldMap().getStructure(x, y);
					if (struct == null || struct.roomCanBeSet()) {
						WorldArea area = ServiceManager.getWorldMap().getArea(x, y);
						area.setRoom(room);
						room.addArea(area);
						_rooms[x][y] = room;
						MainRenderer.getInstance().invalidate(x, y);
					}
				}
			}
		}
		
		if (type == Room.Type.GARDEN) {
			ResourceManager.getInstance().refreshWater();
		}
		
		return room;
	}

	public Room get(int x, int y) {
		if (x >= 0 && y >= 0 && x < Constant.WORLD_WIDTH && y < Constant.WORLD_HEIGHT) {
			return _rooms[x][y];
		}
		return null;
	}

	public Room getNearFreeStorage(int fromX, int fromY) {
		for (int i = 0; i < Constant.WORLD_WIDTH; i++) {
			for (int j = 0; j < Constant.WORLD_HEIGHT; j++) {
				if (hasRoomTypeAtPos(Type.STORAGE, fromX + i, fromY + j)) return _rooms[fromX + i][fromY + j];
				if (hasRoomTypeAtPos(Type.STORAGE, fromX - i, fromY + j)) return _rooms[fromX - i][fromY + j];
				if (hasRoomTypeAtPos(Type.STORAGE, fromX + i, fromY - j)) return _rooms[fromX + i][fromY - j];
				if (hasRoomTypeAtPos(Type.STORAGE, fromX - i, fromY - j)) return _rooms[fromX - i][fromY - j];
			}
		}
		return null;
	}

	private boolean hasRoomTypeAtPos(Type storage, int x, int y) {
		if (x < 0 || x >= Constant.WORLD_WIDTH || y < 0 || y >= Constant.WORLD_HEIGHT) {
			return false;
		}
		if (_rooms[x][y] == null || _rooms[x][y].getType() != storage) {
			return false;
		}
		return true;
	}

	public void removeRoom(int fromX, int fromY, int toX, int toY, Type roomType) {
		boolean hasGarden = false;
		
		// Set room for each area
		for (int x = fromX; x <= toX; x++) {
			for (int y = fromY; y <= toY; y++) {
				if (x >= 0 && y >= 0 && x < Constant.WORLD_WIDTH && y < Constant.WORLD_HEIGHT) {
					if (_rooms[x][y] != null && _rooms[x][y].isType(Room.Type.GARDEN)) {
						hasGarden = true;
					}
					_rooms[x][y] = null;
					MainRenderer.getInstance().invalidate(x, y);
				}
			}
		}
		
		if (hasGarden) {
			ResourceManager.getInstance().refreshWater();
		}
	}

	public Room[][] getRooms() {
		return _rooms;
	}

	public List<Room> getRoomList() {
		return _roomList;
	}

	public Room getNeerRoom(int x, int y, Type type) {
		int bestDistance = Integer.MAX_VALUE;
		Room bestRoom = null;
		for (Room room: _roomList) {
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

	public Room take(Character character, Type type) {
		if (character.getQuarter() != null) {
			return character.getQuarter();
		}
		
		// Check relations
		List<CharacterRelation> relations = character.getRelations();
		for (CharacterRelation relation: relations) {
			
			// Check if relation have there own quarters
			Room relationQuarter = relation.getSecond().getQuarter();
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
		
		for (Room room: _roomList) {
			if (room.isType(type) && room.getOwner() == null) {
				room.setOwner(character);
				room.addOccupant(character);
				character.setQuarter(room);
				return room;
			}
		}
		
		return null;
	}

	public void removeFromRooms(Character character) {
		for (Room room: _roomList) {
			if (room.getOccupants().contains(character)) {
				room.removeOccupant(character);
			}
		}
	}

	public void update() {
		for (Room room: _roomList) {
			room.update();
		}
	}

}

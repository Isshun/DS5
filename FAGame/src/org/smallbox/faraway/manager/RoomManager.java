//
//package org.smallbox.faraway.manager;
//
//import org.smallbox.faraway.engine.util.Constant;
//import org.smallbox.faraway.engine.util.Log;
//import org.smallbox.faraway.model.character.CharacterModel;
//import org.smallbox.faraway.model.character.CharacterRelation;
//import org.smallbox.faraway.model.character.CharacterRelation.Relation;
//import org.smallbox.faraway.model.item.ItemBase;
//import org.smallbox.faraway.model.item.ItemFilter;
//import org.smallbox.faraway.model.item.StructureItem;
//import org.smallbox.faraway.model.item.WorldArea;
//import org.smallbox.faraway.model.room.GardenRoom;
//import org.smallbox.faraway.model.room.QuarterRoom;
//import org.smallbox.faraway.model.room.Room;
//import org.smallbox.faraway.model.room.Room.Type;
//import org.smallbox.faraway.engine.renderer.MainRenderer;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//public class RoomManager {
//	private Room[][] 			_rooms;
//	private List<Room>			_roomList;
//	private Room _currentDiffuseRoom;
//
//	public RoomManager() {
//		_rooms = new Room[Constant.WORLD_WIDTH][Constant.WORLD_HEIGHT];
//		_roomList = new ArrayList<Room>();
//	}
//
//	public void add(Room room) {
//		if (room == null) {
//			Log.error("room cannot be null");
//			return;
//		}
//
//		if (_roomList.contains(room)) {
//			Log.error("room already exists");
//			return;
//		}
//
//		_roomList.add(room);
//
//		for (WorldArea area: room.getAreas()) {
//			_rooms[area.getX()][area.getY()] = room;
//		}
//	}
//
//	public Room putRoom(int startX, int startY, int fromX, int fromY, int toX, int toY, Type type, CharacterModel owner) {
//		Log.info("RoomManager: put room from " + fromX + "x" + fromY + " to " + toX + "x" + toY);
//
//		if (type == null) {
//			Log.error("RoomManager: cannot put new room with NULL type");
//			return null;
//		}
//
//		Room existingRoom = null;
//		Room tempRoom = null;
//		int existingRoomPosX = 0;
//		int existingRoomPosY = 0;
//
//		// Check if room already exists on start area
//		if (startX > 0 && startY > 0 && startX < Constant.WORLD_WIDTH && startY < Constant.WORLD_HEIGHT && _rooms[startX][startY] != null) {
//			existingRoom = _rooms[startX][startY];
//			existingRoomPosX = startX;
//			existingRoomPosY = startY;
//		}
//
//		// Check on others areas
//		else {
//			for (int x = fromX - 1; x <= toX + 1; x++) {
//				for (int y = fromY - 1; y <= toY + 1; y++) {
//					if ((x >= fromX && x <= toX || y >= fromY && y <= toY) && x > 0 && y > 0 && x < Constant.WORLD_WIDTH && y < Constant.WORLD_HEIGHT && _rooms[x][y] != null && _rooms[x][y].getType() == type) {
//						existingRoom = _rooms[x][y];
////						tempRoom = existingRoom;
//						existingRoomPosX = x;
//						existingRoomPosY = y;
//						break;
//					}
//				}
//			}
//		}
//
//		// Create new room if not exist
//		if (tempRoom == null) {
//			tempRoom = createRoom(type, owner);
//		}
//
//		// Set room for each area
//		for (int x = fromX; x <= toX; x++) {
//			for (int y = fromY; y <= toY; y++) {
//				if (x >= 0 && y >= 0 && x < Constant.WORLD_WIDTH && y < Constant.WORLD_HEIGHT) {
//					StructureItem struct = ServiceManager.getWorldMap().getStructure(x, y);
//					if (struct == null || struct.roomCanBeSet()) {
//						if (_rooms[x][y] == null || _rooms[x][y].getType() != tempRoom.getType()) {
//							WorldArea area = ServiceManager.getWorldMap().getArea(x, y);
//							area.setRoom(tempRoom);
//							tempRoom.addArea(area);
//							_rooms[x][y] = tempRoom;
//							MainRenderer.getInstance().invalidate(x, y);
//						}
//					}
//				}
//			}
//		}
//
//		// If already existing room
//		if (existingRoom != null) {
//			_currentDiffuseRoom = existingRoom;
//			diffuseRoom(tempRoom, existingRoomPosX, existingRoomPosY);
//		}
//
//		int leftRoom = Integer.MAX_VALUE;
//		while (tempRoom.getAreas().size() != 0 && tempRoom.getAreas().size() < leftRoom) {
//			leftRoom = tempRoom.getAreas().size();
//			Room newRoom = createRoom(tempRoom.getType(), owner);
//			_roomList.add(newRoom);
//			WorldArea area = tempRoom.getAreas().get(0);
//			replaceArea(newRoom, tempRoom, area.getX(), area.getY());
//			_currentDiffuseRoom = newRoom;
//			diffuseRoom(tempRoom, area.getX(), area.getY());
//			newRoom.refreshPosition();
//		}
//
//		if (type == Room.Type.GARDEN) {
//			ResourceManager.getInstance().refreshWater();
//		}
//
//		// Refresh start position
//		tempRoom.refreshPosition();
//
//		return tempRoom;
//	}
//
//	private Room createRoom(Type type, CharacterModel owner) {
//		Room room = null;
//
//		if (type == Type.GARDEN) {
//			room = new GardenRoom();
//		} else if (type == Type.QUARTER) {
//			room = new QuarterRoom();
//		} else if (type == Type.STORAGE) {
//			room = new StorageRoom();
//		} else {
//			room = new Room(type);
//		}
//		room.setOwner(owner);
//		room.refreshPosition();
//
//		return room;
//	}
//
//	public StorageRoom findStorageContains(ItemFilter filter, int x, int y) {
//		StorageRoom bestStorage = null;
//		int bestDistance = Integer.MAX_VALUE;
//
//		for (Room room: _roomList) {
//			if (room.isStorage()) {
//				StorageRoom storage = (StorageRoom)room;
//				int distance = Math.abs(room.getX() - x) + Math.abs(room.getY() - y);
//				if (distance < bestDistance && storage.contains(filter)) {
//					bestStorage = storage;
//					bestDistance = distance;
//				}
//			}
//		}
//
//		return bestStorage;
//	}
//
//	public StorageRoom getNearestStorage(int x, int y, ItemBase item) {
//		StorageRoom bestStorage = null;
//		int bestDistance = Integer.MAX_VALUE;
//
//		for (Room room: _roomList) {
//			if (room.isStorage()) {
//				StorageRoom storage = (StorageRoom)room;
//				int distance = Math.abs(storage.getX() - x) + Math.abs(storage.getY() - y);
//				if (distance < bestDistance && storage.accept(item)) {
//					bestStorage = storage;
//					bestDistance = distance;
//				}
//			}
//		}
//
//		return bestStorage;
//	}
//
//	private void diffuseRoom(Room tempRoom, int x, int y) {
//		Room neighboorRoom = getRoom(x+1, y);
//
//		// If neighboorRoom IS NOT tempRoom AND IS NOT _currentDiffuseRoom, it's an old existing room
//		// so we replace all room previously set to _currentDiffuseRoom by this new room
//		if (neighboorRoom != null && neighboorRoom != tempRoom && neighboorRoom != _currentDiffuseRoom) {
//			List<WorldArea> areasCopy = new ArrayList<WorldArea>(_currentDiffuseRoom.getAreas());
//			for (WorldArea area: areasCopy) {
//				replaceArea(neighboorRoom, _currentDiffuseRoom, area.getX(), area.getY());
//			}
//			_currentDiffuseRoom = neighboorRoom;
//		}
//
//		// If neighboorRoom IS NOT _currentDiffuseRoom, transform to _currentDiffuseRoom
//		if (neighboorRoom != null && neighboorRoom != _currentDiffuseRoom) {
//			replaceArea(_currentDiffuseRoom, neighboorRoom, x+1, y);
//			diffuseRoom(tempRoom, x+1, y);
//		}
//		neighboorRoom = getRoom(x-1, y);
//		if (neighboorRoom != null && neighboorRoom != _currentDiffuseRoom) {
//			replaceArea(_currentDiffuseRoom, neighboorRoom, x-1, y);
//			diffuseRoom(tempRoom, x-1, y);
//		}
//		neighboorRoom = getRoom(x, y+1);
//		if (neighboorRoom != null && neighboorRoom != _currentDiffuseRoom) {
//			replaceArea(_currentDiffuseRoom, neighboorRoom, x, y+1);
//			diffuseRoom(tempRoom, x, y+1);
//		}
//		neighboorRoom = getRoom(x, y-1);
//		if (neighboorRoom != null && neighboorRoom != _currentDiffuseRoom) {
//			replaceArea(_currentDiffuseRoom, neighboorRoom, x, y-1);
//			diffuseRoom(tempRoom, x, y-1);
//		}
//	}
//
//	private void replaceArea(Room room, Room neighboorRoom, int x, int y) {
//		WorldArea area = ServiceManager.getWorldMap().getArea(x, y);
//		room.addArea(area);
//		neighboorRoom.removeArea(area);
//		_rooms[x][y] = room;
//	}
//
//	private Room getRoom(int x, int y) {
//		if (x >= 0 && x < Constant.WORLD_WIDTH && y >= 0 && y < Constant.WORLD_HEIGHT) {
//			return _rooms[x][y];
//		}
//		return null;
//	}
//
//	public Room get(int x, int y) {
//		if (x >= 0 && y >= 0 && x < Constant.WORLD_WIDTH && y < Constant.WORLD_HEIGHT) {
//			return _rooms[x][y];
//		}
//		return null;
//	}
//
//	public Room getNearFreeStorage(int fromX, int fromY) {
//		for (int i = 0; i < Constant.WORLD_WIDTH; i++) {
//			for (int j = 0; j < Constant.WORLD_HEIGHT; j++) {
//				if (hasRoomTypeAtPos(Type.STORAGE, fromX + i, fromY + j)) return _rooms[fromX + i][fromY + j];
//				if (hasRoomTypeAtPos(Type.STORAGE, fromX - i, fromY + j)) return _rooms[fromX - i][fromY + j];
//				if (hasRoomTypeAtPos(Type.STORAGE, fromX + i, fromY - j)) return _rooms[fromX + i][fromY - j];
//				if (hasRoomTypeAtPos(Type.STORAGE, fromX - i, fromY - j)) return _rooms[fromX - i][fromY - j];
//			}
//		}
//		return null;
//	}
//
//	private boolean hasRoomTypeAtPos(Type storage, int x, int y) {
//		if (x < 0 || x >= Constant.WORLD_WIDTH || y < 0 || y >= Constant.WORLD_HEIGHT) {
//			return false;
//		}
//		if (_rooms[x][y] == null || _rooms[x][y].getType() != storage) {
//			return false;
//		}
//		return true;
//	}
//
//	public void removeRoom(int fromX, int fromY, int toX, int toY) {
//		Set<Room> updatedRooms = new HashSet<Room>();
//		boolean hasGarden = false;
//
//		// Set room for each area
//		for (int x = fromX; x <= toX; x++) {
//			for (int y = fromY; y <= toY; y++) {
//				if (x >= 0 && y >= 0 && x < Constant.WORLD_WIDTH && y < Constant.WORLD_HEIGHT) {
//					if (_rooms[x][y] != null) {
//						updatedRooms.add(_rooms[x][y]);
//
//						// Remove area from room
//						_rooms[x][y].removeArea(x, y);
//
//						// Flag garden
//						if (_rooms[x][y].isType(Room.Type.GARDEN)) {
//							hasGarden = true;
//						}
//					}
//					_rooms[x][y] = null;
//					MainRenderer.getInstance().invalidate(x, y);
//				}
//			}
//		}
//
//		// Refresh start position
//		for (Room room: _roomList) {
//			room.refreshPosition();
//		}
//
//		// Remove non existing room
//		List<Room> toRemove = new ArrayList<Room>();
//		for (Room room: _roomList) {
//			if (room.getAreas().size() == 0) {
//				toRemove.add(room);
//			}
//		}
//		_roomList.removeAll(toRemove);
//
//		// Refresh water if garden was removed
//		if (hasGarden) {
//			ResourceManager.getInstance().refreshWater();
//		}
//	}
//
//	public Room[][] getRooms() {
//		return _rooms;
//	}
//
//	public List<Room> getRoomList() {
//		return _roomList;
//	}
//
//	public Room getNeerRoom(int x, int y, Type type) {
//		int bestDistance = Integer.MAX_VALUE;
//		Room bestRoom = null;
//		for (Room room: _roomList) {
//			if (room.isType(type)) {
//				int distance = Math.abs(room.getX() - x) + Math.abs(room.getY() - y);
//				if (distance < bestDistance) {
//					bestDistance = distance;
//					bestRoom = room;
//				}
//			}
//		}
//		return bestRoom;
//	}
//
//	public Room take(CharacterModel character, Type type) {
//		if (character.getQuarter() != null) {
//			return character.getQuarter();
//		}
//
//		// Check relations
//		List<CharacterRelation> relations = character.getRelations();
//		for (CharacterRelation relation: relations) {
//
//			// Check if relation have there own quarters
//			Room relationQuarter = relation.getSecond().getQuarter();
//			if (relationQuarter != null) {
//
//				// Live in parent's quarters
//				if (relation.getRelation() == Relation.PARENT && character.getOld() <= Constant.CHARACTER_LEAVE_HOME_OLD) {
//					character.setQuarter(relationQuarter);
//					relationQuarter.addOccupant(character);
//					return relationQuarter;
//				}
//
//				// Live with mate
//				if (relation.getRelation() == Relation.MATE && character.getOld() > Constant.CHARACTER_LEAVE_HOME_OLD) {
//					character.setQuarter(relationQuarter);
//					relationQuarter.addOccupant(character);
//					return relationQuarter;
//				}
//
//				// Children take the quarters first
//				if (relation.getRelation() == Relation.CHILDREN && relation.getSecond().getOld() <= Constant.CHARACTER_LEAVE_HOME_OLD) {
//					character.setQuarter(relationQuarter);
//					relationQuarter.addOccupant(character);
//					return relationQuarter;
//				}
//			}
//		}
//
//		for (Room room: _roomList) {
//			if (room.isType(type) && room.getOwner() == null) {
//				room.setOwner(character);
//				room.addOccupant(character);
//				character.setQuarter(room);
//				return room;
//			}
//		}
//
//		return null;
//	}
//
//	public void removeFromRooms(CharacterModel character) {
//		for (Room room: _roomList) {
//			if (room.getOccupants().contains(character)) {
//				room.removeOccupant(character);
//			}
//		}
//	}
//
//	public void update() {
//		for (Room room: _roomList) {
//			room.update();
//		}
//	}
//}

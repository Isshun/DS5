package alone.in.deepspace.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import alone.in.deepspace.engine.ISavable;
import alone.in.deepspace.model.Room;
import alone.in.deepspace.model.Room.Type;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.character.CharacterRelation;
import alone.in.deepspace.model.character.CharacterRelation.Relation;
import alone.in.deepspace.model.item.StructureItem;
import alone.in.deepspace.util.Constant;
import alone.in.deepspace.util.Log;

public class RoomManager implements ISavable {

	private static RoomManager 	_self;
	private Room[][] 			_rooms;
	private List<Room>			_roomList;

	public static RoomManager getInstance() {
		if (_self == null) {
			_self = new RoomManager();
		}
		return _self;
	}
	
	public RoomManager() {
		_rooms = new Room[Constant.WORLD_WIDTH][Constant.WORLD_HEIGHT];
		_roomList = new ArrayList<Room>();
	}

	public void putRoom(int startX, int startY, int fromX, int fromY, int toX, int toY, Type type, Character owner) {
		Log.info("RoomManager: put room from " + fromX + "x" + fromY + " to " + toX + "x" + toY);

		if (type == null) {
			Log.error("RoomManager: cannot put new room with NULL type");
			return;
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
			room = new Room(type, fromX, fromY);
			room.setOwner(owner);
			_roomList.add(room); 
		}
		
		// Set room for each area
		for (int x = fromX; x <= toX; x++) {
			for (int y = fromY; y <= toY; y++) {
				if (x >= 0 && y >= 0 && x < Constant.WORLD_WIDTH && y < Constant.WORLD_HEIGHT) {
					StructureItem struct = ServiceManager.getWorldMap().getStructure(x, y);
					if (struct == null || struct.roomCanBeSet()) {
						_rooms[x][y] = room;
						ServiceManager.getWorldRenderer().invalidate(x, y);
					}
				}
			}
		}
		
		if (type == Room.Type.GARDEN) {
			ResourceManager.getInstance().refreshWater();
		}
	}

	public Room get(int x, int y) {
		if (x >= 0 && y >= 0 && x < Constant.WORLD_WIDTH && y < Constant.WORLD_HEIGHT) {
			return _rooms[x][y];
		}
		return null;
	}

	public void	save(final String filePath) {
		Log.info("Save rooms: " + filePath);

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
			bw.write("BEGIN ROOMS\n");
			for (int x = 0; x < Constant.WORLD_WIDTH; x++) {
				for (int y = 0; y < Constant.WORLD_HEIGHT; y++) {
					Room room = _rooms[x][y];
					if (room != null) {
						bw.write(x + "\t" + y + "\t" + room.getType().ordinal() + "\t" + 0 + "\n");
					}
				}
			}
			bw.write("END ROOMS\n");
		} catch (FileNotFoundException e) {
			Log.error("Unable to open save file: " + filePath);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.info("Save rooms: " + filePath + " done");
	}

	public void	load(final String filePath) {
		Log.error("Load rooms: " + filePath);

		int x, y, type, owner;
		boolean	inBlock = false;

		List<Character> characters = ServiceManager.getCharacterManager().getList();
		
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line = null;

			while ((line = br.readLine()) != null) {

				// Start block
				if ("BEGIN ROOMS".equals(line)) {
					inBlock = true;
				}
				
				// End block
				else if ("END ROOMS".equals(line)) {
					inBlock = false;
				}

				// Item
				else if (inBlock) {
					String[] values = line.split("\t");
					if (values.length == 4) {
						x = Integer.valueOf(values[0]);
						y = Integer.valueOf(values[1]);
						type = Integer.valueOf(values[2]);
						owner = Integer.valueOf(values[3]);
						Character character = null;
						if (characters.size() > 0) {
							character = characters.get((int)(Math.random() * characters.size()));
						}
						putRoom(x, y, x, y, x, y, Room.getType(type), null);
					}
				}
				
			}
		}
		catch (FileNotFoundException e) {
			 Log.error("Unable to open save file: " + filePath);
			 e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
					ServiceManager.getWorldRenderer().invalidate(x, y);
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
		List<Room> rooms = RoomManager.getInstance().getRoomList();
		for (Room room: rooms) {
			if (room.getOccupants().contains(character)) {
				room.removeOccupant(character);
			}
		}
	}

}

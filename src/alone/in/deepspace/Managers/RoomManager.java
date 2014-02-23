package alone.in.deepspace.Managers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import alone.in.deepspace.Engine.ISavable;
import alone.in.deepspace.Models.Room;
import alone.in.deepspace.Models.Room.Type;
import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.Utils.Log;
import alone.in.deepspace.World.BaseItem;
import alone.in.deepspace.World.StructureItem;
import alone.in.deepspace.World.WorldMap;
import alone.in.deepspace.World.WorldRenderer;

public class RoomManager implements ISavable {

	private static RoomManager 	_self;
	private Room[][] 			_rooms;

	public static RoomManager getInstance() {
		if (_self == null) {
			_self = new RoomManager();
		}
		return _self;
	}
	
	public RoomManager() {
		_rooms = new Room[Constant.WORLD_WIDTH][Constant.WORLD_HEIGHT];
	}

	public void putRoom(int fromX, int fromY, int toX, int toY, Type type, int owner) {
		if (type == null) {
			Log.error("RoomManager: cannot put new room with NULL type");
			return;
		}
		
		Room room = null;
		
		// Check if room already exists on selected area
		for (int x = fromX - 1; x <= toX + 1; x++) {
			for (int y = fromY - 1; y <= toY + 1; y++) {
				if (x > 0 && y > 0 && x < Constant.WORLD_WIDTH && y < Constant.WORLD_HEIGHT && _rooms[x][y] != null && _rooms[x][y].getType() == type) {
					room = _rooms[x][y];
					break;
				}
			}
		}
		
		// Create new room if not exist
		if (room == null) {
			room = new Room(type, fromX, fromY);
			room.setOwner(owner);
		}
		
		// Set room for each area
		for (int x = fromX; x <= toX; x++) {
			for (int y = fromY; y <= toY; y++) {
				StructureItem struct = WorldMap.getInstance().getStructure(x, y);
				if (struct == null || struct.isType(BaseItem.Type.STRUCTURE_FLOOR)) {
					_rooms[x][y] = room;
					WorldRenderer.getInstance().invalidate(x, y);
				}
			}
		}
	}

	public Room get(int x, int y) {
		return _rooms[x][y];
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
						putRoom(x, y, x, y, Room.getType(type), owner);
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

}

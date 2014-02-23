package alone.in.deepspace.World;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.newdawn.slick.util.pathfinding.Mover;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

import alone.in.deepspace.Engine.ISavable;
import alone.in.deepspace.Managers.DynamicObjectManager;
import alone.in.deepspace.Managers.JobManager;
import alone.in.deepspace.Managers.PathManager;
import alone.in.deepspace.Models.Room;
import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.Utils.Log;
import alone.in.deepspace.World.BaseItem.Type;

public class WorldMap implements ISavable, TileBasedMap {
	public static class DebugPos {
		public int x;
		public int y;
		public boolean inPath;
	}

	private static final int 	LIMIT_ITEMS = 42000;
	private static WorldMap 	_self;

	private Map<Integer, Room>	_rooms;
	private int					_itemCout;
	private WorldArea[][]		_areas;
	private int					_width;
	private int					_height;
	private int					_count;

	private Vector<DebugPos> 	_debugPath;
	private DebugPos 			_debugPathStart;
	private DebugPos 			_debugPathStop;

	public WorldMap() {
		_itemCout = 0;
		_width = Constant.WORLD_WIDTH;
		_height = Constant.WORLD_HEIGHT;
		_count = 0;

		dump();

		_rooms = new HashMap<Integer, Room>();
		_areas = new WorldArea[_width][_height];
		for (int x = 0; x < _width; x++) {
			_areas[x] = new WorldArea[_height];
			for (int y = 0; y < _height; y++) {
				_areas[x][y] = new WorldArea(BaseItem.Type.NONE, x, y);
			}
		}

		// PathManager.getInstance().addObject(x, y, true);

	}

	public void	create() {
	}

	public void	save(final String filePath) {
		Log.info("Save worldmap: " + filePath);

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
			bw.write("BEGIN WORLDMAP\n");
			for (int x = 0; x < _width; x++) {
				for (int y = 0; y < _height; y++) {
					if (_areas[x][y] != null) {
						WorldArea area = _areas[x][y];
						StructureItem structureItem = area.getStructure();
						UserItem userItem = area.getItem();
						WorldRessource ressource = area.getRessource();

						if (structureItem != null) {
							bw.write(x + "\t" + y + "\t" + structureItem.getType().ordinal() + "\t" + structureItem.getMatterSupply() + "\n");
						}

						if (userItem != null) {
							bw.write(x + "\t" + y + "\t" + userItem.getType().ordinal() + "\t" + userItem.getMatterSupply() + "\n");
						}

						if (ressource != null) {
							bw.write(x + "\t" + y + "\t" + ressource.getType().ordinal() + "\t" + ressource.getMatterSupply() + "\n");
						}
					}
				}
			}
			bw.write("END WORLDMAP\n");
		} catch (FileNotFoundException e) {
			Log.error("Unable to open save file: " + filePath);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.info("Save worldmap: " + filePath + " done");
	}

	public void	load(final String filePath) {
		Log.error("Load worldmap: " + filePath);

		int x, y, type, matter;
		boolean	inBlock = false;

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line = null;

			while ((line = br.readLine()) != null) {

				// Start block
				if ("BEGIN WORLDMAP".equals(line)) {
					inBlock = true;
				}

				// End block
				else if ("END WORLDMAP".equals(line)) {
					inBlock = false;
				}

				// Item
				else if (inBlock) {
					String[] values = line.split("\t");
					if (values.length == 4) {
						x = Integer.valueOf(values[0]);
						y = Integer.valueOf(values[1]);
						type = Integer.valueOf(values[2]);
						matter = Integer.valueOf(values[3]);
						putItem(BaseItem.getTypeIndex(type), x, y, matter);
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

	public void	addRandomSeed() {
		int startX = (int)(Math.random() * 1000) % _width;
		int startY = (int)(Math.random() * 1000) % _height;

		for (int x = 0; x < 5; x++) {
			for (int y = 0; y < 5; y++) {
				if (addRandomSeed(startX + x, startY + y)) return;
				if (addRandomSeed(startX - x, startY - y)) return;
				if (addRandomSeed(startX + x, startY - y)) return;
				if (addRandomSeed(startX - x, startY + y)) return;
			}
		}
	}

	private boolean addRandomSeed(int i, int j) {
		int realX = i % _width;
		int realY = j % _height;
		if (_areas[realX][realY].getStructure() == null) {
			WorldRessource ressource = (WorldRessource)putItem(BaseItem.Type.RES_1, realX, realY, 10);
			JobManager.getInstance().gather(ressource);
			return true;
		}
		return false;
	}

	public int	gather(BaseItem item, int maxValue) {
		if (item == null || maxValue == 0) {
			Log.error("gather: wrong call");
			return 0;
		}

		int value = item.gatherMatter(maxValue);
		int x = item.getX();
		int y = item.getY();
		return value;
	}

	public void	update() {
		_count++;

		// Add random seed each 10 update
		//if (_count % 10 == 0) {
//		addRandomSeed();
		//}
	}

	public BaseItem	find(BaseItem.Type type, boolean free) {
		Log.debug("WorldMap: find");

		int notFree = 0;

		for (int x = 0; x < _width; x++) {
			for (int y = 0; y < _height; y++) {
				WorldArea area = _areas[x][y];
				if (area != null) {

					// item
					BaseItem item = area.getItem();
					if (item != null && item.isType(type) && item.isComplete()) {
						if (free == false || item.isFree()) {
							Log.debug("item found");
							return item;
						}
						notFree++;
					}

					// Structure
					StructureItem structure = area.getStructure();
					if(structure != null && structure.isType(type) && structure.isComplete()) {
						if (free == false || structure.isFree()) {
							Log.debug("item found");
							return structure;
						}
						notFree++;
					}
				}
			}
		}

		Log.debug("No free item found (not free: " + notFree + ")");

		return null;
	}

	//		//TODO: perf
	//		BaseItem	getRandomPosInRoom(int roomId) {
	//			Log.debug("getRandomPosInRoom: " + roomId);
	//
	//		  int count = 0;
	//		  for (int x = 0; x < _width; x++) {
	//			for (int y = 0; y < _height; y++) {
	//			  if(_areas[x][y] != null && _areas[x][y].getRoomId() == roomId && _areas[x][y].isType(BaseItem.Type.STRUCTURE_FLOOR)) {
	//				count++;
	//			  }
	//			}
	//		  }
	//		  Log.debug("getRandomPosInRoom found: " + count);
	//
	//		  if (count > 0) {
	//			int goal = (int) (Math.random() % count);
	//			for (int x = 0; x < _width; x++) {
	//			  for (int y = 0; y < _height; y++) {
	//				if(_areas[x][y] != null
	//				   && _areas[x][y].getRoomId() == roomId
	//				   && _areas[x][y].isType(BaseItem.Type.STRUCTURE_FLOOR)) {
	//				  if (goal-- == 0) {
	//					  Log.debug("getRandomPosInRoom return: " + x + " " + y + " " + count);
	//					return _areas[x][y];
	//				  }
	//				}
	//			  }
	//			}
	//		  }
	//
	//		  Log.warning("getRandomPosInRoom: no room found");
	//		  return  null;
	//		}

	void	initRoom() {
		// for (int y = 0; y < _height; y++) {
		// 	for (int x = 0; x < _width; x++) {
		// 	  BaseItem item = getItem(x, y);
		// 	  if (item != null
		// 		  && (item.isStructure() == false || item.getType() == BaseItem.STRUCTURE_FLOOR)
		// 		  && item.getRoomId() == 0) {
		// 		Room.createFromPos(x, y);
		// 	  }
		// 	}
		// }
	}

	void dump() {
		// // for (int x = 0; x < _width; x++) {
		// // 	for (int y = 0; y < _height; y++) {
		// // 	  if (_items[x][y] != null) {
		// // 		Info() + x + " x " + y + " = " + _items[x][y].type;
		// // 	  }
		// // 	}
		// // }

		// // std.cout + std.endl + "\r";

		// system("clear");

		// for (int y = 0; y < _height; y++) {
		// 	for (int x = 0; x < _width; x++) {
		// 	  std.cout + _tmp[x][y];
		// 	  // std.cout + GetMap(x, y);
		// 	  // if (_items[x][y] != null) {
		// 	  // 	Info() + x + " x " + y + " = " + _items[x][y].type;
		// 	  // }
		// 	}
		// 	std.cout + std.endl;
		// }

	}

	//		public void		dumpItems() {
	//		  for (int x = 0; x < _width; x++) {
	//			for (int y = 0; y < _height; y++) {
	//			  if (_items[x][y] != null && _items[x][y].isStructure() == false) {
	//				Log.info("" + x + " x " + y + " = " + _items[x][y].getType() + ", zone: " + _items[x][y].getZoneId());
	//			  }
	//			}
	//		  }
	//		}

	boolean getSolid(int x, int y) {
		return false;
	}

	// TODO: call job listener
	public void removeStructure(int x, int y) {
		if (_areas[x][y].getItem() != null) {
			_areas[x][y].setItem(null);
		}
		_areas[x][y].setStructure(null);
		WorldRenderer.getInstance().invalidate(x, y);
	}

	// TODO: call job listener
	public void removeItem(BaseItem item) {
		if (item != null) {
			int x = item.getX();
			int y = item.getY();
			item.setOwner(null);
			_areas[x][y].setItem(null);
			WorldRenderer.getInstance().invalidate(x, y);
		}
	}

	public void removeItem(WorldArea area) {
		if (area != null) {
			removeItem(area.getItem());
		}
	}

	public void removeItem(int x, int y) {
		Log.debug("remove item");

		// Return if out of bound
		if (x < 0 || y < 0 || x >= _width || y >= _height) {
			Log.error("remove item out of bound, x: " + x + ", y: " + y + ")");
			return;
		}

		WorldArea item = _areas[x][y];
		removeItem(item);
	}

	BaseItem putItem(BaseItem.Type type, int x, int y, boolean free) {
		if (_itemCout + 1 > LIMIT_ITEMS) {
			Log.error("LIMIT_ITEMS reached");
			return null;
		}

		return putItem(type, x, y, free ? 999 : 0);
	}

	public BaseItem putItem(BaseItem.Type type, int x, int y) {
		if (_itemCout + 1 > LIMIT_ITEMS) {
			Log.error("LIMIT_ITEMS reached");
			return null;
		}

		return putItem(type, x, y, false);
	}

	BaseItem putItem(BaseItem.Type type, int x, int y, int matterSupply) {
		// Return if out of bound
		if (x < 0 || y < 0 || x >= _width || y >= _height) {
			Log.error("put item out of bound, type: "
					+ type + ", x: " + x + ", y: " + y + ")");
			return null;
		}

		// TODO
		//		  // Return if item already exists
		//		  if (_areas[x][y] != null && _areas[x][y].isType(type)) {
		//			Log.debug("Same item existing for " + x + " x " + y);
		//			return null;
		//		  }
		//
		//		  // If item alread exists and different type, remove any job on this item
		//		  if (_areas[x][y] != null)  {
		//			JobManager.getInstance().removeJob(_areas[x][y]);
		//		  }

		// If item alread exists check the roomId
		int roomId = 0;
		if (_areas[x][y] != null)  {
			roomId = _areas[x][y].getRoomId();
		}

		// Return if same item already exists at this position
		WorldArea area = _areas[x][y];
		if (area != null) {
			if (area.getItem() != null && area.getItem().getType() == type ||
					area.getStructure() != null && area.getStructure().getType() == type) {
				return null;
			}
		}

		// Get new item
		BaseItem item = null;
		if (BaseItem.isResource(type)) {
			item = new WorldRessource(type);
			((WorldRessource)item).setValue(matterSupply);
		} else if (BaseItem.isStructure(type)) {
			item = new StructureItem(type);
		} else {
			item = new UserItem(type);
		}
		item.setPosition(x, y);
		//		  int zoneId = item.getZoneId();

		// Ressource
		if (item.isRessource()) {
			_areas[x][y].setRessource((WorldRessource) item);
			if (((WorldRessource)item).getValue() > 0) {
				JobManager.getInstance().gather((WorldRessource) item);
			}
		}

		// Wall
		else if (item.isStructure() && item.isType(BaseItem.Type.STRUCTURE_FLOOR) == false) {
			_areas[x][y].setStructure((StructureItem) item);
			// _items[x][y].setRoomId(roomId);
			// _items[x][y].setZoneId(0);
			//destroyRoom(roomId);
		}

		// Object or floor
		else {

			//			// Room already exists
			//			if (roomId > 0 && getRoom(roomId) != null) {
			//			  Room room = getRoom(roomId);
			//			  if (room != null) {
			//
			//				// Room have no zoneId
			//				if (room.getZoneId() == 0) {
			//				  Log.info("Set room to new zoneId: " + item.getZoneId());
			//				  room.setZoneId(item.getZoneId());
			//				}
			//
			//				// Item have no zoneId
			//				if (item.getZoneId() == 0) {
			//				  zoneId = room.getZoneId();
			//				}
			//
			//				// Room and item zoneId match
			//				else if (room.getZoneId() == item.getZoneId()) {
			//				  Log.info("Room zoneId match with item");
			//				}
			//
			//				// Room and item zoneId don't match
			//				else {
			//				  Log.info("this item can not be put at this position because zoneId not match (item: "
			//						 + item.getZoneId() + ", room: " + room.getZoneId() + ")");
			//				  return null;
			//				}
			//			  }
			//			}
			//
			//			// Create new room if not exists
			//			else {
			//			  //roomId = addRoom(x, y);
			//			}

			if (type == BaseItem.Type.STRUCTURE_FLOOR) {
				_areas[x][y].setStructure((StructureItem) item);
			} else if (BaseItem.isResource(type) == false) {
				if (_areas[x][y] != null) {
					_areas[x][y].setItem((UserItem) item);
					if (item.isType(BaseItem.Type.TACTICAL_PHASER)) {
						DynamicObjectManager.getInstance().add((UserItem)item);
					}
				} else {
					Log.error("Put item on null WorldArea");
				}
			}

			//			item.setZoneId(zoneId);
			//			item.setRoomId(roomId);
		}

		// Put item
		Log.debug("put item: " + type);

		item.setMatterSupply(matterSupply);

		// TODO
		//PathManager.getInstance().addObject(x, y, false);

		WorldRenderer.getInstance().invalidate(item.getX(), item.getY());
		
		return item;
	}

	//		int		addRoom(int x, int y) {
	//		  Log.debug("addRoom: " + x + " x " + y);
	//
	//		  Room room = Room.createFromPos(x, y);
	//
	//		  if (room != null) {
	//			_rooms.put(room.getId(), room);
	//			return room.getId();
	//		  } else {
	//			Log.error("Unable to create Room at position: " + x + " x " + y);
	//		  }
	//		  return  -1;
	//		}

	public UserItem 			getItem(int x, int y) {
		return (x < 0 || x >= _width || y < 0 || y >= _height) || _areas[x][y] == null ? null : _areas[x][y].getItem();
	}

	public StructureItem 		getStructure(int x, int y) {
		return (x < 0 || x >= _width || y < 0 || y >= _height) ? null : _areas[x][y].getStructure();
	}

	public WorldRessource   	getRessource(int x, int y) {
		return (x < 0 || x >= _width || y < 0 || y >= _height) ? null : _areas[x][y].getRessource();
	}

	public WorldArea			getArea(int x, int y) {
		return (x < 0 || x >= _width || y < 0 || y >= _height) ? null : _areas[x][y];
	}

	public Room					getRoom(int id) { return _rooms.get(id); }
	public int					getRoomCount() { return _rooms.size(); }

	public static WorldMap getInstance() {
		if (_self == null) {
			_self = new WorldMap();
		}
		return _self;
	}

	public int					getWidth() { return _width; }
	public int					getHeight() { return _height; }

	public UserItem getNearest(Type type, int startX, int startY) {
		PathManager pathManager = PathManager.getInstance();
		int maxX = Math.max(startX, _width - startX);
		int maxY = Math.max(startY, _height - startY);
		for (int offsetX = 0; offsetX < maxX; offsetX++) {
			for (int offsetY = 0; offsetY < maxY; offsetY++) {
				if (isItemTypeAtPos(startX + offsetX, startY + offsetY, type) && !pathManager.isBlocked(startX, startY, startX + offsetX, startY + offsetY)) {
					return getItem(startX + offsetX, startY + offsetY);
				}
				if (isItemTypeAtPos(startX - offsetX, startY - offsetY, type) && !pathManager.isBlocked(startX, startY, startX - offsetX, startY - offsetY)) {
					return getItem(startX - offsetX, startY - offsetY);
				}
				if (isItemTypeAtPos(startX + offsetX, startY - offsetY, type) && !pathManager.isBlocked(startX, startY, startX + offsetX, startY - offsetY)) {
					return getItem(startX + offsetX, startY - offsetY);
				}
				if (isItemTypeAtPos(startX - offsetX, startY + offsetY, type) && !pathManager.isBlocked(startX, startY, startX - offsetX, startY + offsetY)) {
					return getItem(startX - offsetX, startY + offsetY);
				}
			}
		}
		return null;
	}

	private boolean isItemTypeAtPos(int x, int y, Type type) {
		UserItem item = getItem(x, y);
		return (item != null && item.isType(type));
	}

	@Override
	public int getWidthInTiles() {
		return _width;
	}

	@Override
	public int getHeightInTiles() {
		return _height;
	}

	@Override
	public void pathFinderVisited(int x, int y) {
		DebugPos pos = new DebugPos();
		pos.x = x;
		pos.y = y;
		_debugPath.add(pos);
		//Log.info("visite: " + x + ", " + y);
	}

	@Override
	public boolean blocked(Mover mover, int x, int y) {
		return _areas[x][y].getStructure() != null && _areas[x][y].getStructure().isComplete() && _areas[x][y].getStructure().isSolid();
	}

	@Override
	public float getCost(Mover mover, int sx, int sy, int tx, int ty) {
		WorldArea a1 = _areas[sx][sy];
		WorldArea a2 = _areas[tx][ty];

		if (a1.getStructure() != null && a1.getStructure().isComplete() && a2.getStructure() == null ||
				a2.getStructure() != null && a2.getStructure().isComplete() && a1.getStructure() == null) {
			return 5;
		}

		return sx != tx && sy != ty ? 0.6f : 0.5f;
	}

	public Vector<DebugPos> getDebug() {
		return _debugPath;
	}

	public DebugPos getStartDebug() {
		return _debugPathStart;
	}

	public DebugPos getStopDebug() {
		return _debugPathStop;
	}

	public void stopDebug(int x, int y) {
		_debugPathStop = new DebugPos();
		_debugPathStop.x = x;
		_debugPathStop.y = y;
	}

	public void startDebug(int posX, int posY) {
		_debugPath = new Vector<DebugPos>();
		_debugPathStart = new DebugPos();
		_debugPathStart.x = posX;
		_debugPathStart.y = posY;
	}

	public void storeItem(Type res1, int x, int y) {
		if (_areas[x][y].getItem() == null) {
			_areas[x][y].setItem(new UserItem(Type.QUARTER_CHAIR));
		}
	}
}

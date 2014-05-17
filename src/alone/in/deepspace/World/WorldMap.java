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

import alone.in.deepspace.Character.ServiceManager;
import alone.in.deepspace.Engine.ISavable;
import alone.in.deepspace.Managers.DynamicObjectManager;
import alone.in.deepspace.Managers.JobManager;
import alone.in.deepspace.Managers.PathManager;
import alone.in.deepspace.Models.Room;
import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.Utils.Log;

public class WorldMap implements ISavable, TileBasedMap {
	public static class DebugPos {
		public int x;
		public int y;
		public boolean inPath;
	}

	private static final int 	LIMIT_ITEMS = 42000;

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
				_areas[x][y] = new WorldArea(x, y);
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
							bw.write(x + "\t" + y + "\t" + structureItem.getName() + "\t" + structureItem.getMatterSupply() + "\n");
						}

						if (userItem != null) {
							bw.write(x + "\t" + y + "\t" + userItem.getName() + "\t" + userItem.getMatterSupply() + "\n");
						}

						if (ressource != null) {
							bw.write(x + "\t" + y + "\t" + ressource.getName() + "\t" + ressource.getMatterSupply() + "\n");
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

		int x, y, matter;
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
						matter = Integer.valueOf(values[3]);
						ItemInfo info = ServiceManager.getData().getItemInfo(values[2]);
						putItem(info, x, y, matter);
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
		
		for (int x2 = 4; x2 < 10; x2++) {
			for (int y2 = 20; y2 < 30; y2++) {
				putItem("base.res", x2, y2, 10);
			}
		}
	}

	private BaseItem putItem(String name, int x2, int y2, int i) {
		return putItem(ServiceManager.getData().getItemInfo(name), x2, y2, i);
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
			WorldRessource ressource = (WorldRessource)putItem("base.res", realX, realY, 10);
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
		
		if (item.getMatterSupply() <= 0) {
			_areas[x][y].setItem(null);
		}
		
		ServiceManager.getWorldRenderer().invalidate(x, y);
		
		return value;
	}

	public void	update() {
		_count++;
		
		for (int x = 0; x < _width; x++) {
			for (int y = 0; y < _height; y++) {
				WorldArea area = _areas[x][y];
				_areas[x][y].setLight(0);
			}
		}
		
		for (int x = 0; x < _width; x++) {
			for (int y = 0; y < _height; y++) {
				WorldArea area = _areas[x][y];

				if (area.getItem() != null && area.getItem().getLight() > 0) {
					// TODO
					//diffuseLight(x, y, area.getItem().getLight());
				}
			}
		}

		// Add random seed each 10 update
		//if (_count % 10 == 0) {
//		addRandomSeed();
		//}
	}

	private void diffuseLight(int x, int y, int light) {
		for (int j = 0; j < light; j++) {
			for (double i = -Math.PI; i < Math.PI; i += 0.1) {
				int x2 = (int)Math.round(Math.cos(i) * j);
				int y2 = (int)Math.round(Math.sin(i) * j);
//				double value = Math.sqrt(Math.pow(Math.cos(i) * j, 2) + Math.pow(Math.sin(i) * j, 2));
				double value = Math.sqrt(Math.pow(x2, 2) + Math.pow(y2, 2));
				if (isFree(x, y, x+x2, y+y2)) {
					_areas[x+x2][y+y2].addLight((int)(light*2-value*2));
				}
			}
		}
			
//		for (int i = 0; i < light; i++) {
//			for (int j = 0; j < light; j++) {
//			int value = light - i * 10 - j * 10;
//				_areas[x-i][y-j].setLight(value);
//				_areas[x+i][y-j].setLight(value);
//				_areas[x-i][y+j].setLight(value);
//				_areas[x+i][y+j].setLight(value);
//			}
//		}
	}

	private boolean isFree(int x, int y, int x2, int y2) {
		int fromX = Math.min(x, x2);
		int fromY = Math.min(y, y2);
		int toX = Math.max(x, x2);
		int toY = Math.max(y, y2);
		for (int i = fromX; i <= toX; i++) {
			for (int j = fromY; j <= toY; j++) {
				StructureItem structure = _areas[i][j].getStructure();
				if (structure != null && structure.isFloor() == false) {
					return false;
				}
			}
		}
		return true;
	}

	// TODO: itemName
	public BaseItem	find(String itemName, boolean free) {
		Log.debug("WorldMap: find");

		int notFree = 0;

		for (int x = 0; x < _width; x++) {
			for (int y = 0; y < _height; y++) {
				WorldArea area = _areas[x][y];
				if (area != null) {

					// item
					BaseItem item = area.getItem();
					if (item != null && item.getName().equals(itemName) && item.isComplete()) {
						if (free == false || item.isFree()) {
							Log.debug("item found");
							return item;
						}
						notFree++;
					}

					// Structure
					StructureItem structure = area.getStructure();
					if(structure != null && structure.getName().equals(itemName) && structure.isComplete()) {
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
		if (x >= 0 && y >= 0 && x < _width && y < _height) {
			if (_areas[x][y].getItem() != null) {
				_areas[x][y].setItem(null);
			}
			_areas[x][y].setStructure(null);
			ServiceManager.getWorldRenderer().invalidate(x, y);
		}
	}

	// TODO: call job listener
	public void removeItem(BaseItem item) {
		if (item != null) {
			int x = item.getX();
			int y = item.getY();
			item.setOwner(null);
			_areas[x][y].setItem(null);
			ServiceManager.getWorldRenderer().invalidate(x, y);
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

	public BaseItem putItem(String name, int x, int y, boolean isFree) {
		if (_itemCout + 1 > LIMIT_ITEMS) {
			Log.error("LIMIT_ITEMS reached");
			return null;
		}

		return putItem(ServiceManager.getData().getItemInfo(name), x, y, isFree ? 999 : 0);
	}

	public BaseItem putItem(String name, int x, int y) {
		if (_itemCout + 1 > LIMIT_ITEMS) {
			Log.error("LIMIT_ITEMS reached");
			return null;
		}

		return putItem(name, x, y, false);
	}

	public BaseItem putItem(ItemInfo info, int x, int y, int matterSupply) {
		// Return if out of bound
		if (x < 0 || y < 0 || x >= _width || y >= _height) {
			Log.error("put item out of bound, type: "
					+ info.name + ", x: " + x + ", y: " + y + ")");
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
			if (area.getItem() != null && area.getItem().getName().equals(info.name) ||
					area.getStructure() != null && area.getStructure().getName().equals(info.name)) {
				return null;
			}
		}

		// Get new item
		BaseItem item = null;
//		if (BaseItem.isResource(type)) {
//			item = new WorldRessource(type);
//			((WorldRessource)item).setValue(matterSupply);
//		} else
		
		// TODO: filter
		if ("base.storage".equals(info.name)) {
			item = new StorageItem();
			_areas[x][y].setItem((UserItem) item);
			return item;
		} if (info.isStructure) {
			item = new StructureItem(info);
		} else {
			item = new UserItem(info);
		}
		item.setPosition(x, y);
		//		  int zoneId = item.getZoneId();

		// Ressource
		if (item.isRessource()) {
			_areas[x][y].setItem((UserItem) item);
//			if ((item).getValue() > 0) {
				item.setMatterSupply(10);
				JobManager.getInstance().gather(item);
				ServiceManager.getWorldRenderer().invalidate(item.getX(), item.getY());
				return item;
//			}
		}

		// Wall
		else if (item.isStructure() && item.isFloor() == false) {
			_areas[x][y].setStructure((StructureItem) item);
			// _items[x][y].setRoomId(roomId);
			// _items[x][y].setZoneId(0);
			//destroyRoom(roomId);
		}

		// Object or floor
		else {
			if (item.isFloor()) {
				_areas[x][y].setStructure((StructureItem) item);
			} else if (item.isRessource() == false) {
				if (_areas[x][y] != null) {
					_areas[x][y].setItem((UserItem) item);

					// TODO: dynamic
//					if (item.isType(BaseItem.Type.TACTICAL_PHASER)) {
//						DynamicObjectManager.getInstance().add((UserItem)item);
//					}
				} else {
					Log.error("Put item on null WorldArea");
				}
			}
		}

		// Put item
		Log.debug("put item: " + item.getName());

		item.setMatterSupply(matterSupply);

		ServiceManager.getWorldRenderer().invalidate(item.getX(), item.getY());
		
		return item;
	}

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

	public int					getWidth() { return _width; }
	public int					getHeight() { return _height; }

	public UserItem getNearest(ItemInfo info, int startX, int startY) {
		PathManager pathManager = PathManager.getInstance();
		int maxX = Math.max(startX, _width - startX);
		int maxY = Math.max(startY, _height - startY);
		for (int offsetX = 0; offsetX < maxX; offsetX++) {
			for (int offsetY = 0; offsetY < maxY; offsetY++) {
				if (isItemTypeAtPos(startX + offsetX, startY + offsetY, info) && !pathManager.isBlocked(startX, startY, startX + offsetX, startY + offsetY)) {
					return getItem(startX + offsetX, startY + offsetY);
				}
				if (isItemTypeAtPos(startX - offsetX, startY - offsetY, info) && !pathManager.isBlocked(startX, startY, startX - offsetX, startY - offsetY)) {
					return getItem(startX - offsetX, startY - offsetY);
				}
				if (isItemTypeAtPos(startX + offsetX, startY - offsetY, info) && !pathManager.isBlocked(startX, startY, startX + offsetX, startY - offsetY)) {
					return getItem(startX + offsetX, startY - offsetY);
				}
				if (isItemTypeAtPos(startX - offsetX, startY + offsetY, info) && !pathManager.isBlocked(startX, startY, startX - offsetX, startY + offsetY)) {
					return getItem(startX - offsetX, startY + offsetY);
				}
			}
		}
		return null;
	}

	private boolean isItemTypeAtPos(int x, int y, ItemInfo info) {
		UserItem item = getItem(x, y);
		return (item != null && item.getInfo().equals(info));
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
//		DebugPos pos = new DebugPos();
//		pos.x = x;
//		pos.y = y;
//		_debugPath.add(pos);
		//Log.info("visite: " + x + ", " + y);
	}

	@Override
	public boolean blocked(Mover mover, int x, int y) {
		if (x >= 0 && y >= 0 && x < _width && y < _height) {
			return _areas[x][y].getStructure() != null && _areas[x][y].getStructure().isComplete() && _areas[x][y].getStructure().isSolid();
		}
		return true;
	}

	@Override
	public float getCost(Mover mover, int sx, int sy, int tx, int ty) {

//		 int dx = Math.abs(sx - tx);
//		 int dy = Math.abs(sy - ty);
//		 return Math.max(dx, dy);
				    		
		WorldArea a1 = _areas[sx][sy];
		WorldArea a2 = _areas[tx][ty];

		if (a1.getStructure() != null && a1.getStructure().isComplete() && a2.getStructure() == null ||
				a2.getStructure() != null && a2.getStructure().isComplete() && a1.getStructure() == null) {
			return 5;
		}

//		boolean r = Math.random() * 10 % 2 == 0;
//		return sx != tx ? (r ? 10f : 1f) : (r ? 1f : 10f);
		
		return sx != tx && sy != ty ? 1f : 0.8f;
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

	public void storeItem(BaseItem carriedItem, int x, int y) {
		UserItem onAreaItem = _areas[x][y].getItem();
		
		if (onAreaItem == null) {
			onAreaItem = new StorageItem();
			((StorageItem)carriedItem).addItem(carriedItem);
			_areas[x][y].setItem(onAreaItem);
		} else if (carriedItem.isStorage()) {
			((StorageItem)onAreaItem).addItem(carriedItem);
		} else {
			// TODO: not implemented
			Log.error("Storage area is used by non storage item");
		}
	}

	public BaseItem putItem(ItemInfo info, int x, int y) {
		return putItem(info, x, y, 0);
	}
}

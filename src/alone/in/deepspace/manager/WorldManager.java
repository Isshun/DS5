package alone.in.deepspace.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.newdawn.slick.util.pathfinding.Mover;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

import alone.in.deepspace.engine.ISavable;
import alone.in.deepspace.model.BaseItem;
import alone.in.deepspace.model.ItemInfo;
import alone.in.deepspace.model.ItemInfo.ItemInfoEffects;
import alone.in.deepspace.model.Room;
import alone.in.deepspace.model.StorageItem;
import alone.in.deepspace.model.StructureItem;
import alone.in.deepspace.model.UserItem;
import alone.in.deepspace.model.WorldArea;
import alone.in.deepspace.model.WorldRessource;
import alone.in.deepspace.util.Constant;
import alone.in.deepspace.util.Log;

public class WorldManager implements ISavable, TileBasedMap {
	public static class DebugPos {
		public int x;
		public int y;
		public boolean inPath;
	}

	private static final int 	LIMIT_ITEMS = 42000;

	private static final int START_FLOOR = 5;
	private static final int NB_FLOOR = 10;

	private Map<Integer, Room>	_rooms;
	private int					_itemCout;
	private WorldArea[][]		_areas;
	private WorldArea[][][]		_floors;
	private int					_width;
	private int					_height;

	private Vector<DebugPos> 	_debugPath;
	private DebugPos 			_debugPathStart;
	private DebugPos 			_debugPathStop;

	private int 				_floor;

	public WorldManager() {
		_itemCout = 0;
		_width = Constant.WORLD_WIDTH;
		_height = Constant.WORLD_HEIGHT;

		dump();

		_rooms = new HashMap<Integer, Room>();
		_floors = new WorldArea[NB_FLOOR][_width][_height];
		for (int f = 0; f < NB_FLOOR; f++) {
			_floors[f] = new WorldArea[_width][_height];
			for (int x = 0; x < _width; x++) {
				_floors[f][x] = new WorldArea[_height];
				for (int y = 0; y < _height; y++) {
					_floors[f][x][y] = new WorldArea(x, y);
				}
			}
		}
		
		_areas = _floors[0];
	}

	public void	create() {
	}

	public void	save(final String filePath) {
		Log.info("Save worldmap: " + filePath);

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
			bw.write("BEGIN WORLDMAP\n");
			for (int f = 0; f < NB_FLOOR; f++) {
				for (int x = 0; x < _width; x++) {
					for (int y = 0; y < _height; y++) {
						if (_floors[f][x][y] != null) {
							WorldArea area = _floors[f][x][y];
							StructureItem structureItem = area.getStructure();
							UserItem userItem = area.getItem();
							WorldRessource ressource = area.getRessource();
	
							if (structureItem != null) {
								bw.write(f + "\t" + x + "\t" + y + "\t" + structureItem.getName() + "\t" + structureItem.getMatterSupply() + "\n");
							}
	
							if (userItem != null) {
								bw.write(f + "\t" + x + "\t" + y + "\t" + userItem.getName() + "\t" + userItem.getMatterSupply() + "\n");
							}
	
							if (ressource != null) {
								bw.write(f + "\t" + x + "\t" + y + "\t" + ressource.getName() + "\t" + ressource.getMatterSupply() + "\n");
							}
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

		int f, x, y, matter;
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
					if (values.length == 5) {
						f = Integer.valueOf(values[0]);
						x = Integer.valueOf(values[1]);
						y = Integer.valueOf(values[2]);
						matter = Integer.valueOf(values[4]);
						ItemInfo info = ServiceManager.getData().getItemInfo(values[3]);
						putItem(info, f, x, y, matter);
					}
				}

			}
		}
		catch (FileNotFoundException e) {
			Log.error("Unable to open save file: " + filePath);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private BaseItem putItem(String name, int x2, int y2, int i) {
		return putItem(ServiceManager.getData().getItemInfo(name), _floor, x2, y2, i);
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
		
		// Add random seed each 10 update
		//if (_count % 10 == 0) {
//		addRandomSeed();
		//}
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
					UserItem item = area.getItem();
					if (item != null && item.getName().equals(itemName) && item.isComplete()) {
						if (free == false || item.hasFreeSlot()) {
							Log.debug("item found");
							return item;
						}
						notFree++;
					}

					// Structure
					StructureItem structure = area.getStructure();
					if(structure != null && structure.getName().equals(itemName) && structure.isComplete()) {
						if (free == false || structure.hasFreeSlot()) {
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

		return putItem(ServiceManager.getData().getItemInfo(name), _floor, x, y, isFree ? 999 : 0);
	}

	public BaseItem putItem(String name, int x, int y) {
		if (_itemCout + 1 > LIMIT_ITEMS) {
			Log.error("LIMIT_ITEMS reached");
			return null;
		}

		return putItem(name, x, y, false);
	}

	public BaseItem putItem(ItemInfo info, int f, int x, int y, int matterSupply) {
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
		if (_floors[f][x][y] != null)  {
			roomId = _floors[f][x][y].getRoomId();
		}

		// Return if same item already exists at this position
		WorldArea area = _floors[f][x][y];
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
//		if ("base.storage".equals(info.name)) {
//			item = new StorageItem();
//			_areas[x][y].setItem((UserItem) item);
//			return item;
		if (info.storage > 0) {
			item = new StorageItem(info);
		} else if (info.isRessource) {
			item = new WorldRessource(info);
		} else if (info.isStructure) {
			item = new StructureItem(info);
		} else {
			item = new UserItem(info);
		}
		item.setPosition(x, y);
		//		  int zoneId = item.getZoneId();

		// Ressource
		if (item.isRessource()) {
			_floors[f][x][y].setRessource((WorldRessource) item);
//			if ((item).getValue() > 0) {
//				item.setMatterSupply(10);
				//JobManager.getInstance().gather(item);
				ServiceManager.getWorldRenderer().invalidate(item.getX(), item.getY());
				return item;
//			}
		}

		// Wall
		else if (item.isStructure() && item.isFloor() == false) {
			_floors[f][x][y].setStructure((StructureItem) item);
			// _items[x][y].setRoomId(roomId);
			// _items[x][y].setZoneId(0);
			//destroyRoom(roomId);
		}

		// Object or floor
		else {
			if (item.isFloor()) {
				_floors[f][x][y].setStructure((StructureItem) item);
			} else if (item.isRessource() == false) {
				if (_floors[f][x][y] != null) {
					_floors[f][x][y].setItem((UserItem) item);

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
		
		if (info.name.equals("base.main_computer")) {
			JobManager.getInstance().addRoutineItem(item);
		}

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

	public UserItem getNearest(ItemFilter itemFilter, int startX, int startY) {
		PathManager pathManager = PathManager.getInstance();
		int maxX = Math.max(startX, _width - startX);
		int maxY = Math.max(startY, _height - startY);
		for (int offsetX = 0; offsetX < maxX; offsetX++) {
			for (int offsetY = 0; offsetY < maxY; offsetY++) {
				UserItem item = getItem(startX + offsetX, startY + offsetY);
				if (item != null && item.isComplete() && item.hasFreeSlot() && itemOrProduceMatchFilter(item.getInfo(), itemFilter) && !pathManager.isBlocked(startX, startY, startX + offsetX, startY + offsetY)) {
					return item;
				}
				item = getItem(startX - offsetX, startY - offsetY);
				if (item != null && item.isComplete() && item.hasFreeSlot() && itemOrProduceMatchFilter(item.getInfo(), itemFilter) && !pathManager.isBlocked(startX, startY, startX - offsetX, startY - offsetY)) {
					return getItem(startX - offsetX, startY - offsetY);
				}
				item = getItem(startX + offsetX, startY - offsetY);
				if (item != null && item.isComplete() && item.hasFreeSlot() && itemOrProduceMatchFilter(item.getInfo(), itemFilter) && !pathManager.isBlocked(startX, startY, startX + offsetX, startY - offsetY)) {
					return getItem(startX + offsetX, startY - offsetY);
				}
				item = getItem(startX - offsetX, startY + offsetY);
				if (item != null && item.isComplete() && item.hasFreeSlot() && itemOrProduceMatchFilter(item.getInfo(), itemFilter) && !pathManager.isBlocked(startX, startY, startX - offsetX, startY + offsetY)) {
					return getItem(startX - offsetX, startY + offsetY);
				}
			}
		}
		return null;
	}


	private boolean itemOrProduceMatchFilter(ItemInfo item, ItemFilter itemFilter) {

		// Item
		if (effectMatchFilter(item.onAction.effects, itemFilter)) {
			return true;
		}

		// Produce
		if (item.onAction.itemProduce != null) {
			List<ItemInfo> itemProduces = new ArrayList<ItemInfo>();
			itemProduces.add(item.onAction.itemProduce);
			for (ItemInfo itemProduce: itemProduces) {
				if (itemProduce != null && itemProduce.onAction != null && effectMatchFilter(itemProduce.onAction.effects, itemFilter)) {
					return true;
				}
			}
		}
		
		return false;
	}

	private boolean effectMatchFilter(ItemInfoEffects effects, ItemFilter itemFilter) {
		if (effects != null) {
			if (itemFilter.drink && effects.drink > 0) { return true; }
			if (itemFilter.energy && effects.energy > 0) { return true; }
			if (itemFilter.food && effects.food > 0) { return true; }
			if (itemFilter.hapiness && effects.hapiness > 0) { return true; }
			if (itemFilter.health && effects.health > 0) { return true; }
			if (itemFilter.relation && effects.relation > 0) { return true; }
		}
		return false;
	}

	// TODO: delete
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
		return false;
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
			// TODO
			ItemInfo info = ServiceManager.getData().getItemInfo("base.storage");
			onAreaItem = new StorageItem(info);
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
		return putItem(info, _floor, x, y, 0);
	}

	public int getFloor() {
		return _floor;
	}

	public void upFloor() {
		setFloor(_floor + 1);
	}

	private void setFloor(int floor) {
		_floor = floor;
		_areas = _floors[floor];
		ServiceManager.getWorldRenderer().invalidate();
		ServiceManager.getLightRenderer().initLight();
	}

	public void downFloor() {
		setFloor(_floor - 1);
	}

	public UserItem getRandomToy(int posX, int posY) {
		List<UserItem> items = new ArrayList<UserItem>(); 
		for (int offsetX = 0; offsetX < 14; offsetX++) {
			for (int offsetY = 0; offsetY < 14; offsetY++) {
				UserItem item = null;
				item = getItem(posX + offsetX, posY + offsetY);
				if (item != null && item.isToy() && item.hasFreeSlot()) { items.add(item); }
				item = getItem(posX - offsetX, posY - offsetY);
				if (item != null && item.isToy() && item.hasFreeSlot()) { items.add(item); }
				item = getItem(posX + offsetX, posY - offsetY);
				if (item != null && item.isToy() && item.hasFreeSlot()) { items.add(item); }
				item = getItem(posX - offsetX, posY + offsetY);
				if (item != null && item.isToy() && item.hasFreeSlot()) { items.add(item); }
			}
		}
		if (items.size() > 0) {
			return items.get((int)(Math.random() * items.size()));
		}
		return null;
	}

	// TODO
	public List<Room> getRooms() {
		return new ArrayList<Room>(_rooms.values());
	}

	public void addRoom(Room room) {
		_rooms.put(room.getId(), room);
	}

}

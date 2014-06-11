package alone.in.deepspace.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

import alone.in.deepspace.Game;
import alone.in.deepspace.engine.renderer.MainRenderer;
import alone.in.deepspace.manager.PathManager.MyMover;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.item.FactoryItem;
import alone.in.deepspace.model.item.ItemBase;
import alone.in.deepspace.model.item.ItemFilter;
import alone.in.deepspace.model.item.ItemInfo;
import alone.in.deepspace.model.item.StackItem;
import alone.in.deepspace.model.item.StructureItem;
import alone.in.deepspace.model.item.UserItem;
import alone.in.deepspace.model.item.WorldArea;
import alone.in.deepspace.model.item.WorldResource;
import alone.in.deepspace.model.room.Room;
import alone.in.deepspace.util.Constant;
import alone.in.deepspace.util.Log;

public class WorldManager implements TileBasedMap {
	public static class DebugPos {
		public int 			x;
		public int 			y;
		public boolean 		inPath;
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

	private List<FactoryItem> 	_factoryItems;

	public WorldManager() {
		_itemCout = 0;
		_width = Constant.WORLD_WIDTH;
		_height = Constant.WORLD_HEIGHT;
		_factoryItems = new ArrayList<FactoryItem>();

		_rooms = new HashMap<Integer, Room>();
		_floors = new WorldArea[NB_FLOOR][_width][_height];
		for (int f = 0; f < NB_FLOOR; f++) {
			_floors[f] = new WorldArea[_width][_height];
			for (int x = 0; x < _width; x++) {
				_floors[f][x] = new WorldArea[_height];
				for (int y = 0; y < _height; y++) {
					_floors[f][x][y] = new WorldArea(x, y, f);
				}
			}
		}
		
		_areas = _floors[0];
	}

	public ItemBase putItem(String name, int x, int y, int z, int i) {
		return putItem(Game.getData().getItemInfo(name), z, x, y, i);
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
			WorldResource ressource = (WorldResource)putItem("base.res", 0, realX, realY, 10);
			JobManager.getInstance().addGather(ressource);
			return true;
		}
		return false;
	}

	public int	gather(WorldResource resource, int maxValue) {
		if (resource == null || maxValue == 0) {
			Log.error("gather: wrong call");
			return 0;
		}

		int value = resource.gatherMatter(maxValue);
		int x = resource.getX();
		int y = resource.getY();
		
		if (resource.isDepleted()) {
			_areas[x][y].setRessource(null);
		}
		
		MainRenderer.getInstance().invalidate(x, y);
		
		return value;
	}

	public void	update() {
		
		// Add random seed each 10 update
		//if (_count % 10 == 0) {
//		addRandomSeed();
		//}
	}

	// TODO: itemName
	public ItemBase	find(String itemName, boolean free) {
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

	// TODO: call job listener
	public void removeStructure(int x, int y) {
		if (x >= 0 && y >= 0 && x < _width && y < _height) {
			if (_areas[x][y].getItem() != null) {
				_areas[x][y].setItem(null);
			}
			_areas[x][y].setStructure(null);
			MainRenderer.getInstance().invalidate(x, y);
		}
	}

	// TODO: call job listener
	public void removeItem(ItemBase item) {
		if (item != null) {
			int x = item.getX();
			int y = item.getY();
			item.setOwner(null);
			_areas[x][y].setItem(null);
			MainRenderer.getInstance().invalidate(x, y);
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

	public ItemBase putItem(String name, int x, int y, boolean isFree) {
		if (_itemCout + 1 > LIMIT_ITEMS) {
			Log.error("LIMIT_ITEMS reached");
			return null;
		}

		return putItem(Game.getData().getItemInfo(name), _floor, x, y, isFree ? 999 : 0);
	}

	public ItemBase putItem(String name, int x, int y) {
		if (_itemCout + 1 > LIMIT_ITEMS) {
			Log.error("LIMIT_ITEMS reached");
			return null;
		}

		return putItem(name, x, y, false);
	}

	public ItemBase putItem(ItemInfo info, int f, int x, int y, int matterSupply) {
		// Return if out of bound
		if (f < 0 || f >= NB_FLOOR || x < 0 || y < 0 || x >= _width || y >= _height) {
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

		// Return if same item already exists at this position
		WorldArea area = _floors[f][x][y];
		if (area == null) {
			area = new WorldArea(x, y, f);
			_floors[f][x][y] = area;
		}

		// Item already exists
		if (area.getItem() != null && area.getItem().getName().equals(info.name) ||
				area.getStructure() != null && area.getStructure().getName().equals(info.name)) {
			return null;
		}

		// Get new item
		ItemBase item = null;

		// Base light item
		if ("base.light".equals(info.name)) {
			area.setLightSource(info.light);
			item = new UserItem(info);
			((MainRenderer)MainRenderer.getInstance()).initLight();
		}
		// Storage item
		else if (info.isStorage) {
			Log.error("storage item is deprecated: " + info.name);
			return null;
		}
		// World resource
		else if (info.isResource) {
			item = createResource(area, info, x, y, matterSupply);
		}
		// Structure item
		else if (info.isStructure) {
			item = createStructure(area, info, x, y, matterSupply);
		}
		// User item
		else {
			item = createUserItem(area, info, x, y, matterSupply);
		}

		item.setPosition(x, y);
		MainRenderer.getInstance().invalidate(x, y);

		return item;
	}

	private UserItem createUserItem(WorldArea area, ItemInfo info, int x, int y, int matterSupply) {
		UserItem item = null;

		// Factory item
		if (info.isFactory) {
			item = new FactoryItem(info);
			_factoryItems.add((FactoryItem)item);
		}
		// Stack item
		else if (info.isStack) {
			item = new StackItem();
		}
		// Regular user item
		else {
			item = new UserItem(info);
		}
		
		item.setMatterSupply(matterSupply);
		area.setItem(item);
		
		return item;
	}

	private StructureItem createStructure(WorldArea area, ItemInfo info, int x, int y, int matterSupply) {
		StructureItem structure = new StructureItem(info);
		
		structure.setMatterSupply(matterSupply);
		_floors[0][x][y].setStructure(structure);
		area.setStructure(structure);

		return structure;
	}

	private ItemBase createResource(WorldArea area, ItemInfo info, int x, int y, int matterSupply) {
		WorldResource resource = new WorldResource(info);
		
		resource.setValue(matterSupply);
		_floors[0][x][y].setRessource(resource);
		area.setRessource(resource);
		
		return resource;
	}

	public UserItem 			getItem(int x, int y) {
		return (x < 0 || x >= _width || y < 0 || y >= _height) || _areas[x][y] == null ? null : _areas[x][y].getItem();
	}

	// TODO
	public UserItem 			find(ItemFilter filter) {
		for (int x = 0; x < _width; x++) {
			for (int y = 0; y < _height; y++) {
				UserItem item = _floors[0][x][y].getItem();
				if (item != null && item.matchFilter(filter)) {
					return item;
				}
			}
		}
		return null;
	}
	
	public WorldResource getRessource(int f, int x, int y) {
		return (f < 0 || f >= NB_FLOOR || x < 0 || x >= _width || y < 0 || y >= _height) ? null : _floors[f][x][y].getRessource();
	}

	public StructureItem getStructure(int f, int x, int y) {
		return (f < 0 || f >= NB_FLOOR || x < 0 || x >= _width || y < 0 || y >= _height) ? null : _floors[f][x][y].getStructure();
	}

	public StructureItem 		getStructure(int x, int y) {
		return getStructure(_floor, x, y);
	}

	public WorldResource   	getRessource(int x, int y) {
		return getRessource(_floor, x, y);
	}

	public WorldArea			getArea(int x, int y) {
		return (x < 0 || x >= _width || y < 0 || y >= _height) ? null : _areas[x][y];
	}

	public Room					getRoom(int id) { return _rooms.get(id); }
	public int					getRoomCount() { return _rooms.size(); }
	public int					getWidth() { return _width; }
	public int					getHeight() { return _height; }

	public UserItem getNearest(ItemFilter filter, Character character) {
		int startX = character.getX();
		int startY = character.getY();
		int maxX = Math.max(startX, _width - startX);
		int maxY = Math.max(startY, _height - startY);
		for (int offsetX = 0; offsetX < maxX; offsetX++) {
			for (int offsetY = 0; offsetY < maxY; offsetY++) {
				WorldArea area = getArea(startX + offsetX, startY + offsetY);

				// Check on non-existing area
				if (area == null) {
					continue;
				}
				
				// Private room exists and character is not allowed
				if (area.getRoom() != null && area.getRoom().isPrivate() && area.getRoom().getOccupants().contains(character) == false) {
					continue;
				}
				
				UserItem item = getItem(startX + offsetX, startY + offsetY);
				if (getNearestItemCheck(item, filter)) { return item; }

				item = getItem(startX - offsetX, startY - offsetY);
				if (getNearestItemCheck(item, filter)) { return item; }

				item = getItem(startX + offsetX, startY - offsetY);
				if (getNearestItemCheck(item, filter)) { return item; }

				item = getItem(startX - offsetX, startY + offsetY);
				if (getNearestItemCheck(item, filter)) { return item; }
			}
		}
		return null;
	}

	private boolean getNearestItemCheck(UserItem item, ItemFilter filter) {
		// Item not exists
		if (item == null) {
			return false;
		}
		
		// Item is blocked
		if (item.getLastBlocked() != -1 && item.getLastBlocked() < Game.getUpdate() + Constant.COUNT_BEFORE_REUSE_BLOCKED_ITEM) {
			return false;
		}
		
		// Item is not completed
		if (item.isComplete() == false) {
			return false;
		}
		
		// Item don't match filter
		if (item.matchFilter(filter) == false) {
			return false;
		}
		
		return true;
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

	public List<FactoryItem> getFactories() { return _factoryItems; }

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

//	public void storeItem(UserItem carriedItem, int x, int y) {
//		UserItem onAreaItem = _areas[x][y].getItem();
//		
//		if (onAreaItem == null) {
//			// TODO
//			ItemInfo info = Game.getData().getItemInfo("base.storage");
//			onAreaItem = new StorageItem(info);
//			((StorageItem)carriedItem).addInventory(carriedItem);
//			_areas[x][y].setItem(onAreaItem);
//		} else if (carriedItem.isStorage()) {
//			((StorageItem)onAreaItem).addInventory(carriedItem);
//		} else {
//			// TODO: not implemented
//			Log.error("Storage area is used by non storage item");
//		}
//	}
//
	public void replaceItem(ItemInfo info, int x, int y) {
		replaceItem(info, _floor, x, y);
	}

	private void replaceItem(ItemInfo info, int f, int x, int y) {
		if (info.isResource) {
			_floors[f][x][y].setRessource(null);
		} else if (info.isStructure) {
			_floors[f][x][y].setStructure(null);
		} else {
			_floors[f][x][y].setItem(null);
		}
		putItem(info, f, x, y, 0);
	}

	public ItemBase putItem(ItemInfo info, int x, int y) {
		return putItem(info, _floor, x, y, 0);
	}

	public int getFloor() {
		return _floor;
	}

	public void upFloor() {
		setFloor(_floor + 1);
	}

	private void setFloor(int floor) {
		if (floor < 0 || floor >= NB_FLOOR) {
			return;
		}
		
		_floor = floor;
		_areas = _floors[floor];
		MainRenderer.getInstance().invalidate();
	}

	public void downFloor() {
		setFloor(_floor - 1);
	}

	// TODO: heavy
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

//	public void cleanRock() {
//		for (int f = 0; f < NB_FLOOR; f++) {
//			_areas = _floors[f];
//			for (int x = Constant.WORLD_WIDTH; x >= 0; x--) {
//				for (int y = Constant.WORLD_HEIGHT; y >= 0; y--) {
//					WorldResource structure = getRessource(f, x, y);
//					if (structure != null && structure.isRock()) {
//						if (isRock(x, y-1) && isRock(x, y+1) && isRock(x-1, y) && isRock(x+1, y)) { structure.setTile(23); }
//
//						// Right
//						else if (isRock(x, y-1) && isRock(x, y+1) && isRock(x-1, y) && notRock(x+1, y)) { structure.setTile(35); }
//						// Top
//						else if (notRock(x, y-1) && isRock(x, y+1) && isRock(x-1, y) && isRock(x+1, y)) { structure.setTile(4); }
//						
//						
//						// Top Right
//						else if (notRock(x, y-1) && isRock(x, y+1) && isRock(x-1, y) && notRock(x+1, y)) { structure.setTile(16); }
//						
//					}
//				}
//			}
//		}
//	}
////	
	public void cleanRock() {
		for (int f = 0; f < 1; f++) {
			_areas = _floors[f];
			for (int x = Constant.WORLD_WIDTH; x >= 0; x--) {
				for (int y = Constant.WORLD_HEIGHT; y >= 0; y--) {
					WorldResource resource = getRessource(f, x, y);
					if (resource != null && resource.isRock()) {
						// 4 faces
						if (isRock(x, y-1) && isRock(x, y+1) && isRock(x-1, y) && isRock(x+1, y)) {
							if (notRock(x-1, y-1) && notRock(x+1, y+1)) { resource.setTile(6); } // 16
							else if (notRock(x+1, y+1) && notRock(x-1, y-1)) { resource.setTile(16); } // 6

							else if (notRock(x+1, y-1) && notRock(x-1, y-1)) { resource.setTile(0); } // 7
							else if (notRock(x+1, y+1) && notRock(x-1, y+1)) { resource.setTile(0); } // 17 
							else if (notRock(x+1, y+1) && notRock(x+1, y-1)) { resource.setTile(0); } // 27
							else if (notRock(x-1, y+1) && notRock(x-1, y-1)) { resource.setTile(0); } // 37

							else if (notRock(x+1, y-1)) { resource.setTile(14); setTop(f, x, y+1, 24); } // ok
							else if (notRock(x-1, y-1)) { resource.setTile(11); setTop(f, x, y+1, 21); } // ok
							else if (notRock(x+1, y+1)) { resource.setTile(59); setTop(f, x, y-1, 49); setTop(f, x, y-2, 39); } // ok + 55 bellow
							else if (notRock(x-1, y+1)) { resource.setTile(56); setTop(f, x, y-1, 46); setTop(f, x, y-2, 36); setTop(f, x+1, y, 31); } // ok + 50 beloow
						}
						
						// 3 faces
						else if (isRock(x, y-1) && isRock(x, y+1) && isRock(x-1, y) && notRock(x+1, y)) {
							WorldResource res = getRessource(f, x, y+2);
							// Rock bellow
							if (res != null && res.isRock()) {
								resource.setTile(25); setTop(f, x-1, y, 24);
							} else {
								resource.setTile(25);
							}
							
						} // ok
						else if (isRock(x, y-1) && isRock(x, y+1) && notRock(x-1, y) && isRock(x+1, y)) {
							WorldResource res = getRessource(f, x, y-1);
							if (res != null && res.isRock() && (res.getTile() == 66 || res.getTile() == 56 || res.getTile() == 50)) {
								resource.setTile(25); setTop(f, x+1, y, 31);
							} else {
								res = getRessource(f, x, y+1);
								if (res != null && res.isRock() && (res.getTile() == 66 || res.getTile() == 50)) {
									resource.setTile(20); setTop(f, x+1, y, 31);
								} else {
									resource.setTile(20);
								}
							}
						} // ok
						else if (isRock(x, y-1) && notRock(x, y+1) && isRock(x-1, y) && isRock(x+1, y)) {
							WorldResource res = getRessource(f, x-1, y);
							if (res != null && res.isRock() && res.getTile() == 68) {
								resource.setTile(67); setTop(f, x, y-1, 57); setTop(f, x, y-2, 47); setTop(f, x, y-3, 37);
							} else {
								resource.setTile(68); setTop(f, x, y-1, 58); setTop(f, x, y-2, 48); setTop(f, x, y-3, 38);
							}
						} // ok + 62 bellow
						else if (notRock(x, y-1) && isRock(x, y+1) && isRock(x-1, y) && isRock(x+1, y)) { resource.setTile(2); setTop(f, x, y+1, 12); setTop(f, x, y+2, 22); } // ok

						// 2 faces
						else if (isRock(x, y-1) && isRock(x, y+1) && notRock(x-1, y) && notRock(x+1, y)) { resource.setTile(0); } // 36
						else if (notRock(x, y-1) && notRock(x, y+1) && isRock(x-1, y) && isRock(x+1, y)) { resource.setTile(0); } // 26
						else if (isRock(x, y-1) && notRock(x, y+1) && notRock(x-1, y) && isRock(x+1, y)) { resource.setTile(66); setTop(f, x, y-1, 50); } // 14
						else if (isRock(x, y-1) && notRock(x, y+1) && isRock(x-1, y) && notRock(x+1, y)) { resource.setTile(69); } // 143
						else if (notRock(x, y-1) && isRock(x, y+1) && isRock(x-1, y) && notRock(x+1, y)) { resource.setTile(15); } // ok
						else if (notRock(x, y-1) && isRock(x, y+1) && notRock(x-1, y) && isRock(x+1, y)) { resource.setTile(10); } // ok

						// 1 face
						else if (isRock(x, y-1) && notRock(x, y+1) && notRock(x-1, y) && notRock(x+1, y)) { resource.setTile(0); } // 34
						else if (notRock(x, y-1) && isRock(x, y+1) && notRock(x-1, y) && notRock(x+1, y)) { resource.setTile(0); } // 35
						else if (notRock(x, y-1) && notRock(x, y+1) && isRock(x-1, y) && notRock(x+1, y)) { resource.setTile(0); } // 25
						else if (notRock(x, y-1) && notRock(x, y+1) && notRock(x-1, y) && isRock(x+1, y)) { resource.setTile(0); } // 24

					}
				}
			}
		}
	}
//
//	public void cleanRock() {
//		for (int f = 0; f < NB_FLOOR; f++) {
//			_areas = _floors[f];
//			for (int x = 0; x < Constant.WORLD_WIDTH; x++) {
//				for (int y = 0; y < Constant.WORLD_HEIGHT; y++) {
//					WorldRessource structure = getRessource(x, y);
//					if (structure != null && structure.isRock()) {
//						// 4 faces
//						if (isRock(x, y-1) && isRock(x, y+1) && isRock(x-1, y) && isRock(x+1, y)) {
//							if (notRock(x-1, y-1) && notRock(x+1, y+1)) { structure.setTile(16); }
//							else if (notRock(x+1, y+1) && notRock(x-1, y-1)) { structure.setTile(6); }
//
//							else if (notRock(x+1, y-1) && notRock(x-1, y-1)) { structure.setTile(7); }
//							else if (notRock(x+1, y+1) && notRock(x-1, y+1)) { structure.setTile(17); }
//							else if (notRock(x+1, y+1) && notRock(x+1, y-1)) { structure.setTile(27); }
//							else if (notRock(x-1, y+1) && notRock(x-1, y-1)) { structure.setTile(37); }
//
//							else if (notRock(x+1, y-1)) { structure.setTile(20); }
//							else if (notRock(x-1, y-1)) { structure.setTile(22); }
//							else if (notRock(x+1, y+1)) { structure.setTile(0); }
//							else if (notRock(x-1, y+1)) { structure.setTile(2); }
//							else {structure.setTile(11);}
//						}
//						
//						// 3 faces
//						else if (isRock(x, y-1) && isRock(x, y+1) && isRock(x-1, y) && notRock(x+1, y)) { structure.setTile(10); }
//						else if (isRock(x, y-1) && isRock(x, y+1) && notRock(x-1, y) && isRock(x+1, y)) { structure.setTile(12); }
//						else if (isRock(x, y-1) && notRock(x, y+1) && isRock(x-1, y) && isRock(x+1, y)) { structure.setTile(1); }
//						else if (notRock(x, y-1) && isRock(x, y+1) && isRock(x-1, y) && isRock(x+1, y)) { structure.setTile(21); }
//
//						// 2 faces
//						else if (isRock(x, y-1) && isRock(x, y+1) && notRock(x-1, y) && notRock(x+1, y)) { structure.setTile(36); }
//						else if (notRock(x, y-1) && notRock(x, y+1) && isRock(x-1, y) && isRock(x+1, y)) { structure.setTile(26); }
//						else if (isRock(x, y-1) && notRock(x, y+1) && notRock(x-1, y) && isRock(x+1, y)) { structure.setTile(14); }
//						else if (isRock(x, y-1) && notRock(x, y+1) && isRock(x-1, y) && notRock(x+1, y)) { structure.setTile(15); }
//						else if (notRock(x, y-1) && isRock(x, y+1) && isRock(x-1, y) && notRock(x+1, y)) { structure.setTile(5); }
//						else if (notRock(x, y-1) && isRock(x, y+1) && notRock(x-1, y) && isRock(x+1, y)) { structure.setTile(4); }
//
//						// 1 face
//						else if (isRock(x, y-1) && notRock(x, y+1) && notRock(x-1, y) && notRock(x+1, y)) { structure.setTile(34); }
//						else if (notRock(x, y-1) && isRock(x, y+1) && notRock(x-1, y) && notRock(x+1, y)) { structure.setTile(35); }
//						else if (notRock(x, y-1) && notRock(x, y+1) && isRock(x-1, y) && notRock(x+1, y)) { structure.setTile(25); }
//						else if (notRock(x, y-1) && notRock(x, y+1) && notRock(x-1, y) && isRock(x+1, y)) { structure.setTile(24); }
//
//					}
//				}
//			}
//		}
//	}

	private void setTop(int f, int x, int y, int tile) {
		WorldResource topres = getRessource(f, x, y);
		if (topres != null && topres.isRock() && topres.getTile() == 0) {
			topres.setTile(tile);
		}
	}

	private boolean notRock(int x, int y) {
		if (x < 0 || x >= _width || y < 0 || y >= _height) {
			return false;
		}
		if (_areas[x][y].getRessource() != null && _areas[x][y].getRessource().isRock()) {
			return false;
		}
		return true;
	}

	private boolean notGrass(int x, int y) {
		if (x < 0 || x >= _width || y < 0 || y >= _height) {
			return false;
		}
		if (_areas[x][y].getRessource() != null && _areas[x][y].getRessource().isGrass()) {
			return false;
		}
		return true;
	}

	private boolean isRock(int x, int y) {
		if (x < 0 || x >= _width || y < 0 || y >= _height) {
			return true;
		}
		if (_areas[x][y].getRessource() != null && _areas[x][y].getRessource().isRock()) {
			return true;
		}
		return false;
	}

	private boolean isGrass(int x, int y) {
		if (x < 0 || x >= _width || y < 0 || y >= _height) {
			return true;
		}
		if (_areas[x][y].getRessource() != null && _areas[x][y].getRessource().isGrass()) {
			return true;
		}
		return false;
	}

	public void cleanRock2() {
//		for (int f = 0; f < NB_FLOOR; f++) {
//			_areas = _floors[f];
//			for (int x = Constant.WORLD_WIDTH; x >= 0; x--) {
//				for (int y = Constant.WORLD_HEIGHT; y >= 0; y--) {
//					WorldRessource structure = getRessource(x, y);
//					if (structure != null && structure.isRock() && okErase(structure.getTile())) {
//						if (f < NB_FLOOR - 1) {
//							WorldArea topFloorArea = _floors[f+1][x][y];
//							if (topFloorArea.getRessource() != null) {
//								structure.setDoubleRender(structure.getTile());
//								structure.setTile(topFloorArea.getRessource().getTile());
//							}
//						}
//					}
//				}
//			}
//		}
	}

//	private boolean okErase(int tile) {
//		switch (tile) {
//		case 0:
//		case 36:
//		case 38:
//		case 39:
//		case 48:
//			return true;
//		}
//		return false;
//	}

	public void removeResource(WorldResource resource) {
		if (resource == null) {
			return;
		}
		
		int x = resource.getX();
		int y = resource.getY();

		if (_areas[x][y].getRessource() != resource) {
			return;
		}
		
		_areas[resource.getX()][resource.getY()].setRessource(null);
		MainRenderer.getInstance().invalidate(x, y);
		MainRenderer.getInstance().invalidate();
	}

	public WorldArea getArea(int z, int x, int y) {
		return _floors[z][x][y];
	}

	@Override
	public boolean blocked(PathFindingContext context, int x, int y) {
		MyMover mover = (MyMover)context.getMover();
		if (mover.targetX == x && mover.targetY == y) {
			return false;
		}
		
		if (x >= 0 && y >= 0 && x < _width && y < _height) {
			return _areas[x][y].getStructure() != null && _areas[x][y].getStructure().isComplete() && _areas[x][y].getStructure().isSolid();
		}
		return false;
	}

	@Override
	public float getCost(PathFindingContext context, int tx, int ty) {
		return context.getSourceX() != tx && context.getSourceY() != ty ? 1.5f : 1f;
	}

	public void cleanGrass() {
		for (int f = 0; f < NB_FLOOR; f++) {
			_areas = _floors[f];
			for (int x = Constant.WORLD_WIDTH; x >= 0; x--) {
				for (int y = Constant.WORLD_HEIGHT; y >= 0; y--) {
					WorldResource structure = getRessource(f, x, y);
					if (structure != null && structure.isGrass()) {
						// 4 faces
						if (isGrass(x, y-1) && isGrass(x, y+1) && isGrass(x-1, y) && isGrass(x+1, y)) {
							// inside corner
							if (notGrass(x-1, y-1)) { structure.setTile(3); }
							else if (notGrass(x+1, y-1)) { structure.setTile(4); }
							else if (notGrass(x-1, y+1)) { structure.setTile(13); }
							else if (notGrass(x+1, y+1)) { structure.setTile(14); }
							else { structure.setTile(11); } 
						}

						// 3 faces
						if (notGrass(x, y-1) && isGrass(x, y+1) && isGrass(x-1, y) && isGrass(x+1, y)) { structure.setTile(1); }
						if (isGrass(x, y-1) && notGrass(x, y+1) && isGrass(x-1, y) && isGrass(x+1, y)) { structure.setTile(21); }
						if (isGrass(x, y-1) && isGrass(x, y+1) && notGrass(x-1, y) && isGrass(x+1, y)) { structure.setTile(10); }
						if (isGrass(x, y-1) && isGrass(x, y+1) && isGrass(x-1, y) && notGrass(x+1, y)) { structure.setTile(12); }

						// 2 faces
						if (notGrass(x, y-1) && isGrass(x, y+1) && notGrass(x-1, y) && isGrass(x+1, y)) { structure.setTile(0); }
						if (isGrass(x, y-1) && notGrass(x, y+1) && notGrass(x-1, y) && isGrass(x+1, y)) { structure.setTile(20); }
						if (isGrass(x, y-1) && notGrass(x, y+1) && isGrass(x-1, y) && notGrass(x+1, y)) { structure.setTile(22); }
						if (notGrass(x, y-1) && isGrass(x, y+1) && isGrass(x-1, y) && notGrass(x+1, y)) { structure.setTile(2); }
					}
				}
			}
		}
	}

	public WorldResource putResource(ItemInfo info, int f, int x, int y, int value) {
		// Return if out of bound
		if (f < 0 || f >= NB_FLOOR || x < 0 || y < 0 || x >= _width || y >= _height) {
			Log.error("put item out of bound, type: " + info.name + ", x: " + x + ", y: " + y + ")");
			return null;
		}

		WorldArea area = _floors[f][x][y];

		// Resource already exists
		if (area.getRessource() != null) {
			return null;
		}

		WorldResource resource = new WorldResource(info);
		resource.setPosition(x, y);
		resource.setValue(value);
		area.setRessource(resource);
		MainRenderer.getInstance().invalidate(x, y);

		return resource;
	}

	public UserItem takeItem(int x, int y) {
		UserItem item = getItem(x, y);
		WorldArea area = getArea(x, y);
		if (area != null) {
			area.setItem(null);
			MainRenderer.getInstance().invalidate(x, y);
		}
		return item;
	}
}

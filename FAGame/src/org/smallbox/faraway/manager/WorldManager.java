package org.smallbox.faraway.manager;

import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;
import org.smallbox.faraway.Game;
import org.smallbox.faraway.engine.renderer.MainRenderer;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.PathManager.MyMover;
import org.smallbox.faraway.model.item.*;
import org.smallbox.faraway.model.room.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldManager implements TileBasedMap {

    public WorldArea[][][] getAreas() {
        return _areas;
    }

    public static class DebugPos {
        public int 			x;
        public int 			y;
        public int 			z;
        public boolean 		inPath;
    }

    private static final int 	NB_FLOOR = 10;

    private Map<Integer, Room>	_rooms;
    private WorldArea[][][]		_areas;
    private int					_width;
    private int					_height;
    //	private Vector<DebugPos> 	_debugPath;
//	private DebugPos 			_debugPathStart;
//	private DebugPos 			_debugPathStop;
    private int 				_floor;
    private List<FactoryItem> 	_factoryItems;
    private WorldFinder		 	_finder;

    public WorldManager() {
        _width = Constant.WORLD_WIDTH;
        _height = Constant.WORLD_HEIGHT;
        _factoryItems = new ArrayList<>();

        _rooms = new HashMap<>();
        _areas = new WorldArea[_width][_height][NB_FLOOR];
        for (int x = 0; x < _width; x++) {
            _areas[x] = new WorldArea[_height][NB_FLOOR];
            for (int y = 0; y < _height; y++) {
                _areas[x][y] = new WorldArea[NB_FLOOR];
                for (int f = 0; f < NB_FLOOR; f++) {
                    _areas[x][y][f] = new WorldArea(x, y, f);
                }
            }
        }

        _finder = new WorldFinder(this, _areas);
    }

    public ItemBase putItem(String name, int x, int y, int z, int i) {
        return putItem(Game.getData().getItemInfo(name), x, y, z, i);
    }

    public void	addRandomSeed() {
        int startX = (int)(Math.random() * 10000) % _width;
        int startY = (int)(Math.random() * 10000) % _height;

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
        if (_areas[realX][realY][0].getStructure() == null) {
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
            _areas[x][y][0].setResource(null);
        }

        MainRenderer.getInstance().invalidate(x, y);

        return value;
    }

    public void removeItem(ItemBase item) {
        if (item != null && item.getArea() != null) {
            item.getArea().setItem(null);
        }
    }

    // TODO: call job listener
    public void removeStructure(int x, int y) {
        if (x >= 0 && y >= 0 && x < _width && y < _height) {
            if (_areas[x][y][0].getItem() != null) {
                _areas[x][y][0].setItem(null);
            }
            _areas[x][y][0].setStructure(null);
            MainRenderer.getInstance().invalidate(x, y);
        }
    }

    public ItemBase putItem(ItemInfo itemInfo, int x, int y, int z, int matterSupply) {
        // Return if out of bound
        if (z < 0 || z >= NB_FLOOR || x < 0 || y < 0 || x >= _width || y >= _height) {
            Log.error("put item out of bound, type: " + itemInfo.name + ", x: " + x + ", y: " + y + ")");
            return null;
        }

        // Return if same item already exists at this position
        WorldArea area = _areas[x][y][z];
        if (area == null) {
            area = new WorldArea(x, y, z);
            _areas[x][y][z] = area;
        }

        // Put item on nearest empty area / similar item
        if (itemInfo.isUserItem || itemInfo.isConsomable) {
            for (int i = 0; i < 10; i++) {
                if (areaFreeForItem(area.getX() + 0, area.getY() + 0, itemInfo)) return putItemOnArea(area.getX() + 0, area.getY() + 0, itemInfo);
                if (areaFreeForItem(area.getX() + i, area.getY() + 0, itemInfo)) return putItemOnArea(area.getX() + i, area.getY() + 0, itemInfo);
                if (areaFreeForItem(area.getX() + 0, area.getY() + i, itemInfo)) return putItemOnArea(area.getX() + 0, area.getY() + i, itemInfo);
                if (areaFreeForItem(area.getX() - i, area.getY() + 0, itemInfo)) return putItemOnArea(area.getX() - i, area.getY() + 0, itemInfo);
                if (areaFreeForItem(area.getX() + 0, area.getY() - i, itemInfo)) return putItemOnArea(area.getX() + 0, area.getY() - i, itemInfo);
                if (areaFreeForItem(area.getX() + i, area.getY() + i, itemInfo)) return putItemOnArea(area.getX() + i, area.getY() + i, itemInfo);
                if (areaFreeForItem(area.getX() - 0, area.getY() - i, itemInfo)) return putItemOnArea(area.getX() - 0, area.getY() - i, itemInfo);
                if (areaFreeForItem(area.getX() + i, area.getY() - i, itemInfo)) return putItemOnArea(area.getX() + i, area.getY() - i, itemInfo);
                if (areaFreeForItem(area.getX() - i, area.getY() + i, itemInfo)) return putItemOnArea(area.getX() - i, area.getY() + i, itemInfo);
            }
        }

        // Put structure on area
        else {
            if (area.getStructure() == null || area.getStructure().isGround()) {
                ItemBase item = ItemFactory.create(this, area, itemInfo, matterSupply);
                if (item != null) {
                    item.setPosition(x, y);
                    MainRenderer.getInstance().invalidate(x, y);
                }
                return item;
            }
        }

        return null;
    }

    private UserItem putItemOnArea(int x, int y, ItemInfo itemInfo) {

        // Put UserItem on floor
        if (itemInfo.isUserItem) {
            UserItem item = (UserItem)ItemFactory.create(this, _areas[x][y][0], itemInfo, 100);
            for (int i = 0; i < item.getWidth(); i++) {
                for (int j = 0; j < item.getHeight(); j++) {
                    _areas[x][y][0].setItem(item);
                }
            }
        }

        // Put ConsumableItem on floor / or in existing stack
        else if (itemInfo.isConsomable) {
            if (_areas[x][y][0].getConsumable() != null) {
                _areas[x][y][0].getConsumable().addQuantity(1);
            } else {
                _areas[x][y][0].setConsumable((ConsumableItem)ItemFactory.create(this, _areas[x][y][0], itemInfo, 1));
            }
        }
        MainRenderer.getInstance().invalidate(x, y);
        System.out.println("put item on floor: " + itemInfo.name + " (" + x + "x" + y + ")");

        return _areas[x][y][0].getItem();
    }

    private boolean areaFreeForItem(int x, int y, ItemInfo info) {
        if (x >= 0 && x < _width && y >= 0 && y < _height) {
            WorldArea area = _areas[x][y][0];
            if (area.getStructure() == null || area.getStructure().isFloor()) {

                // If item is UserItem floor need to be clear
                if (info.isUserItem) {
                    return area.getItem() == null && area.getConsumable() == null;
                }

                // If item is consumable check for empty floor or similar item
                else if (info.isConsomable) {
                    return area.getItem() == null && (area.getConsumable() == null || area.getConsumable().getInfo() == info);
                }
            }
        }
        return false;
    }

    public UserItem 			getItem(int x, int y) {
        return (x < 0 || x >= _width || y < 0 || y >= _height) || _areas[x][y] == null ? null : _areas[x][y][0].getItem();
    }

    public WorldResource getResource(int x, int y, int z) {
        return (z < 0 || z >= NB_FLOOR || x < 0 || x >= _width || y < 0 || y >= _height) ? null : _areas[x][y][z].getResource();
    }

    public StructureItem getStructure(int x, int y, int z) {
        return (z < 0 || z >= NB_FLOOR || x < 0 || x >= _width || y < 0 || y >= _height) ? null : _areas[x][y][z].getStructure();
    }

    public ConsumableItem getConsumable(int x, int y) {
        return (x < 0 || x >= _width || y < 0 || y >= _height) || _areas[x][y] == null ? null : _areas[x][y][0].getConsumable();
    }

    public StructureItem 		getStructure(int x, int y) {
        return getStructure(x, y, _floor);
    }

    public WorldResource getResource(int x, int y) {
        return getResource(x, y, _floor);
    }

    public WorldArea			getArea(int x, int y) {
        return (x < 0 || x >= _width || y < 0 || y >= _height) ? null : _areas[x][y][0];
    }

    public Room					getRoom(int id) { return _rooms.get(id); }
    public int					getRoomCount() { return _rooms.size(); }
    public int					getWidth() { return _width; }
    public int					getHeight() { return _height; }
    public WorldFinder			getFinder() { return _finder; }

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

//	public void stopDebug(int x, int y) {
//		_debugPathStop = new DebugPos();
//		_debugPathStop.x = x;
//		_debugPathStop.y = y;
//	}
//
//	public void startDebug(int posX, int posY) {
//		_debugPath = new Vector<DebugPos>();
//		_debugPathStart = new DebugPos();
//		_debugPathStart.x = posX;
//		_debugPathStart.y = posY;
//	}

    public void replaceItem(ItemInfo info, int x, int y, int matterSupply) {
        replaceItem(info, x, y, _floor, matterSupply);
    }

    private void replaceItem(ItemInfo info, int x, int y, int z, int matterSupply) {
        if (info.isResource) {
            _areas[x][y][z].setResource(null);
        } else if (info.isStructure) {
            _areas[x][y][z].setStructure(null);
        } else {
            _areas[x][y][z].setItem(null);
        }
        putItem(info, x, y, z, matterSupply);
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

    public void removeResource(WorldResource resource) {
        if (resource == null) {
            return;
        }

        int x = resource.getX();
        int y = resource.getY();

        if (_areas[x][y][0].getResource() != resource) {
            return;
        }

        _areas[resource.getX()][resource.getY()][0].setResource(null);
        MainRenderer.getInstance().invalidate(x, y);
        MainRenderer.getInstance().invalidate();
    }

    public WorldArea getArea(int z, int x, int y) {
        return _areas[x][y][z];
    }

    @Override
    public boolean blocked(PathFindingContext context, int x, int y) {
        MyMover mover = (MyMover)context.getMover();
        if (mover.targetX == x && mover.targetY == y) {
            return false;
        }

        if (x >= 0 && y >= 0 && x < _width && y < _height) {
            return _areas[x][y][0].getStructure() != null && _areas[x][y][0].getStructure().isComplete() && _areas[x][y][0].getStructure().isSolid();
        }
        return false;
    }

    @Override
    public float getCost(PathFindingContext context, int tx, int ty) {
        float cost = context.getSourceX() != tx && context.getSourceY() != ty ? 1.5f : 1f;

        WorldArea area = _areas[tx][ty][0];
        if (area != null && area.getItem() != null) {
            cost *= 4;
        }

        return cost;
    }

    public UserItem takeItem(UserItem item) {
        if (item != null) {
            WorldArea area = getArea(item.getX(), item.getY());
            if (area != null) {
                if (area.getItem() != item) {
                    Log.error("Area not contains desired item");
                    return null;
                }
                return takeItem(item, area);
            }
        }
        Log.error("Cannot take null item");
        return null;
    }

    private UserItem takeItem(UserItem item, WorldArea area) {
        if (area != null && item != null) {
            area.setItem(null);
            MainRenderer.getInstance().invalidate(area.getX(), area.getY());
            return item;
        }
        Log.error("Area or item is null");
        return null;
    }

    public UserItem takeItem(int x, int y) {
        UserItem item = getItem(x, y);
        WorldArea area = getArea(x, y);
        return takeItem(item, area);
    }

    public void addFactory(FactoryItem factory) {
        _factoryItems.add(factory);
    }

    public void destroy(ItemBase item) {
        if (item == null) {
            Log.error("Cannot destroy null item");
            return;
        }

        // Get area
        int x = item.getX();
        int y = item.getY();
        WorldArea area = getArea(x, y);
        if (area == null) {
            Log.error("Item position not matching existing area");
            return;
        }

        // Remove item according to type
        if (item.isStructure()) {
            area.setStructure(null);
        }
        else if (item.isRessource()) {
            area.setResource(null);
        }
        else if (item.isUserItem()) {
            area.setItem(null);
        }

        // Invalidate renderer
        MainRenderer.getInstance().invalidate(x, y);
    }

    // TODO
    public UserItem getItemById(int itemId) {
        for (int x = 0; x < _width; x++) {
            for (int y = 0; y < _height; y++) {
                if (_areas[x][y][0].getItem() != null && _areas[x][y][0].getItem().getId() == itemId) {
                    return _areas[x][y][0].getItem();
                }
            }
        }
        return null;
    }

}
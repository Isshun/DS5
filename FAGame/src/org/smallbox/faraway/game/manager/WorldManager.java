package org.smallbox.faraway.game.manager;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import org.smallbox.faraway.data.factory.ItemFactory;
import org.smallbox.faraway.engine.renderer.MainRenderer;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.item.*;
import org.smallbox.faraway.util.Log;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class WorldManager extends BaseManager implements IndexedGraph<ParcelModel> {
    private static final int NB_FLOOR = 10;

    private ParcelModel[][][] _parcels;
    private int _width;
    private int _height;
    private int _floor;
    private int _temperature;
    private int _temperatureOffset;
    private final Game _game;
    private Set<ConsumableModel> _consumables = new HashSet<>();

    public ParcelModel[][][] getParcels() {
        return _parcels;
    }

    public WorldManager(Game game) {
        _game = game;
    }

    public void init(int width, int height) {
        _width = width;
        _height = height;

        _parcels = new ParcelModel[_width][_height][NB_FLOOR];
        for (int x = 0; x < _width; x++) {
            _parcels[x] = new ParcelModel[_height][NB_FLOOR];
            for (int y = 0; y < _height; y++) {
                _parcels[x][y] = new ParcelModel[NB_FLOOR];
                for (int f = 0; f < NB_FLOOR; f++) {
                    _parcels[x][y][f] = new ParcelModel(x, y, f);
                    _parcels[x][y][f].setIndex(x * _width + y);
                }
            }
        }

        for (int x = 0; x < _width; x++) {
            for (int y = 0; y < _height; y++) {
                for (int f = 0; f < NB_FLOOR; f++) {
                    createConnection(_parcels[x][y][f]);
                }
            }
        }
    }

    private void createConnection(ParcelModel parcel) {
        Array<Connection<ParcelModel>> connections = new Array<>();
        createConnection(connections, parcel, parcel.getX() + 1, parcel.getY());
        createConnection(connections, parcel, parcel.getX() - 1, parcel.getY());
        createConnection(connections, parcel, parcel.getX(), parcel.getY() + 1);
        createConnection(connections, parcel, parcel.getX(), parcel.getY() - 1);
        parcel.setConnections(connections);
    }

    // TODO: arraylist
    public int getConsumableCount(ItemInfo itemInfo) {
        int count = 0;
        for (int x = 0; x < _width; x++) {
            for (int y = 0; y < _height; y++) {
                if (_parcels[x][y][0] != null && _parcels[x][y][0].getConsumable() != null && _parcels[x][y][0].getConsumable().getInfo() == itemInfo) {
                    count += _parcels[x][y][0].getConsumable().getQuantity();
                }
            }
        }
        return count;
    }

    public MapObjectModel putObject(String name, int x, int y, int z, int i) {
        return putObject(GameData.getData().getItemInfo(name), x, y, z, i);
    }

//    public void	addRandomSeed() {
//        int startX = (int)(Math.random() * 10000) % _width;
//        int startY = (int)(Math.random() * 10000) % _height;
//
//        for (int x = 0; x < 5; x++) {
//            for (int y = 0; y < 5; y++) {
//                if (addRandomSeed(startX + x, startY + y)) return;
//                if (addRandomSeed(startX - x, startY - y)) return;
//                if (addRandomSeed(startX + x, startY - y)) return;
//                if (addRandomSeed(startX - x, startY + y)) return;
//            }
//        }
//    }
//
//    private boolean addRandomSeed(int i, int j) {
//        int realX = i % _width;
//        int realY = j % _height;
//        if (_parcels[realX][realY][0].getStructure() == null) {
//            WorldResource ressource = (WorldResource) putObject("base.res", 0, realX, realY, 10);
//            JobManager.getInstance().addGather(ressource);
//            return true;
//        }
//        return false;
//    }

//    public int	gather(WorldResource resource, int maxValue) {
//        if (resource == null || maxValue == 0) {
//            Log.error("gather: wrong call");
//            return 0;
//        }
//
//        int value = resource.gatherMatter(maxValue);
//        int x = resource.getX();
//        int y = resource.getY();
//
//        if (resource.isDepleted()) {
//            _parcels[x][y][0].setResource(null);
//        }
//
//        MainRenderer.getInstance().invalidate(x, y);
//
//        return value;
//    }

    public void removeItem(ItemModel item) {
        if (item != null && item.getParcel() != null) {
            item.getParcel().setItem(null);
            _game.notify(observer -> observer.onRemoveItem(item));
        }
    }

    public void removeConsumable(ConsumableModel consumable) {
        if (consumable != null && consumable.getParcel() != null) {
            consumable.getParcel().setConsumable(null);
            _consumables.remove(consumable);
            _game.notify(observer -> observer.onRemoveConsumable(consumable));
        }
    }

    public void removeStructure(int x, int y) {
        if (!inMapBounds(x, y)) {
            return;
        }

        StructureModel structure = _parcels[x][y][0].getStructure();
        if (structure != null) {
            _parcels[x][y][0].setStructure(null);
            _game.notify(observer -> observer.onRemoveStructure(structure));
        }
    }

    public void removeStructure(StructureModel structure) {
        if (structure != null && structure.getParcel() != null) {
            structure.getParcel().setStructure(null);
            _game.notify(observer -> observer.onRemoveStructure(structure));
        }
    }

    public ConsumableModel putConsumable(ItemInfo itemInfo, int quantity, int x, int y, int z) {
        if (!inMapBounds(x, y)) {
            return null;
        }

        ParcelModel area = getNearestFreeArea(itemInfo, x, y, quantity);
        if (area == null) {
            return null;
        }

        // Put consumable on free area
        if (area.getConsumable() != null) {
            area.getConsumable().addQuantity(quantity);
        } else {
            area.setConsumable(ItemFactory.createConsumable(area, itemInfo, quantity));
            _consumables.add(area.getConsumable());
        }

        _game.notify(observer -> observer.onAddConsumable(area.getConsumable()));

        return area.getConsumable();
    }

    public ConsumableModel putConsumable(ConsumableModel consumable, int x, int y) {
        if (!inMapBounds(x, y)) {
            return null;
        }

        ParcelModel parcel = getNearestFreeArea(consumable.getInfo(), x, y, consumable.getQuantity());
        if (parcel == null) {
            return null;
        }

        // Put consumable on free area
        if (parcel.getConsumable() != null) {
            parcel.getConsumable().addQuantity(consumable.getQuantity());
        } else {
            parcel.setConsumable(consumable);
        }

        _game.notify(observer -> observer.onAddConsumable(consumable));

        return consumable;
    }

    /**
     * Search for area free to receive a ConsumableItem
     *
     * @param itemInfo
     * @param x
     * @param y
     * @return nearest free area
     */
    private ParcelModel getNearestFreeArea(ItemInfo itemInfo, int x, int y, int quantity) {
        if (itemInfo.isConsumable) {
            for (int i = 0; i < 10; i++) {
                if (areaFreeForConsumable(x + 0, y + 0, itemInfo, quantity)) return _parcels[x + 0][y + 0][0];
                if (areaFreeForConsumable(x + i, y + 0, itemInfo, quantity)) return _parcels[x + i][y + 0][0];
                if (areaFreeForConsumable(x + 0, y + i, itemInfo, quantity)) return _parcels[x + 0][y + i][0];
                if (areaFreeForConsumable(x - i, y + 0, itemInfo, quantity)) return _parcels[x - i][y + 0][0];
                if (areaFreeForConsumable(x + 0, y - i, itemInfo, quantity)) return _parcels[x + 0][y - i][0];
                if (areaFreeForConsumable(x + i, y + i, itemInfo, quantity)) return _parcels[x + i][y + i][0];
                if (areaFreeForConsumable(x - 0, y - i, itemInfo, quantity)) return _parcels[x - 0][y - i][0];
                if (areaFreeForConsumable(x + i, y - i, itemInfo, quantity)) return _parcels[x + i][y - i][0];
                if (areaFreeForConsumable(x - i, y + i, itemInfo, quantity)) return _parcels[x - i][y + i][0];
            }
        }
        return null;
    }

    /**
     * Check if current area is free for consumable
     *
     * @param x
     * @param y
     * @param info
     * @return
     */
    private boolean areaFreeForConsumable(int x, int y, ItemInfo info, int quantity) {
        if (inMapBounds(x, y)) {
            ParcelModel area = _parcels[x][y][0];
            if (area.getStructure() == null || area.getStructure().isFloor()) {
                return area.getItem() == null && (area.getConsumable() == null || (area.getConsumable().getInfo() == info && area.getConsumable().getQuantity() + quantity <= GameData.config.storageMaxQuantity));
            }
        }
        return false;
    }

    /**
     * Check if position is in map bounds
     *
     * @param x
     * @param y
     * @return true if position in bounds
     */
    private boolean inMapBounds(int x, int y) {
        return !(x < 0 || y < 0 || x >= _width || y >= _height);
    }

    public MapObjectModel putObject(ItemInfo itemInfo, int x, int y, int z, int value) {
        if (!inMapBounds(x, y)) {
            return null;
        }

        if (itemInfo.isConsumable) {
            return putConsumable(itemInfo, value, x, y, z);
        }

        if (itemInfo.isStructure) {
            return putStructure(itemInfo, value, x, y, z);
        }

        if (itemInfo.isUserItem) {
            return putItem(itemInfo, value, x, y, z);
        }

        if (itemInfo.isResource) {
            return putResource(itemInfo, value, x, y, z);
        }

        return null;
    }

    private ResourceModel putResource(ItemInfo itemInfo, int matterSupply, int x, int y, int z) {
        if (!inMapBounds(x, y)) {
            return null;
        }

        // Put item on floor
        ResourceModel resource = (ResourceModel) ItemFactory.create(this, _parcels[x][y][z], itemInfo, matterSupply);
        for (int i = 0; i < resource.getWidth(); i++) {
            for (int j = 0; j < resource.getHeight(); j++) {
                _parcels[x][y][z].setResource(resource);
            }
        }
        _game.notify(observer -> observer.onAddResource(resource));

        return resource;
    }

    private ItemModel putItem(ItemInfo itemInfo, int progress, int x, int y, int z) {
        if (!inMapBounds(x, y)) {
            return null;
        }

        // Put item on floor
        ItemModel item = (ItemModel) ItemFactory.create(this, _parcels[x][y][z], itemInfo, progress);
        for (int i = 0; i < item.getWidth(); i++) {
            for (int j = 0; j < item.getHeight(); j++) {
                _parcels[x][y][z].setItem(item);
            }
        }
        _game.notify(observer -> observer.onAddItem(item));

        return item;
    }

    private StructureModel putStructure(ItemInfo itemInfo, int matterSupply, int x, int y, int z) {
        if (!inMapBounds(x, y)) {
            return null;
        }

        // TODO
        if (_parcels[x][y][z].getStructure() == null || _parcels[x][y][z].getStructure().isGround()) {
            StructureModel structure = (StructureModel) ItemFactory.create(this, _parcels[x][y][z], itemInfo, matterSupply);
            if (structure != null) {
                structure.setPosition(x, y);
                _game.notify(observer -> observer.onAddStructure(structure));
            }
            return structure;
        }

        return null;
    }

    public ItemModel getItem(int x, int y) {
        return (x < 0 || x >= _width || y < 0 || y >= _height) || _parcels[x][y] == null ? null : _parcels[x][y][0].getItem();
    }

    public ResourceModel getResource(int x, int y, int z) {
        return (z < 0 || z >= NB_FLOOR || x < 0 || x >= _width || y < 0 || y >= _height) ? null : _parcels[x][y][z].getResource();
    }

    public StructureModel getStructure(int x, int y, int z) {
        return (z < 0 || z >= NB_FLOOR || x < 0 || x >= _width || y < 0 || y >= _height) ? null : _parcels[x][y][z].getStructure();
    }

    public ConsumableModel getConsumable(int x, int y) {
        return (x < 0 || x >= _width || y < 0 || y >= _height) || _parcels[x][y] == null ? null : _parcels[x][y][0].getConsumable();
    }

    public StructureModel getStructure(int x, int y) {
        return getStructure(x, y, _floor);
    }

    public ResourceModel getResource(int x, int y) {
        return getResource(x, y, _floor);
    }

    public ParcelModel getParcel(int x, int y) {
        return (x < 0 || x >= _width || y < 0 || y >= _height) ? null : _parcels[x][y][0];
    }

    public int getWidth() {
        return _width;
    }

    public int getHeight() {
        return _height;
    }

//
//    @Override
//    public int getWidthInTiles() {
//        return _width;
//    }
//
//    @Override
//    public int getHeightInTiles() {
//        return _height;
//    }
//
//    @Override
//    public void pathFinderVisited(int x, int y) {
////		DebugPos pos = new DebugPos();
////		pos.x = x;
////		pos.y = y;
////		_debugPath.add(pos);
//        //Log.info("visite: " + x + ", " + y);
//    }

    public void replaceItem(ItemInfo info, int x, int y, int matterSupply) {
        replaceItem(info, x, y, _floor, matterSupply);
    }

    private void replaceItem(ItemInfo info, int x, int y, int z, int matterSupply) {
        if (info.isResource) {
            _parcels[x][y][z].setResource(null);
        } else if (info.isStructure) {
            _parcels[x][y][z].setStructure(null);
        } else {
            _parcels[x][y][z].setItem(null);
        }
        putObject(info, x, y, z, matterSupply);
    }
//
//    public int getFloor() {
//        return _floor;
//    }
//
//    public void upFloor() {
//        setFloor(_floor + 1);
//    }
//
//    private void setFloor(int floor) {
//        if (floor < 0 || floor >= NB_FLOOR) {
//            return;
//        }
//
//        _floor = floor;
//        MainRenderer.getInstance().invalidate();
//    }
//
//    public void downFloor() {
//        setFloor(_floor - 1);
//    }

//    // TODO: heavy
//    public ItemModel getRandomToy(int posX, int posY) {
//        List<ItemModel> items = new ArrayList<ItemModel>();
//        for (int offsetX = 0; offsetX < 14; offsetX++) {
//            for (int offsetY = 0; offsetY < 14; offsetY++) {
//                ItemModel item = null;
//                item = getItem(posX + offsetX, posY + offsetY);
//                if (item != null && item.isToy() && item.hasFreeSlot()) { items.add(item); }
//                item = getItem(posX - offsetX, posY - offsetY);
//                if (item != null && item.isToy() && item.hasFreeSlot()) { items.add(item); }
//                item = getItem(posX + offsetX, posY - offsetY);
//                if (item != null && item.isToy() && item.hasFreeSlot()) { items.add(item); }
//                item = getItem(posX - offsetX, posY + offsetY);
//                if (item != null && item.isToy() && item.hasFreeSlot()) { items.add(item); }
//            }
//        }
//        if (items.size() > 0) {
//            return items.getRoom((int)(Math.random() * items.size()));
//        }
//        return null;
//    }

    public void removeResource(ResourceModel resource) {
        if (resource == null) {
            return;
        }

        int x = resource.getX();
        int y = resource.getY();

        if (_parcels[x][y][0].getResource() != resource) {
            return;
        }

        _parcels[resource.getX()][resource.getY()][0].setResource(null);
        _game.notify(observer -> observer.onRemoveResource(resource));
    }

    public ParcelModel getParcel(int z, int x, int y) {
        if (inMapBounds(x, y)) {
            if (_parcels[x][y][z] == null) {
                _parcels[x][y][z] = new ParcelModel(x, y, z);
                _parcels[x][y][z].setIndex(x * _width + y);
                throw new RuntimeException("todo");
            }
            return _parcels[x][y][z];
        }
        return null;
    }
//
//    @Override
//    public boolean blocked(PathFindingContext context, int x, int y) {
//        MyMover mover = (MyMover)context.getMover();
//        if (mover.targetX == x && mover.targetY == y) {
//            return false;
//        }
//
//        if (!inMapBounds(x, y)) {
//            return true;
//        }
//
//        // Check structure (wall, closed door)
//        if (_parcels[x][y][0].getStructure() != null && _parcels[x][y][0].getStructure().isSolid()) {
//            return true;
//        }
//
//        // Check structure (wall, closed door)
//        if (_parcels[x][y][0].getResource() != null && _parcels[x][y][0].getResource().isSolid()) {
//            return true;
//        }
//
//        return false;
//    }
//
//    @Override
//    public float getCost(PathFindingContext context, int tx, int ty) {
//        MyMover mover = (MyMover)context.getMover();
//
////        float cost = context.getSourceX() != tx && context.getSourceY() != ty ? 1.5f : 1f;
////
////        ParcelModel area = _parcels[tx][ty][0];
////        if (area != null && area.getItem() != null) {
////            cost *= 4;
////        }
////
//        return 1;
//    }

    public ItemModel takeItem(ItemModel item) {
        if (item != null) {
            ParcelModel area = getParcel(item.getX(), item.getY());
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

    private ItemModel takeItem(ItemModel item, ParcelModel area) {
        if (area != null && item != null) {
            area.setItem(null);
            MainRenderer.getInstance().invalidate(area.getX(), area.getY());
            return item;
        }
        Log.error("Area or item is null");
        return null;
    }

    public ItemModel takeItem(int x, int y) {
        ItemModel item = getItem(x, y);
        ParcelModel area = getParcel(x, y);
        return takeItem(item, area);
    }

    public void remove(MapObjectModel object) {
        if (object == null) {
            Log.error("Cannot remove null object");
            return;
        }

        if (object.isStructure()) {
            removeStructure((StructureModel) object);
            return;
        }

        if (object.isResource()) {
            removeResource((ResourceModel) object);
            return;
        }

        if (object.isUserItem()) {
            removeItem((ItemModel) object);
            return;
        }

        if (object.isConsumable()) {
            removeConsumable((ConsumableModel) object);
            return;
        }
    }

    // TODO
    public ItemModel getItemById(int itemId) {
        for (int x = 0; x < _width; x++) {
            for (int y = 0; y < _height; y++) {
                if (_parcels[x][y][0].getItem() != null && _parcels[x][y][0].getItem().getId() == itemId) {
                    return _parcels[x][y][0].getItem();
                }
            }
        }
        return null;
    }

    public int getTemperature() {
        return _temperature + _temperatureOffset;
    }

    public void setTemperatureOffset(int temperatureOffset) {
        Log.info("Set world temperature offset: " + temperatureOffset);
        _temperatureOffset = temperatureOffset;
    }

    @Override
    protected void onUpdate(int tick) {

    }

    public Collection<ConsumableModel> getConsumables() {
        return _consumables;
    }

    public int getEnvironmentValue(int startX, int startY, int distance) {
        int fromX = startX - distance;
        int fromY = startY - distance;
        int toX = startX + distance;
        int toY = startY + distance;
        int value = 0;
        for (int x = fromX; x < toX; x++) {
            for (int y = fromY; y < toY; y++) {
                if (inMapBounds(x, y)) {
                    if (_parcels[x][y][0].hasSnow()) {
                        value += 1;
                    }
                    if (_parcels[x][y][0].hasBlood()) {
                        value += -5;
                    }
                    if (_parcels[x][y][0].hasDirt()) {
                        value += -5;
                    }
                    if (_parcels[x][y][0].hasRubble()) {
                        value += -5;
                    }
                    if (_parcels[x][y][0].getItem() != null) {
                        value += _parcels[x][y][0].getItem().getValue();
                    }
                }
            }
        }
        return value;
    }

    @Override
    public int getNodeCount() {
        return _width * _height;
    }

    @Override
    public Array<Connection<ParcelModel>> getConnections(ParcelModel parcel) {
        return parcel.getConnections();
    }

    private void createConnection(Array<Connection<ParcelModel>> array, ParcelModel parcel, int x, int y) {
        if (inMapBounds(x, y) && !parcel.isBlocked()) {
            array.add(new DefaultConnection(parcel, _parcels[x][y][0]));
        }
    }

    @Override
    public void onAddStructure(StructureModel structure) {
        createConnection(structure.getParcel());
    }

    @Override
    public void onAddResource(ResourceModel resource) {
        createConnection(resource.getParcel());
    }

    @Override
    public void onRemoveStructure(StructureModel structure) {
        createConnection(structure.getParcel());
    }

    @Override
    public void onRemoveResource(ResourceModel resource) {
        createConnection(resource.getParcel());
    }

    public ParcelModel getNearestFreeSpace(int x, int y, boolean isExterior) {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                if (isFreeSpace(x + i, y, isExterior)) return _parcels[x + i][y][0];
                if (isFreeSpace(x - i, y, isExterior)) return _parcels[x - i][y][0];
                if (isFreeSpace(x, y + j, isExterior)) return _parcels[x][y + j][0];
                if (isFreeSpace(x, y - j, isExterior)) return _parcels[x][y - j][0];
            }
        }
        return null;
    }

    private boolean isFreeSpace(int x, int y, boolean isExterior) {
        if (!inMapBounds(x, y)) {
            return false;
        }
        if (isExterior && _parcels[x][y][0].getRoom() != null && !_parcels[x][y][0].getRoom().isExterior()) {
            return false;
        }
        if (_parcels[x][y][0].getStructure() != null && _parcels[x][y][0].getStructure().isSolid()) {
            return false;
        }
        if (_parcels[x][y][0].getResource() != null) {
            return false;
        }
        if (_parcels[x][y][0].getItem() != null) {
            return false;
        }
        if (_parcels[x][y][0].getConsumable() != null) {
            return false;
        }
        return true;
    }
}
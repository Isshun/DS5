package org.smallbox.faraway.game.manager;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import org.smallbox.faraway.data.factory.ItemFactory;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.WorldFactory;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.item.*;
import org.smallbox.faraway.util.Log;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WorldManager extends BaseManager implements IndexedGraph<ParcelModel> {
    private static final int    NB_FLOOR = 10;

    private boolean                         _callWorldFactory;
    private ParcelModel[][][]               _parcels;
    private int                             _width;
    private int                             _height;
    private int                             _floor;
    private int                             _temperature;
    private int                             _temperatureOffset;
    private Game                            _game;
    private Set<ConsumableModel>            _consumables = new HashSet<>();
    private BlockingQueue<ResourceModel>    _resources = new LinkedBlockingQueue<>();
    private Set<ItemModel>                  _items = new HashSet<>();
    private List<ParcelModel>               _parcelList;
    private int                             _light;

    public WorldManager(boolean callWorldFactory) {
        _callWorldFactory = callWorldFactory;
    }

    public ParcelModel[][][] getParcels() {
        return _parcels;
    }

    @Override
    public void onCreate() {
        _game = Game.getInstance();
        _game.setWorldManager(this);
        _width = _game.getWidth();
        _height = _game.getHeight();
        _temperature = _game.getRegion().getInfo().temperature;

        List<ParcelModel> parcelList = new ArrayList<>();
        _parcels = new ParcelModel[_width][_height][NB_FLOOR];
        for (int x = 0; x < _width; x++) {
            _parcels[x] = new ParcelModel[_height][NB_FLOOR];
            for (int y = 0; y < _height; y++) {
                _parcels[x][y] = new ParcelModel[NB_FLOOR];
                for (int f = 0; f < NB_FLOOR; f++) {
                    _parcels[x][y][f] = new ParcelModel(x, y, f);
                    _parcels[x][y][f].setIndex(x * _width + y);
                    parcelList.add(_parcels[x][y][f]);
                }
            }
        }

        for (int x = 0; x < _width; x++) {
            for (int y = 0; y < _height; y++) {
                _parcels[x][y][0]._neighbors = new ParcelModel[4];
                _parcels[x][y][0]._neighbors[0] = getParcel(x+1, y);
                _parcels[x][y][0]._neighbors[1] = getParcel(x-1, y);
                _parcels[x][y][0]._neighbors[2] = getParcel(x, y+1);
                _parcels[x][y][0]._neighbors[3] = getParcel(x, y-1);
            }
        }

        _parcelList = parcelList;

        for (int x = 0; x < _width; x++) {
            for (int y = 0; y < _height; y++) {
                for (int f = 0; f < NB_FLOOR; f++) {
                    createConnection(_parcels[x][y][f]);
                }
            }
        }

        if (_callWorldFactory) {
            new WorldFactory().create(this, _game.getRegion().getInfo());
        }
    }

    private void createConnection(ParcelModel parcel) {
        Array<Connection<ParcelModel>> connections = new Array<>();
        createConnection(connections, parcel, parcel.getX() + 1, parcel.getY());
        createConnection(connections, parcel, parcel.getX() - 1, parcel.getY());
        createConnection(connections, parcel, parcel.getX(), parcel.getY() + 1);
        createConnection(connections, parcel, parcel.getX(), parcel.getY() - 1);

        // Corners
        createConnection(connections, parcel, parcel.getX() + 1, parcel.getY() + 1);
        createConnection(connections, parcel, parcel.getX() + 1, parcel.getY() - 1);
        createConnection(connections, parcel, parcel.getX() - 1, parcel.getY() + 1);
        createConnection(connections, parcel, parcel.getX() - 1, parcel.getY() - 1);
        parcel.setConnections(connections);
    }

    public MapObjectModel putObject(String name, int x, int y, int z, int data) {
        return putObject(GameData.getData().getItemInfo(name), x, y, z, data);
    }

    public void removeItem(ItemModel item) {
        if (item != null && item.getParcel() != null) {
            item.getParcel().setItem(null);
            _items.remove(item);
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
            for (int d = 0; d < 8; d++) {
                for (int i = -d; i < d; i++) {
                    if (areaFreeForConsumable(x + i, y + d, itemInfo, quantity)) return _parcels[x + i][y + d][0];
                    if (areaFreeForConsumable(x + i, y - d, itemInfo, quantity)) return _parcels[x + i][y - d][0];
                    if (areaFreeForConsumable(x + d, y + i, itemInfo, quantity)) return _parcels[x + d][y + i][0];
                    if (areaFreeForConsumable(x - d, y + i, itemInfo, quantity)) return _parcels[x - d][y + i][0];
                }
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
                return area.getItem() == null && (area.getConsumable() == null || (area.getConsumable().getInfo() == info && area.getConsumable().getQuantity() + quantity <= Math.max(GameData.config.storageMaxQuantity, area.getConsumable().getInfo().stack)));
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

    public MapObjectModel putObject(ItemInfo itemInfo, int x, int y, int z, int data) {
        if (!inMapBounds(x, y)) {
            return null;
        }

        if (itemInfo.isConsumable) {
            return putConsumable(itemInfo, data, x, y, z);
        }

        if (itemInfo.isStructure) {
            return putStructure(itemInfo, data, x, y, z);
        }

        if (itemInfo.isUserItem) {
            return putItem(itemInfo, data, x, y, z);
        }

        if (itemInfo.isResource) {
            return putResource(itemInfo, data, x, y, z);
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
        _resources.add(resource);
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
        _items.add(item);
        _game.notify(observer -> observer.onAddItem(item));

        return item;
    }

    private StructureModel putStructure(ItemInfo itemInfo, int matterSupply, int x, int y, int z) {
        if (!inMapBounds(x, y)) {
            return null;
        }

        // TODO
        if (_parcels[x][y][z].getStructure() == null || _parcels[x][y][z].getStructure().isFloor()) {
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

    public void removeResource(ResourceModel resource) {
        if (resource == null) {
            return;
        }

        int x = resource.getX();
        int y = resource.getY();

        if (_parcels[x][y][0].getResource() != resource) {
            return;
        }

        _resources.remove(resource);
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

    private ItemModel takeItem(ItemModel item, ParcelModel area) {
        if (area != null && item != null) {
            area.setItem(null);
            Game.getInstance().notify(observer -> observer.onRefreshItem(item));
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
            array.add(new Connection() {
                @Override
                public float getCost() {
                    if (_parcels[x][y][0].getItem() != null) {
                        return 10;
                    }
                    if (_parcels[x][y][0].getResource() != null) {
                        return 5;
                    }

                    return parcel.getX() == x || parcel.getY() == y ? 1 : 3;
                }

                @Override
                public Object getFromNode() {
                    return parcel;
                }

                @Override
                public Object getToNode() {
                    return _parcels[x][y][0];
                }
            });
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

    public ParcelModel getNearestFreeSpace(int x, int y, boolean acceptInterior, boolean acceptExterior) {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < i; j++) {
                // Top
                if (isFreeSpace(x + j, y + i, acceptInterior, acceptExterior)) return _parcels[x + j][y + i][0];
                if (isFreeSpace(x - j, y + i, acceptInterior, acceptExterior)) return _parcels[x - j][y + i][0];

                // Bottom
                if (isFreeSpace(x + j, y - i, acceptInterior, acceptExterior)) return _parcels[x + j][y - i][0];
                if (isFreeSpace(x - j, y - i, acceptInterior, acceptExterior)) return _parcels[x - j][y - i][0];

                // Right
                if (isFreeSpace(x + i, y + j, acceptInterior, acceptExterior)) return _parcels[x + i][y + j][0];
                if (isFreeSpace(x + i, y - j, acceptInterior, acceptExterior)) return _parcels[x + i][y + j][0];

                // Left
                if (isFreeSpace(x - i, y + j, acceptInterior, acceptExterior)) return _parcels[x - i][y + j][0];
                if (isFreeSpace(x - i, y - j, acceptInterior, acceptExterior)) return _parcels[x - i][y - j][0];
            }
        }
        return null;
    }

    private boolean isFreeSpace(int x, int y, boolean acceptInterior, boolean acceptExterior) {
        if (!inMapBounds(x, y)) {
            return false;
        }
        if (!acceptInterior && _parcels[x][y][0].getRoom() != null && !_parcels[x][y][0].getRoom().isExterior()) {
            return false;
        }
        if (!acceptExterior && (_parcels[x][y][0].getRoom() == null || _parcels[x][y][0].getRoom().isExterior())) {
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

    public ParcelModel getRandomFreeSpace(boolean acceptInterior, boolean acceptExterior) {
        int startX = (int) (Math.random() * _width);
        int startY = (int) (Math.random() * _height);
        for (int i = 0; i < _width; i++) {
            for (int j = 0; j < _height; j++) {
                if (isFreeSpace((startX + i) % _width, (startY + j) % _height, acceptInterior, acceptExterior)) {
                    return _parcels[(startX + i) % _width][(startY + j) % _height][0];
                }
            }
        }
        return null;
    }

    public List<ParcelModel> getParcelList() {
        return _parcelList;
    }

    public boolean isBlocked(int x, int y) {
        ParcelModel parcel = getParcel(x, y);
        return parcel != null && parcel.isBlocked();
    }

    public Set<ItemModel> getItems() {
        return _items;
    }

    public Collection<ResourceModel> getResources() {
        return _resources;
    }

    public void setLight(int light) {
        _light = light;
    }

    public int getLight() {
        return _light;
    }
}
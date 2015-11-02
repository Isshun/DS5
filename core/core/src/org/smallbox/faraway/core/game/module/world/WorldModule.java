package org.smallbox.faraway.core.game.module.world;

import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.StructureModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.game.module.world.model.resource.ResourceModel;
import org.smallbox.faraway.core.module.GameModule;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.Constant;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WorldModule extends GameModule {
    private static final int                    NB_FLOOR = 10;

    private ParcelModel[][][]                   _parcels;
    private int                                 _width;
    private int                                 _height;
    private Game                                _game;
    private Set<ConsumableModel>                _consumables = new HashSet<>();
    private BlockingQueue<ResourceModel>        _resources = new LinkedBlockingQueue<>();
    private Set<ItemModel>                      _items = new HashSet<>();
    private Set<StructureModel>                 _structures = new HashSet<>();
    private List<ParcelModel>                   _parcelList;
    private double                              _light;

    public ParcelModel[][][]                    getParcels() { return _parcels; }
    public List<ParcelModel>                    getParcelList() { return _parcelList; }
    public Collection<ItemModel>                getItems() { return _items; }
    public Collection<ConsumableModel>          getConsumables() { return _consumables; }
    public Collection<StructureModel>           getStructures() { return _structures; }
    public Collection<ResourceModel>            getResources() { return _resources; }
    public double                               getLight() { return _light; }
    public ParcelModel                          getParcel(int x, int y) { return (x < 0 || x >= _width || y < 0 || y >= _height) ? null : _parcels[x][y][0]; }

    public void                                 setLight(double light) { _light = light; }

    @Override
    public void onLoaded() {
        ModuleHelper.setWorldModule(this);

        _game = Game.getInstance();
        _width = _game.getInfo().worldWidth;
        _height = _game.getInfo().worldHeight;

        List<ParcelModel> parcelList = new ArrayList<>();
        _parcels = new ParcelModel[_width][_height][NB_FLOOR];
        for (int x = 0; x < _width; x++) {
            _parcels[x] = new ParcelModel[_height][NB_FLOOR];
            for (int y = 0; y < _height; y++) {
                _parcels[x][y] = new ParcelModel[NB_FLOOR];
                for (int f = 0; f < NB_FLOOR; f++) {
                    ParcelModel parcel = new ParcelModel(x, y, f);
                    _parcels[x][y][f] = parcel;
                    _parcels[x][y][f].setIndex(x * _width + y);
                    parcelList.add(_parcels[x][y][f]);
                }
            }
        }

        for (int x = 0; x < _width; x++) {
            for (int y = 0; y < _height; y++) {
                _parcels[x][y][0]._neighbors = new ParcelModel[8];
                _parcels[x][y][0]._neighbors[0] = getParcel(x + 1, y);
                _parcels[x][y][0]._neighbors[1] = getParcel(x - 1, y);
                _parcels[x][y][0]._neighbors[2] = getParcel(x, y + 1);
                _parcels[x][y][0]._neighbors[3] = getParcel(x, y - 1);
                _parcels[x][y][0]._neighbors[4] = getParcel(x + 1, y + 1);
                _parcels[x][y][0]._neighbors[5] = getParcel(x + 1, y - 1);
                _parcels[x][y][0]._neighbors[6] = getParcel(x - 1, y + 1);
                _parcels[x][y][0]._neighbors[7] = getParcel(x - 1, y - 1);
            }
        }

        WorldHelper.init(_parcels);

        _parcelList = parcelList;
    }

    // Used only by serializers
    public MapObjectModel putObject(String name, int x, int y, int z, int data, boolean complete) {
        return putObject(WorldHelper.getParcel(x, y), GameData.getData().getItemInfo(name), data, complete);
    }

    public void removeItem(ItemModel item) {
        if (item != null && item.getParcel() != null) {
            moveItemToParcel(item.getParcel(), item);
            _items.remove(item);
            _game.notify(observer -> observer.onRemoveItem(item));
        }
    }

    public void removeConsumable(ConsumableModel consumable) {
        if (consumable != null && consumable.getParcel() != null) {
            moveConsumableToParcel(consumable.getParcel(), null);
            _consumables.remove(consumable);
            _game.notify(observer -> observer.onRemoveConsumable(consumable));
        }
    }

    public void removeStructure(int x, int y) {
        if (!WorldHelper.inMapBounds(x, y)) {
            return;
        }

        StructureModel structure = _parcels[x][y][0].getStructure();
        if (structure != null) {
            moveStructureToParcel(_parcels[x][y][0], null);
            _structures.remove(structure);
            _game.notify(observer -> observer.onRemoveStructure(structure));
        }
    }

    public void removeStructure(StructureModel structure) {
        if (structure != null && structure.getParcel() != null) {
            moveStructureToParcel(structure.getParcel(), null);
            _game.notify(observer -> observer.onRemoveStructure(structure));
        }
    }

    public ConsumableModel putConsumable(ParcelModel parcel, ItemInfo itemInfo, int quantity) {
        ConsumableModel consumable = null;
        if (parcel != null && quantity > 0) {
            final ParcelModel finalParcel = WorldHelper.getNearestFreeArea(parcel, itemInfo, quantity);
            if (finalParcel != null) {
                if (finalParcel.getConsumable() != null) {
                    consumable = finalParcel.getConsumable();
                    consumable.addQuantity(quantity);
                } else {
                    consumable = new ConsumableModel(itemInfo);
                    consumable.setQuantity(quantity);
                    moveConsumableToParcel(finalParcel, consumable);
                    _consumables.add(finalParcel.getConsumable());
                }
                _game.notify(observer -> observer.onAddConsumable(finalParcel.getConsumable()));
            }
        }
        return consumable;
    }

    public ConsumableModel putConsumable(ParcelModel parcel, ConsumableModel consumable) {
        if (parcel != null) {
            ParcelModel finalParcel = WorldHelper.getNearestFreeArea(parcel, consumable.getInfo(), consumable.getQuantity());
            if (finalParcel == null) {
                return null;
            }

            // Put consumable on free model
            if (finalParcel.getConsumable() != null) {
                finalParcel.getConsumable().addQuantity(consumable.getQuantity());
            } else {
                moveConsumableToParcel(finalParcel, consumable);
                _consumables.add(finalParcel.getConsumable());
            }

            _game.notify(observer -> observer.onAddConsumable(consumable));

            return consumable;
        }
        return null;
    }

    public MapObjectModel putObject(ParcelModel parcel, ItemInfo itemInfo, int quantity) {
        return putObject(parcel, itemInfo, quantity, false);
    }

    private MapObjectModel putObject(ParcelModel parcel, ItemInfo itemInfo, int data, boolean complete) {
        if (parcel != null) {
            if (itemInfo.isConsumable) {
                return putConsumable(parcel, itemInfo, data);
            }

            if (itemInfo.isStructure) {
                return putStructure(parcel, itemInfo, data, complete);
            }

            if (itemInfo.isUserItem) {
                return putItem(parcel, itemInfo, data);
            }

            if (itemInfo.isResource) {
                return putResource(parcel, itemInfo, data);
            }
        }
        return null;
    }

    public MapObjectModel putObject(String itemName, ParcelModel parcel, int data) {
        return putObject(itemName, parcel.x, parcel.y, parcel.z, data, true);
    }

    private ResourceModel putResource(ParcelModel parcel, ItemInfo itemInfo, int matterSupply) {
        // Put item on floor
        ResourceModel resource = new ResourceModel(itemInfo);
        if (resource.isRock()) {
            resource.getRock().setQuantity(matterSupply);
        }
        for (int i = 0; i < resource.getWidth(); i++) {
            for (int j = 0; j < resource.getHeight(); j++) {
                moveResourceToParcel(parcel, resource);
            }
        }
        _resources.add(resource);
        _game.notify(observer -> observer.onAddResource(resource));

        return resource;
    }

    private ItemModel putItem(ParcelModel parcel, ItemInfo itemInfo, int progress) {
        // Put item on floor
        ItemModel item = new ItemModel(itemInfo, parcel);
        item.addProgress(progress);
        moveItemToParcel(parcel, item);
        if (item.getInfo().receipts != null && item.getInfo().receipts.size() > 0) {
            item.setReceipt(item.getInfo().receipts.get(0));
        }
        _items.add(item);
        _game.notify(observer -> observer.onAddItem(item));

        return item;
    }

    private StructureModel putStructure(ParcelModel parcel, ItemInfo itemInfo, int matterSupply, boolean complete) {
        // TODO
        if (parcel.getStructure() == null || parcel.getStructure().isFloor()) {
            StructureModel structure = new StructureModel(itemInfo);
            structure.addProgress(complete ? itemInfo.cost : 0);
            structure.setComplete(complete);
            if (structure.getInfo().receipts != null && structure.getInfo().receipts.size() > 0) {
                structure.setReceipt(structure.getInfo().receipts.get(0));
            }
            moveStructureToParcel(parcel, structure);
            _structures.add(structure);
            _game.notify(observer -> observer.onAddStructure(structure));
            return structure;
        }

        return null;
    }

    public void replaceItem(ParcelModel parcel, ItemInfo info, int matterSupply) {
        if (info.isResource) {
            moveResourceToParcel(parcel, null);
        } else if (info.isStructure) {
            moveStructureToParcel(parcel, null);
        } else {
            moveItemToParcel(parcel, null);
        }
        putObject(parcel, info, matterSupply, true);
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
        moveResourceToParcel(_parcels[resource.getX()][resource.getY()][0], null);
        _game.notify(observer -> observer.onRemoveResource(resource));
    }

    private ItemModel takeItem(ItemModel item, ParcelModel parcel) {
        if (parcel != null && item != null) {
            moveItemToParcel(parcel, null);
            Game.getInstance().notify(observer -> observer.onRefreshItem(item));
            return item;
        }
        printError("Area or item is null");
        return null;
    }

    public ItemModel takeItem(int x, int y) {
        ParcelModel area = getParcel(x, y);
        if (area != null) {
            return takeItem(area.getItem(), area);
        }
        return null;
    }

    public void remove(MapObjectModel object) {
        if (object == null) {
            printError("Cannot remove null object");
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

    @Override
    protected void onUpdate(int tick) {
        _consumables.forEach(consumable -> consumable.fixPosition());
    }

    public int getEnvironmentValue(int startX, int startY, int distance) {
        int fromX = startX - distance;
        int fromY = startY - distance;
        int toX = startX + distance;
        int toY = startY + distance;
        int value = 0;
        for (int x = fromX; x < toX; x++) {
            for (int y = fromY; y < toY; y++) {
                if (WorldHelper.inMapBounds(x, y)) {
                    value += _parcels[x][y][0].getEnvironmentScore();
                }
            }
        }
        return value;
    }

    private void moveItemToParcel(ParcelModel parcel, ItemModel item) {
        parcel.setItem(item);
        if (item != null) {
            item.setParcel(parcel);
            item.setPosition(parcel.x, parcel.y);
            for (int i = 0; i < item.getWidth(); i++) {
                for (int j = 0; j < item.getHeight(); j++) {
                    if (WorldHelper.inMapBounds(parcel.x + i, parcel.y + j)) {
                        _parcels[parcel.x + i][parcel.y + j][0].setItem(item);
                    }
                }
            }
        }
    }

    private void moveStructureToParcel(ParcelModel parcel, StructureModel structure) {
        parcel.setStructure(structure);
        if (structure != null) {
            structure.setParcel(parcel);
            structure.setPosition(parcel.x, parcel.y);
        }
    }

    private void moveConsumableToParcel(ParcelModel parcel, ConsumableModel consumable) {
        parcel.setConsumable(consumable);
        if (consumable != null && consumable.getParcel() != null) {
            consumable.getParcel().setConsumable(null);
        }
        if (consumable != null) {
            consumable.setParcel(parcel);
            consumable.setPosition(parcel.x, parcel.y);

            if (parcel.getConsumable() == null) {
                parcel.setConsumable(consumable);
            }
        }
    }

    private void moveResourceToParcel(ParcelModel parcel, ResourceModel resource) {
        parcel.setResource(resource);
        if (resource != null) {
            resource.setParcel(parcel);
            resource.setPosition(parcel.x, parcel.y);
        }
    }

    @Override
    public int getModulePriority() {
        return Constant.MODULE_WORLD_PRIORITY;
    }

    @Override
    public boolean isModuleMandatory() {
        return true;
    }

    public int getWidth() { return _width; }
    public int getHeight() { return _height; }
}
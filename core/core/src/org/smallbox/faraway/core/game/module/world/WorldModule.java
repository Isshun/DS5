package org.smallbox.faraway.core.game.module.world;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.engine.renderer.GetParcelListener;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.world.model.*;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.game.module.world.model.resource.PlantModel;
import org.smallbox.faraway.core.module.GameModule;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.module.java.ModuleManager;
import org.smallbox.faraway.core.util.Constant;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WorldModule extends GameModule {
    private static final int CACHE_SIZE = 50000;
    private ParcelModel[][][]                   _parcels;
    private Map<Integer, ParcelModel>           _parcelsDo;
    private int                                 _width;
    private int                                 _height;
    private int                                 _floors;
    private Game                                _game;
    private Set<NetworkObjectModel>             _networks = new HashSet<>();
    private Set<ConsumableModel>                _consumables = new HashSet<>();
    private BlockingQueue<PlantModel>        _resources = new LinkedBlockingQueue<>();
    private Set<ItemModel>                      _items = new HashSet<>();
    private Set<ItemModel>                      _factories = new HashSet<>();
    private Set<StructureModel>                 _structures = new HashSet<>();
    private List<ParcelModel>                   _parcelList;
    private Map<Integer, List<ParcelModel>>     _parcelListFloor;
    private double                              _light;
    private int                                 _floor = 9;

    private Map<Integer, ParcelModel>           _parcelCache = new HashMap<>();
    private List<ParcelModel>                   _parcelCacheQueue = new ArrayList<>(CACHE_SIZE);
    private List<ChunkCacheModel>               _parcelChunkCacheQueue = new ArrayList<>(50);
    private Map<Integer, ChunkCacheModel>       _parcelChunkCache = new HashMap<>();

    public ParcelModel[][][]                    getParcels() { return _parcels; }

    public void getParcels(int fromX, int toX, int fromY, int toY, int fromZ, int toZ, GetParcelListener getParcelListener) {
        assert getParcelListener != null;

        List<ParcelModel> parcels = new ArrayList<>();
        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                for (int z = fromZ; z <= toZ; z++) {
                    if (x >= 0 && x < _width && y >= 0 && y < _height && z >= 0 && z < _floors) {
                        parcels.add(_parcels[x][y][z]);
                    }
                }
            }
        }
        getParcelListener.onGetParcel(parcels);
    }

//    public void                                 getParcelsDo(int fromX, int toX, int fromY, int toY, int fromZ, int toZ, GetParcelListener getParcelListener) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                List<ParcelModel> parcels = new ArrayList<>();
//                ItemInfo resourceInfo = Data.getData().getItemInfo("base.granite");
//
//                int index = fromX * _width * _height + fromY * _height + fromZ;
//                if (_parcelChunkCache.containsKey(index)) {
//                    for (int x = fromX; x <= toX; x++) {
//                        for (int y = fromY; y <= toY; y++) {
//                            for (int z = fromZ; z <= toZ; z++) {
//                                int i = x * _width * _height + y * _height + z;
//
//                                ParcelModel parcel = _parcelCache.get(i);
//                                if (parcel != null) {
//                                    parcels.add(parcel);
//                                }
//                            }
//                        }
//                    }
//
//                    getParcelListener.onGetParcel(parcels);
//                }
//
//                else {
//                    SQLHelper.getInstance().post((db) -> {
//                        if (_parcelChunkCacheQueue.size() == 50) {
//                            ChunkCacheModel chunkCache = _parcelChunkCacheQueue.remove(0);
//                            for (int x2 = chunkCache.fromX; x2 <= chunkCache.toX; x2++) {
//                                for (int y2 = chunkCache.fromY; y2 <= chunkCache.toY; y2++) {
//                                    for (int z2 = chunkCache.fromZ; z2 <= chunkCache.toZ; z2++) {
//                                        int i2 = x2 * _width * _height + y2 * _height + z2;
//                                        ParcelModel p2 = _parcelCache.get(i2);
//                                        _parcelCache.remove(i2);
//                                        _parcelCacheQueue.remove(p2);
//                                    }
//                                }
//                            }
//                            _parcelChunkCache.remove(chunkCache._index);
//                        }
//
//                        ChunkCacheModel chunkCache = new ChunkCacheModel(index, fromX, toX, fromY, toY, fromZ, toZ);
//                        _parcelChunkCacheQueue.add(chunkCache);
//                        _parcelChunkCache.put(index, chunkCache);
//
//                        try {
//                            SQLiteStatement st = db.prepare("SELECT x, y, z, ground FROM parcel WHERE x >= ? AND x <= ? AND y >= ? AND y <= ? AND z >= ? AND z <= ?");
////            SQLiteStatement st = db.prepare("SELECT x, y, z, ground FROM parcel WHERE x = ? AND y = ? AND z = ?");
//
//                            try {
////                                st.bind(1, x).bind(2, y).bind(3, z);
//
//                                st.bind(1, fromX);
//                                st.bind(2, toX);
//                                st.bind(3, fromY);
//                                st.bind(4, toY);
//                                st.bind(5, fromZ);
//                                st.bind(6, toZ);
//
//                                while (st.step()) {
//                                    int x = st.columnInt(0);
//                                    int y = st.columnInt(1);
//                                    int z = st.columnInt(2);
//                                    int ground = st.columnInt(3);
//                                    ParcelModel parcel = new ParcelModel(x * _width * _height + y * _height + z, null, x, y, z);
//                                    if (ground == 1) {
//                                        ResourceModel resource = new ResourceModel(resourceInfo);
//                                        resource.setParcel(parcel);
//                                        parcel.setResource(resource);
//                                    }
//                                    parcels.add(parcel);
//
//                                    _parcelCache.put(parcel.getIndex(), parcel);
//                                }
//
//                                st.reset(false);
//                            } finally {
//                                st.dispose();
//                            }
//
//                        } catch (SQLiteException e) {
//                            e.printStackTrace();
//                        }
//
//                        getParcelListener.onGetParcel(parcels);
//                    });
//                }
//            }
//        }).start();
//
////        System.out.println("get parcels time: " + (System.currentTimeMillis() - time));
////
////        return parcels;
//    }

    public void setParcels(ParcelModel[][][] parcels, List<ParcelModel> parcelList) {
        _parcels = parcels;
        _parcelList = parcelList;
        _parcelListFloor = new HashMap<>();

    }

    //    public List<ParcelModel>                    getParcelList() { return _parcelList; }
//    public List<ParcelModel>                    getParcelList(int floor) { return _parcelListFloor.get(floor); }
    public Collection<ItemModel>                getItems() { return _items; }
    public Collection<ItemModel>                getFactories() { return _factories; }
    public Collection<ConsumableModel>          getConsumables() { return _consumables; }
    public Collection<StructureModel>           getStructures() { return _structures; }
    public Collection<PlantModel>            getResources() { return _resources; }
    public double                               getLight() { return _light; }
    public ParcelModel                          getParcel(int x, int y) { return getParcel(x, y, _floor); }
    public ParcelModel                          getParcel(int x, int y, int z) {
//        int index = x * _width * _height + y * _height + z;
//
//        ParcelModel parcel = _parcelCache.get(index);
//        if (parcel != null) {
//            return parcel;
//        }
//
//        System.out.println("Get parcel in DB: " + x + "x" + y + "x" + z);
//
//        SQLHelper.getInstance().post(new DBRunnable() {
//            @Override
//            public void run(SQLiteConnection db) {
//                try {
//                    SQLiteStatement st = db.prepare("SELECT x, y, z, ground FROM parcel WHERE x = ? AND y = ? AND z = ?");
//                    try {
//                        st.bind(1, x).bind(2, y).bind(3, z);
//                        ItemInfo resourceInfo = Data.getData().getItemInfo("base.granite");
//                        while (st.step()) {
//                            int ground = st.columnInt(3);
//                            parcel = new ParcelModel(index, null, x, y, z);
//                            if (ground == 1) {
//                                ResourceModel resource = new ResourceModel(resourceInfo);
//                                resource.setParcel(parcel);
//                                parcel.setResource(resource);
//                            }
//
//                            if (_parcelCacheQueue.size() == CACHE_SIZE) {
//                                _parcelCache.remove(_parcelCacheQueue.remove(0).getIndex());
//                            }
//                            _parcelCacheQueue.add(parcel);
//                            _parcelCache.put(index, parcel);
//                        }
//                    } finally {
//                        st.dispose();
//                    }
//                } catch (SQLiteException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        return parcel;
        return (x < 0 || x >= _width || y < 0 || y >= _height || z < 0 || z >= _floors) ? null : _parcels[x][y][z];
    }

    public void                                 setLight(double light) { _light = light; }

    public WorldModule() {
        ModuleHelper.setWorldModule(this);
    }

    @Override
    public void onLoaded(Game game) {
        WeatherModule weatherModule = (WeatherModule)ModuleManager.getInstance().getModule(WeatherModule.class);

        ModuleHelper.setWorldModule(this);

        _game = Game.getInstance();
        _width = _game.getInfo().worldWidth;
        _height = _game.getInfo().worldHeight;
        _floors = _game.getInfo().worldFloors;

//        List<ParcelModel> parcelList = new ArrayList<>();
//        _parcels = new ParcelModel[_width][_height][_floors];

//        _parcelsDo = new ParcelModel[(int)Math.pow(2, 30)];
//        parcelList.forEach(parcel -> {
//            _parcelsDo[(parcel.x << 16) + (parcel.y << 8) + parcel.z] = parcel;
//        });

//        WorldHelper.init(_parcels);

//        _parcelList = parcelList;
//        _parcelListFloor = parcelListFloors;
    }

    // Used only by serializers
    public MapObjectModel putObject(String name, int x, int y, int z, int data, boolean complete) {
        return putObject(WorldHelper.getParcel(x, y), Data.getData().getItemInfo(name), data, complete);
    }

    public void removeItem(ItemModel item) {
        if (item != null && item.getParcel() != null) {
            if (item.getParcel().getItem() == item) {
                item.getParcel().setItem(null);
            }
            _items.remove(item);
            _factories.remove(item);
            Application.getInstance().notify(observer -> observer.onRemoveItem(item));
        }
    }

    public void removeConsumable(ConsumableModel consumable) {
        if (consumable != null && consumable.getParcel() != null) {
            if (consumable.getParcel().getConsumable() == consumable) {
                consumable.getParcel().setConsumable(null);
            }
            _consumables.remove(consumable);
            Application.getInstance().notify(observer -> observer.onRemoveConsumable(consumable));
        }
    }

    public void removeStructure(int x, int y) {
        if (!WorldHelper.inMapBounds(x, y)) {
            return;
        }

        StructureModel structure = _parcels[x][y][0].getStructure();
        if (structure != null) {
            if (structure.getParcel().getStructure() == structure) {
                structure.getParcel().setStructure(null);
            }
            _structures.remove(structure);
            Application.getInstance().notify(observer -> observer.onRemoveStructure(structure));
        }
    }

    public void removeStructure(StructureModel structure) {
        if (structure != null && structure.getParcel() != null) {
            moveStructureToParcel(structure.getParcel(), null);
            Application.getInstance().notify(observer -> observer.onRemoveStructure(structure));
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
                Application.getInstance().notify(observer -> observer.onAddConsumable(finalParcel.getConsumable()));
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

            Application.getInstance().notify(observer -> observer.onAddConsumable(consumable));

            return consumable;
        }
        return null;
    }

    public MapObjectModel putObject(ParcelModel parcel, ItemInfo itemInfo, int quantity) {
        return putObject(parcel, itemInfo, quantity, false);
    }

    public MapObjectModel putObject(ParcelModel parcel, ItemInfo itemInfo, int data, boolean complete) {
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

            if (itemInfo.isPlant) {
                return putPlant(parcel, itemInfo, data);
            }

            if (itemInfo.isRock) {
                parcel.setRockInfo(itemInfo);
                return null;
            }

            if (itemInfo.isNetworkItem) {
                return putNetworkItem(parcel, itemInfo, data, complete);
            }
        }
        return null;
    }

    public MapObjectModel putObject(String itemName, ParcelModel parcel, int data) {
        return putObject(itemName, parcel.x, parcel.y, parcel.z, data, true);
    }

    private PlantModel putPlant(ParcelModel parcel, ItemInfo itemInfo, int matterSupply) {
        // Put item on floor
        PlantModel plant = new PlantModel(itemInfo);
        for (int i = 0; i < plant.getWidth(); i++) {
            for (int j = 0; j < plant.getHeight(); j++) {
                movePlantToParcel(parcel, plant);
            }
        }
        _resources.add(plant);
        Application.getInstance().notify(observer -> observer.onAddPlant(plant));

        return plant;
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

        if (item.getFactory() != null) {
            _factories.add(item);
        }

        Application.getInstance().notify(observer -> observer.onAddItem(item));

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
            Application.getInstance().notify(observer -> observer.onAddStructure(structure));
            return structure;
        }

        return null;
    }

    public NetworkObjectModel putNetworkItem(ParcelModel parcel, ItemInfo itemInfo, int data, boolean complete) {
        if (!parcel.hasNetwork(itemInfo.network)) {
            NetworkObjectModel networkObject = new NetworkObjectModel(itemInfo, itemInfo.network);
            networkObject.setComplete(complete);
            moveNetworkToParcel(parcel, networkObject);
            _networks.add(networkObject);
            Application.getInstance().notify(observer -> observer.onAddNetworkObject(networkObject));
            return networkObject;
        }
        return null;
    }

    public void removeResource(PlantModel resource) {
        if (resource != null) {
            if (resource.getParcel().getPlant() == resource) {
                resource.getParcel().setPlant(null);
            }

            _resources.remove(resource);
            Application.getInstance().notify(observer -> observer.onRemoveResource(resource));
        }
    }

    private ItemModel takeItem(ItemModel item, ParcelModel parcel) {
        if (parcel != null && item != null) {
            moveItemToParcel(parcel, null);
            Application.getInstance().notify(observer -> observer.onRefreshItem(item));
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
            removeResource((PlantModel) object);
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
        _consumables.forEach(ConsumableModel::fixPosition);
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
            for (int i = 0; i < item.getWidth(); i++) {
                for (int j = 0; j < item.getHeight(); j++) {
                    if (WorldHelper.inMapBounds(parcel.x + i, parcel.y + j)) {
                        _parcels[parcel.x + i][parcel.y + j][0].setItem(item);
                    }
                }
            }
        }
    }

    private void moveNetworkToParcel(ParcelModel parcel, NetworkObjectModel network) {
        if (network != null) {
            if (network.getParcel() != null) {
                network.getParcel().removeNetwork(network);
            }
            network.setParcel(parcel);
            parcel.addNetwork(network);
        }
    }

    private void moveStructureToParcel(ParcelModel parcel, StructureModel structure) {
        parcel.setStructure(structure);
        if (structure != null) {
            structure.setParcel(parcel);
        }
    }

    private void moveConsumableToParcel(ParcelModel parcel, ConsumableModel consumable) {
        parcel.setConsumable(consumable);
        if (consumable != null && consumable.getParcel() != null) {
            consumable.getParcel().setConsumable(null);
        }
        if (consumable != null) {
            consumable.setParcel(parcel);

            if (parcel.getConsumable() == null) {
                parcel.setConsumable(consumable);
            }
        }
    }

    private void movePlantToParcel(ParcelModel parcel, PlantModel resource) {
        parcel.setPlant(resource);
        if (resource != null) {
            resource.setParcel(parcel);
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

    @Override
    public void onFloorUp() {
        if (_floor < _floors - 1) {
            _floor++;
            Application.getInstance().notify(observer -> observer.onFloorChange(_floor));
        }
    }

    @Override
    public void onFloorDown() {
        if (_floor > 0) {
            _floor--;
            Application.getInstance().notify(observer -> observer.onFloorChange(_floor));
        }
    }

    public int getFloor() {
        return _floor;
    }

//    public int getWidth() { return _width; }
//    public int getHeight() { return _height; }
}
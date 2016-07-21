package org.smallbox.faraway.module.world;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.engine.renderer.GetParcelListener;
import org.smallbox.faraway.core.engine.renderer.Viewport;
import org.smallbox.faraway.core.game.BindLuaController;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.world.controller.WorldInfoParcel2Controller;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.PlantModel;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.module.job.JobModule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class WorldModule extends GameModule<WorldModuleObserver> {
    @BindModule("base.module.jobs")
    private JobModule _jobs;

    @BindLuaController
    private WorldInfoParcel2Controller          _infoParcel2Controller;

    private ParcelModel[][][]                   _parcels;
    private int                                 _width;
    private int                                 _height;
    private int                                 _floors;
    private Game                                _game;
    private Collection<PlantModel>              _plants;
    private double                              _light;
    private int                                 _floor = WorldHelper.getCurrentFloor();
    private Viewport                            _viewport;

    @Override
    protected void onGameCreate(Game game) {
        _viewport = game.getViewport();

        game.getRenders().add(new WorldGroundRenderer(this));
        game.getRenders().add(new WorldTopRenderer(this));
        getSerializers().add(new WorldModuleSerializer(this));
    }

    @Override
    protected void onGameStart(Game game) {
        assert _game != null;
    }

    @Override
    public boolean onSelectParcel(ParcelModel parcel) {
        _infoParcel2Controller.select(parcel);
        return false;
    }

    public void init(Game game, ParcelModel[][][] parcels, List<ParcelModel> parcelList) {
        _game = game;
        _width = _game.getInfo().worldWidth;
        _height = _game.getInfo().worldHeight;
        _floors = _game.getInfo().worldFloors;
        _floor = _floors - 1;

        _parcels = parcels;
        _plants = new LinkedBlockingQueue<>();

        // Notify world observers
        parcelList.forEach(parcel -> {
            notifyObservers(observer -> observer.onAddParcel(parcel));

            if (parcel.hasPlant()) {
                _plants.add(parcel.getPlant());
            }
        });
    }

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

    public Collection<PlantModel>               getPlants() { return _plants; }
    public double                               getLight() { return _light; }
    public ParcelModel                          getParcel(int x, int y, int z) {
        return (x < 0 || x >= _width || y < 0 || y >= _height || z < 0 || z >= _floors) ? null : _parcels[x][y][z];
    }

    public void                                 setLight(double light) { _light = light; }

    // Used only by serializers
    public void putObject(String name, int x, int y, int z, int data, boolean complete) {
        putObject(WorldHelper.getParcel(x, y, z), Data.getData().getItemInfo(name), data, complete);
    }

    public void replaceGround(ParcelModel parcel, ItemInfo groundInfo) {
        if (parcel != null && !parcel.hasRock() && parcel.hasGround()) {
            ParcelModel parcelBottom = WorldHelper.getParcel(parcel.x, parcel.y, parcel.z - 1);
            if (parcelBottom != null && !parcelBottom.hasRock()) {
                parcel.setGroundInfo(groundInfo);
                Application.getInstance().notify(observer -> observer.onChangeGround(parcel));
            }
        }
    }

    public void putObject(ParcelModel parcel, ItemInfo itemInfo, int quantity) {
        putObject(parcel, itemInfo, quantity, false);
    }

    public void putObject(ParcelModel parcel, ItemInfo itemInfo, int data, boolean complete) {
        Application.getInstance().notify(observer -> observer.putObject(parcel, itemInfo, data, complete));

        if (parcel != null) {
            notifyObservers(observer -> observer.putObject(parcel, itemInfo, data, complete));

            if (itemInfo.isRock) {
                parcel.setRockInfo(itemInfo);
            }
        }
    }

    @Override
    public void onMouseMove(int x, int y) {
        // TODO
        if (x < 1500) {
            notifyObservers(observer -> observer.onMouseMove(getRelativePosX(x), getRelativePosY(y), WorldHelper.getCurrentFloor()));
        }
    }

    @Override
    public void onMousePress(int x, int y, GameEventListener.MouseButton button) {
        // TODO
        if (x < 1500) {
            notifyObservers(observer -> observer.onMousePress(getRelativePosX(x), getRelativePosY(y), WorldHelper.getCurrentFloor(), button));
        }
    }

    public void onMouseRelease(int x, int y, GameEventListener.MouseButton button) {
        // TODO
        if (x < 1500) {
            notifyObservers(observer -> observer.onMouseRelease(getRelativePosX(x), getRelativePosY(y), WorldHelper.getCurrentFloor(), button));
        }
    }

    public int                      getRelativePosX(int x) { return (int) ((x - _viewport.getPosX()) / _viewport.getScale() / Constant.TILE_WIDTH); }
    public int                      getRelativePosY(int y) { return (int) ((y - _viewport.getPosY()) / _viewport.getScale() / Constant.TILE_HEIGHT); }

    public void putObject(String itemName, ParcelModel parcel, int data) {
        putObject(itemName, parcel.x, parcel.y, parcel.z, data, true);
    }

//    private ItemModel putItem(ParcelModel parcel, ItemInfo itemInfo, int progress) {
//        // Put item on floor
//        ItemModel item = new ItemModel(itemInfo, parcel);
//        item.addProgress(progress);
//        moveItemToParcel(parcel, item);
//        if (item.getInfo().receipts != null && item.getInfo().receipts.size() > 0) {
//            item.setReceipt(item.getInfo().receipts.get(0));
//        }
//        item.init();
//        _items.add(item);
//
//        notifyObservers(observer -> observer.onAddItem(parcel, item));
//
//        return item;
//    }
//
//    private ItemModel takeItem(ItemModel item, ParcelModel parcel) {
//        if (parcel != null && item != null) {
//            moveItemToParcel(parcel, null);
//            Application.getInstance().notify(observer -> observer.onRefreshItem(item));
//            return item;
//        }
//        printError("Area or item is null");
//        return null;
//    }
//
//    public ItemModel takeItem(int x, int y, int z) {
//        ParcelModel area = getParcel(x, y, z);
//        if (area != null) {
//            return takeItem(area.getItem(), area);
//        }
//        return null;
//    }

    public void remove(MapObjectModel mapObject) {
        if (mapObject == null) {
            printError("Cannot remove null object");
            return;
        }

        Application.getInstance().notify(observer -> observer.removeObject(mapObject));
    }

//    // TODO
//    public ItemModel getItemById(int itemId) {
//        for (int x = 0; x < _width; x++) {
//            for (int y = 0; y < _height; y++) {
//                if (_parcels[x][y][0].getItem() != null && _parcels[x][y][0].getItem().getId() == itemId) {
//                    return _parcels[x][y][0].getItem();
//                }
//            }
//        }
//        return null;
//    }

    public int getEnvironmentValue(int startX, int startY, int z, int distance) {
        int fromX = startX - distance;
        int fromY = startY - distance;
        int toX = startX + distance;
        int toY = startY + distance;
        int value = 0;
        for (int x = fromX; x < toX; x++) {
            for (int y = fromY; y < toY; y++) {
                if (WorldHelper.inMapBounds(x, y, z)) {
                    value += _parcels[x][y][z].getEnvironmentScore();
                }
            }
        }
        return value;
    }

//    private void moveItemToParcel(ParcelModel parcel, ItemModel item) {
//        parcel.setItem(item);
//        if (item != null) {
//            item.setParcel(parcel);
//            for (int i = 0; i < item.getWidth(); i++) {
//                for (int j = 0; j < item.getHeight(); j++) {
//                    if (WorldHelper.inMapBounds(parcel.x + i, parcel.y + j, parcel.z)) {
//                        _parcels[parcel.x + i][parcel.y + j][parcel.z].setItem(item);
//                    }
//                }
//            }
//        }
//    }

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
            WorldHelper.setCurrentFloor(_floor);
            Application.getInstance().notify(observer -> observer.onFloorChange(_floor));
        }
    }

    @Override
    public void onFloorDown() {
        if (_floor > 0) {
            _floor--;
            WorldHelper.setCurrentFloor(_floor);
            Application.getInstance().notify(observer -> observer.onFloorChange(_floor));
        }
    }
}
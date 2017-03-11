package org.smallbox.faraway.modules.world;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.ModuleSerializer;
import org.smallbox.faraway.core.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.util.Constant;

import java.util.ArrayList;
import java.util.List;

@ModuleSerializer(WorldModuleSerializer.class)
//@ModuleRenderer({WorldGroundRenderer.class, WorldTopRenderer.class})
public class WorldModule extends GameModule<WorldModuleObserver> {

    @BindModule
    private JobModule jobModule;

//    @BindLuaController
//    private WorldInfoParcel2Controller          _infoParcel2Controller;

    private ParcelModel[][][]                   _parcels;
    private List<ParcelModel>                   _parcelList;
    private int                                 _width;
    private int                                 _height;
    private int                                 _floors;
    private double                              _light;
    private int                                 _floor = WorldHelper.getCurrentFloor();

    @Override
    public void onGameStart(Game game) { }

    @Override
    public void onModuleUpdate(Game game) {
    }

    @Override
    public boolean onSelectParcel(ParcelModel parcel) {
//        _infoParcel2Controller.select(parcel);
        return false;
    }

    public void init(Game game, ParcelModel[][][] parcels, List<ParcelModel> parcelList) {
        WorldHelper.init(game.getInfo(), parcels);

        _width = game.getInfo().worldWidth;
        _height = game.getInfo().worldHeight;
        _floors = game.getInfo().worldFloors;
        _floor = _floors - 1;

        _parcels = parcels;
        _parcelList = parcelList;

        // Notify world observers
        parcelList.forEach(parcel -> notifyObservers(observer -> observer.onAddParcel(parcel)));

        Application.pathManager.init(parcelList);
    }

    public ParcelModel[][][]                    getParcels() { return _parcels; }
    public List<ParcelModel>                    getParcelList() { return _parcelList; }

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

    public double                               getLight() { return _light; }
    public int getWidth() { return _width; }
    public int getHeight() { return _height; }
    public int getFloors() { return _floors; }
    public ParcelModel                          getParcel(int x, int y, int z) {
        return (x < 0 || x >= _width || y < 0 || y >= _height || z < 0 || z >= _floors) ? null : _parcels[x][y][z];
    }

    public void                                 setLight(double light) { _light = light; }

    // Used only by serializers
    public void putObject(String name, int x, int y, int z, int data, boolean complete) {
        putObject(WorldHelper.getParcel(x, y, z), Application.data.getItemInfo(name), data, complete);
    }

    public void replaceGround(ParcelModel parcel, ItemInfo groundInfo) {
        if (parcel != null && !parcel.hasRock() && parcel.hasGround()) {
            ParcelModel parcelBottom = WorldHelper.getParcel(parcel.x, parcel.y, parcel.z - 1);
            if (parcelBottom != null && !parcelBottom.hasRock()) {
                parcel.setGroundInfo(groundInfo);
                Application.notify(observer -> observer.onChangeGround(parcel));
            }
        }
    }

    public void putObject(ParcelModel parcel, ItemInfo itemInfo, int quantity) {
        putObject(parcel, itemInfo, quantity, false);
    }

    public void putObject(ParcelModel parcel, ItemInfo itemInfo, int data, boolean complete) {
        Application.notify(observer -> observer.putObject(parcel, itemInfo, data, complete));

        if (parcel != null) {
            notifyObservers(observer -> observer.putObject(parcel, itemInfo, data, complete));

            if (itemInfo.isRock) {
                parcel.setRockInfo(itemInfo);
            }
        }
    }

//    @Override
//    public void onMouseMove(GameEvent event) {
//        // TODO
//        if (!event.consumed && event.mouseEvent.x < 1500) {
//            notifyObservers(observer -> {
//                if (!event.consumed) {
//                    observer.onMouseMove(event, getRelativePosX(event.mouseEvent.x), getRelativePosY(event.mouseEvent.y), WorldHelper.getCurrentFloor());
//                }
//            });
//        }
//    }
//
//    @Override
//    public void onMousePress(GameEvent event) {
//        // TODO
//        if (!event.consumed && event.mouseEvent.x < 1500) {
//            notifyObservers(observer -> {
//                if (!event.consumed) {
//                    observer.onMousePress(event, getRelativePosX(event.mouseEvent.x), getRelativePosY(event.mouseEvent.y), WorldHelper.getCurrentFloor(), event.mouseEvent.button);
//                }
//            });
//        }
//    }
//
//    public void onMouseRelease(GameEvent event) {
//        // TODO
//        if (!event.consumed && event.mouseEvent.x < 1500) {
//            notifyObservers(observer -> {
//                if (!event.consumed) {
//                    observer.onMouseRelease(event, getRelativePosX(event.mouseEvent.x), getRelativePosY(event.mouseEvent.y), WorldHelper.getCurrentFloor(), event.mouseEvent.button);
//                }
//            });
//        }
//    }

    public void putObject(String itemName, ParcelModel parcel, int data) {
        putObject(itemName, parcel.x, parcel.y, parcel.z, data, true);
    }

//    private UsableItem putItem(ParcelModel parcel, ItemInfo itemInfo, int progress) {
//        // Put item on floor
//        UsableItem item = new UsableItem(itemInfo, parcel);
//        item.addProgress(progress);
//        moveItemToParcel(parcel, item);
//        if (item.getInfo().receiptGroups != null && item.getInfo().receiptGroups.size() > 0) {
//            item.setReceipt(item.getInfo().receiptGroups.get(0));
//        }
//        item.gameStart();
//        _items.addSubJob(item);
//
//        notifyObservers(observer -> observer.onAddItem(parcel, item));
//
//        return item;
//    }
//
//    private UsableItem takeItem(UsableItem item, ParcelModel parcel) {
//        if (parcel != null && item != null) {
//            moveItemToParcel(parcel, null);
//            Application.notify(observer -> observer.onRefreshItem(item));
//            return item;
//        }
//        printError("Area or item is null");
//        return null;
//    }
//
//    public UsableItem takeItem(int x, int y, int z) {
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

        Application.notify(observer -> observer.removeObject(mapObject));
    }

//    // TODO
//    public UsableItem getItemById(int itemId) {
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

    @Override
    public int getModulePriority() {
        return Constant.MODULE_WORLD_PRIORITY;
    }

    @Override
    public boolean isModuleMandatory() {
        return true;
    }

}
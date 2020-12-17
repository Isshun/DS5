package org.smallbox.faraway.modules.world;

import org.smallbox.faraway.common.ParcelCommon;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.bridge.Connection;
import org.smallbox.faraway.core.dependencyInjector.Inject;
import org.smallbox.faraway.core.dependencyInjector.GameObject;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.ModuleSerializer;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.weather.WeatherModule;
import org.smallbox.faraway.util.Constant;

import java.util.ArrayList;
import java.util.List;

@GameObject
@ModuleSerializer(WorldModuleSerializer.class)
public class WorldModule extends GameModule {

    @Inject
    private JobModule jobModule;

    @Inject
    private WeatherModule weatherModule;

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

    @Override
    public void onClientConnect(Connection client) {
        _parcelList.forEach(parcel -> {
            ParcelCommon parcelCommon = new ParcelCommon();
            parcelCommon.x = parcel.x;
            parcelCommon.y = parcel.y;
            parcelCommon.z = parcel.z;
            Application.gameServer.writeObject(client, parcelCommon);
        });
    }

    public void init(Game game, ParcelModel[][][] parcels, List<ParcelModel> parcelList) {
        WorldHelper.init(game.getInfo(), parcels);

        _width = game.getInfo().worldWidth;
        _height = game.getInfo().worldHeight;
        _floors = game.getInfo().worldFloors;
        _floor = _floors - 1;

        _parcels = parcels;
        _parcelList = parcelList;
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

    public void replaceGround(ParcelModel parcel, ItemInfo groundInfo) {
        if (parcel != null && !parcel.hasRock() && parcel.hasGround()) {
            ParcelModel parcelBottom = WorldHelper.getParcel(parcel.x, parcel.y, parcel.z - 1);
            if (parcelBottom != null && !parcelBottom.hasRock()) {
                parcel.setGroundInfo(groundInfo);
                Application.notify(observer -> observer.onChangeGround(parcel));
            }
        }
    }

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

    public double getTemperature(ParcelModel parcel) {
        if (parcel.getRoom() != null) {
            return parcel.getRoom().getTemperature();
        }
        return weatherModule.getTemperature();
    }

    public double getLight(ParcelModel parcel) {
        if (parcel.getRoom() != null) {
            return parcel.getRoom().getLight();
        }
        return weatherModule.getLight();
    }

}
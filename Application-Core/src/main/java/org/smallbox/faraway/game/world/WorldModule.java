package org.smallbox.faraway.game.world;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.module.GenericGameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.model.MovableModel.Direction;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.game.weather.WeatherModule;
import org.smallbox.faraway.util.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

@GameObject
public class WorldModule extends GenericGameModule<Parcel> {
    @Inject private WeatherModule weatherModule;
    @Inject private Game game;

    private Parcel[][][] _parcels;
    private int _width;
    private int _height;
    private int _floors;
    private double _light;

    public void init(List<Parcel> parcelList) {
        _width = game.getInfo().worldWidth;
        _height = game.getInfo().worldHeight;
        _floors = game.getInfo().worldFloors;
        _parcels = new Parcel[_width][_height][_floors];

        modelList = parcelList;
        modelList.forEach(parcel -> _parcels[parcel.x][parcel.y][parcel.z] = parcel);

        WorldHelper.init(game.getInfo(), _parcels);
    }

    public void getParcels(int fromX, int toX, int fromY, int toY, int fromZ, int toZ, GetParcelListener getParcelListener) {
        assert getParcelListener != null;

        List<Parcel> parcels = new ArrayList<>();
        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                for (int z = fromZ; z <= toZ; z++) {
                    if (x >= 0 && x < _width && y >= 0 && y < _height && z >= 0 && z < _floors) {
                        parcels.add(getParcel(x, y, z));
                    }
                }
            }
        }
        getParcelListener.onGetParcel(parcels);
    }

    public double getLight() {
        return _light;
    }

    public int getWidth() {
        return _width;
    }

    public int getHeight() {
        return _height;
    }

    public int getFloors() {
        return _floors;
    }

    public Parcel getParcel(int x, int y, int z) {
        return (x < 0 || x >= _width || y < 0 || y >= _height || z < 0 || z >= _floors) ? null : _parcels[x][y][z];
    }

    public void setLight(double light) {
        _light = light;
    }

    public void replaceGround(Parcel parcel, ItemInfo groundInfo) {
        if (parcel != null && !parcel.hasRock() && parcel.hasGround()) {
            Parcel parcelBottom = WorldHelper.getParcel(parcel.x, parcel.y, parcel.z - 1);
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
                    value += getParcel(x, y, z).getEnvironmentScore();
                }
            }
        }
        return value;
    }

    @Override
    public int getModulePriority() {
        return Constant.MODULE_WORLD_PRIORITY;
    }

    public double getTemperature(Parcel parcel) {
        if (parcel.getRoom() != null) {
            return parcel.getRoom().getTemperature();
        }
        return weatherModule.getTemperature();
    }

    public double getLight(Parcel parcel) {
        if (parcel.getRoom() != null) {
            return parcel.getRoom().getLight();
        }
        return weatherModule.getLight();
    }

    public Optional<Parcel> getOptional(int x, int y, int z) {
        return Optional.ofNullable(WorldHelper.getParcel(x, y, z));
    }

    public Parcel getParcel(Parcel parcel, Direction direction) {
        switch (direction) {
            case NONE:
                return parcel;
            case TOP:
                return WorldHelper.getParcel(parcel.x, parcel.y - 1, parcel.z);
            case LEFT:
                return WorldHelper.getParcel(parcel.x - 1, parcel.y, parcel.z);
            case RIGHT:
                return WorldHelper.getParcel(parcel.x + 1, parcel.y, parcel.z);
            case BOTTOM:
                return WorldHelper.getParcel(parcel.x, parcel.y + 1, parcel.z);
            case TOP_LEFT:
                return WorldHelper.getParcel(parcel.x - 1, parcel.y - 1, parcel.z);
            case TOP_RIGHT:
                return WorldHelper.getParcel(parcel.x + 1, parcel.y - 1, parcel.z);
            case BOTTOM_LEFT:
                return WorldHelper.getParcel(parcel.x - 1, parcel.y + 1, parcel.z);
            case BOTTOM_RIGHT:
                return WorldHelper.getParcel(parcel.x + 1, parcel.y + 1, parcel.z);
            case UNDER:
                return WorldHelper.getParcel(parcel.x, parcel.y, parcel.z - 1);
            case OVER:
                return WorldHelper.getParcel(parcel.x, parcel.y, parcel.z + 1);
        }
        return null;
    }

    public Optional<Parcel> getOptional(Parcel parcel, Direction direction) {
        return Optional.ofNullable(getParcel(parcel, direction));
    }

    public boolean check(Parcel parcel, Predicate<Parcel> predicate, Direction... directions) {
        return Stream.of(directions).map(direction -> getParcel(parcel, direction)).allMatch(p -> p != null && predicate.test(p));
    }

    public boolean checkOrNull(Parcel parcel, Predicate<Parcel> predicate, Direction... directions) {
        return Stream.of(directions).map(direction -> getParcel(parcel, direction)).allMatch(p -> p == null || predicate.test(p));
    }

}
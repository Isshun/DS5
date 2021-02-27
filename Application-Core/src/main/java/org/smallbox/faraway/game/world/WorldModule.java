package org.smallbox.faraway.game.world;

import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.DependencyNotifier;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameAction.OnGameMapChange;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.model.MovableModel.Direction;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.GenericGameModule;
import org.smallbox.faraway.game.item.ItemModule;
import org.smallbox.faraway.game.weather.WeatherModule;
import org.smallbox.faraway.game.weather.WorldTemperatureModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;

@GameObject
public class WorldModule extends GenericGameModule<Parcel> {
    @Inject private DependencyManager dependencyManager;
    @Inject private DependencyNotifier dependencyNotifier;
    @Inject private WorldTemperatureModule worldTemperatureModule;
    @Inject private WeatherModule weatherModule;
    @Inject private ItemModule itemModule;
    @Inject private Game game;

    private Parcel[][][] _parcels;
    private int _width;
    private int _height;
    private int _floors;

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

    public void replaceGround(Parcel parcel, ItemInfo groundInfo) {
        if (parcel != null && !parcel.hasRock() && parcel.hasGround()) {
            Parcel parcelBottom = WorldHelper.getParcel(parcel.x, parcel.y, parcel.z - 1);
            if (parcelBottom != null && !parcelBottom.hasRock()) {
                parcel.setGroundInfo(groundInfo);
                dependencyNotifier.notify(OnGameMapChange.class);
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

    public double getTemperature(Parcel parcel) {
        if (parcel.getRoom() != null) {
            return parcel.getRoom().getTemperature();
        }
        return worldTemperatureModule.getTemperature();
    }

    public double getLight(Parcel parcel) {
        if (parcel.getRoom() != null) {
            return parcel.getRoom().getLight();
        }
        return weatherModule.getLight();
    }

    public Parcel getParcel(Parcel parcel, Direction direction) {
        return switch (direction) {
            case NONE -> parcel;
            case TOP -> WorldHelper.getParcel(parcel.x, parcel.y - 1, parcel.z);
            case LEFT -> WorldHelper.getParcel(parcel.x - 1, parcel.y, parcel.z);
            case RIGHT -> WorldHelper.getParcel(parcel.x + 1, parcel.y, parcel.z);
            case BOTTOM -> WorldHelper.getParcel(parcel.x, parcel.y + 1, parcel.z);
            case TOP_LEFT -> WorldHelper.getParcel(parcel.x - 1, parcel.y - 1, parcel.z);
            case TOP_RIGHT -> WorldHelper.getParcel(parcel.x + 1, parcel.y - 1, parcel.z);
            case BOTTOM_LEFT -> WorldHelper.getParcel(parcel.x - 1, parcel.y + 1, parcel.z);
            case BOTTOM_RIGHT -> WorldHelper.getParcel(parcel.x + 1, parcel.y + 1, parcel.z);
            case UNDER -> WorldHelper.getParcel(parcel.x, parcel.y, parcel.z - 1);
            case OVER -> WorldHelper.getParcel(parcel.x, parcel.y, parcel.z + 1);
        };
    }

    public boolean check(Parcel parcel, Predicate<Parcel> predicate, Direction... directions) {
        return Stream.of(directions).map(direction -> getParcel(parcel, direction)).allMatch(p -> p != null && predicate.test(p));
    }

    public boolean checkOrNull(Parcel parcel, Predicate<Parcel> predicate, Direction... directions) {
        return Stream.of(directions).map(direction -> getParcel(parcel, direction)).allMatch(p -> p == null || predicate.test(p));
    }

    public Parcel getRandom(int floor) {
        return modelList.stream().filter(parcel -> parcel.z == floor).skip(new Random().nextInt(_width * _height)).findFirst().orElse(null);
    }

    public void refreshGlue(Parcel parcel) {
        int glue = 0;
        glue = refreshGlue(parcel, getParcel(parcel, Direction.TOP), glue, 0b0001);
        glue = refreshGlue(parcel, getParcel(parcel, Direction.RIGHT), glue, 0b0010);
        glue = refreshGlue(parcel, getParcel(parcel, Direction.BOTTOM), glue, 0b0100);
        glue = refreshGlue(parcel, getParcel(parcel, Direction.LEFT), glue, 0b1000);
        parcel.setGlue(glue);
    }

    private int refreshGlue(Parcel parcel, Parcel parcelAround, int glue, int mask) {
        return hasGlue(parcel) && hasGlue(parcelAround) ? glue | mask : glue;
    }

    private boolean hasGlue(Parcel parcel) {
        return parcel.hasRock() || Optional.ofNullable(itemModule.getItem(parcel)).filter(item -> item.getInfo().glue).isPresent();
    }

}
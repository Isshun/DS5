package org.smallbox.faraway.core.module.world.model;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedNode;
import com.badlogic.gdx.utils.Array;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.NetworkInfo;
import org.smallbox.faraway.core.module.area.model.AreaModel;
import org.smallbox.faraway.core.module.job.model.DigJob;
import org.smallbox.faraway.core.module.room.model.RoomModel;
import org.smallbox.faraway.modules.flora.model.PlantItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ParcelModel implements IndexedNode<ParcelModel> {
    public final int                        x;
    public final int                        y;
    public final int                        z;

    private ParcelEnvironment               _environment;
    private RoomModel                       _room;
    private AreaModel                       _area;
    private Array<Connection<ParcelModel>>  _connections;
    private final int                       _index;
    private int                             _tile;
    private double _liquidValue;
    private DigJob                          _digJob;
    private ItemInfo                        _rockInfo;
    private ItemInfo                        _groundInfo;
//    public ConsumableItem                  _consumable;
    public PlantItem _plant;
    public List<NetworkItem>         _networks;
    private ItemInfo                        _liquidInfo;

    private Map<Class<? extends MapObjectModel>, MapObjectModel> _items = new ConcurrentHashMap<>();

    public ParcelModel(int index, int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        _index = index;
    }

    public void                     setRoom(RoomModel room) { _room = room; }
    public void                     setArea(AreaModel area) { _area = area; }
    public void                     setConnections(Array<Connection<ParcelModel>> connections) { _connections = connections; }

    public void                     setPlant(PlantItem plant) { _plant = plant; }
    public void                     setItem(MapObjectModel item) { _items.put(item.getClass(), item); }
    public void                     setGroundInfo(ItemInfo groundInfo) {
        _groundInfo = groundInfo;
    }
    public void                     setRockInfo(ItemInfo rockInfo) { _rockInfo = rockInfo; }
    public void                     setRockName(String rockName) { _rockInfo = Application.data.getItemInfo(rockName); }
    public void                     setTile(int tile) { _tile = tile; }
    public void                     setDigJob(DigJob digJob) { _digJob = digJob; }
    public void                     setLiquidInfo(ItemInfo liquidInfo, double value) { _liquidInfo = liquidInfo; _liquidValue = value; }

    public void                     removeItem(MapObjectModel item) { _items.remove(item); }

    public boolean                  isExterior() { return _room == null || _room.isExterior(); }
    public boolean                  canSupportRoof() { return (hasItem(StructureItem.class) && getItem(StructureItem.class).getInfo().canSupportRoof) || _rockInfo != null; }
    public boolean                  hasNetwork(NetworkInfo networkInfo) { return getNetworkObject(networkInfo) != null; }
    public boolean                  hasPlant() { return _plant != null; }
    public boolean                  hasGround() { return _groundInfo != null; }
    public boolean                  hasLiquid() { return _liquidInfo != null; }
    public boolean                  hasRock() { return _rockInfo != null; }
    public boolean                  hasDigJob() { return _digJob != null; }
    public boolean                  hasRoom() { return _room != null; }

    public List<NetworkItem>        getNetworkObjects() { return _networks; }
    public ItemInfo                 getRockInfo() { return _rockInfo; }
    public ItemInfo                 getGroundInfo() { return _groundInfo; }
    public ItemInfo                 getLiquidInfo() { return _liquidInfo; }
    public DigJob                   getDigJob() { return _digJob; }
    public PlantItem getPlant() { return _plant; }
//    public ConsumableItem          getConsumable() { return _consumable; }
    public RoomModel                getRoom() { return _room; }
    public AreaModel                getArea() { return _area; }
    public int                      getTile() { return _tile; }
    public ParcelEnvironment        getEnvironment() { return _environment; }
    public double                   getLight() { return _room != null ? _room.getLight() : -1; }
    public double                   getTemperature() { return _room != null ? _room.getTemperature() : -1; }
    public double                   getOxygen() { return _room != null ? _room.getOxygen() : -1; }
    public double                   getMoisture() { return 0.5; }

    @Override
    public String toString() {
        return x + "x" + y + "x" + z;
    }

    public boolean isRoomOpen() {
        if (!isWalkable()) {
            return false;
        }
        if (_connections == null || _connections.size == 0) {
            return false;
        }
        if (hasItem(StructureItem.class) && getItem(StructureItem.class).getInfo().isDoor) {
            return false;
        }
        return true;
    }

    public void addNetwork(NetworkItem network) {
        if (_networks == null) { _networks = new ArrayList<>(); }
        _networks.add(network);
    }

    public void removeNetwork(NetworkItem network) {
        if (_networks == null) { _networks = new ArrayList<>(); }
        _networks.remove(network);
    }

    public NetworkItem getNetworkObject(NetworkInfo networkInfo) {
        if (_networks == null) {
            return null;
        }
        for (NetworkItem networkObject: _networks) {
            if (networkObject.getNetworkInfo() == networkInfo) {
                return networkObject;
            }
        }
        return null;
    }

    public boolean          isWalkable() {
        // Check ground
        if (_groundInfo == null || !_groundInfo.isWalkable) {
            return false;
        }

        // Check rock
        if (_rockInfo != null) {
            return false;
        }

        // Check structure (wall, closed door)
        StructureItem structure = getItem(StructureItem.class);
        if (structure != null && !structure.getInfo().isWalkable && structure.isComplete()) {
            return false;
        }

        // TODO: addSubJob is walkable field set by modules
//        // Check item
//        if (_item != null && !_item.getInfo().isWalkable && _item.isComplete()) {
//            return false;
//        }

        // Check resource
        if (_plant != null && !_plant.getInfo().isWalkable) {
            return false;
        }

        return true;
    }

    public double getPermeability() {
        if (hasItem(StructureItem.class)) {
            return getItem(StructureItem.class).getInfo().permeability;
        }
        if (_rockInfo != null) {
            return _rockInfo.permeability;
        }
        return 1;
    }

    public double getFloorPermeability() {
        return _groundInfo != null ? _groundInfo.permeability : 1;
    }

    public double getCeilPermeability() {
        ItemInfo groundInfo = WorldHelper.getGroundInfo(x, y, z);
        return groundInfo != null ? groundInfo.permeability : 1;
    }

    @Override
    public int getIndex() {
        return _index;
    }

    @Override
    public Array<Connection<ParcelModel>> getConnections() {
        return _connections;
    }

    public int getEnvironmentScore() {
        int score = 0;

        if (_environment != null) {
            score += _environment.getScore();
        }
//        if (_item != null) {
//            score += _item.getValue();
//        }

        return score;
    }

    public boolean equals(int x, int y, int z) {
        return this.x == x && this.y == y && this.z == z;
    }

    public boolean accept(ConsumableItem consumable) {
        return accept(consumable.getInfo(), consumable.getFreeQuantity());
    }

    public boolean accept(ItemInfo itemInfo, int quantity) {
        if (_groundInfo == null || _groundInfo.isLinkDown) {
            return false;
        }

        // TODO
//        if (_rockInfo != null || _liquidInfo != null || _plant != null || _item != null || this.hasWallOrDoor()) {
//            return false;
//        }

//        if (_consumable != null && _consumable.getInfo() != itemInfo) {
//            return false;
//        }
//
//        if (_consumable != null && _consumable.getFreeQuantity() + quantity > Utils.getStorageMaxQuantity(itemInfo)) {
//            return false;
//        }

        if (_area != null && !_area.accept(itemInfo)) {
            return false;
        }

        return true;
    }

    public double getLiquidValue() {
        return _liquidValue;
    }

    @Deprecated
    public <T> T getItem(Class<T> cls) { return (T) _items.get(cls); }

    public boolean hasItem(Class cls) { return _items.get(cls) != null; }
}
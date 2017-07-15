package org.smallbox.faraway.core.module.world.model;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedNode;
import com.badlogic.gdx.utils.Array;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.ObjectModel;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.NetworkInfo;
import org.smallbox.faraway.core.module.path.ParcelConnection;
import org.smallbox.faraway.modules.area.AreaModel;
import org.smallbox.faraway.modules.room.model.RoomModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ParcelModel extends ObjectModel implements IndexedNode<ParcelModel> {
    public final int                        x;
    public final int                        y;
    public final int                        z;

    private int                             _environmentScore;
    private ParcelEnvironment               _environment;
    private RoomModel                       _room;
    private AreaModel                       _area;
    private Array<Connection<ParcelModel>>  _connections;
    private static Array<Connection<ParcelModel>>  _emptyConnections = new Array<>();
    private final int                       _index;
    private int                             _tile;
    private double                          _liquidValue;
    private ItemInfo                        _rockInfo;
    private ItemInfo                        _groundInfo;
    public List<NetworkItem>                _networks;
    private ItemInfo                        _liquidInfo;

    private Map<Class<? extends MapObjectModel>, MapObjectModel> _items = new ConcurrentHashMap<>();
    private boolean _connectionDirty;

    public ParcelModel(int index, int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        _index = index;
    }

    public void                     setRoom(RoomModel room) { _room = room; }
    public void                     setArea(AreaModel area) { _area = area; }
    public void                     setConnections(Array<Connection<ParcelModel>> connections) { _connections = connections; }

    public void                     setItem(MapObjectModel item) {
        if (!_items.containsValue(item)) {
            _items.put(item.getClass(), item);
            _environmentScore = _items.values().stream().mapToInt(i -> i.getInfo().environment).sum();
            item.setParcel(this);
        }
    }

    public void                     setGroundInfo(ItemInfo groundInfo) {
        _groundInfo = groundInfo;
    }
    public void                     setRockInfo(ItemInfo rockInfo) { _rockInfo = rockInfo; _connectionDirty = true; }
    public void                     setRockName(String rockName) { _rockInfo = Application.data.getItemInfo(rockName); }
    public void                     setTile(int tile) { _tile = tile; }
    public void                     setLiquidInfo(ItemInfo liquidInfo, double value) { _liquidInfo = liquidInfo; _liquidValue = value; }

    public void                     removeItem(MapObjectModel item) { _items.remove(item); }

    public boolean                  isExterior() { return _room == null || _room.isExterior(); }
    public boolean                  isConnectionDirty() { return _connectionDirty; }
    public boolean                  canSupportRoof() { return (hasItem(StructureItem.class) && getItem(StructureItem.class).getInfo().canSupportRoof) || _rockInfo != null; }
    public boolean                  hasNetwork(NetworkInfo networkInfo) { return getNetworkObject(networkInfo) != null; }
    public boolean                  hasGround() { return _groundInfo != null; }
    public boolean                  hasLiquid() { return _liquidInfo != null; }
    public boolean                  hasRock() { return _rockInfo != null; }
    public boolean                  hasRoom() { return _room != null; }

    public List<NetworkItem>        getNetworkObjects() { return _networks; }
    public ItemInfo                 getRockInfo() { return _rockInfo; }
    public ItemInfo                 getGroundInfo() { return _groundInfo; }
    public ItemInfo                 getLiquidInfo() { return _liquidInfo; }
    public RoomModel                getRoom() { return _room; }
    public AreaModel                getArea() { return _area; }
    public int                      getEnvironmentScore() { return _environmentScore; }
    public int                      getTile() { return _tile; }
    public double                   getLight() { return _room != null ? _room.getLight() : 1; }
    public double                   getTemperature() { return _room != null ? _room.getTemperature() : -1; }
    public double                   getOxygen() { return _room != null ? _room.getOxygen() : -1; }
    public double                   getMoisture() { return 0.5; }

    public void addParcelToConnections(Array<Connection<ParcelModel>> connections, ParcelModel toParcel) {
        if (toParcel != null) {
            if (isWalkable() && toParcel.isWalkable()) {
                if (z == toParcel.z
                        || (toParcel.z == z - 1 && (!hasGround() || getGroundInfo().isLinkDown) && toParcel.hasItem(StructureItem.class) && toParcel.getItem(StructureItem.class).getInfo().isRamp)
                        || (toParcel.z == z + 1 && (!toParcel.hasGround() || toParcel.getGroundInfo().isLinkDown) && hasItem(StructureItem.class) && getItem(StructureItem.class).getInfo().isRamp)) {
                    connections.add(new ParcelConnection(this, toParcel));
                }
            }
        }
    }

    public void resetConnection() {
        Array<Connection<ParcelModel>> connections = new Array<>(6);
        WorldHelper.getParcelArround(this, 1, toParcel -> addParcelToConnections(connections, toParcel));
        setConnections(connections);
    }

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
        return !(hasItem(StructureItem.class) && getItem(StructureItem.class).getInfo().isDoor);
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
        if (structure != null && !structure.getInfo().isWalkable && structure.isBuildComplete()) {
            return false;
        }

        // TODO: addSubJob is walkable field set by modules
//        // Check item
//        if (_item != null && !_item.getInfo().isWalkable && _item.isComplete()) {
//            return false;
//        }

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
        if (!isWalkable()) {
            return _emptyConnections;
        }

        return _connections;
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

        return !(_area != null && !_area.isAccepted(itemInfo));
    }

    public double getLiquidValue() {
        return _liquidValue;
    }

    public <T> T getItem(Class<T> cls) { return (T) _items.get(cls); }

    public boolean hasItem(Class cls) { return _items.get(cls) != null; }

    public Collection<MapObjectModel> getItems() {
        return _items.values();
    }

    public boolean hasConnection(ParcelModel parcel) {
        for (Connection<ParcelModel> connection: _connections) {
            if (connection.getFromNode() == this && connection.getToNode() == parcel) {
                return true;
            }
        }
        return false;
    }

    public boolean hasItems() {
        return !_items.isEmpty();
    }

    public void addConnection(ParcelModel toParcel) {

        // Don't add connection if already exists
        for (int i = 0; i < _connections.size; i++) {
            if (_connections.get(i).getToNode() == toParcel) {
                return;
            }
        }

        _connections.add(new ParcelConnection(this, toParcel));
    }

    public void removeConnection(ParcelModel toParcel) {
        for (int i = 0; i < _connections.size; i++) {
            if (_connections.get(i).getToNode() == toParcel) {
                _connections.removeIndex(i);
                return;
            }
        }
    }
}
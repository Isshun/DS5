package org.smallbox.faraway.core.module.world.model;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedNode;
import com.badlogic.gdx.utils.Array;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.NetworkInfo;
import org.smallbox.faraway.core.module.area.model.AreaModel;
import org.smallbox.faraway.core.module.job.model.DigJob;
import org.smallbox.faraway.core.module.room.model.RoomModel;
import org.smallbox.faraway.util.Utils;

import java.util.ArrayList;
import java.util.List;

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
    public ConsumableModel                  _consumable;
    public StructureModel                   _structure;
    public PlantModel                       _plant;
    public List<NetworkObjectModel>         _networks;
    private ItemInfo                        _liquidInfo;

    public ParcelModel(int index, int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        _index = index;
    }

    public void                     setRoom(RoomModel room) { _room = room; }
    public void                     setArea(AreaModel area) { _area = area; }
    public void                     setConnections(Array<Connection<ParcelModel>> connections) { _connections = connections; }

    public void                     setConsumable(ConsumableModel consumable) { _consumable = consumable; }
    public void                     setPlant(PlantModel plant) { _plant = plant; }
    public void                     setStructure(StructureModel structure) { _structure = structure; }
    public void                     setGroundInfo(ItemInfo groundInfo) { _groundInfo = groundInfo; }
    public void                     setRockInfo(ItemInfo rockInfo) { _rockInfo = rockInfo; }
    public void                     setTile(int tile) { _tile = tile; }
    public void                     setDigJob(DigJob digJob) { _digJob = digJob; }
    public void                     setLiquidInfo(ItemInfo liquidInfo, double value) { _liquidInfo = liquidInfo; _liquidValue = value; }

    public boolean                  isExterior() { return _room == null || _room.isExterior(); }
    public boolean                  canSupportRoof() { return (_structure != null && _structure.getInfo().canSupportRoof) || _rockInfo != null; }
    public boolean                  hasNetwork(NetworkInfo networkInfo) { return getNetworkObject(networkInfo) != null; }
    public boolean                  hasConsumable() { return _consumable != null; }
    public boolean                  hasPlant() { return _plant != null; }
    public boolean                  hasStructure() { return _structure != null; }
    public boolean                  hasNonFloorStructure() { return _structure != null && !_structure.isFloor(); }
    public boolean                  hasGround() { return _groundInfo != null; }
    public boolean                  hasLiquid() { return _liquidInfo != null; }
    public boolean                  hasRock() { return _rockInfo != null; }
    public boolean                  hasWallOrDoor() { return _structure != null && (_structure.getInfo().isWall || _structure.getInfo().isDoor); }
    public boolean                  hasWall() { return _structure != null && _structure.getInfo().isWall; }
    public boolean                  hasDoor() { return _structure != null && _structure.getInfo().isDoor; }
    public boolean                  hasDigJob() { return _digJob != null; }
    public boolean                  hasRoom() { return _room != null; }

    public List<NetworkObjectModel> getNetworkObjects() { return _networks; }
    public ItemInfo                 getRockInfo() { return _rockInfo; }
    public ItemInfo                 getGroundInfo() { return _groundInfo; }
    public ItemInfo                 getLiquidInfo() { return _liquidInfo; }
    public DigJob                   getDigJob() { return _digJob; }
    public StructureModel           getStructure() { return _structure; }
    public ItemInfo                 getStructureInfo() { return _structure != null ? _structure.getInfo() : null; }
    public PlantModel               getPlant() { return _plant; }
    public ConsumableModel          getConsumable() { return _consumable; }
    public RoomModel                getRoom() { return _room; }
    public AreaModel                getArea() { return _area; }
    public int                      getTile() { return _tile; }
    public ParcelEnvironment        getEnvironment() { return _environment; }
    public double                   getLight() { return _room != null ? _room.getLight() : -1; }
    public double                   getTemperature() { return _room != null ? _room.getTemperature() : -1; }
    public double                   getOxygen() { return _room != null ? _room.getOxygen() : -1; }

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
        if (getStructure() != null && getStructure().getInfo().isDoor) {
            return false;
        }
        return true;
    }

    public void addNetwork(NetworkObjectModel network) {
        if (_networks == null) { _networks = new ArrayList<>(); }
        _networks.add(network);
    }

    public void removeNetwork(NetworkObjectModel network) {
        if (_networks == null) { _networks = new ArrayList<>(); }
        _networks.remove(network);
    }

    public NetworkObjectModel getNetworkObject(NetworkInfo networkInfo) {
        if (_networks == null) {
            return null;
        }
        for (NetworkObjectModel networkObject: _networks) {
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
        if (_structure != null && !_structure.getInfo().isWalkable && _structure.isComplete()) {
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
        if (_structure != null) {
            return _structure.getInfo().permeability;
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

    public boolean accept(ConsumableModel consumable) {
        return accept(consumable.getInfo(), consumable.getQuantity());
    }

    public boolean accept(ItemInfo itemInfo, int quantity) {
        if (_groundInfo == null || _groundInfo.isLinkDown) {
            return false;
        }

        // TODO
//        if (_rockInfo != null || _liquidInfo != null || _plant != null || _item != null || this.hasWallOrDoor()) {
//            return false;
//        }

        if (_consumable != null && _consumable.getInfo() != itemInfo) {
            return false;
        }

        if (_consumable != null && _consumable.getQuantity() + quantity > Utils.getStorageMaxQuantity(itemInfo)) {
            return false;
        }

        if (_area != null && !_area.accept(itemInfo)) {
            return false;
        }

        return true;
    }

    public double getLiquidValue() {
        return _liquidValue;
    }
}
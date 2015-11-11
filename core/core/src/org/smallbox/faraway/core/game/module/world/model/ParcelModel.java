package org.smallbox.faraway.core.game.module.world.model;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedNode;
import com.badlogic.gdx.utils.Array;
import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.model.NetworkInfo;
import org.smallbox.faraway.core.game.module.area.model.AreaModel;
import org.smallbox.faraway.core.game.module.job.model.DigJob;
import org.smallbox.faraway.core.game.module.room.model.RoomModel;
import org.smallbox.faraway.core.game.module.world.WeatherModule;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.game.module.world.model.resource.PlantModel;

import java.util.ArrayList;
import java.util.List;

public class ParcelModel implements IndexedNode<ParcelModel> {
    public final int                        x;
    public final int                        y;
    public final int                        z;

    private final WeatherModule             _weatherModule;
    private ParcelEnvironment               _environment;
    private double                          _light;
    private RoomModel                       _room;
    private boolean                         _isStorage;
    private AreaModel                       _area;
    private int                             _type = 1;
    private Array<Connection<ParcelModel>>  _connections;
    public int                              tmpData;
    public double                           light;
    private boolean                         _isExterior;
    private double                          _oxygen;
    private final int                       _index;
    private ItemInfo                        _rockInfo;
    private int                             _tile;
    private DigJob                          _digJob;
    public ConsumableModel                  _consumable;
    public StructureModel                   _structure;
    public PlantModel                       _plant;
    public ItemModel                        _item;
    public List<NetworkObjectModel>         _networks;

    public ParcelModel(int index, WeatherModule weatherModule, int x, int y, int z) {
        _weatherModule = weatherModule;
        this.x = x;
        this.y = y;
        this.z = z;
        _index = index;
        _light = 0;
        _isStorage = false;
    }

    public void                     setLight(double light) { _light = light; this.light = light; }
    public void                     setRoom(RoomModel room) { _room = room; }
    public void                     setStorage(boolean isStorage) { _isStorage = isStorage; }
    public void                     setArea(AreaModel area) { _area = area; }
    public void                     setConnections(Array<Connection<ParcelModel>> connections) { _connections = connections; }
    public void                     setExterior(boolean isExterior) { _isExterior = isExterior; }
    public void                     setOxygen(double oxygen) { _oxygen = oxygen; }
    public void                     setType(int type) { _type = type; }

    public void                     setItem(ItemModel item) { _item = item; }
    public void                     setConsumable(ConsumableModel consumable) { _consumable = consumable; }
    public void                     setPlant(PlantModel plant) { _plant = plant; }
    public void                     setStructure(StructureModel structure) { _structure = structure; }
    public void                     setRockInfo(ItemInfo rockInfo) { _rockInfo = rockInfo; }
    public void                     setTile(int tile) { _tile = tile; }
    public void                     setDigJob(DigJob digJob) { _digJob = digJob; }

    public boolean                  isStorage() { return _isStorage; }
    public boolean                  isExterior() { return _room == null || _room.isExterior(); }
    public boolean                  canSupportRoof() { return (getStructure() != null && getStructure().getInfo().canSupportRoof) || (hasPlant() && getPlant().getInfo().canSupportRoof); }
    public boolean                  hasNetwork(NetworkInfo networkInfo) { return getNetworkObject(networkInfo) != null; }
    public boolean                  hasPlant() { return _plant != null; }
    public boolean                  hasItem() { return _item != null; }
    public boolean                  hasStructure() { return _structure != null; }
    public boolean                  hasRock() { return _rockInfo != null; }
    public boolean                  hasDigJob() { return _digJob != null; }

    public List<NetworkObjectModel> getNetworkObjects() { return _networks; }
    public ItemInfo                 getRockInfo() { return _rockInfo; }
    public DigJob                   getDigJob() { return _digJob; }
    public ItemModel                getItem() { return _item; }
    public StructureModel           getStructure() { return _structure; }
    public PlantModel               getPlant() { return _plant; }
    public ConsumableModel          getConsumable() { return _consumable; }
    public double                   getOxygen() { return _oxygen; }
    public double                   getLight() { return _light; }
    public RoomModel                getRoom() { return _room; }
    public AreaModel                getArea() { return _area; }
    public int                      getType() { return _type; }
    public int                      getTile() { return _tile; }
    public ParcelEnvironment        getEnvironment() { return _environment; }
    public double                   getTemperature() { return _room != null ? _room.getTemperature() : 10; }
//    public double                 getTemperature() { return _room != null ? _room.getTemperature() : _weatherModule.getTemperature(); }

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
        // Check rock
        if (_rockInfo != null) {
            return false;
        }

        // Check structure (wall, closed door)
        if (_structure != null && !_structure.getInfo().isWalkable && _structure.isComplete()) {
            return false;
        }

        // Check item
        if (_item != null && !_item.getInfo().isWalkable && _item.isComplete()) {
            return false;
        }

        // Check resource
        if (_plant != null && !_plant.getInfo().isWalkable) {
            return false;
        }

        return true;
    }

    public double getSealValue() {
        if (_structure != null) {
            return _structure.getInfo().sealing;
        }
        if (_plant != null) {
            return _plant.getInfo().sealing;
        }
        return 0;
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
        if (_item != null) {
            score += _item.getValue();
        }

        return score;
    }

}
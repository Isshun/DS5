package org.smallbox.faraway.core.game.module.world.model;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedNode;
import com.badlogic.gdx.utils.Array;
import org.smallbox.faraway.core.game.model.NetworkInfo;
import org.smallbox.faraway.core.game.module.area.model.AreaModel;
import org.smallbox.faraway.core.game.module.room.model.RoomModel;
import org.smallbox.faraway.core.game.module.world.WeatherModule;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.game.module.world.model.resource.ResourceModel;

import java.util.ArrayList;
import java.util.List;

public class ParcelModel implements IndexedNode<ParcelModel> {
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
        if (_content == null) { _content = new ParcelContent(); }
        if (_content.networks == null) { _content.networks = new ArrayList<>(); }
        _content.networks.add(network);
    }

    public void removeNetwork(NetworkObjectModel network) {
        if (_content == null) { _content = new ParcelContent(); }
        if (_content.networks == null) { _content.networks = new ArrayList<>(); }
        _content.networks.remove(network);
    }

    public List<NetworkObjectModel> getNetworkObjects() {
        return _content != null ? _content.networks : null;
    }

    public static class ParcelContent {
        public ConsumableModel          consumable;
        public StructureModel           structure;
        public ResourceModel            resource;
        public ItemModel                item;
        public List<NetworkObjectModel> networks;
    }

    public static class ParcelEnvironment {
        public double               rubble;
        public double               dirt;
        public double               blood;
        public double               snow;

        public int getScore() {
            int score = 0;

            if (this.snow > 0) {
                score += 1;
            }
            if (this.blood > 0) {
                score += -5;
            }
            if (this.dirt > 0) {
                score += -5;
            }
            if (this.rubble > 0) {
                score += -5;
            }

            return score;
        }
    }

    public final int                x;
    public final int                y;
    public final int                z;

    private final WeatherModule     _weatherModule;
    public ParcelModel[]            _neighbors;
    private ParcelContent           _content;
    private ParcelEnvironment       _environment;
    private double                  _light;
    private RoomModel               _room;
    private boolean                 _isStorage;
    private AreaModel               _area;
    private int                     _index;
    private int                     _type = 1;
    private Array<Connection<ParcelModel>>     _connections;
    public int                      tmpData;
    public double                   light;
    private boolean                 _isExterior;
    private double                  _oxygen;

    public ParcelModel(WeatherModule weatherModule, int x, int y, int z) {
        _weatherModule = weatherModule;
        this.x = x;
        this.y = y;
        this.z = z;
        _light = 0;
        _isStorage = false;
    }

    public void             setLight(double light) { _light = light; this.light = light; }
    public void             setRoom(RoomModel room) { _room = room; }
    public void             setStorage(boolean isStorage) { _isStorage = isStorage; }
    public void             setArea(AreaModel area) { _area = area; }
    public void             setIndex(int index) { _index = index; }
    public void             setConnections(Array<Connection<ParcelModel>> connections) { _connections = connections; }
    public void             setExterior(boolean isExterior) { _isExterior = isExterior; }
    public void             setOxygen(double oxygen) { _oxygen = oxygen; }
    public void             setType(int type) { _type = type; }

    public void             setItem(ItemModel item) { if (_content == null) _content = new ParcelContent(); _content.item = item; }
    public void             setConsumable(ConsumableModel consumable) { if (_content == null) _content = new ParcelContent(); _content.consumable = consumable; }
    public void             setResource(ResourceModel resource) { if (_content == null) _content = new ParcelContent(); _content.resource = resource; }
    public void             setStructure(StructureModel structure) { if (_content == null) _content = new ParcelContent(); _content.structure = structure; }

    public boolean          isStorage() { return _isStorage; }
    public boolean          isExterior() { return _room != null && _room.isExterior(); }
    public boolean          canSupportRoof() { return (getStructure() != null && getStructure().getInfo().canSupportRoof) || (getResource() != null && getResource().getInfo().canSupportRoof); }
    public boolean          hasNetwork(NetworkInfo networkInfo) { return getNetworkObject(networkInfo) != null; }

    public ParcelContent    getContent() { return _content; }
    public ItemModel        getItem() { return _content != null ? _content.item : null; }
    public StructureModel   getStructure() { return _content != null ? _content.structure : null; }
    public ResourceModel    getResource() { return _content != null ? _content.resource : null; }
    public ConsumableModel  getConsumable() { return _content != null ? _content.consumable : null; }
    public double           getOxygen() { return _oxygen; }
    public double           getLight() { return _light; }
    public RoomModel        getRoom() { return _room; }
    public AreaModel        getArea() { return _area; }
    public int              getType() { return _type; }
    public double           getTemperature() { return _room != null ? _room.getTemperature() : _weatherModule.getTemperature(); }

    public NetworkObjectModel getNetworkObject(NetworkInfo networkInfo) {
        if (_content == null || _content.networks == null) {
            return null;
        }
        for (NetworkObjectModel networkObject: _content.networks) {
            if (networkObject.getNetworkInfo() == networkInfo) {
                return networkObject;
            }
        }
        return null;
    }

    /**
     * @return ParcelEnvironment or null
     */
    public ParcelEnvironment getEnvironment() { return _environment; }

    public boolean          isWalkable() {
        // Check structure (wall, closed door)
        if (_content != null && _content.structure != null && !_content.structure.getInfo().isWalkable && _content.structure.isComplete()) {
            return false;
        }

        // Check structure (wall, closed door)
        if (_content != null && _content.item != null && !_content.item.getInfo().isWalkable && _content.item.isComplete()) {
            return false;
        }

        // Check structure (wall, closed door)
        if (_content != null && _content.resource != null && !_content.resource.getInfo().isWalkable) {
            return false;
        }

        return true;
    }

    public double getSealValue() {
        if (_content.structure != null) {
            return _content.structure.getInfo().sealing;
        }
        if (_content.resource != null) {
            return _content.resource.getInfo().sealing;
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
        if (_content != null && _content.item != null) {
            score += _content.item.getValue();
        }

        return score;
    }

}
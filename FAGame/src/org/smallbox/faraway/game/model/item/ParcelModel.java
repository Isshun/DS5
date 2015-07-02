package org.smallbox.faraway.game.model.item;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedNode;
import com.badlogic.gdx.utils.Array;
import org.smallbox.faraway.game.model.room.RoomModel;
import org.smallbox.faraway.ui.AreaModel;

public class ParcelModel implements IndexedNode<ParcelModel> {
    private ItemModel 		_item;
    private ConsumableModel _consumable;
    private StructureModel 	_structure;
    private ResourceModel 	_resource;
    private int				_x;
    private int				_y;
    private int 			_z;
    private double 			_light;
    private int 			_lightPass;
    private RoomModel 		_room;
    private boolean 		_isStorage;
    private int				_lightSource;
    private AreaModel       _area;
    private double 			_rubble;
    private double 			_dirt;
    private double 			_blood;
    private double 			_snow;
    private int 			_index;
    private int 			_type = 1;
    private Array<Connection<ParcelModel>> 	_connections;
    private double          _elevation;
    public int              tmpData;
    private int             _environment;

    public ParcelModel(int x, int y, int z) {
        _light = 0;
        _x = x;
        _y = y;
        _z = z;
        _isStorage = false;
    }

    public ParcelModel() {
        _light = 0;
        _isStorage = false;
    }

    public void 			addLight(double value) { _light += value; }

    public void 			setLight(double value) { _light = value; }
    public void 			setLightPass(int pass) { _lightPass = pass; }
    public void 			setRoom(RoomModel room) { _room = room; }
    public void 			setStorage(boolean isStorage) { _isStorage = isStorage; }
    public void				setLightSource(int value) { _lightSource = value; }
    public void 			setArea(AreaModel area) { _area = area; }
    public void 			setBlood(double blood) { _blood = blood; }
    public void 			setDirt(double dirt) { _dirt = dirt; }
    public void 			setRubble(double rubble) { _rubble = rubble; }
    public void 			setSnow(double snow) { _snow = snow; }
    public void             setX(int x) { _x = x; }
    public void             setY(int y) { _y = y; }
    public void             setZ(int z) { _z = z; }

    public ItemModel 		getItem() { return _item; }
    public StructureModel 	getStructure() { return _structure; }
    public ResourceModel 	getResource() { return _resource; }
    public double   		getOxygen() { return _room != null ? _room.getOxygen() : -1; }
    public int				getX() { return _x; }
    public int				getY() { return _y; }
    public int				getZ() { return _z; }
    public double 			getLight() { return _light; }
    public int 				getLightPass() { return _lightPass; }
    public RoomModel 		getRoom() { return _room; }
    public double 			getBlood() { return _blood; }
    public double 			getDirt() { return _dirt; }
    public double 			getRubble() { return _rubble; }
    public double 			getSnow() { return _snow; }
    public int				getLightSource() { return _lightSource; }
    public ConsumableModel 	getConsumable() { return _consumable; }
    public AreaModel getArea() { return _area; }
    public double           getElevation() { return _elevation; }
    public void             setElevation(double elevation) { _elevation = elevation; }
    public int              getType() { return _type; }
    public void             setType(int type) { _type = type; }
    public boolean			isStorage() { return _isStorage; }
    public boolean 			isEmpty() { return _resource == null && _item == null && _structure == null; }
    public boolean 			canSupportRoof() { return (_structure != null && (_structure.isWall() || _structure.isDoor())) || (_resource != null && _resource.isRock()); }
    public boolean			hasLightSource() { return _lightSource > 0; }
    public boolean 			hasSnow() { return _snow > 0; }
    public boolean 			hasRubble() { return _rubble > 0; }
    public boolean 			hasDirt() { return _dirt > 0; }
    public boolean 			hasBlood() { return _blood > 0; }
    public boolean          hasItem() { return _item != null; }
    public boolean          hasStructure() { return _structure != null; }
    public boolean          hasResource() { return _resource != null; }

    public void				setItem(ItemModel item) {
        _item = item;
        if (item != null) {
            item.setParcel(this);
            item.setX(_x);
            item.setY(_y);
        }
    }

    public void				setStructure(StructureModel structure) {
        _structure = structure;
        if (structure != null) {
            structure.setParcel(this);
            structure.setX(_x);
            structure.setY(_y);
        }
    }

    public void 			setResource(ResourceModel resource) {
        _resource = resource;
        if (resource != null) {
            resource.setParcel(this);
            resource.setX(_x);
            resource.setY(_y);
        }
    }

    public void 			setConsumable(ConsumableModel consumable) {
        _consumable = consumable;
        if (consumable != null) {
            consumable.setParcel(this);
            consumable.setX(_x);
            consumable.setY(_y);
        }
    }

    public ItemModel 		getRootItem() {
        if (_item != null && _item.getX() == _x && _item.getY() == _y) {
            return _item;
        }
        return null;
    }

    public double getSealing() {
        if (_structure != null) {
            return _structure.getSealing();
        }
        if (_resource != null) {
            return _resource.getSealing();
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

    public void setIndex(int index) {
        _index = index;
    }

    public void setConnections(Array<Connection<ParcelModel>> connections) {
        _connections = connections;
    }

    public boolean isBlocked() {
        // Check structure (wall, closed door)
        if (_structure != null && _structure.isSolid()) {
            return true;
        }

        // Check structure (wall, closed door)
        if (_resource != null && _resource.isSolid()) {
            return true;
        }

        return false;
    }

    public boolean isFree(int filter) {
        return !isBlocked();
    }

    public boolean isFree() {
        return !isBlocked();
    }

    public boolean isWalkable() {
        return !isBlocked();
    }

    public int getEnvironment() {
        if (_snow > 0) {
            _environment += 1;
        }
        if (_blood > 0) {
            _environment += -5;
        }
        if (_dirt > 0) {
            _environment += -5;
        }
        if (_rubble > 0) {
            _environment += -5;
        }
        if (_item != null) {
            _environment += _item.getValue();
        }
        return _environment;
    }
}

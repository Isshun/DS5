package org.smallbox.faraway.game.model.item;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedNode;
import com.badlogic.gdx.utils.Array;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.TemperatureManager;
import org.smallbox.faraway.game.model.area.AreaModel;
import org.smallbox.faraway.game.model.room.RoomModel;

public class ParcelModel implements IndexedNode<ParcelModel> {
    public static class ParcelContent {
        public ConsumableModel      consumable;
        public StructureModel 	    structure;
        public ResourceModel 	    resource;
        public ItemModel            item;
    }

    public class ParcelEnvironment {
        public double 			    rubble;
        public double 			    dirt;
        public double 			    blood;
        public double 			    snow;

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
            if (_content.item != null) {
                score += _content.item.getValue();
            }

            return score;
        }
    }

    public final int                x;
    public final int                y;
    public final int                z;

    public ParcelModel[]            _neighbors;
    private ParcelContent           _content;
    private ParcelEnvironment       _environment = new ParcelEnvironment();
    private double 			        _light;
    private RoomModel 		        _room;
    private boolean 		        _isStorage;
    private AreaModel               _area;
    private int 			        _index;
    private int 			        _type = 1;
    private Array<Connection<ParcelModel>> 	_connections;
    public int                      tmpData;
    public double                   light;
    private boolean                 _isExterior;
    private double                  _oxygen;

    public ParcelModel(int x, int y, int z, ParcelContent content) {
        this.x = x;
        this.y = y;
        this.z = z;
        _content = content;
        _light = 0;
        _isStorage = false;
    }

    public void 			setLight(double light) { _light = light; this.light = light; }
    public void 			setRoom(RoomModel room) { _room = room; }
    public void 			setStorage(boolean isStorage) { _isStorage = isStorage; }
    public void 			setArea(AreaModel area) { _area = area; }
    public void             setIndex(int index) { _index = index; }
    public void             setConnections(Array<Connection<ParcelModel>> connections) { _connections = connections; }
    public void             setExterior(boolean isExterior) { _isExterior = isExterior; }
    public void             setOxygen(double oxygen) { _oxygen = oxygen; }
    public void             setType(int type) { _type = type; }

    public ItemModel 		getItem() { return _content.item; }
    public StructureModel 	getStructure() { return _content.structure; }
    public ResourceModel 	getResource() { return _content.resource; }
    public double   		getOxygen() { return _oxygen; }
    public double 			getLight() { return _light; }
    public RoomModel 		getRoom() { return _room; }
    public ConsumableModel 	getConsumable() { return _content.consumable; }
    public AreaModel        getArea() { return _area; }
    public int              getType() { return _type; }
    public double           getTemperature() { return _room != null ? _room.getTemperatureInfo().temperature : ((TemperatureManager)Game.getInstance().getManager(TemperatureManager.class)).getTemperature(); }

    public boolean          isFree() { return !isBlocked(); }
    public boolean          isWalkable() { return !isBlocked(); }
    public boolean			isStorage() { return _isStorage; }
    public boolean          isExterior() { return _isExterior; }
    public boolean 			canSupportRoof() { return (_content.structure != null && (_content.structure.isWall() || _content.structure.isDoor())) || (_content.resource != null && _content.resource.isRock()); }

    public double getSealing() {
        if (_content.structure != null) {
            return _content.structure.getSealing();
        }
        if (_content.resource != null) {
            return _content.resource.getSealing();
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

    public boolean isBlocked() {
        // Check structure (wall, closed door)
        if (_content.structure != null && _content.structure.isSolid()) {
            return true;
        }

        // Check structure (wall, closed door)
        if (_content.resource != null && _content.resource.isSolid()) {
            return true;
        }

        return false;
    }

    public ParcelEnvironment getEnvironment() {
        return _environment;
    }
}

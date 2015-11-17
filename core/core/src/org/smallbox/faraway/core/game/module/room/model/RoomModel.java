package org.smallbox.faraway.core.game.module.room.model;

import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.world.model.ItemFilter;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.util.Utils;

import java.util.*;

public class RoomModel {
    int                                 _id;
    int                                 _zoneId;
    List<MapObjectModel>                _doors;
    private RoomType                    _type;
    private CharacterModel              _owner;
    private int                         _x;
    private int                         _y;
    private Color                       _color;
    private int                         _minX;
    private int                         _maxX;
    private boolean                     _isCommon;
    private Set<CharacterModel>         _occupants;
    protected Set<ParcelModel>          _parcels;
    private boolean                     _isExterior;
    private double                      _lightValue;
    private double                      _permeability;
    private List<NeighborModel>         _neighborhood;
    private List<ItemModel>             _heatItems = new ArrayList<>();
    private List<ItemModel>             _coldItems = new ArrayList<>();
    private List<ItemModel>             _oxygenItems = new ArrayList<>();
    private RoomTemperatureModel        _temperatureInfo = new RoomTemperatureModel();
    private String                      _autoName;
    private String                      _name;
    private double                      _oxygen;
    private final ParcelModel           _baseParcel;
    private int                         _floor;
    private double                      _targetOxygen;
    private double                      _targetOxygenPression;
    private int                         _pressure;

    public double getOxygen() { return _oxygen; }
    public void setOxygen(double oxygen) {
        if (_oxygen != oxygen) {
            _oxygen = oxygen;
        }
    }

    public void addOxygen(double oxygen) { _oxygen = Math.max(0, Math.min(1, _oxygen + oxygen / _parcels.size())); }
    public void addParcels(Collection<ParcelModel> parcels) { parcels.forEach(parcel -> parcel.setRoom(this)); _parcels.addAll(parcels); }
    public void setAutoName(String autoName) { _autoName = autoName; }
    public List<ItemModel> getHeatItems() { return _heatItems; }
    public List<ItemModel> getColdItems() { return _coldItems; }
    public List<ItemModel> getOxygenItems() { return _oxygenItems; }

    public boolean hasNeighbors() { return !_neighborhood.isEmpty(); }
    public ParcelModel getBaseParcel() { return _baseParcel; }
    public double getTemperature() { return _temperatureInfo.temperature; }
    public int getFloor() { return _floor; }
    public double getTargetOxygen() { return _targetOxygen; }
    public double getTargetOxygenPressure() { return _targetOxygenPression; }

    public void setTemperature(double temperature) { _temperatureInfo.temperature = temperature; }
    public void setTargetOxygen(double targetOxygen) { _targetOxygen = targetOxygen; }
    public void setTargetOxygenPression(double pression) { _targetOxygenPression = pression; }

    public void setPressure(int pressure) {
        _pressure = pressure;
    }

    public enum RoomType {
        NONE,
        QUARTER,
        SICKBAY,
        ENGINEERING,
        METTING,
        HOLODECK,
        STORAGE,
        WORLD, GARDEN
    }

    public RoomModel(RoomType type, int floor, ParcelModel baseParcel) {
        _baseParcel = baseParcel;
        init(Utils.getUUID(), type, floor);
    }

    private void init(int id, RoomType type, int floor) {
        _color = new Color((int)(Math.random() * 200), (int)(Math.random() * 200), (int)(Math.random() * 200));
        _parcels = new HashSet<>();
        _id = id;
        _floor = floor;
        _isCommon = true;
        _maxX = Integer.MIN_VALUE;
        _minX = Integer.MAX_VALUE;
        _zoneId = 0;
        _permeability = 0.8;
        _type = type;
        _doors = new ArrayList<>();
        _occupants = new HashSet<>();
        _neighborhood = new ArrayList<>();

//        _autoName = type.toString();
        _autoName = "Room " + _id;
    }

    public int                      getId() { return _id; }
    public int                      getZoneId() { return _zoneId; }
    public CharacterModel           getOwner() { return _owner; }
    public int                      getX() { return _x; }
    public int                      getY() { return _y; }
    public Color                    getColor() { return _color; }
    public int                      getMinX() { return _minX; }
    public int                      getMaxX() { return _maxX; }
    public int                      getWidth() { return _maxX - _minX + 1; }
    public RoomType                 getType() { return _type; }
    public int                      getSize() { return _parcels.size(); }
    public double                   getLight() { return _lightValue; }
    public int                      getPressure() { return _pressure; }
    public Set<CharacterModel>      getOccupants() { return _occupants; }
    public RoomTemperatureModel     getTemperatureInfo() { return _temperatureInfo; }
    public List<NeighborModel>      getNeighbors() { return _neighborhood; }
    public Set<ParcelModel>         getParcels() { return _parcels; }
    public String                   getName() { return _name != null ? _name : _autoName; }

    public void                     addParcel(ParcelModel area) { _parcels.add(area); }
    public void                     setMaxX(int x) { _maxX = x; }
    public void                     setMinX(int x) { _minX = x; }
    public void                     setCommon(boolean common) { _isCommon = common; }
    public void                     setExterior(boolean isExterior) { _isExterior = isExterior; }
    public void                     setLight(double lightValue) { _lightValue = lightValue; }
    public void                     setNeighborhoods(List<NeighborModel> neighborhood) { _neighborhood = neighborhood; }

    public boolean                  isCommon() { return _isCommon; }
    public boolean                  isType(RoomType type) { return _type == type; }
    public boolean                  isExterior() { return _isExterior; }
    public boolean                  isPrivate() { return _type == RoomType.QUARTER; }
    public boolean                  isStorage() { return _type == RoomType.STORAGE; }
    public boolean                  isEmpty() { return _parcels.isEmpty(); }


    public boolean containsParcel(int x, int y) {
        for (ParcelModel parcel: _parcels) {
            if (parcel.x == x && parcel.y == y) {
                return true;
            }
        }
        return false;
    }

    public void             setOwner(CharacterModel owner) {
        _owner = owner;
        if (owner != null) {
            _occupants.add(owner);
        }
    }

    public void             addOccupant(CharacterModel character) {
        if (character != null) {
            _occupants.add(character);
            if (_owner == null) {
                _owner = character;
            }
        }
    }

    public void             removeOccupant(CharacterModel character) {
        _occupants.remove(character);

        // Owner is removed occupant
        if (_owner == character) {
            _owner = _occupants.isEmpty() ? null : _occupants.iterator().next();
        }
    }

    public static RoomType getType(int type) {
        switch (type) {
        case 1: return RoomType.QUARTER;
        case 2: return RoomType.SICKBAY;
        case 3: return RoomType.ENGINEERING;
        case 4: return RoomType.METTING;
        case 5: return RoomType.HOLODECK;
        case 6: return RoomType.STORAGE;
        case 7: return RoomType.GARDEN;
        };
        return null;
    }

    public void update() {
        if (_owner != null && !_owner.isAlive()) {
            _owner = null;
        }
    }

    /**
     * Search and return desired item in room
     *
     * @param filter
     * @return
     */
    public MapObjectModel find(ItemFilter filter) {
        for (ParcelModel area: _parcels) {
            ItemModel item = area.getItem();
            if (item != null && item.matchFilter(filter)) {
                return item;
            }
        }
        return null;
    }

    public void removeArea(ParcelModel area) {
        _parcels.remove(area);
    }

    public void removeArea(int x, int y) {
        for (ParcelModel parcel: _parcels) {
            if (parcel.x == x && parcel.y == y) {
                removeArea(parcel);
                return;
            }
        }
    }

    public void refreshPosition() {
        _x = Integer.MAX_VALUE;
        _y = Integer.MAX_VALUE;

        for (ParcelModel parcel: _parcels) {
            if (parcel.x <= _x  && parcel.y <= _y) {
                _x = parcel.x;
                _y = parcel.y;
            }
        }
    }

    public class RoomTemperatureModel {
        public int heatPotency;
        public int coldPotency;
        public int targetHeat;
        public int targetHeatTotal;
        public int targetCold;
        public int targetColdTotal;
        public int heatPotencyLeft;
        public int coldPotencyLeft;
        public double temperatureTotal;
        public double temperature;
    }
}

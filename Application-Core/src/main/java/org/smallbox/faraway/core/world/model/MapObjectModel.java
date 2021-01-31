package org.smallbox.faraway.core.world.model;

import org.smallbox.faraway.game.world.ObjectModel;
import org.smallbox.faraway.core.game.modelInfo.GraphicInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.game.consumable.Consumable;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.world.Parcel;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

public abstract class MapObjectModel extends ObjectModel {

    private String              _name;
    private int                 _mode;
    private int                 _light;
    protected ItemInfo          _info;
    private String              _label;
    protected Parcel _parcel;
    private Set<JobModel>       _jobs;
    private GraphicInfo         _graphic;
    private int gridPosition;

    private final Collection<Consumable>      _inventory = new ConcurrentLinkedQueue<>();

    public int getGridPosition() {
        return gridPosition;
    }

    public void setGridPosition(int gridPosition) {
        this.gridPosition = gridPosition;
    }

    public void removeInventory(ItemInfo itemInfo, int quantity) {
        _inventory.forEach(consumable -> {
            if (consumable.getInfo().instanceOf(itemInfo)) {
                consumable.removeQuantity(quantity);
            }
        });
        _inventory.removeIf(consumable -> consumable.getTotalQuantity() == 0);
    }

    private Consumable getInventory(ItemInfo itemInfo) {
        for (Consumable consumable: _inventory) {
            if (consumable.getInfo().instanceOf(itemInfo)) {
                return consumable;
            }
        }
        return null;
    }

    public int getInventoryQuantity(ItemInfo itemInfo) {
        for (Consumable consumable: _inventory) {
            if (consumable.getInfo().instanceOf(itemInfo)) {
                return consumable.getTotalQuantity();
            }
        }
        return 0;
    }

    public MapObjectModel(ItemInfo info) {
        init(info);
    }

    public MapObjectModel(ItemInfo info, int id) {
        super(id);
        init(info);
    }

    public void addInventory(ItemInfo itemInfo, int quantity) {
        _inventory.add(new Consumable(itemInfo, quantity));
    }

    public void addInventory(Consumable consumable) {
        _inventory.add(consumable);
    }

    public Collection<Consumable> getInventory() {
        return _inventory;
    }

    protected void init(ItemInfo info) {

        // Init
        _name = null;
        _info = info;
        _jobs = new ConcurrentSkipListSet<>();

        // Info
        {
            _light = info.light;
            _name = info.name;
            _label = info.label != null ? info.label : info.name;
        }
    }

    // Sets
    public void                 setMode(int mode) { _mode = mode; }
    public void                 setParcel(Parcel parcel) {
        if (_parcel != parcel) {
            _parcel = parcel;
            _parcel.setItem(this);
        }
    }

    // Gets
    public int                  getId() { return _id; }
    public String               getName() { return _name; }
    public int                  getMode() { return _mode; }
    public int                  getLight() { return _light; }
    public String               getLabel() { return _label; }
    public ItemInfo             getInfo() { return _info; }
    public Parcel getParcel() { return _parcel; }
    public Collection<JobModel> getJobs() { return _jobs; }

    // Boolean
    public boolean              isConsumable() { return _info.isConsumable; }
    public boolean              isWalkable() { return _info.isWalkable; }
    public boolean              isStructure() { return _info.isStructure; }
    public boolean              isResource() { return _info.isResource; }
    public boolean              isDoor() { return _info.isDoor; }
    public boolean              isWall() { return _info.isWall; }
    public boolean              isFood() { return _info.isFood; }
    public boolean              isFactory() { return _info.isFactory; }
    public boolean              isUserItem() { return _info.isUserItem; }
    public boolean              isLight() { return _info.light > 0; }
    public boolean              isComplete() { return true; }

    public boolean matchFilter(ItemFilter filter) {
        return false;
    }

    public void addJob(JobModel job) {
        _jobs.add(job);
    }

    public void removeJob(JobModel job) {
        _jobs.remove(job);
    }

    public GraphicInfo getGraphic() {
        if (_graphic == null && _info.graphics != null) {
            _graphic = _info.graphics.get((int) (Math.random() * _info.graphics.size()));
        }
        return _graphic;
    }
}
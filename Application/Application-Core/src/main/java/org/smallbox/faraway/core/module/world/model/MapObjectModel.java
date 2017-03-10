package org.smallbox.faraway.core.module.world.model;

import org.smallbox.faraway.core.game.model.ObjectModel;
import org.smallbox.faraway.core.game.modelInfo.GraphicInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.modules.job.JobModel;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public abstract class MapObjectModel extends ObjectModel {
    private static int          _maxId;
    private int                 _id;
    private String              _name;
    private int                 _mode;
    private int                 _light;
    protected ItemInfo          _info;
    private String              _label;
    protected ParcelModel       _parcel;
    private Set<JobModel>       _jobs;
    private GraphicInfo         _graphic;

    public MapObjectModel(ItemInfo info) {
        init(info, ++_maxId);
    }

    public MapObjectModel(ItemInfo info, int id) {
        if (_maxId < id) {
            _maxId = id;
        }
        init(info, id);
    }

    protected void init(ItemInfo info, int id) {
        // Init
        _id = id;
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
    public void             setId(int id) { _id = id; }
    public void             setMode(int mode) { _mode = mode; }
    public void             setParcel(ParcelModel parcel) { _parcel = parcel; }

    // Gets
    public int              getId() { return _id; }
    public String           getName() { return _name; }
    public int              getMode() { return _mode; }
    public int              getLight() { return _light; }
    public String           getLabel() { return _label; }
    public ItemInfo         getInfo() { return _info; }
    public ParcelModel      getParcel() { return _parcel; }
    public Collection<JobModel> getJobs() { return _jobs; }

    // Boolean
    public boolean          isConsumable() { return _info.isConsumable; }
    public boolean          isWalkable() { return _info.isWalkable; }
    public boolean          isStructure() { return _info.isStructure; }
    public boolean          isResource() { return _info.isResource; }
    public boolean          isDoor() { return _info.isDoor; }
    public boolean          isWall() { return _info.isWall; }
    public boolean          isFood() { return _info.isFood; }
    public boolean          isFactory() { return _info.isFactory; }
    public boolean          isUserItem() { return _info.isUserItem; }
    public boolean          isLight() { return _info.light > 0; }

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
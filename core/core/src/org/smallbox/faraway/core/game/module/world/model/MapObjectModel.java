package org.smallbox.faraway.core.game.module.world.model;

import org.smallbox.faraway.core.GraphicInfo;
import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.model.ObjectModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;

import java.util.ArrayList;
import java.util.List;

public abstract class MapObjectModel extends ObjectModel {
    private static int          _maxId;
    private int                 _id;
    private String              _name;
    private int                 _width;
    private int                 _height;
    private int                 _matter;
    private int                 _mode;
    private int                 _light;
    protected ItemInfo          _info;
    private String              _label;
    private int                 _currentFrame;
    private int                 _nbFrame;
    private int                 _animFrame;
    private int                 _animFrameInterval;
    private boolean             _selected;
    private int                 _lastBlocked;
    protected ParcelModel       _parcel;
    private int                 _health;
    private List<JobModel>      _jobs;
    protected boolean           _needRefresh;
    private double              _progress;
//    private JobBuild            _jobBuild;
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

    private void init(ItemInfo info, int id) {
        // Init
        _health = info.health / 2;
        _lastBlocked = -1;
        _id = id;
        _name = null;
        _info = info;

        // Default values
        _width = 1;
        _height = 1;
        _matter = 1;

        // Info
        {
            _light = info.light;
            _name = info.name;
            _label = info.label != null ? info.label : info.name;
            _width = info.width;
            _height = info.height;
            _nbFrame = info.frames > 0 ? info.frames : 1;
            _animFrameInterval = info.framesInterval > 0 ? info.framesInterval : 1;
            _matter = info.cost;
        }
    }

    // Sets
    public void             setId(int id) { _id = id; }
    public void             setMode(int mode) { _mode = mode; }
    public void             setParcel(ParcelModel parcel) { _parcel = parcel; }
    public void             setSelected(boolean selected) { _selected = selected; }
    public void             setBlocked(int update) { _lastBlocked = update; }
    public void             setNeedRefresh() { _needRefresh = true; }
    public boolean          needRefresh() { return _needRefresh; }

    // Gets
    public int              getWidth() { return _width; }
    public int              getHeight() { return _height; }
    public int              getId() { return _id; }
    public String           getName() { return _name; }
    public int              getMode() { return _mode; }
    public int              getLight() { return _light; }
    public String           getLabel() { return _label; }
    public ItemInfo         getInfo() { return _info; }
    public int              getMatter() { return _matter; }
    public int              getLastBlocked() { return _lastBlocked; }
    public ParcelModel      getParcel() { return _parcel; }
    public int              getProgress() { return (int)_progress; }
    public int              getCurrentFrame() { return _currentFrame; }
    public int              getHealth() { return _health; }
    public int              getMaxHealth() { return _info.health; }
    public List<JobModel>   getJobs() { return _jobs; }

    // Boolean
    public boolean          isConsumable() { return _info.isConsumable; }
    public boolean          isSelected() { return _selected; }
    public boolean          isSolid() { return _progress >= _info.cost && !_info.isWalkable; }
    public boolean          isWalkable() { return _info.isWalkable; }
    public boolean          isSleepingItem() { return _info.isBed; }
    public boolean          isStructure() { return _info.isStructure; }
    public boolean          isResource() { return _info.isResource; }
    public boolean          isDoor() { return getName().equals("base.door"); }
    public boolean          isWall() { return getName().equals("base.wall") || getName().equals("base.window"); }
    public boolean          isFood() { return _info.isFood; }
    public boolean          isFactory() { return _info.isFactory; }
    public boolean          isUserItem() { return _info.isUserItem; }
    public boolean          isDestroy() { return _health <= 0; }
    public boolean          isCloseRoom() { return _info.isCloseRoom; }
    public boolean          isLight() { return _info.light > 0; }
    public boolean          matchPosition(int x, int y) { return _parcel != null && _parcel.x == x && _parcel.y == y; }
    public boolean          hasJobs() { return _jobs != null && !_jobs.isEmpty(); }

    public ItemModel use(CharacterModel character, int durationLeft) {
        // Add buffEffect on characters
        character.getNeeds().use(this, _info.actions.get(0));

        // Play animation
        if (_animFrame++ % _animFrameInterval == 0) {
            _currentFrame = (_currentFrame + 1) % _nbFrame;
        }

        return null;
    }

    public boolean matchFilter(ItemFilter filter) {
        return false;
    }

    public void addJob(JobModel job) {
        _needRefresh = true;

        if (_jobs == null) {
            _jobs = new ArrayList<>();
        }

        _jobs.add(job);
    }

    public void removeJob(JobModel job) {
        _needRefresh = true;

        if (_jobs != null) {
            _jobs.remove(job);
        }
    }

    public int addComponent(ConsumableModel consumable) {
        throw new RuntimeException("add component on MapObjectItem is not allowed");
    }

    public List<BuildableMapObject.ComponentModel>     getComponents() {
        throw new RuntimeException("get component on MapObjectItem is not allowed");
    }

    public void setHealth(int health) {
        _health = health;
        _needRefresh = true;
    }

    public void addHealth(int health) {
        _health = _health + health;
        _needRefresh = true;
    }

    public void addProgress(double value) {
        _progress += value;
        _needRefresh = true;
    }

    public GraphicInfo getGraphic() {
        if (_graphic == null && _info.graphics != null) {
            _graphic = _info.graphics.get((int) (Math.random() * _info.graphics.size()));
        }
        return _graphic;
    }
}
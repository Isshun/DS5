package org.smallbox.faraway.core.game.model.item;

import org.smallbox.faraway.core.GraphicInfo;
import org.smallbox.faraway.core.game.model.ObjectModel;
import org.smallbox.faraway.core.game.model.character.base.CharacterModel;
import org.smallbox.faraway.core.game.model.job.abs.JobModel;

import java.util.ArrayList;
import java.util.List;

public abstract class MapObjectModel extends ObjectModel {
    private static int             _maxId;
    private int                    _id;
    protected int                    _x;
    protected int                    _y;
    private CharacterModel      _owner;
    private String                 _name;
    private int                 _width;
    private int                 _height;
    private int                 _matter;
    private int                 _mode;
    private int                    _light;
    protected ItemInfo            _info;
    private String                 _label;
    private int                    _currentFrame;
    private int                    _nbFrame;
    private int                 _animFrame;
    private int                 _animFrameInterval;
    private boolean             _selected;
    private int                 _lastBlocked;
    protected ParcelModel       _parcel;
    private int                 _health;
    private List<JobModel>  _jobs;
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
        _health = info.maxHealth;
        _lastBlocked = -1;
        _owner = null;
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

    public void    setOwner(CharacterModel character) {
        _owner = character;
    }

    // Sets
    public void             setId(int id) { _id = id; }
    public void                setPosition(int x, int y) { _x = x; _y = y; }
    public void             setMode(int mode) { _mode = mode; }
    public void             setParcel(ParcelModel parcel) { _parcel = parcel; }
    public void             setX(int x) { _x = x; }
    public void             setY(int y) { _y = y; }
    public void             setSelected(boolean selected) { _selected = selected; }
    public void             setBlocked(int update) { _lastBlocked = update; }
    public void             setNeedRefresh() { _needRefresh = true; }
    public boolean          needRefresh() { return _needRefresh; }
//    public void             setJobBuild(JobBuild job) { _jobBuild = job; }

    // Gets
    public CharacterModel   getOwner() { return _owner; }
    public int                getWidth() { return _width; }
    public int                getHeight() { return _height; }
    public int                getX() { return _x; }
    public int                getY() { return _y; }
    public int                getId() { return _id; }
    public String            getName() { return _name; }
    public int                 getMode() { return _mode; }
    public int                 getLight() { return _light; }
    public String             getLabel() { return _label; }
    public ItemInfo         getInfo() { return _info; }
    public int                 getMatter() { return _matter; }
    public int                 getLastBlocked() { return _lastBlocked; }
    public ParcelModel      getParcel() { return _parcel; }
    public int              getProgress() { return (int)_progress; }
    public double           getSealing() { return _info.sealing; }
    public int              getCurrentFrame() { return _currentFrame; }
    public int              getHealth() { return _health; }
    public int              getMaxHealth() { return _info.maxHealth; }
    public List<JobModel> getJobs() { return _jobs; }
//    public JobBuild         getJobBuild() { return _jobBuild; }

    // Boolean
    public boolean          isConsumable() { return _info.isConsumable; }
    public boolean          isSelected() { return _selected; }
    public boolean          isSolid() { return _progress >= _info.cost && !_info.isWalkable; }
    public boolean          isWalkable() { return _progress < _info.cost || _info.isWalkable; }
    public boolean            isComplete() { return false; }
    public boolean            isSleepingItem() { return _info.isBed; }
    public boolean            isStructure() { return _info.isStructure; }
    public boolean          isResource() { return _info.isResource; }
    public boolean             isDoor() { return getName().equals("base.door"); }
    public boolean             isWall() { return getName().equals("base.wall") || getName().equals("base.window"); }
    public boolean             isFood() { return _info.isFood; }
    public boolean             isFactory() { return _info.isFactory; }
    public boolean             isUserItem() { return _info.isUserItem; }
    public boolean          isDestroy() { return _health <= 0; }
    public boolean          isDump() { return _progress <= 0; }
    public boolean          isCloseRoom() { return _info.isCloseRoom; }
    public boolean             isLight() { return _info.light > 0; }
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
        // Filter looking for item
        if (filter.lookingForItem) {

            // Filter on item
            if (filter.itemNeeded == _info) {
                filter.itemMatched = _info;
                return true;
            }

            if (_info.actions != null) {
                for (ItemInfo.ItemInfoAction action: _info.actions) {
                    if (_info.matchFilter(action.effects, filter)) {
                        filter.itemMatched = _info;
                        return true;
                    }
                }
            }
        }

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

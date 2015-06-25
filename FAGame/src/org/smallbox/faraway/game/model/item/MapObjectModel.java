package org.smallbox.faraway.game.model.item;

import org.smallbox.faraway.Strings;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public abstract class MapObjectModel {
    private static int 		_maxId;

    private int			    _x;
    private int			    _y;
    private int			    _id;
    private boolean 	    _isSolid;
    private int 		    _zoneIdRequired;
    private CharacterModel  _owner;
    private String 		    _name;
    private int 		    _width;
    private int 		    _height;
    private int 		    _matter;
    private int 		    _power;
    private int 		    _powerSupply;
    private int 		    _mode;
    private int			    _light;
    private int 		    _nbMode;
    protected ItemInfo	    _info;
    private boolean		    _isWorking;
    private String 		    _label;
    private boolean 	    _isToy;
    public int 			    actionRemain;
    private ArrayList<ItemSlot> _slots;
    private int 		    _nbTotalUsed;
    private int 		    _nbFreeSlot;
    private int 		    _nbSlot;
    private int			    _currentFrame;
    private int			    _nbFrame;
    private int 		    _animFrame;
    private int 		    _animFrameInterval;
    private boolean 	    _selected;
    private int 		    _lastBlocked;
    private ParcelModel _area;
    private int             _health;
    private List<BaseJobModel>  _jobs;
    protected boolean       _needRefresh;
    private double          _progress;

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
        _isSolid = false;
        _zoneIdRequired = 0;
        _owner = null;
        _id = id;
        _name = null;
        _nbFreeSlot = -1;

        // TODO: modes
//		_nbMode = 1;
//		
//		if (t == Type.STRUCTURE_DOOR) {
//			_nbMode = 3;
//		}

        // Default values
        _width = 1;
        _height = 1;
        _matter = 1;
        _power = 0;
        _powerSupply = 0;
        _isSolid = false;

        _info = info;

        // Info
        {
            _light = info.light;
            _name = info.name;
            _label = info.label != null ? info.label : info.name;
            _width = info.width;
            _height = info.height;
            _nbFrame = info.frames > 0 ? info.frames : 1;
            _animFrameInterval = info.framesInterval > 0 ? info.framesInterval : 1;
            _isToy = info.isToy;

//			if (info.actions != null && info.actions.effects != null) {
//				ItemInfoEffects effects = info.actions.effects;
//				if (effects.happiness > 0 && effects.happiness > effects.energy && effects.happiness > effects.health && effects.happiness > effects.food && effects.happiness > effects.drink) {
//					_isToy = true;
//				}
//				if (effects.relation > 0 && effects.relation > effects.energy && effects.relation > effects.health && effects.relation > effects.food && effects.relation > effects.drink) {
//					_isToy = true;
//				}
//			}

            // TODO: zone
            //_zoneIdRequired = info.zone;
//            if (info.cost != null) {
                _matter = info.cost;
//                _power = info.cost.power;
//            }

            _isSolid = !info.isWalkable;
        }

        initSlot(info);
    }

    private void initSlot(ItemInfo info) {
        _nbFreeSlot = -1;

        if (info.actions != null) {
            _slots = new ArrayList<>();

            // Get slot from item infos
            if (info.slots != null) {
                _slots.addAll(info.slots.stream().map(slot -> new ItemSlot(this, slot[0], slot[1])).collect(Collectors.toList()));
            }

            // Unique slot at 0x0
            else {
                _slots.add(new ItemSlot(this, 0, 0));
            }

            _nbFreeSlot = _nbSlot = _slots.size();
        }
    }

    public ItemSlot takeSlot(BaseJobModel job) {
        if (_nbFreeSlot != -1) {
            for (ItemSlot slot : _slots) {
                if (slot.isFree()) {
                    slot.take(job);
                    _nbFreeSlot--;
                    _nbTotalUsed++;
                    return slot;
                }
            }
        }
        return null;
    }

    public void releaseSlot(ItemSlot slot) {
        if (slot.isFree() == false) {
            slot.free();
        }
        _nbFreeSlot = 0;
        for (ItemSlot s: _slots) {
            if (s.isFree()) {
                _nbFreeSlot++;
            }
        }
    }

    public void	setOwner(CharacterModel character) {
        _owner = character;
    }

    // Sets
    public void				setPosition(int x, int y) { _x = x; _y = y; }
    public void 			setPowerSupply(int i) { _powerSupply = i; }
    public void 			setSolid(boolean isSolid) { _isSolid = isSolid; }
    public void 			setMode(int mode) { _mode = mode; }
    public void 			setWorking(boolean working) { _isWorking = working; }
    public void             setParcel(ParcelModel area) { _area = area; }
    public void 			setX(int x) { _x = x; }
    public void 			setY(int y) { _y = y; }

    // Gets
    public CharacterModel   getOwner() { return _owner; }
    public int				getWidth() { return _width; }
    public int				getHeight() { return _height; }
    public int				getX() { return _x; }
    public int				getY() { return _y; }
    public int				getZoneIdRequired() { return _zoneIdRequired; }
    public int				getId() { return _id; }
    public String			getName() { return _name; }
    public int 				getPower() { return _power; }
    public int 				getMode() { return _mode; }
    public int 				getLight() { return _light; }
    public String 			getLabel() { return _label; }
    public List<ItemSlot> 	getSlots() { return _slots; }
    public ItemInfo 		getInfo() { return _info; }
    public int 				getNbFreeSlots() { return _nbFreeSlot; }
    public int 				getNbSlots() { return _nbSlot; }
    public int 				getTotalUse() { return _nbTotalUsed; }
    public int 				getMatter() { return _matter; }
    public int 				getLastBlocked() { return _lastBlocked; }
    public ParcelModel      getParcel() { return _area; }

    // Boolean
    public boolean			isSolid() { return _isSolid; }
    public boolean			isWorking() { return _isWorking; }
    // TODO
//    public boolean			isComplete() { return _progress >= _info.cost; }
    public boolean			isComplete() { return true; }
    public boolean			isSupply() { return _power == _powerSupply; }
    public boolean			isSleepingItem() { return _info.isSleeping; }
    public boolean			isStructure() { return _info.isStructure; }
    public boolean          isResource() { return _info.isResource; }
    public boolean			isWalkable() { return !_info.isWalkable; }
    public boolean 			isFloor() { return getName().equals("base.floor") || getName().equals("base.rock") || getName().equals("base.ground"); }
    public boolean 			isDoor() { return getName().equals("base.door"); }
    public boolean 			isWall() { return getName().equals("base.wall") || getName().equals("base.window"); }
    public boolean 			isWindow() { return getName().equals("base.window"); }
    public boolean 			isToy() { return _isToy; }
    //	public boolean 			isStorage() { return _info.isStorage; }
    public boolean 			isFood() { return _info.isFood; }
    public boolean 			isFactory() { return _info.isFactory; }
    public boolean 			hasFreeSlot() { return _nbFreeSlot == -1 || _nbFreeSlot > 0; }
    public boolean 			isUserItem() { return _info.isUserItem; }

    public static boolean isResource(ItemInfo info) {
        return info.isResource;
    }

    public void nextMode() {
        _mode = _nbMode > 0 ? (_mode + 1) % _nbMode : 0;
    }

    public ItemModel use(CharacterModel character, int durationLeft) {
        // Add effect on character
        character.getNeeds().use(this, _info.actions.get(0));

        // Play animation
        if (_animFrame++ % _animFrameInterval == 0) {
            _currentFrame = (_currentFrame + 1) % _nbFrame;
        }

        return null;
    }

    public String getLabelType() {
        if (_info.isConsumable) { return Strings.LB_ITEM_CONSOMABLE; }
        if (_info.isStructure) { return Strings.LB_ITEM_STRUCTURE; }
        if (_info.isResource) { return Strings.LB_ITEM_RESSOURCE; }
        return Strings.LB_ITEM_USER;
    }

    public boolean matchFilter(ItemFilter filter) {
        // Filter need free slots but item is busy
        if (filter.needFreeSlot && hasFreeSlot() == false) {
            return false;
        }

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

    public String getLabelCategory() {
        if (_info.isUserItem) { return "item"; }
        if (_info.isConsumable) { return "consomable"; }
        if (_info.isStructure) { return "structure"; }
        if (_info.isResource) { return "resource"; }
        if (_info.isFood) { return "food"; }
        return null;
    }

    public boolean isDrink() {
        return _info.isDrink;
    }

    public boolean isConsumable() {
        return _info.isConsumable;
    }

    public boolean isGarbage() {
        return false;
    }

    public int getCurrentFrame() {
        return _currentFrame;
    }

    public boolean isUsable() {
        return _info.actions != null;
    }

    public void setSelected(boolean selected) {
        _selected = selected;
    }

    public boolean isSelected() {
        return _selected;
    }

    public boolean isType(ItemInfo info) {
        return _info == info;
    }

    public void setBlocked(int update) {
        _lastBlocked = update;
    }

    public int getHealth() {
        return _health;
    }

    public int getMaxHealth() {
        return _info.maxHealth;
    }

    public void addJob(BaseJobModel job) {
        _needRefresh = true;

        if (_jobs == null) {
            _jobs = new ArrayList<>();
        }

        _jobs.add(job);
    }

    public boolean hasJobs() {
        return _jobs != null && !_jobs.isEmpty();
    }

    public List<BaseJobModel> getJobs() {
        return _jobs;
    }

    public boolean needRefresh() {
        return _needRefresh;
    }

    public void setNeedRefresh() {
        _needRefresh = true;
    }

    public void removeJob(BaseJobModel job) {
        _needRefresh = true;

        if (_jobs != null) {
            _jobs.remove(job);
        }
    }

    public void setId(int id) {
        _id = id;
    }

    public int getQuantity() { return 1; }

    public void addComponent(ConsumableModel consumable) {
        throw new RuntimeException("add inventory on MapObjectItem is not allowed");
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

    public int getProgress() {
        return (int)_progress;
    }

    public boolean isDestroy() {
        return _health <= 0;
    }

    public boolean isDump() {
        return _progress <= 0;
    }

    public boolean matchPosition(int x, int y) {
        return _area != null && _area.getX() == x && _area.getY() == y;
    }

    public double getSealing() {
        return _info.sealing;
    }

    public boolean isCloseRoom() {
        return _info.isCloseRoom;
    }
}

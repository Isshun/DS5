package alone.in.deepspace.model;

import java.util.ArrayList;
import java.util.List;

import alone.in.deepspace.Strings;
import alone.in.deepspace.manager.ItemFilter;
import alone.in.deepspace.manager.ItemSlot;
import alone.in.deepspace.model.ItemInfo.ItemInfoEffects;
import alone.in.deepspace.model.ItemInfo.ItemInfoSlot;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.job.Job;


public class BaseItem {

//	public enum Type {
//		NONE,
//		STRUCTURE_START,
//		STRUCTURE_ROOM,
//		STRUCTURE_WALL,
//		STRUCTURE_HULL,
//		STRUCTURE_FLOOR,
//		STRUCTURE_WINDOW,
//		STRUCTURE_DOOR,
//		STRUCTURE_GREENHOUSE,
//		STRUCTURE_STOP,
//		ITEM_START,
//		SICKBAY_BIOBED,
//		SICKBAY_LAB,
//		SICKBAY_EMERGENCY_SHELTERS,
//		ENGINE_CONTROL_CENTER,
//		ENGINE_REACTION_CHAMBER,
//		HOLODECK_GRID,
//		ARBORETUM_TREE_1,
//		ARBORETUM_TREE_2,
//		ARBORETUM_TREE_3,
//		ARBORETUM_TREE_4,
//		ARBORETUM_TREE_5,
//		ARBORETUM_TREE_6,
//		ARBORETUM_TREE_7,
//		ARBORETUM_TREE_8,
//		ARBORETUM_TREE_9,
//		GYMNASIUM_STUFF_1,
//		GYMNASIUM_STUFF_2,
//		GYMNASIUM_STUFF_3,
//		GYMNASIUM_STUFF_4,
//		GYMNASIUM_STUFF_5,
//		SCHOOL_DESK,
//		BAR_PUB,
//		AMPHITHEATER_STAGE,
//		QUARTER_BED,
//		QUARTER_DESK,
//		QUARTER_CHAIR,
//		QUARTER_WARDROBE,
//		QUARTER_CHEST,
//		QUARTER_BEDSIDE_TABLE,
//		ENVIRONMENT_O2_RECYCLER,
//		ENVIRONMENT_TEMPERATURE_REGULATION,
//		TRANSPORTATION_SHUTTLECRAFT,
//		TRANSPORTATION_CARGO,
//		TRANSPORTATION_CONTAINER,
//		TRANSPORTATION_TRANSPORTER_SYSTEMS,
//		TACTICAL_PHOTON_TORPEDO,
//		TACTICAL_PHASER,
//		TACTICAL_SHIELD_GRID,
//		TACTICAL_CLOAKING_DEVICE,
//		SCIENCE_HYDROPONICS,
//		RES_1,
//		SCIENCE_ZYGOTE,
//		SCIENCE_ROBOT_MAKER,
//		SPECIAL_STORAGE,
//		ITEM_STOP,
//	};

	private int			_x;
	private int			_y;
	private int			_id;
	private boolean 	_isSolid;
	private int 		_matterSupply;
	private int 		_zoneIdRequired;
	private Character 	_owner;
	private String 		_name;
	private int 		_width;
	private int 		_height;
	private int 		_matter;
	private int 		_power;
	private int 		_powerSupply;
	private int 		_mode;
	private int			_light;
	private int 		_nbMode;
	private int 		_maxId;
	protected ItemInfo	_info;
	private boolean		_isWorking;
	private String 		_label;
	private boolean 	_isToy;
	public int 			actionRemain;
	private ArrayList<ItemSlot> _slots;
	private int 		_nbTotalUsed;
	private int 		_nbFreeSlot;
	private int 		_nbSlot;
	private int			_currentFrame;
	private int			_nbFrame;
	private int 		_animFrame;
	private int 		_animFrameInterval;
	
	public BaseItem(ItemInfo info) {
		init(info, ++_maxId);
	}

	public BaseItem(ItemInfo info, int id) {
		if (_maxId < id) {
			_maxId = id;
		}
		init(info, id);
	}

	private void init(ItemInfo info, int id) {
		// Init
		_isSolid = false;
		_matterSupply = 0;
		_zoneIdRequired = 0;
		_owner = null;
		_id = id;
		_name = null;

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
			_label = info.label;
			_width = info.width;
			_height = info.height;
			_nbFrame = info.frames > 0 ? info.frames : 1;
			_animFrameInterval = info.framesInterval > 0 ? info.framesInterval : 1;
			
			if (info.onAction != null && info.onAction.effects != null) {
				ItemInfoEffects effects = info.onAction.effects;
				if (effects.hapiness > 0 && effects.hapiness > effects.energy && effects.hapiness > effects.health && effects.hapiness > effects.food && effects.hapiness > effects.drink) {
					_isToy = true;
				}
				if (effects.relation > 0 && effects.relation > effects.energy && effects.relation > effects.health && effects.relation > effects.food && effects.relation > effects.drink) {
					_isToy = true;
				}
			}
			
			// TODO: zone
			//_zoneIdRequired = info.zone;
			if (info.cost != null) {
				_matter = info.cost.matter;
				_power = info.cost.power;
			}
			
			_isSolid = !info.isWalkable;
		}
		
		initSlot(info);
	}
	
	private void initSlot(ItemInfo info) {
		if (info.onAction != null) {
			_slots = new ArrayList<ItemSlot>();

			// Get slot from item infos
			if (info.onAction.slots != null) {
				for (ItemInfoSlot s: info.onAction.slots) {
					_slots.add(new ItemSlot(this, s.x, s.y));
				}
			}
			
			// Unique slot at 0x0
			else {
				_slots.add(new ItemSlot(this, 0, 0));
			}
			
			_nbFreeSlot = _nbSlot = _slots.size();
		}
	}

	public ItemSlot takeSlot(Job job) {
		for (ItemSlot slot: _slots) {
			if (slot.isFree()) {
				slot.take(job);
				_nbFreeSlot--;
				_nbTotalUsed++;
				return slot;
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

	public boolean hasFreeSlot() {
		return _nbFreeSlot > 0;
	}

	public void	setOwner(Character character) {
		_owner = character;
	}

	public int			gatherMatter(int maxValue) {
		int value = Math.min(maxValue, _matterSupply);
		_matterSupply -= value;
		return value;
	}

	void					addMatter(int value) { _matterSupply += _matter; }

	// Sets
	public void				setPosition(int x, int y) { _x = x; _y = y; }
	public void 			setMatterSupply(int matterSupply) { _matterSupply = matterSupply; }
	public void 			setPowerSupply(int i) { _powerSupply = i; }
	public void 			setSolid(boolean isSolid) { _isSolid = isSolid; }
	public void 			setMode(int mode) { _mode = mode; }
	public void 			setWorking(boolean working) { _isWorking = working; }

	// Gets
	public int				getMatterSupply() { return _matterSupply; }
	public Character		getOwner() { return _owner; }
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

	// Boolean
	public boolean			isSolid() { return _isSolid; }
	public boolean			isWorking() { return _isWorking; }
	public boolean			isComplete() { return _matterSupply >= _matter; }
	public boolean			isSupply() { return _power == _powerSupply; }
	public boolean			isFree() { return _nbFreeSlot > 0; }
	public boolean			isSleepingItem() { return "base.bed".equals(_name) || "base.chair".equals(_name); }
	public boolean			isStructure() { return _info.isStructure; }
	public boolean			isRessource() { return _info.isResource; }
	public boolean			isWalkable() { return !_info.isWalkable; }
	public boolean 			isFloor() { return getName().equals("base.floor") || getName().equals("base.rock") || getName().equals("base.ground"); }
	public boolean 			isDoor() { return getName().equals("base.door"); }
	public boolean 			isWall() { return getName().equals("base.wall") || getName().equals("base.window"); }
	public boolean 			isWindow() { return getName().equals("base.window"); }
	public boolean 			isToy() { return _isToy; }
	public boolean 			isStorage() { return _info.isStorage; }
	public boolean 			isFood() { return _info.isFood; }
	public boolean 			isDispenser() { return _info.isDispenser; }

	public static boolean isUserItem(ItemInfo info) {
		return !info.isStructure && !info.isResource;
	}

	public static boolean isResource(ItemInfo info) {
		return info.isResource;
	}

	public void nextMode() {
		_mode = _nbMode > 0 ? (_mode + 1) % _nbMode : 0;
	}

	public UserItem use(Character character, int durationLeft) {
		// Add effect on character
		character.getNeeds().use(this, _info.onAction, durationLeft);
		
		// Play animation
		if (_animFrame++ % _animFrameInterval == 0) {
			_currentFrame = (_currentFrame + 1) % _nbFrame;
		}
		
		return null;
	}

	public String getLabelType() {
		if (_info.isConsomable) { return Strings.LB_ITEM_CONSOMABLE; }
		if (_info.isStructure) { return Strings.LB_ITEM_STRUCTURE; }
		if (_info.isResource) { return Strings.LB_ITEM_RESSOURCE; }
		return Strings.LB_ITEM_USER;
	}

	public boolean matchFilter(ItemFilter filter) {
		
		// Item immediate effect
		if (filter.isImmediate) {

			// Filter on item
			if (filter.neededItem == _info) {
				filter.matchingItem = _info;
				return true;
			}

			if (_info.onAction != null && _info.matchFilter(_info.onAction.effects, filter)) {
				filter.matchingItem = _info;
				return true;
			}
		}
		
		return false;
	}

	public String getLabelCategory() {
		if (_info.isUserItem) { return "item"; }
		if (_info.isConsomable) { return "consomable"; }
		if (_info.isStructure) { return "structure"; }
		if (_info.isResource) { return "resource"; }
		if (_info.isFood) { return "food"; }
		return null;
	}

	public boolean isDrink() {
		return _info.isDrink;
	}

	public boolean isConsomable() {
		return _info.isConsomable;
	}

	public boolean isGarbage() {
		return false;
	}

	public int getCurrentFrame() {
		return _currentFrame;
	}

	public boolean isFactory() {
		return _info.onAction != null && _info.onAction.itemsProduce != null;
	}

	public boolean isUsable() {
		return _info.onAction != null;
	}
}

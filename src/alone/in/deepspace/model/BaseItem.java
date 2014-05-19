package alone.in.deepspace.model;


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
	private ItemInfo	_info;
	private boolean		_isWorking;

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
			_width = info.width;
			_height = info.height;
			
			// TODO: zone
			//_zoneIdRequired = info.zone;
			if (info.cost != null) {
				_matter = info.cost.matter;
				_power = info.cost.power;
			}
			
			_isSolid = !info.isWalkable;
		}
	}

	public void	setOwner(Character character) {
		_owner = character;
	}

	public int			gatherMatter(int maxValue) {
		int value = Math.min(maxValue, _matterSupply);
		_matterSupply -= value;
		return value;
	}

	void				addMatter(int value) { _matterSupply += _matter; }

	// Sets
	public void			setPosition(int x, int y) { _x = x; _y = y; }
	public void 		setMatterSupply(int matterSupply) { _matterSupply = matterSupply; }
	public void 		setPowerSupply(int i) { _powerSupply = i; }
	public void 		setSolid(boolean isSolid) { _isSolid = isSolid; }
	public void 		setMode(int mode) { _mode = mode; }
	public void 		setWorking(boolean working) { _isWorking = working; }

	// Gets
	public int			getMatterSupply() { return _matterSupply; }
	public Character	getOwner() { return _owner; }
	public int			getWidth() { return _width; }
	public int			getHeight() { return _height; }
	public int			getX() { return _x; }
	public int			getY() { return _y; }
	public int			getZoneIdRequired() { return _zoneIdRequired; }
	public int			getId() { return _id; }
	public String		getName() { return _name; }
	public int 			getPower() { return _power; }
	public int 			getMode() { return _mode; }
	public int 			getLight() { return _light; }

	public ItemInfo 	getInfo() { return _info; }

	// Boolean
	public boolean		isSolid() { return _isSolid; }
	public boolean		isWorking() { return _isWorking; }

	public boolean		isComplete() { return _matterSupply >= _matter; }
	public boolean		isSupply() { return _power == _powerSupply; }
	public boolean		isFree() { return _owner == null; }
	
	// TODO
	public boolean		isSleepingItem() { return "base.bed".equals(_name) || "base.chair".equals(_name); }
	public boolean		isStructure() { return _info.isStructure; }
	public boolean		isRessource() { return _info.isRessource; }
	public boolean		isWalkable() { return !_info.isWalkable; }
	
	public boolean isStorage() { return _info.storage > 0; }

	public int getMatter() {
		return _matter;
	}

	public static boolean isUserItem(ItemInfo info) {
		return !info.isStructure && !info.isRessource;
	}

	public static boolean isResource(ItemInfo info) {
		return info.isRessource;
	}

	public void nextMode() {
		_mode = _nbMode > 0 ? (_mode + 1) % _nbMode : 0;
	}

	// TODO: item
	public boolean isFloor() { return getName().equals("base.floor") || getName().equals("base.rock") || getName().equals("base.ground"); }

	// TODO: item
	public boolean isDoor() { return getName().equals("base.door"); }

	// TODO
	public boolean isWall() { return getName().equals("base.wall") || getName().equals("base.window"); }

	public boolean isWindow() { return getName().equals("base.window"); }


}

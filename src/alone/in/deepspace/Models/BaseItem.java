package alone.in.DeepSpace.Models;

import alone.in.DeepSpace.Models.BaseItem.Type;


public class BaseItem {
	
	private static ItemInfo[] _itemInfo  = {
		new ItemInfo(Type.STRUCTURE_ROOM, "ROOM", true, 1, 1, 1, 0, 0),
		new ItemInfo(Type.STRUCTURE_HULL,							"HULL", true,	1, 1, 1, 0, 0),
		new ItemInfo(Type.STRUCTURE_WALL,							"WALL", true,	1, 1, 1, 0, 0),
		new ItemInfo(Type.STRUCTURE_FLOOR,							"FLOOR", false,	1, 1, 1, 0, 0),
		new ItemInfo(Type.STRUCTURE_DOOR,							"DOOR", false,	1, 1, 1, 0, 0),
		new ItemInfo(Type.STRUCTURE_WINDOW,							"WINDOW", true,	1, 1, 1, 0, 0),
//		new ItemInfo(Type.TRANSPORTATION_TRANSPORTER_SYSTEMS,		"SYSTEMS", false,	1, 1, 10, 10, UserInterfaceMenu.CODE_ZONE_OPERATION),
//		new ItemInfo(Type.QUARTER_BED,								"BED", false,	2, 2, 4, 0, UserInterfaceMenu.CODE_ZONE_QUARTER),
//		new ItemInfo(Type.QUARTER_CHAIR,								"CHAIR", false,	1, 1, 2, 0, UserInterfaceMenu.CODE_ZONE_QUARTER),
//		new ItemInfo(Type.HOLODECK_GRID,								"GRID", false,	1, 1, 6, 6, UserInterfaceMenu.CODE_ZONE_HOLODECK),
//		new ItemInfo(Type.BAR_PUB,									"PUB", false,	1, 1, 5, 0, UserInterfaceMenu.CODE_ZONE_BAR),
//		new ItemInfo(Type.ENGINE_CONTROL_CENTER,						"CENTER", false,	3, 2, 10, 5, UserInterfaceMenu.CODE_ZONE_ENGINE),
//		new ItemInfo(Type.ENGINE_REACTION_CHAMBER,					"CHAMBER", false,	2, 3, 50, -200, UserInterfaceMenu.CODE_ZONE_ENGINE),
//		new ItemInfo(Type.SICKBAY_BIOBED,							"BIOBED", false,	1, 2, 10, 10, UserInterfaceMenu.CODE_ZONE_SICKBAY),
		new ItemInfo(Type.ARBORETUM_TREE_1,							"1", false,	1, 2, 2, 0, 0),
		new ItemInfo(Type.ARBORETUM_TREE_2,							"2", false,	1, 2, 2, 0, 0),
		new ItemInfo(Type.ARBORETUM_TREE_3,							"3", false,	1, 2, 2, 0, 0),
		new ItemInfo(Type.ARBORETUM_TREE_4,							"4", false,	1, 2, 2, 0, 0),
		new ItemInfo(Type.ARBORETUM_TREE_5,							"5", false,	1, 1, 1, 0, 0),
		new ItemInfo(Type.ARBORETUM_TREE_6,							"6", false,	1, 1, 1, 0, 0),
		new ItemInfo(Type.ARBORETUM_TREE_7,							"7", false,	1, 1, 1, 0, 0),
		new ItemInfo(Type.ARBORETUM_TREE_8,							"8", false,	1, 1, 1, 0, 0),
		new ItemInfo(Type.ARBORETUM_TREE_9,							"9", false,	1, 1, 1, 0, 0),
//		new ItemInfo(Type.ENVIRONMENT_O2_RECYCLER,					"RECYCLER", false,	1, 2, 10, 10, UserInterfaceMenu.CODE_ZONE_OPERATION),
		new ItemInfo(Type.NONE,										"NONE", false,	0, 0, 0, 0, 0)
	};
	
	public enum Type {
		NONE,
	    STRUCTURE_START,
	    STRUCTURE_ROOM,
		STRUCTURE_WALL,
		STRUCTURE_HULL,
		STRUCTURE_FLOOR,
		STRUCTURE_WINDOW,
		STRUCTURE_DOOR,
	    STRUCTURE_STOP,
	    ITEM_START,
		SICKBAY_BIOBED,
		SICKBAY_LAB,
		SICKBAY_EMERGENCY_SHELTERS,
		ENGINE_CONTROL_CENTER,
		ENGINE_REACTION_CHAMBER,
		HOLODECK_GRID,
		ARBORETUM_TREE_1,
		ARBORETUM_TREE_2,
		ARBORETUM_TREE_3,
		ARBORETUM_TREE_4,
		ARBORETUM_TREE_5,
		ARBORETUM_TREE_6,
		ARBORETUM_TREE_7,
		ARBORETUM_TREE_8,
		ARBORETUM_TREE_9,
		GYMNASIUM_STUFF_1,
		GYMNASIUM_STUFF_2,
		GYMNASIUM_STUFF_3,
		GYMNASIUM_STUFF_4,
		GYMNASIUM_STUFF_5,
		SCHOOL_DESK,
		BAR_PUB,
		AMPHITHEATER_STAGE,
		QUARTER_BED,
		QUARTER_DESK,
		QUARTER_CHAIR,
		QUARTER_WARDROBE,
		QUARTER_CHEST,
		QUARTER_BEDSIDE_TABLE,
		ENVIRONMENT_O2_RECYCLER,
		ENVIRONMENT_TEMPERATURE_REGULATION,
		TRANSPORTATION_SHUTTLECRAFT,
		TRANSPORTATION_CARGO,
		TRANSPORTATION_CONTAINER,
		TRANSPORTATION_TRANSPORTER_SYSTEMS,
		TACTICAL_PHOTON_TORPEDO,
		TACTICAL_PHASER,
		TACTICAL_SHIELD_GRID,
		TACTICAL_CLOAKING_DEVICE,
		SCIENCE_HYDROPONICS,
		RES_1,
	    ITEM_STOP,
	  };
		  
	  protected Type		_type;
	  private int			_x;
	  private int			_y;
	  private int			_id;

	  public boolean isSolid;
	  public int _matterSupply;
	  private int _zoneId;
	  private int _zoneIdRequired;
	  private int _roomId;
	  private Character _owner;
	  private String _name;
	  private int _width;
	  private int _height;
	  private int matter;
	  public int power;
	  public int powerSupply;

	public BaseItem(Type t, int id) {
	  // Init
	  isSolid = false;
	  _type = t;
	  _matterSupply = 0;
	  _zoneId = 0;
	  _zoneIdRequired = 0;
	  _roomId = 0;
	  _owner = null;
	  _id = id;
	  _name = null;

	  // Default values
	  _width = 1;
	  _height = 1;
	  matter = 1;
	  power = 0;
	  powerSupply = 0;
	  isSolid = false;

	  for (ItemInfo info: _itemInfo) {
		if (info.mType == t) {
		  _name = info.mName;
		  _width = info.mWidth;
		  _height = info.mHeight;
		  _zoneId = info.mZone;
		  _zoneIdRequired = info.mZone;
		  matter = info.mMatter;
		  power = info.mPower;
		  powerSupply = 0;
		  isSolid = info.mSolid;
		}
	  }
	}

	public static ItemInfo getItemInfo(Type type) {
	  int i = 0;

	  for (ItemInfo info: _itemInfo) {
		if (info.mType == type) {
		  return info;
		}
	  }

	  return null;
	}

	public void	setOwner(Character character) {
	  Character currentOwner = _owner;

	  _owner = character;

	  // if (currentOwner != NULL && currentOwner.getItem() != NULL) {
	  // 	currentOwner.setItem(NULL);
	  // }
	  // if (character != NULL && character.getItem() != this) {
	  // 	character.setItem(this);
	  // }
	}

	public int		gatherMatter(int maxValue) {
	  int value = Math.min(maxValue, _matterSupply);
	  _matterSupply -= value;
	  return value;
	}

	  void				addMatter(int value) { _matterSupply += matter; }

	  // Sets
	  public void				setPosition(int x, int y) { _x = x; _y = y; }
	  public void				setRoomId(int roomId) { _roomId = roomId; }
	  public void				setZoneId(int zoneId) { _zoneId = zoneId; }

	  // Gets
	  public int		getMatterSupply() { return _matterSupply; }
	  public Character	getOwner() { return _owner; }
	  public int		getWidth() { return _width; }
	  public int		getHeight() { return _height; }
	  public int		getX() { return _x; }
	  public int		getY() { return _y; }
	  public Type		getType() { return _type; }
	  public int		getZoneId() { return _zoneId; }
	  public int		getZoneIdRequired() { return _zoneIdRequired; }
	  public int		getRoomId() { return _roomId; }
	  public int		getId() { return _id; }
	  public String		getName() { return _name; }
	  
	  public static String getItemName(Type type) {
			switch(type) {
			case NONE: return "NONE";
			case STRUCTURE_ROOM: return "room";
			case STRUCTURE_WALL: return "wall";
			case STRUCTURE_HULL: return "hull";
			case STRUCTURE_FLOOR: return "floor";
			case STRUCTURE_WINDOW: return "window";
			case STRUCTURE_DOOR: return "door";
			case SICKBAY_BIOBED: return "biobed";
			case SICKBAY_LAB: return "lab";
			case SICKBAY_EMERGENCY_SHELTERS: return "emergency shelters";
			case ENGINE_CONTROL_CENTER: return "control center";
			case ENGINE_REACTION_CHAMBER: return "reaction chamber";
			case HOLODECK_GRID: return "grid";
			case ARBORETUM_TREE_1: return "tree 1";
			case ARBORETUM_TREE_2: return "tree 2";
			case ARBORETUM_TREE_3: return "tree 3";
			case ARBORETUM_TREE_4: return "tree 4";
			case ARBORETUM_TREE_5: return "tree 5";
			case ARBORETUM_TREE_6: return "tree 6";
			case ARBORETUM_TREE_7: return "tree 7";
			case ARBORETUM_TREE_8: return "tree 8";
			case ARBORETUM_TREE_9: return "tree 9";
			case GYMNASIUM_STUFF_1: return "stuff 1";
			case GYMNASIUM_STUFF_2: return "stuff 2";
			case GYMNASIUM_STUFF_3: return "stuff 3";
			case GYMNASIUM_STUFF_4: return "stuff 4";
			case GYMNASIUM_STUFF_5: return "stuff 5";
			case SCHOOL_DESK: return "desk";
			case BAR_PUB: return "pub";
			case AMPHITHEATER_STAGE: return "stage";
			case QUARTER_BED: return "bed";
			case QUARTER_DESK: return "desk";
			case QUARTER_CHAIR: return "chair";
			case QUARTER_WARDROBE: return "wardrobe";
			case QUARTER_CHEST: return "chest";
			case QUARTER_BEDSIDE_TABLE: return "bedside table";
			case ENVIRONMENT_O2_RECYCLER: return "o2 recycler";
			case ENVIRONMENT_TEMPERATURE_REGULATION: return "temperature regulation";
			case TRANSPORTATION_SHUTTLECRAFT: return "shuttlecraft";
			case TRANSPORTATION_CARGO: return "cargo";
			case TRANSPORTATION_CONTAINER: return "container";
			case TRANSPORTATION_TRANSPORTER_SYSTEMS: return "transporter systems";
			case TACTICAL_PHOTON_TORPEDO: return "photon torpedo";
			case TACTICAL_PHASER: return "phaser";
			case TACTICAL_SHIELD_GRID: return "shield grid";
			case TACTICAL_CLOAKING_DEVICE: return "cloaking device";
			case SCIENCE_HYDROPONICS: return "hydroponics";
			case RES_1: return "res 1";
			default: return "unknow_item";
			}
		  }
	  
	  public boolean		isComplete() { return _matterSupply == matter; }
	  public boolean		isSupply() { return power == powerSupply; }
	  public boolean		isFree() { return _owner == null; }
	  public boolean		isType(Type type) { return _type == type; }
	  public boolean		isZoneMatch() { return _zoneId == _zoneIdRequired; }
	  public boolean		isSleepingItem() { return _type == Type.QUARTER_BED || _type == Type.QUARTER_CHAIR; }
	  public boolean		isStructure() { return BaseItem.isStructure(_type); }
	  public boolean		isRessource() { return _type == Type.RES_1; }
	  public boolean		isWalkable() { return _type != Type.STRUCTURE_WALL; }

	  public static boolean isStructure(Type type) {
		  return type == Type.STRUCTURE_START || type == Type.STRUCTURE_WALL || type == Type.STRUCTURE_HULL || type == Type.STRUCTURE_FLOOR || type == Type.STRUCTURE_WINDOW || type == Type.STRUCTURE_DOOR;
	  }

	public int getMatter() {
		return matter;
	}

	public static boolean isItem(Type type) {
		return !isStructure(type) && type != Type.RES_1;
	}

	public static Type getTypeIndex(int type) {
		if (type == 0) { return Type.NONE; }
		if (type == 1) { return Type.STRUCTURE_START; }
		if (type == 2) { return Type.STRUCTURE_ROOM; }
		if (type == 3) { return Type.STRUCTURE_WALL; }
		if (type == 4) { return Type.STRUCTURE_HULL; }
		if (type == 5) { return Type.STRUCTURE_FLOOR; }
		if (type == 6) { return Type.STRUCTURE_WINDOW; }
		if (type == 7) { return Type.STRUCTURE_DOOR; }
		if (type == 8) { return Type.STRUCTURE_STOP; }
		if (type == 9) { return Type.ITEM_START; }
		if (type == 10) { return Type.SICKBAY_BIOBED; }
		if (type == 11) { return Type.SICKBAY_LAB; }
		if (type == 12) { return Type.SICKBAY_EMERGENCY_SHELTERS; }
		if (type == 13) { return Type.ENGINE_CONTROL_CENTER; }
		if (type == 14) { return Type.ENGINE_REACTION_CHAMBER; }
		if (type == 15) { return Type.HOLODECK_GRID; }
		if (type == 16) { return Type.ARBORETUM_TREE_1; }
		if (type == 17) { return Type.ARBORETUM_TREE_2; }
		if (type == 18) { return Type.ARBORETUM_TREE_3; }
		if (type == 19) { return Type.ARBORETUM_TREE_4; }
		if (type == 20) { return Type.ARBORETUM_TREE_5; }
		if (type == 21) { return Type.ARBORETUM_TREE_6; }
		if (type == 22) { return Type.ARBORETUM_TREE_7; }
		if (type == 23) { return Type.ARBORETUM_TREE_8; }
		if (type == 24) { return Type.ARBORETUM_TREE_9; }
		if (type == 25) { return Type.GYMNASIUM_STUFF_1; }
		if (type == 26) { return Type.GYMNASIUM_STUFF_2; }
		if (type == 27) { return Type.GYMNASIUM_STUFF_3; }
		if (type == 28) { return Type.GYMNASIUM_STUFF_4; }
		if (type == 29) { return Type.GYMNASIUM_STUFF_5; }
		if (type == 30) { return Type.SCHOOL_DESK; }
		if (type == 31) { return Type.BAR_PUB; }
		if (type == 32) { return Type.AMPHITHEATER_STAGE; }
		if (type == 33) { return Type.QUARTER_BED; }
		if (type == 34) { return Type.QUARTER_DESK; }
		if (type == 35) { return Type.QUARTER_CHAIR; }
		if (type == 36) { return Type.QUARTER_WARDROBE; }
		if (type == 37) { return Type.QUARTER_CHEST; }
		if (type == 38) { return Type.QUARTER_BEDSIDE_TABLE; }
		if (type == 39) { return Type.ENVIRONMENT_O2_RECYCLER; }
		if (type == 40) { return Type.ENVIRONMENT_TEMPERATURE_REGULATION; }
		if (type == 41) { return Type.TRANSPORTATION_SHUTTLECRAFT; }
		if (type == 42) { return Type.TRANSPORTATION_CARGO; }
		if (type == 43) { return Type.TRANSPORTATION_CONTAINER; }
		if (type == 44) { return Type.TRANSPORTATION_TRANSPORTER_SYSTEMS; }
		if (type == 45) { return Type.TACTICAL_PHOTON_TORPEDO; }
		if (type == 46) { return Type.TACTICAL_PHASER; }
		if (type == 47) { return Type.TACTICAL_SHIELD_GRID; }
		if (type == 48) { return Type.TACTICAL_CLOAKING_DEVICE; }
		if (type == 49) { return Type.SCIENCE_HYDROPONICS; }
		if (type == 50) { return Type.RES_1; }
		if (type == 51) { return Type.ITEM_STOP; }
		return Type.NONE;
	}

	public static boolean isResource(Type type) {
		if (type == BaseItem.Type.RES_1) {
			return true;
		}
		return false;
	}

}

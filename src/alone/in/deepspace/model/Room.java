package alone.in.deepspace.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsfml.graphics.Color;

import alone.in.deepspace.model.character.Character;

public class Room {
	public enum Type {
		NONE,
		QUARTER,
		SICKBAY,
		ENGINEERING,
		METTING,
		HOLODECK,
		STORAGE,
		GARDEN
	}

	static int 			_roomCount = 0;
	static int 			_roomTmpId = 0;

	int						_id;
	int						_zoneId;
	List<BaseItem>			_doors;
	private Type 			_type;
	private Character 		_owner;
	private int 			_x;
	private int 			_y;
	private Color 			_color;
	private int 			_minX;
	private int 			_maxX;
	private boolean 		_isCommon;
	private Set<Character> 	_occupants;

	public Room(Type type, int x, int y) {
		_color = new Color((int)(Math.random() * 200), (int)(Math.random() * 200), (int)(Math.random() * 200));
		_id = -1;
		_x = x;
		_y = y;
		_isCommon = true;
		_maxX = Integer.MIN_VALUE;
		_minX = Integer.MAX_VALUE;
		_zoneId = 0;
		_type = type;
		_doors = new ArrayList<BaseItem>();
		_occupants = new HashSet<Character>();
	}

	//	public static Room	createFromPos(int x, int y) {
	//	  int ret = checkZone(x, y, --_roomTmpId);
	//
	//	  if (ret == 0) {
	//		Room room = new Room();
	//		room.setId(++_roomCount);
	//		setZone(x, y, _roomCount, 0);
	//		Log.info("Room create: " + _roomCount);
	//		return room;
	//	  }
	//
	//	  else if (ret > 0) {
	//		Room room = ServiceManager.getWorldMap().getRoom(ret);
	//		if (room != null) {
	//		  setZone(x, y, ret, room.getZoneId());
	//		  Log.info("Room set: " + ret);
	//		  return room;
	//		} else {
	//		  Log.error("Room #" + ret + " not exists");
	//		}
	//	  }
	//
	//	  else {
	//		Log.info("Room not complete");
	//	  }
	//
	//	  return null;
	//	}

	public int				getId() { return _id; }
	public int				getZoneId() { return _zoneId; }
	public static int		getNewId() { return ++_roomCount; }
	public Character 		getOwner() { return _owner; }
	public int 				getX() { return _x; }
	public int 				getY() { return _y; }
	public Color 			getColor() { return _color; }
	public int 				getMinX() { return _minX; }
	public int 				getMaxX() { return _maxX; }
	public int 				getWidth() { return _maxX - _minX + 1; }
	public Type 			getType() { return _type; }
	public Set<Character>	getOccupants() { return _occupants; }

	public void 			setMaxX(int x) { _maxX = x; }
	public void 			setMinX(int x) { _minX = x; }
	public void 			setCommon(boolean common) { _isCommon = common; }
	public void 			setId(int i) { _id = i; }

	public boolean 			isCommon() { return _isCommon; }
	public boolean			isType(Type type) { return _type == type; }

	public void 			setOwner(Character owner) { 
		_owner = owner; 
		if (owner != null) { 
			_occupants.add(owner); 
		} 
	}

	public void 			addOccupant(Character character) {
		_occupants.add(character);
		if (_owner == null) {
			_owner = character;
		}
	}

	public void 			removeOccupant(Character character) {
		_occupants.remove(character);

		// Owner is removed occupant
		if (_owner == character) {
			_owner = _occupants.isEmpty() ? null : _occupants.iterator().next();
		}
	}

	//	public void			setZoneId(int zoneId) {
	//		_zoneId = zoneId;
	//
	//		int w = ServiceManager.getWorldMap().getWidth();
	//		int h = ServiceManager.getWorldMap().getHeight();
	//		for (int i = 0; i < w; i++) {
	//			for (int j = 0; j < h; j++) {
	//				WorldArea item = ServiceManager.getWorldMap().getArea(i, j);
	//				if (item != null && item.getRoomId() == _id) {
	//					item.setZoneId(zoneId);
	//				}
	//			}
	//		}
	//	}

	//	static int	checkZone(int x, int y, int id) {
	//	  WorldArea item = ServiceManager.getWorldMap().getArea(x, y);
	//
	//	  // Out of bound or empty
	//	  if (item == null) {
	//		Log.debug("Room: out of bound");
	//		return -1;
	//	  }
	//
	//	  // Add to doors list
	//	  if (item.isType(BaseItem.Type.STRUCTURE_DOOR)) {
	//		// _doorspush_back(item);
	//		Log.debug("Room: door");
	//		return -1;
	//	  }
	//
	//	  // Room limit
	//	  if (item.isType(BaseItem.Type.STRUCTURE_WALL) ||
	//		  item.isType(BaseItem.Type.STRUCTURE_HULL) ||
	//		  item.isType(BaseItem.Type.STRUCTURE_WINDOW)) {
	//		Log.debug("Room: wall / hull / window");
	//		return -1;
	//	  }
	//
	//	  // Already tag
	//	  if (item.getRoomId() == id) {
	//		return 0;
	//	  }
	//
	//	  if (item.getRoomId() != id && item.getRoomId() > 0) {
	//		return item.getRoomId();
	//	  }
	//
	//	  item.setRoomId(id);
	//	  
	//	  int ret = 0;
	//
	//	  for (int i = 0; i < 4; i++) {
	//		switch (i) {
	//		case 0: ret = checkZone(x, y+1, id); break;
	//		case 1: ret = checkZone(x, y-1, id); break;
	//		case 2: ret = checkZone(x+1, y, id); break;
	//		case 3: ret = checkZone(x-1, y, id); break;
	//		}
	//		if (ret > 0) {
	//		  return ret;
	//		}
	//	  }
	//
	//	  return 0;
	//	}

	//	public static void	setZone(int x, int y, int roomId, int zoneId) {
	//	  WorldArea item = ServiceManager.getWorldMap().getArea(x, y);
	//
	//	  // Out of bound or empty
	//	  if (item == null) {
	//		return;
	//	  }
	//
	//	  // Add tro doors list
	//	  if (item.isType(BaseItem.Type.STRUCTURE_DOOR)) {
	//		// _doorspush_back(item);
	//		return;
	//	  }
	//
	//	  // Room limit
	//	  if (item.isType(BaseItem.Type.STRUCTURE_WALL) ||
	//		  item.isType(BaseItem.Type.STRUCTURE_HULL) ||
	//		  item.isType(BaseItem.Type.STRUCTURE_WINDOW)) {
	//		return;
	//	  }
	//
	//	  // Already tag
	//	  if (item.getRoomId() == roomId) {
	//		return;
	//	  }
	//
	//	  item.setRoomId(roomId);
	//	  item.setZoneId(zoneId);
	//	  
	//	  setZone(x, y+1, roomId, zoneId);
	//	  setZone(x, y-1, roomId, zoneId);
	//	  setZone(x+1, y, roomId, zoneId);
	//	  setZone(x-1, y, roomId, zoneId);
	//	}

	public static Type getType(int type) {
		switch (type) {
		case 1: return Type.QUARTER;
		case 2: return Type.SICKBAY;
		case 3: return Type.ENGINEERING;
		case 4: return Type.METTING;
		case 5: return Type.HOLODECK;
		case 6: return Type.STORAGE;
		case 7: return Type.GARDEN;
		};
		return null;
	}

	public String getName() {
		switch (_type) {
		case QUARTER:		return "Quarter";
		case SICKBAY: 		return "Sickbay";
		case ENGINEERING: 	return "Engineering";
		case METTING: 			return "Pub";
		case HOLODECK: 		return "Holodeck";
		case STORAGE: 		return "Storage";
		case GARDEN: 		return "Garden";
		default: 			return "";
		}
	}
}

package org.smallbox.faraway.model.room;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.manager.Utils;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.ItemBase;
import org.smallbox.faraway.model.item.ItemFilter;
import org.smallbox.faraway.model.item.UserItem;
import org.smallbox.faraway.model.item.WorldArea;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Room {
	private boolean _isExterior;

	public void setExterior(boolean isExterior) {
		_isExterior = isExterior;
	}

	public boolean isExterior() {
		return _isExterior;
	}

	public enum RoomType {
		NONE,
		QUARTER,
		SICKBAY,
		ENGINEERING,
		METTING,
		HOLODECK,
		STORAGE,
		GARDEN
	}

	int						_id;
	int						_zoneId;
	List<ItemBase>			_doors;
	private RoomType _type;
	private CharacterModel _owner;
	private int 			_x;
	private int 			_y;
	private Color 			_color;
	private int 			_minX;
	private int 			_maxX;
	private boolean 		_isCommon;
	private Set<CharacterModel> 	_occupants;
	protected List<WorldArea> 	_areas;

	public Room(int id, RoomType type) {
		init(id, type);
	}

	public Room(RoomType type) {
		init(Utils.getUUID(), type);
	}

	private void init(int id, RoomType type) {
		_color = new Color((int)(Math.random() * 200), (int)(Math.random() * 200), (int)(Math.random() * 200));
		_areas = new ArrayList<WorldArea>();
		_id = id;
		_isCommon = true;
		_maxX = Integer.MIN_VALUE;
		_minX = Integer.MAX_VALUE;
		_zoneId = 0;
		_type = type;
		_doors = new ArrayList<ItemBase>();
		_occupants = new HashSet<CharacterModel>();
	}

	public int				getId() { return _id; }
	public int				getZoneId() { return _zoneId; }
	public CharacterModel getOwner() { return _owner; }
	public int 				getX() { return _x; }
	public int 				getY() { return _y; }
	public Color 			getColor() { return _color; }
	public int 				getMinX() { return _minX; }
	public int 				getMaxX() { return _maxX; }
	public int 				getWidth() { return _maxX - _minX + 1; }
	public RoomType getType() { return _type; }
	public Set<CharacterModel>	getOccupants() { return _occupants; }

	public void 			setMaxX(int x) { _maxX = x; }
	public void 			setMinX(int x) { _minX = x; }
	public void 			setCommon(boolean common) { _isCommon = common; }

	public boolean 			isCommon() { return _isCommon; }
	public boolean			isType(RoomType type) { return _type == type; }

	public void 			setOwner(CharacterModel owner) {
		_owner = owner; 
		if (owner != null) { 
			_occupants.add(owner); 
		} 
	}

	public void 			addOccupant(CharacterModel character) {
		if (character != null) {
			_occupants.add(character);
			if (_owner == null) {
				_owner = character;
			}
		}
	}

	public void 			removeOccupant(CharacterModel character) {
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

	public String getName() {
		switch (_type) {
		case QUARTER:		return "Quarter";
		case SICKBAY: 		return "Sickbay";
		case ENGINEERING: 	return "Engineering";
		case METTING: 		return "Pub";
		case HOLODECK: 		return "Holodeck";
		case STORAGE: 		return "Storage";
		case GARDEN: 		return "Garden";
		default: 			return "";
		}
	}

	public void update() {
		if (_owner != null && _owner.isDead()) {
			_owner = null;
		}
	}

	public void addArea(WorldArea area) {
		_areas.add(area);
	}

	public List<WorldArea> getAreas() {
		return _areas;
	}

	public boolean isGarden() {
		return _type == RoomType.GARDEN;
	}

	public void setOption(int index) {
	}

	public RoomOptions getOptions() {
		return null;
	}

	public boolean isQuarter() {
		return RoomType.QUARTER.equals(_type);
	}

	public boolean isPrivate() {
		return _type == RoomType.QUARTER;
	}

	/**
	 * Search and return desired item in room
	 * 
	 * @param filter
	 * @return
	 */
	public ItemBase find(ItemFilter filter) {
		for (WorldArea area: _areas) {
			UserItem item = area.getItem();
			if (item != null && item.matchFilter(filter)) {
				return item;
			}
		}
		return null;
	}

	public void removeArea(WorldArea area) {
		_areas.remove(area);
	}

	public void removeArea(int x, int y) {
		for (WorldArea area: _areas) {
			if (area.getX() == x && area.getY() == y) {
				removeArea(area);
				return;
			}
		}
	}

	public void refreshPosition() {
		_x = Integer.MAX_VALUE;
		_y = Integer.MAX_VALUE;
		
		for (WorldArea area: _areas) {
			if (area.getX() <= _x  && area.getY() <= _y) {
				_x = area.getX();
				_y = area.getY();
			}
		}
	}

	public boolean isStorage() {
		return _type == RoomType.STORAGE;
	}

}

package alone.in.deepspace.Models;
import java.util.ArrayList;
import java.util.List;

import alone.in.deepspace.Utils.Log;
import alone.in.deepspace.World.WorldArea;
import alone.in.deepspace.World.WorldMap;


public class Room {
	static int _roomCount = 0;
	static int _roomTmpId = 0;

	int					_id;
	int					_zoneId;
	List<BaseItem>		_doors;

	public Room() {
	  _id = -1;
	  _zoneId = 0;
	  _doors = new ArrayList<BaseItem>();
	}

	public static Room	createFromPos(int x, int y) {
	  int ret = checkZone(x, y, --_roomTmpId);

	  if (ret == 0) {
		Room room = new Room();
		room.setId(++_roomCount);
		setZone(x, y, _roomCount, 0);
		Log.info("Room create: " + _roomCount);
		return room;
	  }

	  else if (ret > 0) {
		Room room = WorldMap.getInstance().getRoom(ret);
		if (room != null) {
		  setZone(x, y, ret, room.getZoneId());
		  Log.info("Room set: " + ret);
		  return room;
		} else {
		  Log.error("Room #" + ret + " not exists");
		}
	  }

	  else {
		Log.info("Room not complete");
	  }

	  return null;
	}
	
	public int				getId() { return _id; }
	public int				getZoneId() { return _zoneId; }
	public static int		getNewId() { return ++_roomCount; }

	public void setId(int i) {
		_id = i;
	}

	public void			setZoneId(int zoneId) {
	  _zoneId = zoneId;

	  int w = WorldMap.getInstance().getWidth();
	  int h = WorldMap.getInstance().getHeight();
	  for (int i = 0; i < w; i++) {
		for (int j = 0; j < h; j++) {
		  WorldArea item = WorldMap.getInstance().getArea(i, j);
		  if (item != null && item.getRoomId() == _id) {
			item.setZoneId(zoneId);
		  }
		}
	  }
	}

	static int	checkZone(int x, int y, int id) {
	  WorldArea item = WorldMap.getInstance().getArea(x, y);

	  // Out of bound or empty
	  if (item == null) {
		Log.debug("Room: out of bound");
		return -1;
	  }

	  // Add to doors list
	  if (item.isType(BaseItem.Type.STRUCTURE_DOOR)) {
		// _doorspush_back(item);
		Log.debug("Room: door");
		return -1;
	  }

	  // Room limit
	  if (item.isType(BaseItem.Type.STRUCTURE_WALL) ||
		  item.isType(BaseItem.Type.STRUCTURE_HULL) ||
		  item.isType(BaseItem.Type.STRUCTURE_WINDOW)) {
		Log.debug("Room: wall / hull / window");
		return -1;
	  }

	  // Already tag
	  if (item.getRoomId() == id) {
		return 0;
	  }

	  if (item.getRoomId() != id && item.getRoomId() > 0) {
		return item.getRoomId();
	  }

	  item.setRoomId(id);
	  
	  int ret = 0;

	  for (int i = 0; i < 4; i++) {
		switch (i) {
		case 0: ret = checkZone(x, y+1, id); break;
		case 1: ret = checkZone(x, y-1, id); break;
		case 2: ret = checkZone(x+1, y, id); break;
		case 3: ret = checkZone(x-1, y, id); break;
		}
		if (ret > 0) {
		  return ret;
		}
	  }

	  return 0;
	}

	public static void	setZone(int x, int y, int roomId, int zoneId) {
	  WorldArea item = WorldMap.getInstance().getArea(x, y);

	  // Out of bound or empty
	  if (item == null) {
		return;
	  }

	  // Add to doors list
	  if (item.isType(BaseItem.Type.STRUCTURE_DOOR)) {
		// _doorspush_back(item);
		return;
	  }

	  // Room limit
	  if (item.isType(BaseItem.Type.STRUCTURE_WALL) ||
		  item.isType(BaseItem.Type.STRUCTURE_HULL) ||
		  item.isType(BaseItem.Type.STRUCTURE_WINDOW)) {
		return;
	  }

	  // Already tag
	  if (item.getRoomId() == roomId) {
		return;
	  }

	  item.setRoomId(roomId);
	  item.setZoneId(zoneId);
	  
	  setZone(x, y+1, roomId, zoneId);
	  setZone(x, y-1, roomId, zoneId);
	  setZone(x+1, y, roomId, zoneId);
	  setZone(x-1, y, roomId, zoneId);
	}

}

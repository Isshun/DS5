#include "WorldMap.h"
#include "Room.h"

int Room::_roomCount = 0;
int Room::_roomTmpId = 0;

Room::Room() {
  _id = -1;
  _zoneId = 0;
  _doors = new list<BaseItem*>();
}

Room::~Room() {
}

Room*	Room::createFromPos(int x, int y) {
  int ret = Room::checkZone(x, y, --_roomTmpId);

  if (ret == 0) {
	Room* room = new Room();
	room->setId(++_roomCount);
	Room::setZone(x, y, _roomCount, 0);
	Info() << "Room create: " << _roomCount;
	return room;
  }

  else if (ret > 0) {
	Room* room = WorldMap::getInstance()->getRoom(ret);
	if (room != NULL) {
	  Room::setZone(x, y, ret, room->getZoneId());
	  Info() << "Room set: " << ret;
	  return room;
	} else {
	  Error() << "Room #" << ret << " not exists";
	}
  }

  else {
	Info() << "Room not complete";
  }

  return NULL;
}

void			Room::setZoneId(int zoneId) {
  _zoneId = zoneId;

  int w = WorldMap::getInstance()->getWidth();
  int h = WorldMap::getInstance()->getHeight();
  for (int i = 0; i < w; i++) {
	for (int j = 0; j < h; j++) {
	  WorldArea* item = WorldMap::getInstance()->getArea(i, j);
	  if (item != NULL && item->getRoomId() == _id) {
		item->setZoneId(zoneId);
	  }
	}
  }
}

int	Room::checkZone(int x, int y, int id) {
  WorldArea* item = WorldMap::getInstance()->getArea(x, y);

  // Out of bound or empty
  if (item == NULL) {
	Debug() << "Room: out of bound";
	return -1;
  }

  // Add to doors list
  if (item->isType(BaseItem::STRUCTURE_DOOR)) {
	// _doors->push_back(item);
	Debug() << "Room: door";
	return -1;
  }

  // Room limit
  if (item->isType(BaseItem::STRUCTURE_WALL) ||
	  item->isType(BaseItem::STRUCTURE_HULL) ||
	  item->isType(BaseItem::STRUCTURE_WINDOW)) {
	Debug() << "Room: wall / hull / window";
	return -1;
  }

  // Already tag
  if (item->getRoomId() == id) {
	return 0;
  }

  if (item->getRoomId() != id && item->getRoomId() > 0) {
	return item->getRoomId();
  }

  item->setRoomId(id);
  
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

void	Room::setZone(int x, int y, int roomId, int zoneId) {
  WorldArea* item = WorldMap::getInstance()->getArea(x, y);

  // Out of bound or empty
  if (item == NULL) {
	return;
  }

  // Add to doors list
  if (item->isType(BaseItem::STRUCTURE_DOOR)) {
	// _doors->push_back(item);
	return;
  }

  // Room limit
  if (item->isType(BaseItem::STRUCTURE_WALL) ||
	  item->isType(BaseItem::STRUCTURE_HULL) ||
	  item->isType(BaseItem::STRUCTURE_WINDOW)) {
	return;
  }

  // Already tag
  if (item->getRoomId() == roomId) {
	return;
  }

  item->setRoomId(roomId);
  item->setZoneId(zoneId);
  
  setZone(x, y+1, roomId, zoneId);
  setZone(x, y-1, roomId, zoneId);
  setZone(x+1, y, roomId, zoneId);
  setZone(x-1, y, roomId, zoneId);
}

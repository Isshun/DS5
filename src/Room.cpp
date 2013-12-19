#include "WorldMap.h"
#include "Room.h"

int Room::_roomCount = 0;
int Room::_roomTmpId = 0;

Room::Room() {
  _id = -1;
  _doors = new list<BaseItem*>();
}

Room::~Room() {
}

Room*	Room::createFromPos(int x, int y) {
  int ret = Room::checkZone(x, y, --_roomTmpId);

  if (ret == 0) {
	Room::setZone(x, y, ++_roomCount);
	Info() << "Room create: " << _roomCount;
  }

  else if (ret > 0) {
	Room::setZone(x, y, ret);
	Info() << "Room set: " << ret;
  }

  else {
	Info() << "Room not complete";
  }
  // Room* room = new Room();

  // int roomId = room->setZone(x, y);
  // if (roomId == -1) {
  // 	roomId = ++_roomCount;
  // }
  // room->setId(roomId);
  // room->setZone(x, y);

  // Info() << "Create room: " << roomId;

  return NULL;
}

int	Room::checkZone(int x, int y, int id) {
  BaseItem* item = WorldMap::getInstance()->getItem(x, y);

  // Out of bound or empty
  if (item == NULL) {
	return -2;
  }

  // Add to doors list
  if (item->isType(BaseItem::STRUCTURE_DOOR)) {
	// _doors->push_back(item);
	return -1;
  }

  // Room limit
  if (item->isType(BaseItem::STRUCTURE_WALL) ||
	  item->isType(BaseItem::STRUCTURE_HULL) ||
	  item->isType(BaseItem::STRUCTURE_WINDOW)) {
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

void	Room::setZone(int x, int y, int id) {
  BaseItem* item = WorldMap::getInstance()->getItem(x, y);

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
  if (item->getRoomId() == id) {
	return;
  }

  // if (item->getRoomId() != id && item->getRoomId() > 0) {
  // 	return;
  // }

  item->setRoomId(id);
  
  setZone(x, y+1, id);
  setZone(x, y-1, id);
  setZone(x+1, y, id);
  setZone(x-1, y, id);
}

// int	Room::setZone(int x, int y) {
//   BaseItem* item = WorldMap::getInstance()->getItem(x, y);

//   // Out of bound or empty
//   if (item == NULL) {
// 	return _id;
//   }

//   // Add to doors list
//   if (item->isType(BaseItem::STRUCTURE_DOOR)) {
// 	_doors->push_back(item);
// 	return _id;
//   }

//   // Room limit
//   if (item->isType(BaseItem::STRUCTURE_WALL) ||
// 	  item->isType(BaseItem::STRUCTURE_HULL) ||
// 	  item->isType(BaseItem::STRUCTURE_WINDOW)) {
// 	return _id;
//   }

//   // Already tag
//   if (item->getRoomId() == _id) {
// 	return _id;
//   }

//   if (item->getRoomId() != _id && item->getRoomId() > 0) {
// 	_id = item->getRoomId();
// 	return _id;
//   }

//   item->setRoomId(_id);
  
//   setZone(x, y+1);
//   setZone(x, y-1);
//   setZone(x+1, y);
//   setZone(x-1, y);

//   return _id;
// }

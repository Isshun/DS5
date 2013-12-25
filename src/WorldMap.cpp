/*
 * WorldMap.cpp
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#include <iostream>
#include <cstdlib>
#include <stdio.h>
#include <string.h>
#include <list>
#include "WorldMap.h"
#include "defines.h"
#include "Log.h"

WorldMap* WorldMap::_self = new WorldMap();

WorldMap::WorldMap() {
  _itemCout = 0;
  _width = 120;
  _height = 50;

  // memset(_tmp, 0, 250 * 250 * 4);

  for (int y = 0; y < _height; y++) {
	for (int x = 0; x < _width; x++) {
	  _tmp[x][y] = 0;
	}
  }

  dump();

  _rooms = std::map<int, Room*>();
  _todo = new std::list<BaseItem*>();
  _building = new std::list<BaseItem*>();
  _buildingAborted = new std::list<BaseItem*>();
  _items = new BaseItem**[_width];
  for (int x = 0; x < _width; x++) {
	_items[x] = new BaseItem*[_height];
	for (int y = 0; y < _height; y++) {
	  _items[x][y] = NULL;
	}
  }
}

WorldMap::~WorldMap() {
  BaseItem* i;

  // // Free todo list
  // while ((i = _todo->front()) != NULL) {
  // 	delete i;
  // }
  delete _todo;

  // // Free building list
  // while ((i = _building->front()) != NULL) {
  // 	delete i;
  // }
  delete _building;

  // // Free building abort list
  // while ((i = _buildingAborted->front()) != NULL) {
  // 	delete i;
  // }
  delete _buildingAborted;


  for (int x = 0; x < _width; x++) {
	for (int y = 0; y < _height; y++) {
	  if(_items[x][y] != NULL) {
		delete _items[x][y];
	  }
	}
	delete _items[x];
  }
  delete _items;
}

void	WorldMap::init() {
  initMap();
  initRoom();
}

BaseItem*	WorldMap::find(int type, bool free) {
  Debug() << "WorldMap: find";

  int notFree = 0;

  for (int x = 0; x < _width; x++) {
	for (int y = 0; y < _height; y++) {
	  if(_items[x][y] != NULL && _items[x][y]->isType(type) && _items[x][y]->isComplete()) {
		if (free == false || _items[x][y]->isFree()) {
		  Debug() << "item found";
		  return _items[x][y];
		}
		notFree++;
	  }
	}
  }

  Debug() << "No free item found (not free: " << notFree << ")";

  return NULL;
}

//TODO: perf
BaseItem*	WorldMap::getRandomPosInRoom(int roomId) {
  Debug() << "getRandomPosInRoom: " << roomId;

  int count = 0;
  for (int x = 0; x < _width; x++) {
	for (int y = 0; y < _height; y++) {
	  if(_items[x][y] != NULL && _items[x][y]->getRoomId() == roomId && _items[x][y]->isType(BaseItem::STRUCTURE_FLOOR)) {
		count++;
	  }
	}
  }
  Debug() << "getRandomPosInRoom found: " << count;

  if (count > 0) {
	int goal = rand() % count;
	for (int x = 0; x < _width; x++) {
	  for (int y = 0; y < _height; y++) {
		if(_items[x][y] != NULL
		   && _items[x][y]->getRoomId() == roomId
		   && _items[x][y]->isType(BaseItem::STRUCTURE_FLOOR)) {
		  if (goal-- == 0) {
			Debug() << "getRandomPosInRoom return: " << x << y << count;
			return _items[x][y];
		  }
		}
	  }
	}
  }

  Warning() << "getRandomPosInRoom: no room found";
  return  NULL;
}

void	WorldMap::initRoom() {
  // for (int y = 0; y < _height; y++) {
  // 	for (int x = 0; x < _width; x++) {
  // 	  BaseItem* item = getItem(x, y);
  // 	  if (item != NULL
  // 		  && (item->isStructure() == false || item->getType() == BaseItem::STRUCTURE_FLOOR)
  // 		  && item->getRoomId() == 0) {
  // 		Room::createFromPos(x, y);
  // 	  }
  // 	}
  // }
}

void WorldMap::initMap() {
  putItem(8, 7, 3, true);
  putItem(8, 8, 3, true);
  putItem(8, 9, 7, true);
  putItem(8, 10, 3, true);
  putItem(8, 11, 3, true);
  putItem(8, 12, 3, true);
  putItem(8, 13, 3, true);
  putItem(8, 14, 3, true);
  putItem(8, 15, 3, true);
  putItem(8, 16, 3, true);
  putItem(8, 17, 3, true);
  putItem(8, 18, 3, true);
  putItem(8, 19, 3, true);
  putItem(8, 20, 3, true);
  putItem(8, 21, 3, true);
  putItem(8, 22, 3, true);
  putItem(8, 23, 3, true);
  putItem(8, 24, 3, true);
  putItem(8, 25, 3, true);
  putItem(8, 26, 3, true);
  putItem(8, 27, 3, true);
  putItem(8, 28, 3, true);
  putItem(9, 7, 3, true);
  putItem(9, 8, 5, true);
  putItem(9, 9, 5, true);
  putItem(9, 10, 5, true);
  putItem(9, 11, 5, true);
  putItem(9, 12, 5, true);
  putItem(9, 13, 5, true);
  putItem(9, 14, 5, true);
  putItem(9, 15, 5, true);
  putItem(9, 16, 5, true);
  putItem(9, 17, 5, true);
  putItem(9, 18, 5, true);
  putItem(9, 19, 5, true);
  putItem(9, 20, 5, true);
  putItem(9, 21, 5, true);
  putItem(9, 22, 5, true);
  putItem(9, 23, 3, true);
  putItem(9, 24, 5, true);
  putItem(9, 25, 5, true);
  putItem(9, 26, 5, true);
  putItem(9, 27, 5, true);
  putItem(9, 28, 3, true);
  putItem(10, 3, 3, true);
  putItem(10, 4, 3, true);
  putItem(10, 5, 3, true);
  putItem(10, 6, 3, true);
  putItem(10, 7, 3, true);
  putItem(10, 8, 5, true);
  putItem(10, 9, 5, true);
  putItem(10, 10, 5, true);
  putItem(10, 11, 5, true);
  putItem(10, 12, 5, true);
  putItem(10, 13, 5, true);
  putItem(10, 14, 5, true);
  putItem(10, 15, 5, true);
  putItem(10, 16, 5, true);
  putItem(10, 17, 5, true);
  putItem(10, 18, 5, true);
  putItem(10, 19, 5, true);
  putItem(10, 20, 5, true);
  putItem(10, 21, 5, true);
  putItem(10, 22, 5, true);
  putItem(10, 23, 7, true);
  putItem(10, 24, 5, true);
  putItem(10, 25, 5, true);
  putItem(10, 26, 5, true);
  putItem(10, 27, 5, true);
  putItem(10, 28, 3, true);
  putItem(11, 3, 3, true);
  putItem(11, 4, 5, true);
  putItem(11, 5, 5, true);
  putItem(11, 6, 5, true);
  putItem(11, 7, 3, true);
  putItem(11, 8, 5, true);
  putItem(11, 9, 5, true);
  putItem(11, 10, 5, true);
  putItem(11, 11, 5, true);
  putItem(11, 12, 5, true);
  putItem(11, 13, 5, true);
  putItem(11, 14, 5, true);
  putItem(11, 15, 5, true);
  putItem(11, 16, 5, true);
  putItem(11, 17, 5, true);
  putItem(11, 18, 5, true);
  putItem(11, 19, 5, true);
  putItem(11, 20, 5, true);
  putItem(11, 21, 5, true);
  putItem(11, 22, 5, true);
  putItem(11, 23, 3, true);
  putItem(11, 24, 5, true);
  putItem(11, 25, 5, true);
  putItem(11, 26, 5, true);
  putItem(11, 27, 5, true);
  putItem(11, 28, 3, true);
  putItem(12, 3, 3, true);
  putItem(12, 4, 5, true);
  putItem(12, 5, 5, true);
  putItem(12, 6, 5, true);
  putItem(12, 7, 7, true);
  putItem(12, 8, 5, true);
  putItem(12, 9, 5, true);
  putItem(12, 10, 5, true);
  putItem(12, 11, 3, true);
  putItem(12, 12, 3, true);
  putItem(12, 13, 3, true);
  putItem(12, 14, 7, true);
  putItem(12, 15, 3, true);
  putItem(12, 16, 3, true);
  putItem(12, 17, 3, true);
  putItem(12, 18, 3, true);
  putItem(12, 19, 3, true);
  putItem(12, 20, 7, true);
  putItem(12, 21, 3, true);
  putItem(12, 22, 3, true);
  putItem(12, 23, 3, true);
  putItem(12, 25, 5, true);
  putItem(12, 26, 5, true);
  putItem(12, 27, 5, true);
  putItem(12, 28, 3, true);
  putItem(13, 3, 3, true);
  putItem(13, 4, 5, true);
  putItem(13, 5, 5, true);
  putItem(13, 6, 5, true);
  putItem(13, 7, 3, true);
  putItem(13, 8, 5, true);
  putItem(13, 9, 5, true);
  putItem(13, 10, 5, true);
  putItem(13, 11, 3, true);
  putItem(13, 12, 5, true);
  putItem(13, 13, 5, true);
  putItem(13, 14, 5, true);
  putItem(13, 15, 5, true);
  putItem(13, 16, 5, true);
  putItem(13, 17, 3, true);
  putItem(13, 19, 5, true);
  putItem(13, 20, 5, true);
  putItem(13, 21, 5, true);
  putItem(13, 23, 3, true);
  putItem(13, 24, 5, true);
  putItem(13, 25, 5, true);
  putItem(13, 26, 5, true);
  putItem(13, 27, 5, true);
  putItem(13, 28, 3, true);
  putItem(14, 3, 3, true);
  putItem(14, 4, 5, true);
  putItem(14, 5, 5, true);
  putItem(14, 6, 5, true);
  putItem(14, 7, 3, true);
  putItem(14, 8, 3, true);
  putItem(14, 9, 3, true);
  putItem(14, 10, 3, true);
  putItem(14, 11, 3, true);
  putItem(14, 12, 5, true);
  putItem(14, 13, 5, true);
  putItem(14, 14, 5, true);
  putItem(14, 15, 5, true);
  putItem(14, 16, 5, true);
  putItem(14, 17, 3, true);
  putItem(14, 18, 5, true);
  putItem(14, 19, 5, true);
  putItem(14, 20, 5, true);
  putItem(14, 21, 5, true);
  putItem(14, 22, 5, true);
  putItem(14, 23, 3, true);
  putItem(14, 24, 5, true);
  putItem(14, 25, 5, true);
  putItem(14, 26, 5, true);
  putItem(14, 27, 5, true);
  putItem(14, 28, 3, true);
  putItem(15, 3, 3, true);
  putItem(15, 4, 5, true);
  putItem(15, 5, 5, true);
  putItem(15, 6, 5, true);
  putItem(15, 7, 3, true);
  putItem(15, 8, 5, true);
  putItem(15, 9, 5, true);
  putItem(15, 10, 5, true);
  putItem(15, 11, 3, true);
  putItem(15, 12, 5, true);
  putItem(15, 13, 5, true);
  putItem(15, 14, 5, true);
  putItem(15, 15, 5, true);
  putItem(15, 16, 5, true);
  putItem(15, 17, 3, true);
  putItem(15, 18, 5, true);
  putItem(15, 19, 5, true);
  putItem(15, 20, 5, true);
  putItem(15, 21, 5, true);
  putItem(15, 22, 5, true);
  putItem(15, 23, 3, true);
  putItem(15, 24, 5, true);
  putItem(15, 25, 5, true);
  putItem(15, 26, 5, true);
  putItem(15, 27, 5, true);
  putItem(15, 28, 3, true);
  putItem(16, 3, 3, true);
  putItem(16, 4, 5, true);
  putItem(16, 5, 5, true);
  putItem(16, 6, 5, true);
  putItem(16, 7, 3, true);
  putItem(16, 8, 5, true);
  putItem(16, 9, 5, true);
  putItem(16, 10, 5, true);
  putItem(16, 11, 3, true);
  putItem(16, 13, 5, true);
  putItem(16, 14, 5, true);
  putItem(16, 15, 5, true);
  putItem(16, 16, 5, true);
  putItem(16, 17, 3, true);
  putItem(16, 19, 5, true);
  putItem(16, 20, 5, true);
  putItem(16, 21, 5, true);
  putItem(16, 22, 5, true);
  putItem(16, 23, 3, true);
  putItem(16, 24, 5, true);
  putItem(16, 25, 5, true);
  putItem(16, 26, 5, true);
  putItem(16, 27, 5, true);
  putItem(16, 28, 3, true);
  putItem(17, 3, 3, true);
  putItem(17, 4, 3, true);
  putItem(17, 5, 3, true);
  putItem(17, 6, 3, true);
  putItem(17, 7, 3, true);
  putItem(17, 9, 5, true);
  putItem(17, 10, 5, true);
  putItem(17, 11, 3, true);
  putItem(17, 12, 5, true);
  putItem(17, 13, 5, true);
  putItem(17, 14, 5, true);
  putItem(17, 15, 5, true);
  putItem(17, 16, 5, true);
  putItem(17, 17, 3, true);
  putItem(17, 18, 5, true);
  putItem(17, 19, 5, true);
  putItem(17, 20, 5, true);
  putItem(17, 21, 5, true);
  putItem(17, 23, 3, true);
  putItem(17, 24, 5, true);
  putItem(17, 26, 5, true);
  putItem(17, 27, 5, true);
  putItem(17, 28, 3, true);
  putItem(18, 7, 3, true);
  putItem(18, 8, 5, true);
  putItem(18, 9, 5, true);
  putItem(18, 10, 5, true);
  putItem(18, 11, 3, true);
  putItem(18, 12, 5, true);
  putItem(18, 13, 5, true);
  putItem(18, 14, 5, true);
  putItem(18, 15, 5, true);
  putItem(18, 16, 5, true);
  putItem(18, 17, 3, true);
  putItem(18, 18, 5, true);
  putItem(18, 19, 5, true);
  putItem(18, 20, 5, true);
  putItem(18, 21, 5, true);
  putItem(18, 22, 5, true);
  putItem(18, 23, 3, true);
  putItem(18, 24, 5, true);
  putItem(18, 25, 5, true);
  putItem(18, 26, 5, true);
  putItem(18, 27, 5, true);
  putItem(18, 28, 3, true);
  putItem(19, 7, 3, true);
  putItem(19, 8, 3, true);
  putItem(19, 9, 3, true);
  putItem(19, 10, 3, true);
  putItem(19, 11, 3, true);
  putItem(19, 12, 3, true);
  putItem(19, 13, 3, true);
  putItem(19, 14, 3, true);
  putItem(19, 15, 3, true);
  putItem(19, 16, 3, true);
  putItem(19, 17, 3, true);
  putItem(19, 18, 3, true);
  putItem(19, 19, 3, true);
  putItem(19, 20, 3, true);
  putItem(19, 21, 3, true);
  putItem(19, 22, 3, true);
  putItem(19, 23, 3, true);
  putItem(19, 24, 3, true);
  putItem(19, 25, 3, true);
  putItem(19, 26, 3, true);
  putItem(19, 27, 3, true);
  putItem(19, 28, 3, true);
  putItem(14, 9, 7, true);

  putItem(17, 8, 5, true);
  putItem(13, 18, 5, true);
  putItem(12, 24, 5, true);
  putItem(13, 22, 5, true);
  putItem(16, 12, 5, true);
  putItem(16, 18, 5, true);
  putItem(17, 22, 5, true);
  putItem(17, 25, 5, true);

  putItem(18, 8, 9, true);
  putItem(13, 18, 32, true);
  putItem(12, 24, 12, true);
  putItem(13, 22, 34, true);
  putItem(16, 12, 30, true);
  putItem(16, 18, 32, true);
  putItem(17, 22, 34, true);
  putItem(17, 25, 13, true);
}

int GetMap(int x, int y);

void WorldMap::dump() {
  // // for (int x = 0; x < _width; x++) {
  // // 	for (int y = 0; y < _height; y++) {
  // // 	  if (_items[x][y] != NULL) {
  // // 		Info() << x << " x " << y << " = " << _items[x][y]->type;
  // // 	  }
  // // 	}
  // // }

  // // std::cout << std::endl << "\r";

  // system("clear");

  // for (int y = 0; y < _height; y++) {
  // 	for (int x = 0; x < _width; x++) {
  // 	  std::cout << _tmp[x][y];
  // 	  // std::cout << GetMap(x, y);
  // 	  // if (_items[x][y] != NULL) {
  // 	  // 	Info() << x << " x " << y << " = " << _items[x][y]->type;
  // 	  // }
  // 	}
  // 	std::cout << std::endl;
  // }

}

void		WorldMap::dumpItems() {
  for (int x = 0; x < _width; x++) {
	for (int y = 0; y < _height; y++) {
	  if (_items[x][y] != NULL && _items[x][y]->isStructure() == false) {
		Info() << x << " x " << y << " = " << _items[x][y]->getType() << ", zone: " << _items[x][y]->getZoneId();
	  }
	}
  }
}

bool WorldMap::getSolid(int x, int y) {
  return false;
}

void WorldMap::removeItem(int x, int y) {
  Debug() << "remove item";

  // Return if out of bound
  if (x < 0 || y < 0 || x >= _width || y >= _height) {
	Error() << "remove item out of bound, x: " << x << ", y: " << y << ")";
	return;
  }

  BaseItem *item = _items[x][y];
  if (item == NULL) {
	return;
  }

  // // Characters
  // {
  item->setOwner(NULL);
  // 	std::list<Character*>::iterator it;
  // 	for (it = _characteres->begin(); it != _characteres->end(); ++it) {
  // 	  if ((*it)->getItem() == item) {
  // 		_characteres->setItem(NULL);
  // 	  }
  // 	}
  // }

  // Todo
  {
	std::list<BaseItem*>::iterator it;
	for (it = _todo->begin(); it != _todo->end(); ++it) {
	  if (*it == item) {
		_todo->erase(it);
		break;
	  }
	}
  }

  // Building
  {
	std::list<BaseItem*>::iterator it;
	for (it = _building->begin(); it != _building->end(); ++it) {
	  if (*it == item) {
		_building->erase(it);
		break;
	  }
	}
  }

  // BuildingAborted
  {
	std::list<BaseItem*>::iterator it;
	for (it = _buildingAborted->begin(); it != _buildingAborted->end(); ++it) {
	  if (*it == item) {
		_buildingAborted->erase(it);
		break;
	  }
	}
  }

  int newType = BaseItem::NONE;
  if (_items[x][y]->isType(BaseItem::STRUCTURE_FLOOR)) {
	newType = BaseItem::STRUCTURE_FLOOR;
  }

  delete item;
  _items[x][y] = NULL;

  if (newType != BaseItem::NONE) {
	putItem(x, y, newType);
  }
}

void	WorldMap::destroyRoom(int roomId) {
  if (roomId == 0) {
	return;
  }

  // TODO: destroy room

  bool found = true;
  int count = 0;

  while (found) {
	found = false;
	for (int x = 0; x < _width; x++) {
	  for (int y = 0; y < _height; y++) {
		if (_items[x][y] != NULL && _items[x][y]->getRoomId() == roomId) {
		  found = true;
		  int newRoomId = Room::getNewId();
		  Room* room = new Room();
		  room->setId(newRoomId);
		  Room::setZone(x, y, newRoomId, _items[x][y]->getZoneId());
		  _rooms[newRoomId] = room;
		  Info() << "Room create: " << newRoomId << ", old: " << roomId;
		  count++;
		}
	  }
	}
  }

  Info() << "DestroyRoom: " << count << " rooms added";
}

void WorldMap::putItem(int x, int y, int type) {
  if (_itemCout + 1 > LIMIT_ITEMS) {
	Error() << "LIMIT_ITEMS reached";
	return;
  }

  putItem(x, y, type, false);
}

void WorldMap::putItem(int x, int y, int type, bool free) {
  // Return if out of bound
  if (x < 0 || y < 0 || x >= _width || y >= _height) {
	Error() << "put item out of bound, type: "
			<< type << ", x: " << x << ", y: " << y << ")";
	return;
  }

  // Return if item already exists
  if (_items[x][y] != NULL && _items[x][y]->isType(type)) {
	Debug() << "Same item existing for " << x << " x " << y;
	return;
  }

  BaseItem *item = new BaseItem(type, _itemCout++);
  int zoneId = item->getZoneId();
  int roomId = 0;

  // If item alread exists check the roomId
  if (_items[x][y] != NULL)  {
	roomId = _items[x][y]->getRoomId();
  }

  // Wall
  if (item->isStructure() && item->isType(BaseItem::STRUCTURE_FLOOR) == false) {
	_items[x][y] = item;
	// _items[x][y]->setRoomId(roomId);
	// _items[x][y]->setZoneId(0);
	destroyRoom(roomId);
  }

  // Object or floor
  else {

	// Room already exists
	if (roomId > 0 && getRoom(roomId) != NULL) {
	  Room* room = getRoom(roomId);
	  if (room != NULL) {

		// Room have no zoneId
		if (room->getZoneId() == 0) {
		  Info() << "Set room to new zoneId: " << item->getZoneId();
		  room->setZoneId(item->getZoneId());
		}

		// Item have no zoneId
		if (item->getZoneId() == 0) {
		  zoneId = room->getZoneId();
		}

		// Room and item zoneId match
		else if (room->getZoneId() == item->getZoneId()) {
		  Info() << "Room zoneId match with item";
		}

		// Room and item zoneId don't match
		else {
		  Info() << "this item can not be put at this position because zoneId not match (item: "
				 << item->getZoneId() << ", room: " << room->getZoneId() << ")";
		  return;
		}
	  }

	  _items[x][y] = item;
	}

	// Create new room if not exists
	else {
	  _items[x][y] = item;
	  roomId = addRoom(x, y);
	}

	item->setZoneId(zoneId);
	item->setRoomId(roomId);
  }

  // Put item
  Debug() << "put item: " << type;
  item->setPosition(x, y);

  // add to todo list if building is required
  if (free) {
	_items[x][y]->progress = _items[x][y]->matter;
  } else {
	_todo->push_back(item);
  }
}

int		WorldMap::addRoom(int x, int y) {
  Debug() << "addRoom: " << x << " x " << y;

  Room* room = Room::createFromPos(x, y);

  if (room != NULL) {
	_rooms[room->getId()] = room;
  }

  return room->getId();
}

BaseItem*		WorldMap::getItemToBuild() {
  if (_todo->size() == 0) {
	//std::cout << Debug() << "WorldMap: todo list is empty" << std::endl;
	return NULL;
  }

  BaseItem* item = _todo->front();
  _todo->pop_front();
  _building->push_back(item);

  return item;
}

void		WorldMap::buildAbort(BaseItem* item) {
  item->setOwner(NULL);

  std::list<BaseItem*>::iterator it;
  for (it = _building->begin(); it != _building->end(); ++it) {
	if (*it == item) {
	  _buildingAborted->push_back(item);
	  _building->erase(it);
	  Info() << "WorldMap: item building abort";
	  return;
	}
  }
}

void		WorldMap::buildComplete(BaseItem* item) {
  item->setOwner(NULL);

  std::list<BaseItem*>::iterator it;
  for (it = _building->begin(); it != _building->end(); ++it) {
	if (*it == item) {
	  _building->erase(it);
	  Info() << "WorldMap: item now complete";
	  return;
	}
  }
}

void		WorldMap::reloadAborted() {
  std::list<BaseItem*>::iterator it;
  for (it = _buildingAborted->begin(); it != _buildingAborted->end(); ++it) {
	_todo->push_back(*it);
  }
  _buildingAborted->clear();
}

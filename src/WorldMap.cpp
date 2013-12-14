/*
 * WorldMap.cpp
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#include <iostream>
#include <stdio.h>
#include <string.h>
#include <list>
#include "WorldMap.h"
#include "Log.h"

int WorldMap::_roomCount = 0;

WorldMap* WorldMap::_self = new WorldMap();

WorldMap::WorldMap() {
  _roomCount = 0;
  _width = 200;
  _height = 200;
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

  init();
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

BaseItem*	WorldMap::find(int type, bool free) {
  for (int x = 0; x < _width; x++) {
	for (int y = 0; y < _height; y++) {
	  if(_items[x][y] != NULL && _items[x][y]->type == type && _items[x][y]->isComplete()) {
		if (free == false || _items[x][y]->isFree()) {
		  return _items[x][y];
		}
		Error() << "not free: " ;
	  }
	}
  }

  return NULL;
}

//TODO: perf
BaseItem*	WorldMap::getRandomPosInRoom(int roomId) {
  Debug() << "getRandomPosInRoom: " << roomId;

  int count = 0;
  for (int x = 0; x < _width; x++) {
	for (int y = 0; y < _height; y++) {
	  if(_items[x][y] != NULL && _items[x][y]->room == roomId && _items[x][y]->type == BaseItem::STRUCTURE_FLOOR) {
		count++;
	  }
	}
  }
  Debug() << "getRandomPosInRoom found: " << count;

  if (count > 0) {
	int goal = rand() % count;
	for (int x = 0; x < _width; x++) {
	  for (int y = 0; y < _height; y++) {
		if(_items[x][y] != NULL && _items[x][y]->room == roomId && _items[x][y]->type == BaseItem::STRUCTURE_FLOOR) {
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

int	WorldMap::setZone(int x, int y, int zoneId) {
  return setZone(x, y, zoneId, ++_roomCount);
}

int	WorldMap::setZone(int x, int y, int zoneId, int roomId) {

  // Out of bound
  if (x < 0 || x >= _width || y < 0 || y >= _height) {
	return 0;
  }

  // Not a floor
  if (_items[x][y] == NULL ||
	  _items[x][y]->type == BaseItem::STRUCTURE_WALL ||
	  _items[x][y]->type == BaseItem::STRUCTURE_HULL ||
	  _items[x][y]->type == BaseItem::STRUCTURE_DOOR ||
	  _items[x][y]->type == BaseItem::STRUCTURE_WINDOW) {
	return 0;
  }

  // Already tag
  if (_items[x][y]->zone == zoneId) {
	return 0;
  }

  _items[x][y]->zone = zoneId;
  _items[x][y]->room = roomId;
  
  setZone(x, y+1, zoneId, roomId);
  setZone(x, y-1, zoneId, roomId);
  setZone(x+1, y, zoneId, roomId);
  setZone(x-1, y, zoneId, roomId);

  return roomId;
}

void WorldMap::init() {
  putItem(4, 4, 3, true);
  putItem(4, 5, 3, true);
  putItem(4, 6, 3, true);
  putItem(4, 7, 3, true);
  putItem(4, 8, 3, true);
  putItem(4, 9, 3, true);
  putItem(4, 10, 3, true);
  putItem(4, 11, 3, true);
  putItem(4, 12, 3, true);
  putItem(4, 13, 3, true);
  putItem(4, 14, 3, true);
  putItem(4, 15, 3, true);
  putItem(4, 16, 3, true);
  putItem(5, 4, 3, true);
  putItem(5, 5, 5, true);
  putItem(5, 6, 5, true);
  putItem(5, 7, 5, true);
  putItem(5, 8, 5, true);
  putItem(5, 9, 5, true);
  putItem(5, 10, 5, true);
  putItem(5, 11, 5, true);
  putItem(5, 12, 3, true);
  putItem(5, 13, 5, true);
  putItem(5, 14, 5, true);
  putItem(5, 15, 5, true);
  putItem(5, 16, 3, true);
  putItem(6, 4, 3, true);
  putItem(6, 5, 5, true);
  putItem(6, 6, 5, true);
  putItem(6, 7, 5, true);
  putItem(6, 8, 5, true);
  putItem(6, 9, 5, true);
  putItem(6, 10, 5, true);
  putItem(6, 11, 5, true);
  putItem(6, 12, 3, true);
  putItem(6, 13, 5, true);
  putItem(6, 14, 5, true);
  putItem(6, 15, 5, true);
  putItem(6, 16, 3, true);
  putItem(7, 4, 3, true);
  putItem(7, 5, 5, true);
  putItem(7, 6, 5, true);
  putItem(7, 7, 5, true);
  putItem(7, 8, 5, true);
  putItem(7, 9, 5, true);
  putItem(7, 10, 5, true);
  putItem(7, 11, 5, true);
  putItem(7, 12, 3, true);
  putItem(7, 13, 5, true);
  putItem(7, 14, 5, true);
  putItem(7, 15, 5, true);
  putItem(7, 16, 3, true);
  putItem(8, 4, 3, true);
  putItem(8, 5, 5, true);
  putItem(8, 6, 5, true);
  putItem(8, 7, 5, true);
  putItem(8, 8, 5, true);
  putItem(8, 9, 5, true);
  putItem(8, 10, 5, true);
  putItem(8, 11, 5, true);
  putItem(8, 12, 7, true);
  putItem(8, 13, 5, true);
  putItem(8, 14, 5, true);
  putItem(8, 15, 5, true);
  putItem(8, 16, 3, true);
  putItem(9, 4, 3, true);
  putItem(9, 5, 5, true);
  putItem(9, 6, 5, true);
  putItem(9, 7, 5, true);
  putItem(9, 8, 5, true);
  putItem(9, 9, 5, true);
  putItem(9, 10, 5, true);
  putItem(9, 11, 5, true);
  putItem(9, 12, 3, true);
  putItem(9, 13, 5, true);
  putItem(9, 14, 5, true);
  putItem(9, 15, 5, true);
  putItem(9, 16, 3, true);
  putItem(10, 4, 3, true);
  putItem(10, 5, 5, true);
  putItem(10, 6, 5, true);
  putItem(10, 7, 5, true);
  putItem(10, 8, 5, true);
  putItem(10, 9, 5, true);
  putItem(10, 10, 5, true);
  putItem(10, 11, 5, true);
  putItem(10, 12, 3, true);
  putItem(10, 13, 5, true);
  putItem(10, 14, 5, true);
  putItem(10, 15, 5, true);
  putItem(10, 16, 3, true);
  putItem(11, 4, 3, true);
  putItem(11, 5, 5, true);
  putItem(11, 6, 5, true);
  putItem(11, 7, 5, true);
  putItem(11, 8, 5, true);
  putItem(11, 9, 5, true);
  putItem(11, 10, 5, true);
  putItem(11, 11, 5, true);
  putItem(11, 12, 3, true);
  putItem(11, 13, 5, true);
  putItem(11, 14, 5, true);
  putItem(11, 15, 5, true);
  putItem(11, 16, 3, true);
  putItem(12, 4, 3, true);
  putItem(12, 5, 3, true);
  putItem(12, 6, 3, true);
  putItem(12, 7, 3, true);
  putItem(12, 8, 3, true);
  putItem(12, 9, 3, true);
  putItem(12, 10, 3, true);
  putItem(12, 11, 3, true);
  putItem(12, 12, 3, true);
  putItem(12, 13, 5, true);
  putItem(12, 14, 5, true);
  putItem(12, 15, 5, true);
  putItem(12, 16, 3, true);
  putItem(13, 4, 3, true);
  putItem(13, 5, 5, true);
  putItem(13, 6, 5, true);
  putItem(13, 7, 5, true);
  putItem(13, 8, 5, true);
  putItem(13, 9, 5, true);
  putItem(13, 10, 5, true);
  putItem(13, 11, 5, true);
  putItem(13, 12, 3, true);
  putItem(13, 13, 5, true);
  putItem(13, 14, 5, true);
  putItem(13, 15, 5, true);
  putItem(13, 16, 3, true);
  putItem(14, 4, 3, true);
  putItem(14, 5, 5, true);
  putItem(14, 6, 5, true);
  putItem(14, 7, 5, true);
  putItem(14, 8, 5, true);
  putItem(14, 9, 5, true);
  putItem(14, 10, 5, true);
  putItem(14, 11, 5, true);
  putItem(14, 12, 3, true);
  putItem(14, 13, 5, true);
  putItem(14, 14, 5, true);
  putItem(14, 15, 5, true);
  putItem(14, 16, 3, true);
  putItem(15, 4, 3, true);
  putItem(15, 5, 5, true);
  putItem(15, 6, 5, true);
  putItem(15, 7, 5, true);
  putItem(15, 8, 5, true);
  putItem(15, 9, 5, true);
  putItem(15, 10, 5, true);
  putItem(15, 11, 5, true);
  putItem(15, 12, 3, true);
  putItem(15, 13, 5, true);
  putItem(15, 14, 5, true);
  putItem(15, 15, 5, true);
  putItem(15, 16, 3, true);
  putItem(16, 4, 3, true);
  putItem(16, 5, 5, true);
  putItem(16, 6, 5, true);
  putItem(16, 7, 5, true);
  putItem(16, 8, 5, true);
  putItem(16, 9, 5, true);
  putItem(16, 10, 5, true);
  putItem(16, 11, 5, true);
  putItem(16, 12, 7, true);
  putItem(16, 13, 5, true);
  putItem(16, 14, 5, true);
  putItem(16, 15, 5, true);
  putItem(16, 16, 3, true);
  putItem(17, 4, 3, true);
  putItem(17, 5, 5, true);
  putItem(17, 6, 5, true);
  putItem(17, 7, 5, true);
  putItem(17, 8, 5, true);
  putItem(17, 9, 5, true);
  putItem(17, 10, 5, true);
  putItem(17, 11, 5, true);
  putItem(17, 12, 3, true);
  putItem(17, 13, 5, true);
  putItem(17, 14, 5, true);
  putItem(17, 15, 5, true);
  putItem(17, 16, 3, true);
  putItem(18, 4, 3, true);
  putItem(18, 5, 5, true);
  putItem(18, 6, 5, true);
  putItem(18, 7, 5, true);
  putItem(18, 8, 5, true);
  putItem(18, 9, 5, true);
  putItem(18, 10, 5, true);
  putItem(18, 11, 5, true);
  putItem(18, 12, 3, true);
  putItem(18, 13, 5, true);
  putItem(18, 14, 5, true);
  putItem(18, 15, 5, true);
  putItem(18, 16, 3, true);
  putItem(19, 4, 3, true);
  putItem(19, 5, 5, true);
  putItem(19, 6, 5, true);
  putItem(19, 7, 5, true);
  putItem(19, 8, 5, true);
  putItem(19, 9, 5, true);
  putItem(19, 10, 5, true);
  putItem(19, 11, 5, true);
  putItem(19, 12, 3, true);
  putItem(19, 13, 5, true);
  putItem(19, 14, 5, true);
  putItem(19, 15, 5, true);
  putItem(19, 16, 3, true);
  putItem(20, 4, 3, true);
  putItem(20, 5, 3, true);
  putItem(20, 6, 3, true);
  putItem(20, 7, 3, true);
  putItem(20, 8, 3, true);
  putItem(20, 9, 3, true);
  putItem(20, 10, 3, true);
  putItem(20, 11, 3, true);
  putItem(20, 12, 3, true);
  putItem(20, 13, 5, true);
  putItem(20, 14, 5, true);
  putItem(20, 15, 5, true);
  putItem(20, 16, 3, true);
  putItem(21, 4, 3, true);
  putItem(21, 5, 5, true);
  putItem(21, 6, 5, true);
  putItem(21, 7, 5, true);
  putItem(21, 8, 5, true);
  putItem(21, 9, 5, true);
  putItem(21, 10, 5, true);
  putItem(21, 11, 5, true);
  putItem(21, 12, 3, true);
  putItem(21, 13, 5, true);
  putItem(21, 14, 5, true);
  putItem(21, 15, 5, true);
  putItem(21, 16, 3, true);
  putItem(22, 4, 3, true);
  putItem(22, 5, 5, true);
  putItem(22, 6, 5, true);
  putItem(22, 7, 5, true);
  putItem(22, 8, 5, true);
  putItem(22, 9, 5, true);
  putItem(22, 10, 5, true);
  putItem(22, 11, 5, true);
  putItem(22, 12, 3, true);
  putItem(22, 13, 5, true);
  putItem(22, 14, 5, true);
  putItem(22, 15, 5, true);
  putItem(22, 16, 3, true);
  putItem(23, 4, 3, true);
  putItem(23, 5, 5, true);
  putItem(23, 6, 5, true);
  putItem(23, 7, 5, true);
  putItem(23, 8, 5, true);
  putItem(23, 9, 5, true);
  putItem(23, 10, 5, true);
  putItem(23, 11, 5, true);
  putItem(23, 12, 3, true);
  putItem(23, 13, 5, true);
  putItem(23, 14, 5, true);
  putItem(23, 15, 5, true);
  putItem(23, 16, 3, true);
  putItem(24, 4, 3, true);
  putItem(24, 5, 5, true);
  putItem(24, 6, 5, true);
  putItem(24, 7, 5, true);
  putItem(24, 8, 5, true);
  putItem(24, 9, 5, true);
  putItem(24, 10, 5, true);
  putItem(24, 11, 5, true);
  putItem(24, 12, 3, true);
  putItem(24, 13, 5, true);
  putItem(24, 14, 5, true);
  putItem(24, 15, 5, true);
  putItem(24, 16, 3, true);
  putItem(25, 4, 3, true);
  putItem(25, 5, 5, true);
  putItem(25, 6, 5, true);
  putItem(25, 7, 5, true);
  putItem(25, 8, 5, true);
  putItem(25, 9, 5, true);
  putItem(25, 10, 5, true);
  putItem(25, 11, 5, true);
  putItem(25, 12, 7, true);
  putItem(25, 13, 5, true);
  putItem(25, 14, 5, true);
  putItem(25, 15, 5, true);
  putItem(25, 16, 3, true);
  putItem(26, 4, 3, true);
  putItem(26, 5, 5, true);
  putItem(26, 6, 5, true);
  putItem(26, 7, 5, true);
  putItem(26, 8, 5, true);
  putItem(26, 9, 5, true);
  putItem(26, 10, 5, true);
  putItem(26, 11, 5, true);
  putItem(26, 12, 3, true);
  putItem(26, 13, 5, true);
  putItem(26, 14, 5, true);
  putItem(26, 15, 5, true);
  putItem(26, 16, 3, true);
  putItem(27, 4, 3, true);
  putItem(27, 5, 5, true);
  putItem(27, 6, 5, true);
  putItem(27, 7, 5, true);
  putItem(27, 8, 5, true);
  putItem(27, 9, 5, true);
  putItem(27, 10, 5, true);
  putItem(27, 11, 5, true);
  putItem(27, 12, 3, true);
  putItem(27, 13, 5, true);
  putItem(27, 14, 5, true);
  putItem(27, 15, 5, true);
  putItem(27, 16, 3, true);
  putItem(28, 4, 3, true);
  putItem(28, 5, 5, true);
  putItem(28, 6, 5, true);
  putItem(28, 7, 5, true);
  putItem(28, 8, 5, true);
  putItem(28, 9, 5, true);
  putItem(28, 10, 5, true);
  putItem(28, 11, 5, true);
  putItem(28, 12, 3, true);
  putItem(28, 13, 5, true);
  putItem(28, 14, 5, true);
  putItem(28, 15, 5, true);
  putItem(28, 16, 3, true);
  putItem(29, 4, 3, true);
  putItem(29, 5, 3, true);
  putItem(29, 6, 3, true);
  putItem(29, 7, 3, true);
  putItem(29, 8, 3, true);
  putItem(29, 9, 3, true);
  putItem(29, 10, 3, true);
  putItem(29, 11, 3, true);
  putItem(29, 12, 3, true);
  putItem(29, 13, 3, true);
  putItem(29, 14, 7, true);
  putItem(29, 15, 3, true);
  putItem(29, 16, 3, true);
}

void WorldMap::dump() {
  for (int x = 0; x < _width; x++) {
	for (int y = 0; y < _height; y++) {
	  if (_items[x][y] != NULL) {
		Info() << x << " x " << y << " = " << _items[x][y]->type;
	  }
	}
  }
}

void		WorldMap::dumpItems() {
  for (int x = 0; x < _width; x++) {
	for (int y = 0; y < _height; y++) {
	  if (_items[x][y] != NULL && _items[x][y]->isStructure() == false) {
		Info() << x << " x " << y << " = " << _items[x][y]->type;
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

  delete item;
  _items[x][y] = NULL;
}

void WorldMap::putItem(int x, int y, int type) {
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
  if (_items[x][y] != NULL && _items[x][y]->type == type) {
	Debug() << "Same item existing for " << x << " x " << y;
	return;
  }

  BaseItem *item = new BaseItem(type);
  int zoneId = item->getZone();
  int roomId = 0;

  // If item alread exists check the zoneId
  if (_items[x][y] != NULL && _items[x][y]->zone != 0 && zoneId != 0 && _items[x][y]->zone != zoneId) {
	Debug() << "this item can not be put at this position because zoneId not match";
	return;
  }

  // if item is zoned set the zone
  if (_items[x][y] != NULL && _items[x][y]->zone == 0 && zoneId != 0) {
	roomId = setZone(x, y, zoneId);
	item->room = roomId;
  }

  // Put item
  Debug() << "put item: " << type;
  item->setPosition(x, y);
  _items[x][y] = item;

  if (free) {
	_items[x][y]->progress = _items[x][y]->matter;
  } else {
	_todo->push_back(item);
  }
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

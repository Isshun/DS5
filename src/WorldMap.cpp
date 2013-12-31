/*
 * WorldMap.cpp
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#include <iostream>
#include <cstdlib>
#include <stdio.h>
#include <sstream>
#include <string.h>
#include <list>
#include "WorldMap.h"
#include "defines.h"
#include "Log.h"
#include "JobManager.h"

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
  _items = new WorldArea**[_width];
  for (int x = 0; x < _width; x++) {
	_items[x] = new WorldArea*[_height];
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

void	WorldMap::create() {
}

void	WorldMap::load(const char* filePath) {
  ifstream ifs(filePath);
  string line;
  std::vector<std::string> vector;
  int x, y, type;
  bool	inBlock = false;

  if (ifs.is_open()) {
    while (getline(ifs, line)) {

	  // Start block
	  if (line.compare("BEGIN WORLDMAP") == 0) {
		inBlock = true;
	  }

	  // End block
	  else if (line.compare("END WORLDMAP") == 0) {
		inBlock = false;
	  }

	  // Items
	  else if (inBlock) {
		vector.clear();
		FileManager::split(line, '\t', vector);
		if (vector.size() == 3) {
		  std::istringstream issX(vector[0]);
		  std::istringstream issY(vector[1]);
		  std::istringstream issType(vector[2]);
		  issX >> x;
		  issY >> y;
		  issType >> type;
		  putItem(type, x, y, true);
		}
	  }
	}
    ifs.close();
  } else {
	Error() << "Unable to open save file: " << filePath;
  }
}

void	WorldMap::save(const char* filePath) {
  ofstream ofs(filePath, ios_base::app);

  if (ofs.is_open()) {
	ofs << "BEGIN WORLDMAP\n";
	for (int x = 0; x < _width; x++) {
	  for (int y = 0; y < _height; y++) {
		if (_items[x][y] != NULL) {
		  WorldArea* area = _items[x][y];
		  ofs << x << "\t" << y << "\t" << area->getType() << "\n";

		  if (area->getItem() != NULL) {
			BaseItem* item = area->getItem();
			ofs << x << "\t" << y << "\t" << item->getType() << "\n";
		  }
		}
	  }
	}
	ofs << "END WORLDMAP\n";

	ofs.close();
  } else {
	Error() << "Unable to open save file: " << filePath;
  }
}

BaseItem*	WorldMap::find(int type, bool free) {
  Debug() << "WorldMap: find";

  int notFree = 0;

  for (int x = 0; x < _width; x++) {
	for (int y = 0; y < _height; y++) {
	  WorldArea* area = _items[x][y];
	  if (area != NULL) {

		// item
		BaseItem* item = area->getItem();
		if (item != NULL && item->isType(type) && _items[x][y]->isComplete()) {
		  if (free == false || item->isFree()) {
			Debug() << "item found";
			return item;
		  }
		  notFree++;
		}

		// Area
		if(area->isType(type) && area->isComplete()) {
		  if (free == false || area->isFree()) {
			Debug() << "item found";
			return area;
		  }
		  notFree++;
		}
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
	putItem(newType, x, y);
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

BaseItem* WorldMap::putItem(int type, int x, int y) {
  if (_itemCout + 1 > LIMIT_ITEMS) {
	Error() << "LIMIT_ITEMS reached";
	return NULL;
  }

  return putItem(type, x, y, false);
}

BaseItem* WorldMap::putItem(int type, int x, int y, bool free) {
  // Return if out of bound
  if (x < 0 || y < 0 || x >= _width || y >= _height) {
	Error() << "put item out of bound, type: "
			<< type << ", x: " << x << ", y: " << y << ")";
	return NULL;
  }

  // Return if item already exists
  if (_items[x][y] != NULL && _items[x][y]->isType(type)) {
	Debug() << "Same item existing for " << x << " x " << y;
	return NULL;
  }

  // If item alread exists check the roomId
  int roomId = 0;
  if (_items[x][y] != NULL)  {
	roomId = _items[x][y]->getRoomId();
  }

  BaseItem *item = NULL;

  if (type > BaseItem::STRUCTURE_START && type < BaseItem::STRUCTURE_STOP) {
	item = new WorldArea(type, _itemCout++);
  } else {
	item = new BaseItem(type, _itemCout++);
  }
  int zoneId = item->getZoneId();

  // Wall
  if (item->isStructure() && item->isType(BaseItem::STRUCTURE_FLOOR) == false) {
	_items[x][y] = (WorldArea*)item;
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
		  return NULL;
		}
	  }
	}

	// Create new room if not exists
	else {
	  roomId = addRoom(x, y);
	}

	if (type == BaseItem::STRUCTURE_FLOOR) {
	  _items[x][y] = (WorldArea*)item;
	} else {
	  if (_items[x][y] != NULL) {
		_items[x][y]->setItem(item);
	  } else {
		Error() << "Put item on NULL WorldArea";
	  }
	}

	item->setZoneId(zoneId);
	item->setRoomId(roomId);
  }

  // Put item
  Debug() << "put item: " << type;
  item->setPosition(x, y);

  // add to todo list if building is required
  if (free) {
	item->_matterSupply = item->matter;
  } else {
	_todo->push_back(item);
  }

  return item;
}

int		WorldMap::addRoom(int x, int y) {
  Debug() << "addRoom: " << x << " x " << y;

  Room* room = Room::createFromPos(x, y);

  if (room != NULL) {
	_rooms[room->getId()] = room;
	return room->getId();
  } else {
	Error() << "Unable to create Room at position: " << x << " x " << y;
  }
  return  -1;
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

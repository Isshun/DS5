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
#include "Log.hpp"

WorldMap::WorldMap() {
  _todo = new std::list<BaseItem*>();
  _building = new std::list<BaseItem*>();
  _width = 500;
  _height = 500;
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
	// TODO Auto-generated destructor stub
}

void WorldMap::init() {
  // putItem(2, 4, BaseItem::STRUCTURE_HULL);
  // putItem(3, 4, BaseItem::STRUCTURE_HULL);
  // putItem(4, 4, BaseItem::STRUCTURE_HULL);
  // putItem(5, 4, BaseItem::STRUCTURE_HULL);
  // putItem(6, 4, BaseItem::STRUCTURE_HULL);
  // putItem(6, 3, BaseItem::STRUCTURE_HULL);
  // putItem(6, 2, BaseItem::STRUCTURE_HULL);
  // putItem(6, 1, BaseItem::STRUCTURE_HULL);
  // putItem(1, 2, BaseItem::STRUCTURE_HULL);
  // putItem(1, 3, BaseItem::STRUCTURE_HULL);
  // putItem(1, 4, BaseItem::STRUCTURE_HULL);

  // putItem(0, 2, BaseItem::STRUCTURE_FLOOR);
  // putItem(0, 3, BaseItem::STRUCTURE_FLOOR);
  // putItem(0, 4, BaseItem::STRUCTURE_FLOOR);
  // putItem(0, 5, BaseItem::STRUCTURE_FLOOR);
  // putItem(1, 6, BaseItem::STRUCTURE_FLOOR);
  // putItem(2, 7, BaseItem::STRUCTURE_FLOOR);
  // putItem(3, 8, BaseItem::STRUCTURE_FLOOR);
  // putItem(4, 8, BaseItem::STRUCTURE_FLOOR);
  // putItem(5, 8, BaseItem::STRUCTURE_FLOOR);
  // putItem(0, 1, BaseItem::STRUCTURE_FLOOR);
  // putItem(1, 1, BaseItem::STRUCTURE_FLOOR);
  // putItem(2, 1, BaseItem::STRUCTURE_FLOOR);
  // putItem(2, 2, BaseItem::STRUCTURE_FLOOR);
  // putItem(2, 3, BaseItem::STRUCTURE_FLOOR);
  // putItem(3, 2, BaseItem::STRUCTURE_FLOOR);
  // putItem(3, 3, BaseItem::STRUCTURE_FLOOR);
  // putItem(4, 2, BaseItem::STRUCTURE_FLOOR);
  putItem(4, 3, BaseItem::STRUCTURE_FLOOR);
  putItem(5, 2, BaseItem::STRUCTURE_FLOOR);
  putItem(5, 3, BaseItem::STRUCTURE_FLOOR);
  // putItem(1, 4, BaseItem::STRUCTURE_FLOOR);
  // putItem(1, 4, BaseItem::STRUCTURE_FLOOR);
  // putItem(1, 4, BaseItem::STRUCTURE_FLOOR);
  // putItem(1, 4, BaseItem::STRUCTURE_FLOOR);
}

bool WorldMap::getSolid(int x, int y) {
  return false;
}

void WorldMap::putItem(int x, int y, int type) {
  // Return if out of bound
  if (x < 0 || y < 0 || x >= _width || y >= _height) {
	std::cout << Error() << "put item out of bound (type: "
			  << type << ", x: " << x << ", y: " << y << ")" << std::endl;
	return;
  }

  // Return if item already exists
  if (_items[x][y] != NULL && _items[x][y]->type == type) {
	std::cout << Debug() << "Same item existing for " << x << " x " << y << std::endl;
	return;
  }

  // Put item
  std::cout << Debug() << "put item: " << type << std::endl;
  BaseItem *item = new BaseItem(type);
  item->setPosition(x, y);
  _items[x][y] = item;
  _todo->push_back(item);
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
  item->builder = NULL;

  std::list<BaseItem*>::iterator it;
  for (it = _building->begin(); it != _building->end(); ++it) {
	if (*it == item) {
	  _todo->push_back(item);
	  _building->erase(it);
	  std::cout << Info() << "WorldMap: item building abort" << std::endl;
	  return;
	}
  }
}

void		WorldMap::buildComplete(BaseItem* item) {
  item->builder = NULL;

  std::list<BaseItem*>::iterator it;
  for (it = _building->begin(); it != _building->end(); ++it) {
	if (*it == item) {
	  _building->erase(it);
	  std::cout << Info() << "WorldMap: item now complete" << std::endl;
	  return;
	}
  }
}

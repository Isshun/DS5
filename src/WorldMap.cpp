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
  _width = 20;
  _height = 20;
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
  // putItem(4, 3, BaseItem::STRUCTURE_FLOOR);
  // putItem(5, 2, BaseItem::STRUCTURE_FLOOR);
  // putItem(5, 3, BaseItem::STRUCTURE_FLOOR);
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

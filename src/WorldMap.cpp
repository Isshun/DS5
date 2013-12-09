/*
 * WorldMap.cpp
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#include <iostream>
#include <stdio.h>
#include <string.h>
#include "WorldMap.h"

WorldMap::WorldMap() {
	_width = 20;
	_height = 10;
	_items = new BaseItem**[_width];
	for (int x = 0; x < _width; x++) {
		_items[x] = new BaseItem*[_height];
		for (int y = 0; y < _height; y++) {
			_items[x][y] = 0;
		}
	}

	init();
}

WorldMap::~WorldMap() {
	// TODO Auto-generated destructor stub
}

void WorldMap::init() {
  putItem(2, 4, BaseItem::STRUCTURE_HULL);
  putItem(3, 4, BaseItem::STRUCTURE_HULL);
  putItem(4, 4, BaseItem::STRUCTURE_HULL);
  putItem(5, 4, BaseItem::STRUCTURE_HULL);
  putItem(6, 4, BaseItem::STRUCTURE_HULL);
  putItem(6, 3, BaseItem::STRUCTURE_HULL);
  putItem(6, 2, BaseItem::STRUCTURE_HULL);
  putItem(6, 1, BaseItem::STRUCTURE_HULL);
  putItem(1, 2, BaseItem::STRUCTURE_HULL);
  putItem(1, 3, BaseItem::STRUCTURE_HULL);
  putItem(1, 4, BaseItem::STRUCTURE_HULL);

  putItem(0, 2, BaseItem::STRUCTURE_FLOOR);
  putItem(0, 3, BaseItem::STRUCTURE_FLOOR);
  putItem(0, 4, BaseItem::STRUCTURE_FLOOR);
  putItem(0, 5, BaseItem::STRUCTURE_FLOOR);
  putItem(1, 6, BaseItem::STRUCTURE_FLOOR);
  putItem(2, 7, BaseItem::STRUCTURE_FLOOR);
  putItem(3, 8, BaseItem::STRUCTURE_FLOOR);
  putItem(4, 8, BaseItem::STRUCTURE_FLOOR);
  putItem(5, 8, BaseItem::STRUCTURE_FLOOR);
  putItem(0, 1, BaseItem::STRUCTURE_FLOOR);
  putItem(1, 1, BaseItem::STRUCTURE_FLOOR);
  putItem(2, 1, BaseItem::STRUCTURE_FLOOR);
  putItem(2, 2, BaseItem::STRUCTURE_FLOOR);
  putItem(2, 3, BaseItem::STRUCTURE_FLOOR);
  putItem(3, 2, BaseItem::STRUCTURE_FLOOR);
  putItem(3, 3, BaseItem::STRUCTURE_FLOOR);
  putItem(4, 2, BaseItem::STRUCTURE_FLOOR);
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
  if (x < 0 || y < 0 || x >= _width || y >= _height) {
	std::cout << "put item out of bound (type: " << type << ", x: " << x << ", y: " << y << ")" << std::endl;
	return;
  }

  std::cout << "put item: " << type << std::endl;

  BaseItem *item = new BaseItem(type);
  _items[x][y] = item;
}


BaseItem*	WorldMap::getItem(int x, int y) {
  if (x < 0 || x >= _width) return 0;
  if (y < 0 || y >= _height) return 0;

  return _items[x][y];
}

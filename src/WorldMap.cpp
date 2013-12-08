/*
 * WorldMap.cpp
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

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
}

WorldMap::~WorldMap() {
	// TODO Auto-generated destructor stub
}

bool WorldMap::getSolid(int x, int y) {
	return false;
}


void WorldMap::putItem(BaseItem* item, int x, int y) {
	_items[x][y] = item;
}


BaseItem*	WorldMap::getItem(int x, int y) {
	if (x < 0 || x >= _width) return 0;
	if (y < 0 || y >= _height) return 0;

	return _items[x][y];
}

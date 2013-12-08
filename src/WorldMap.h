/*
 * WorldMap.h
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#ifndef WORLDMAP_H_
#define WORLDMAP_H_

#include "BaseItem.h"

class WorldMap {
public:
				WorldMap();
				~WorldMap();

	int			getWidth() { return _width; }
	int			getHeight() { return _height; }

	bool		getSolid(int x, int y);

	void		putItem(int x, int y, int type);
	BaseItem*	getItem(int x, int y);

private:
	BaseItem***	_items;
	int			_width;
	int			_height;

	void		init();
};

#endif /* WORLDMAP_H_ */

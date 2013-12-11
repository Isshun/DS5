/*
 * WorldMap.h
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#ifndef WORLDMAP_H_
#define WORLDMAP_H_

#include <list>
#include "BaseItem.h"

class WorldMap {
public:
				WorldMap();
				~WorldMap();

	int			getWidth() { return _width; }
	int			getHeight() { return _height; }
	BaseItem*	getItemToBuild();
	void		buildComplete(BaseItem* item);
	void		buildAbort(BaseItem* item);
	bool		getSolid(int x, int y);
	void		putItem(int x, int y, int type);
	BaseItem*	getItem(int x, int y) {return (x < 0 || x >= _width || y < 0 || y >= _height) ? NULL : _items[x][y]; }
	int			getBuildListSize() { return _todo->size(); }
	void		setZone(int x, int y, int zoneId);
	void		reloadAborted();
	void		dump();

private:
	void		putItem(int x, int y, int type, bool free);

	BaseItem***	_items;
	int			_width;
	int			_height;
	std::list<BaseItem*>*		_todo;
	std::list<BaseItem*>*		_building;
	std::list<BaseItem*>*		_buildingAborted;

	void		init();

	enum		{GO_UP, GO_DOWN, GO_LEFT, GO_RIGHT};
};

#endif /* WORLDMAP_H_ */

/*
 * WorldMap.h
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#ifndef WORLDMAP_H_
#define WORLDMAP_H_

#include <map>
#include <list>
#include "BaseItem.h"
#include "Room.h"

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
	Room*		getRoom(int id) { return _rooms[id]; }

	int			setZone(int x, int y, int zoneId, int roomId);
	int			setZone(int x, int y, int zoneId);
	void		reloadAborted();
	void		dump();
	void		dumpItems();
	BaseItem*	find(int type, bool free);
	static WorldMap*	getInstance() { return _self; }
	BaseItem*	getRandomPosInRoom(int roomId);
	void		removeItem(int x, int y);
	void		init();
	void		initMap();
	void		initRoom();
	void		addRoom(int x, int y);

private:
	void		putItem(int x, int y, int type, bool free);
	static WorldMap* _self;
	int			_itemCout;

	std::map<int, Room*>	_rooms;
	BaseItem***	_items;
	int			_width;
	int			_height;
	std::list<BaseItem*>*		_todo;
	std::list<BaseItem*>*		_building;
	std::list<BaseItem*>*		_buildingAborted;
	/* std::list<Room*>*			_rooms; */

	enum		{GO_UP, GO_DOWN, GO_LEFT, GO_RIGHT};
};

#endif /* WORLDMAP_H_ */

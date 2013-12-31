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
#include "FileManager.h"

class WorldArea : public BaseItem {
 public:
 WorldArea(int type, int id) : BaseItem(type, id) {
	_item = NULL;
	_oxygen = rand() % 100;
  }
  ~WorldArea() {}

  void			setItem(BaseItem* item) { _item = item; }
  void			setOxygen(int oxygen) { _oxygen = oxygen; }
  BaseItem*		getItem() { return _item; }
  int			getOxygen() { return _oxygen; }
  bool			isType(int type) { return _type == type; }

 private:
  int			_oxygen;
  BaseItem*		_item;
};

class WorldMap : public Serializable {
 public:
  WorldMap();
  ~WorldMap();

  void					create();
  void					load(const char* filePath);
  void					save(const char* filePath);

  // Actions
  BaseItem*				putItem(int type, int x, int y);
  void					buildComplete(BaseItem* item);
  void					buildAbort(BaseItem* item);
  void					reloadAborted();
  void					dump();
  void					dumpItems();
  BaseItem*				find(int type, bool free);
  void					removeItem(int x, int y);
  void					initRoom();
  int					addRoom(int x, int y);
  void					destroyRoom(int roomId);

  // Gets
  static WorldMap*		getInstance() { return _self; }
  int					getWidth() { return _width; }
  int					getHeight() { return _height; }
  BaseItem*				getItemToBuild();
  bool					getSolid(int x, int y);
  BaseItem*				getItem(int x, int y) {
	return (x < 0 || x >= _width || y < 0 || y >= _height) || _items[x][y] == NULL ? NULL : _items[x][y]->getItem();
  }
  WorldArea*			getArea(int x, int y) {
	return (x < 0 || x >= _width || y < 0 || y >= _height) ? NULL : _items[x][y];
  }
  void					debugAstar(int x, int y) { _tmp[x][y] = 1; }
  int					getBuildListSize() { return _todo->size(); }
  Room*					getRoom(int id) { return _rooms[id]; }
  int					getRoomCount() { return _rooms.size(); }
  BaseItem*				getRandomPosInRoom(int roomId);

  // Sets
  int					setZone(int x, int y, int zoneId, int roomId);
  int					setZone(int x, int y, int zoneId);

 private:
  BaseItem*				putItem(int type, int x, int y, bool free);

  std::list<BaseItem*>*	_todo;
  std::list<BaseItem*>*	_building;
  std::list<BaseItem*>*	_buildingAborted;
  std::map<int, Room*>	_rooms;
  static WorldMap* 		_self;
  int					_itemCout;
  WorldArea***			_items;
  int					_width;
  int					_height;
  int					_tmp[250][250];
};

#endif /* WORLDMAP_H_ */

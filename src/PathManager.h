#ifndef _C_PATHMANAGER_
#define _C_PATHMANAGER_

#define _GLIBCXX_USE_NANOSLEEP
#include <iostream>
#include <string>
#include <thread>
#include <map>

#include "ThreadPool.h"

#include "defines.h"
#include "MapSearchNode.h"
#include "BaseItem.h"

class	Character;
class	BaseItem;

class IPathManagerCallback {
 public:
  virtual void	onPathSearch(Path* path, BaseItem* item) = 0;
  virtual void	onPathComplete(Path* path, BaseItem* item) = 0;
  virtual void	onPathFailed(BaseItem* item) = 0;
};

class	PathManager
{
 public:
	PathManager();
	~PathManager();
	void							getPathAsync(Character* character, BaseItem* item);
	void							init();

	static PathManager*	getInstance() { return _self; }

 private:
	AStarSearch<MapSearchNode>*		getPath(MapSearchNode nodeStart, MapSearchNode nodeEnd);
	AStarSearch<MapSearchNode>*		getPath(Character* character, BaseItem* item);

	map<pair<BaseItem*, BaseItem*>, AStarSearch<MapSearchNode>*>*	_data;

	/* std::multimap<int, int>*		_map; */
	int		_map[LIMIT_CHARACTER][LIMIT_ITEMS];
	ThreadPool*	_pool;
	static PathManager* _self;

};

#endif

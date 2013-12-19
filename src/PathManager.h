#ifndef _C_PATHMANAGER_
#define _C_PATHMANAGER_

#include <map>
#include "defines.h"
#include "MapSearchNode.h"
#include "BaseItem.h"

class	Character;
class	BaseItem;

class	PathManager
{
 public:
	PathManager();
	~PathManager();
	AStarSearch<MapSearchNode>*		getPath(MapSearchNode nodeStart, MapSearchNode nodeEnd);
	AStarSearch<MapSearchNode>*		getPath(Character* character, BaseItem* item);
	void							init();

	static PathManager*	getInstance() { return _self; }

 private:
	map<pair<BaseItem*, BaseItem*>, AStarSearch<MapSearchNode>*>*	_data;

	/* std::multimap<int, int>*		_map; */
	int		_map[LIMIT_CHARACTER][LIMIT_ITEMS];
	static PathManager* _self;

};

#endif

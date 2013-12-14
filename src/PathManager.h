#ifndef _C_PATHMANAGER_
#define _C_PATHMANAGER_

#include <map>
#include "defines.h"
#include "MapSearchNode.h"

class	Character;
class	BaseItem;

class	PathManager
{
 public:
	PathManager();
	~PathManager();
	AStarSearch<MapSearchNode>*		getPath(MapSearchNode nodeStart, MapSearchNode nodeEnd);
	AStarSearch<MapSearchNode>*		getPath(Character* character, BaseItem* item);

	static PathManager*	getInstance() { return _self; }

 private:
	std::multimap<Character*, BaseItem*>*		_map;
	static PathManager* _self;

};

#endif

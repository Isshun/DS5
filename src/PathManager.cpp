#include <list>

#include "defines.h"
#include "PathManager.h"
#include "Character.h"
#include "BaseItem.h"

PathManager* PathManager::_self = new PathManager();

PathManager::PathManager() {
  _data = new map<pair<BaseItem*, BaseItem*>, AStarSearch<MapSearchNode>*>();
  memset(_map, 0, LIMIT_CHARACTER * LIMIT_ITEMS * sizeof(int));
}

PathManager::~PathManager() {
  // delete _map;
}

void							PathManager::init() {
  Info() << "PathManager: init";

  std::list<BaseItem*> list;
  int w = WorldMap::getInstance()->getWidth();
  int h = WorldMap::getInstance()->getHeight();
  for (int i = w-1; i >= 0; i--) {
  	for (int j = h-1; j >= 0; j--) {
	  BaseItem* item = WorldMap::getInstance()->getItem(i, j);
	  if (item != NULL && item->getType() == BaseItem::STRUCTURE_DOOR) {
		Debug() << "Door at pos: " << i << " x " << j;
		list.push_back(item);
	  }
	}
  }

  std::list<BaseItem*>::iterator it1;
  std::list<BaseItem*>::iterator it2;
  for (it1 = list.begin(); it1 != list.end(); ++it1) {
	for (it2 = list.begin(); it2 != list.end(); ++it2) {
	  if (*it1 != *it2) {
		MapSearchNode nodeStart;
		nodeStart.x = (*it1)->getX();
		nodeStart.y = (*it1)->getY();

		MapSearchNode nodeEnd;
		nodeEnd.x = (*it2)->getX();
		nodeEnd.y = (*it2)->getY();

		AStarSearch<MapSearchNode>* path = getPath(nodeStart, nodeEnd);
		pair<BaseItem*, BaseItem*> key = std::make_pair(*it1, *it2);

		_data->insert(make_pair(make_pair(*it1, *it2), path));

		Info() << "PathManager: find path"
			   << " from: " << nodeStart.x << " x " << nodeStart.y
			   << ", to: " << nodeEnd.x << " x " << nodeEnd.y
			   << ", cost: " << path->GetSolutionCost();
		  // done (" << _data->size() << " path)";
	  }
	}
  }

  Info() << "PathManager: init done (" << _data->size() << " path)";
}

AStarSearch<MapSearchNode>*		PathManager::getPath(MapSearchNode nodeStart, MapSearchNode nodeEnd) {
  // std::multimap<int,int>::iterator it;
  // it = _map->find(std::pair<int, int>(nodeStart.x + nodeStart.y * WORLD_MAX_SIZE,

  // if (it != multimap::end) {
  // 	Error() << "PathManager: this path is already know and cannot be resolve";
  // }

  AStarSearch<MapSearchNode>* astarsearch = new AStarSearch<MapSearchNode>();
  unsigned int SearchCount = 0;
  const unsigned int NumSearches = 1;

  while(SearchCount < NumSearches) {

	 // Set Start and goal states
	 astarsearch->SetStartAndGoalStates( nodeStart, nodeEnd );

	 unsigned int SearchState;
	 unsigned int SearchSteps = 0;

	 do {
	   SearchState = astarsearch->SearchStep();
	   SearchSteps++;
	 }
	 while( SearchState == AStarSearch<MapSearchNode>::SEARCH_STATE_SEARCHING );

	 // Path found
	 if( SearchState == AStarSearch<MapSearchNode>::SEARCH_STATE_SUCCEEDED ) {
	   Debug() << "Search found goal state: " << SearchSteps;
	   return astarsearch;
	 }

	 // No path found
	 else if( SearchState == AStarSearch<MapSearchNode>::SEARCH_STATE_FAILED ) {
	   Warning() << "Search terminated. Did not find goal state\n";
	   Debug() << "free 2";
	   astarsearch->EnsureMemoryFreed();
	   delete astarsearch;
	   // _map->insert(std::pair<int, int>(nodeStart.x + nodeStart.y * WORLD_MAX_SIZE,
	   // 									nodeEnd.x + nodeEnd.y * WORLD_MAX_SIZE));
	 }

	 SearchCount ++;
   }

  return NULL;
}

AStarSearch<MapSearchNode>*		PathManager::getPath(Character* character, BaseItem* item) {

  if (_map[character->getId()][item->getId()]) {
	Error() << "PathManager: this path is already know and cannot be resolve";
  }

  MapSearchNode nodeStart;
  nodeStart.x = character->getX();
  nodeStart.y = character->getY();

  MapSearchNode nodeEnd;
  nodeEnd.x = item->getX();
  nodeEnd.y = item->getY();

  AStarSearch<MapSearchNode>* path = getPath(nodeStart, nodeEnd);

  if (path == NULL) {
	_map[character->getId()][item->getId()] = 1;
  } else {
	_map[character->getId()][item->getId()] = 0;
  }

  return path;
}

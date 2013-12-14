#include "defines.h"
#include "PathManager.h"
#include "Character.h"
#include "BaseItem.h"

PathManager* PathManager::_self = new PathManager();

PathManager::PathManager() {
  // _map = new std::multimap<int, int>();
  memset(_map, 0, LIMIT_CHARACTER * LIMIT_ITEMS * sizeof(int));
}

PathManager::~PathManager() {
  // delete _map;
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

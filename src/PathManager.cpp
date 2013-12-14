#include "PathManager.h"
#include "Character.h"
#include "BaseItem.h"

PathManager* PathManager::_self = new PathManager();

PathManager::PathManager() {
	std::multimap<Character*, BaseItem*>*		_map;
}

PathManager::~PathManager() {
  delete _map;
}

AStarSearch<MapSearchNode>*		PathManager::getPath(MapSearchNode nodeStart, MapSearchNode nodeEnd) {
  AStarSearch<MapSearchNode>* astarsearch = new AStarSearch<MapSearchNode>();
  unsigned int SearchCount = 0;
  const unsigned int NumSearches = 1;

  while(SearchCount < NumSearches) {

	 // // Create a start state
	 // MapSearchNode nodeStart;
	 // nodeStart.x = fromX;
	 // nodeStart.y = fromY;

	 // // Define the goal state
	 // MapSearchNode nodeEnd;
	 // nodeEnd.x = toX;
	 // nodeEnd.y = toY;

	 // Set Start and goal states
	 astarsearch->SetStartAndGoalStates( nodeStart, nodeEnd );

	 unsigned int SearchState;
	 unsigned int SearchSteps = 0;

	 do {
	   SearchState = astarsearch->SearchStep();
	   SearchSteps++;
	 }
	 while( SearchState == AStarSearch<MapSearchNode>::SEARCH_STATE_SEARCHING );

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
	 }

	 SearchCount ++;
   }

  return NULL;
}

AStarSearch<MapSearchNode>*		PathManager::getPath(Character* character, BaseItem* item) {
  MapSearchNode nodeStart;
  nodeStart.x = character->getX();
  nodeStart.y = character->getY();

  MapSearchNode nodeEnd;
  nodeEnd.x = item->getX();
  nodeEnd.y = item->getY();

  return getPath(nodeStart, nodeEnd);
}

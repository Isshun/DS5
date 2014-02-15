#include <list>

#include "defines.h"
#include "PathManager.h"
#include "Character.h"
#include "Job.h"

#include "ThreadPool.h"

PathManager* PathManager::_self = new PathManager();

PathManager::PathManager() {
  _pool = new ThreadPool(4);
  _data = new map<int, AStarSearch<MapSearchNode>*>();
  memset(_map, 0, LIMIT_CHARACTER * LIMIT_ITEMS * sizeof(int));
}

PathManager::~PathManager() {
  // delete _map;
  delete		_pool;
}

int								PathManager::getSum(int fromX, int fromY, int toX, int toY) {
  int sum = 0;
  sum += fromX;
  sum = sum << 16;
  sum += fromY;
  sum = sum << 16;
  sum += toX;
  sum = sum << 16;
  sum += toY;

  Info() << "sum: " << sum;

  // int y = sum & 0xFFFF;
  // int x = sum >> 12;

  // Info() << "after: " << x << ", " << y;
}

void							PathManager::init() {
  Info() << "PathManager: init";

  _storage = new list<AStarSearch<MapSearchNode>*>();



  int max = 2;

  for (int fromX = 0; fromX < max; fromX++) {
  	for (int fromY = 0; fromY < max; fromY++) {
  	  for (int toX = 0; toX < max; toX++) {
  		for (int toY = 0; toY < max; toY++) {
  		  AStarSearch<MapSearchNode>* path = getPath(fromX, fromY, toX, toY);
  		  // _storage->push_back(path);
		  _data->insert(make_pair(getSum(fromX, fromY, toX, toY), path));

  		  // Info() << "size: " << _storage->size();
  		}
  	  }
  	}
  }


  // exit(0);

  // std::list<Job*> list;
  // int w = WorldMap::getInstance()->getWidth();
  // int h = WorldMap::getInstance()->getHeight();
  // for (int i = w-1; i >= 0; i--) {
  // 	for (int j = h-1; j >= 0; j--) {
  // 	  Job* item = WorldMap::getInstance()->getArea(i, j);
  // 	  if (item != NULL && item->getType() == Job::STRUCTURE_DOOR) {
  // 		Debug() << "Door at pos: " << i << " x " << j;
  // 		list.push_back(item);
  // 	  }
  // 	}
  // }

  // std::list<Job*>::iterator it1;
  // std::list<Job*>::iterator it2;
  // for (it1 = list.begin(); it1 != list.end(); ++it1) {
  // 	for (it2 = list.begin(); it2 != list.end(); ++it2) {
  // 	  if (*it1 != *it2) {
  // 		MapSearchNode nodeStart;
  // 		nodeStart.x = (*it1)->getX();
  // 		nodeStart.y = (*it1)->getY();

  // 		MapSearchNode nodeEnd;
  // 		nodeEnd.x = (*it2)->getX();
  // 		nodeEnd.y = (*it2)->getY();

  // 		AStarSearch<MapSearchNode>* path = getPath(nodeStart, nodeEnd);
  // 		pair<Job*, Job*> key = std::make_pair(*it1, *it2);

  // 		_data->insert(make_pair(make_pair(*it1, *it2), path));

  // 		Info() << "PathManager: find path"
  // 			   << " from: " << nodeStart.x << " x " << nodeStart.y
  // 			   << ", to: " << nodeEnd.x << " x " << nodeEnd.y
  // 			   << ", cost: " << path->GetSolutionCost();
  // 		  // done (" << _data->size() << " path)";
  // 	  }
  // 	}
  // }

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

	 Debug() << "PathManager: searching...";
	 do {
	   SearchState = astarsearch->SearchStep();
	   SearchSteps++;
	   WorldMap::getInstance()->dump();
	   // if (SearchSteps > 10000 && SearchSteps % 100 == 0)
	   // 	 Debug() << "PathManager: step " << SearchSteps;
	 }
	 while( SearchState == AStarSearch<MapSearchNode>::SEARCH_STATE_SEARCHING );
	 Debug() << "PathManager: search complete";

	 // Path found
	 if( SearchState == AStarSearch<MapSearchNode>::SEARCH_STATE_SUCCEEDED ) {
	   Debug() << "Search found goal state: " << SearchSteps;
	   return astarsearch;
	 }

	 // No path found
	 else if( SearchState == AStarSearch<MapSearchNode>::SEARCH_STATE_FAILED ) {
	   Debug() << "Search terminated. Did not find goal state";
	   astarsearch->EnsureMemoryFreed();
	   delete astarsearch;
	   // _map->insert(std::pair<int, int>(nodeStart.x + nodeStart.y * WORLD_MAX_SIZE,
	   // 									nodeEnd.x + nodeEnd.y * WORLD_MAX_SIZE));
	 }

	 SearchCount++;
   }

  return NULL;
}

AStarSearch<MapSearchNode>*		PathManager::getPath(int fromX, int fromY, int toX, int toY) {

  MapSearchNode nodeStart;
  nodeStart.x = fromX;
  nodeStart.y = fromY;

  MapSearchNode nodeEnd;
  nodeEnd.x = toX;
  nodeEnd.y = toY;

  AStarSearch<MapSearchNode>* path = getPath(nodeStart, nodeEnd);

  return path;
}

AStarSearch<MapSearchNode>*		PathManager::getPath(Character* character, Job* item) {

  // if (_map[character->getId()][item->getId()]) {
  // 	Error() << "PathManager: this path is already know and cannot be resolve";
  // 	return NULL;
  // }

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

// void	PathManager::getPathAsync(Character* character, int x, int y) {

//   _pool->enqueue([this, character, x, y] {

// 	  MapSearchNode nodeStart;
// 	  nodeStart.x = character->getX();
// 	  nodeStart.y = character->getY();

// 	  MapSearchNode nodeEnd;
// 	  nodeEnd.x = x;
// 	  nodeEnd.y = y;

// 	  AStarSearch<MapSearchNode>* path = getPath(nodeStart, nodeEnd);

// 	  if (path != NULL) {
// 		character->onPathComplete(path, NULL);
// 	  } else {
// 		character->onPathFailed(NULL);
// 	  }

// 	});
// }

void	PathManager::getPathAsync(Character* character, Job* job) {

  _pool->enqueue([this, character, job] {

	  AStarSearch<MapSearchNode>* path = this->getPath(character, job);

	  if (path != NULL) {
		character->onPathComplete(path, job);
	  } else {
		character->onPathFailed(job);
	  }

	});
}

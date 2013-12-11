#include <iostream>
#include <SFML/Graphics.hpp>
#include "defines.h"
#include "Character.h"
#include "ResourceManager.h"
#include "stlastar.h"

extern WorldMap* gl_worldmap;

Character::Character(int x, int y) {
  _astarsearch = NULL;
  _job = NULL;
  _posY = y;
  _posX = x;
}

Character::~Character() {
}

void	Character::build(BaseItem* item) {
  _job = item;
  item->builder = this;
  int posX = item->getX();
  int posY = item->getY();
  go(posX, posY);
}

void		Character::go(int toX, int toY) {
  cout << Debug() << "Charactere: go(" << toX << ", " << toY << ")" << endl;

  if (_astarsearch != 0) {
	_astarsearch->FreeSolutionNodes();
	std::cout << Debug() << "free 1" << std::endl;
	_astarsearch->EnsureMemoryFreed();
	delete _astarsearch;
	_astarsearch = 0;
  }

  _steps = 0;
  _astarsearch = new AStarSearch<MapSearchNode>();

  unsigned int SearchCount = 0;

  const unsigned int NumSearches = 1;

  while(SearchCount < NumSearches) {

	 // Create a start state
	 MapSearchNode nodeStart;
	 nodeStart.x = _posX;
	 nodeStart.y = _posY;

	 // Define the goal state
	 MapSearchNode nodeEnd;
	 nodeEnd.x = toX;
	 nodeEnd.y = toY;

	 // Set Start and goal states
	 _astarsearch->SetStartAndGoalStates( nodeStart, nodeEnd );

	 unsigned int SearchState;
	 unsigned int SearchSteps = 0;

	 do {
	   SearchState = _astarsearch->SearchStep();
	   SearchSteps++;
	 }
	 while( SearchState == AStarSearch<MapSearchNode>::SEARCH_STATE_SEARCHING );

	 if( SearchState == AStarSearch<MapSearchNode>::SEARCH_STATE_SUCCEEDED ) {
	   // cout << "Search found goal state: " << SearchSteps << endl;
	 }

	 // No path found
	 else if( SearchState == AStarSearch<MapSearchNode>::SEARCH_STATE_FAILED ) {
	   cout << Warning() << "Search terminated. Did not find goal state\n";
	   gl_worldmap->buildAbort((BaseItem*)_job);
	   _job = NULL;
	   std::cout << Debug() << "free 2" << std::endl;
	   _astarsearch->EnsureMemoryFreed();
	   delete _astarsearch;
	   _astarsearch = 0;
	 }

	 SearchCount ++;
   }
}

void		Character::move()
{
  // Move
  if (_astarsearch != NULL) {

	// Next node
	MapSearchNode *node = 0;
	if (_steps == 0) {
	  node = _astarsearch->GetSolutionStart();
	} else {
	  node = _astarsearch->GetSolutionNext();
	}

	// Goto node
	if (node) {
	  node->PrintNodeInfo();
	  _posX = node->x;
	  _posY = node->y;
	  _steps++;
	}

	// clear path
	else {
	  std::cout << Debug() << "Character: reached" << std::endl;
	  _astarsearch->FreeSolutionNodes();
	  std::cout << Debug() << "free 3" << std::endl;
	  _astarsearch->EnsureMemoryFreed();
	  delete _astarsearch;
	  _astarsearch = 0;
	}
  }

  // Work
  if (_job != NULL) {
	BaseItem* item = (BaseItem*)_job;
	if (item->getX() == _posX && item->getY() == _posY) {
	  switch (ResourceManager::getInstance().build(item)) {
	  case ResourceManager::NO_MATTER:
		std::cout << Debug() << "Character: not enough matter" << std::endl;
		gl_worldmap->buildAbort(item);
		_job = NULL;
		break;
	  case ResourceManager::BUILD_COMPLETE:
		std::cout << Debug() << "Character: build complete" << std::endl;
		gl_worldmap->buildComplete(item);
		_job = NULL;
	  case ResourceManager::BUILD_PROGRESS:
		std::cout << Debug() << "Character: build progress" << std::endl;
	  }
	}
  }

}

#include <iostream>
#include <SFML/Graphics.hpp>
#include "defines.h"
#include "Character.h"
#include "ResourceManager.h"
#include "stlastar.h"
#include "WorldMap.h"

const char* firstname[] = {
  "Vic",
  "Lewis",
  "Alice",
  "Adam",
  "Janice",
  "Ezri",
  "Tom",
  "Jadzia",
};

const char* middlename[] = {
  "Harry",
  "Apollo",
  "Boomer",
  "",
  "",
  "",
  "",
  "",
};

const char* lastname[] = {
  "Zimmerman",
  "Dax",
  "Nerys",
  "Laren",
  "Barclay",
  "Mudd",
  "Rand",
  "McCoy",
};

const Job jobs[] = {
  {Character::JOB_ENGINEER, "Engineer"},
  {Character::JOB_MINER, "Miner"},
  {Character::JOB_DOCTOR, "Doctor"},
  {Character::JOB_SCIENCE, "Science"}
};

Character::Character(int x, int y) {
  std::cout << Debug() << "Character" << std::endl;

  _astarsearch = NULL;
  _job = NULL;
  _posY = y;
  _posX = x;
  _sleep = 0;

  _jobName = jobs[rand() % 4].name;

  // Needs
  _food = CHARACTER_INIT_FOOD;
  _oxygen = CHARACTER_INIT_OXYGEN;
  _hapiness = CHARACTER_INIT_HAPINESS;
  _health = CHARACTER_INIT_HEALTH;
  _energy = CHARACTER_INIT_ENERGY;

  const char* middle = middlename[rand() % 8];
  if (strlen(middle) == 0) {
    snprintf(_name, 20, "%s %s", firstname[rand() % 8], lastname[rand() % 8]);
  } else {
    snprintf(_name, 20, "%s (%s) %s", firstname[rand() % 8], middle, lastname[rand() % 8]);
  }

  std::cout << Debug() << "Character done: " << _name << std::endl;
}

Character::~Character() {
  if (_astarsearch != NULL) {
	_astarsearch->FreeSolutionNodes();
	std::cout << Debug() << "free 1" << std::endl;
	_astarsearch->EnsureMemoryFreed();
	delete _astarsearch;
	_astarsearch = NULL;
  }
}

void	Character::build(BaseItem* item) {
  _job = item;
  item->setOwner(this);
  int posX = item->getX();
  int posY = item->getY();
  go(posX, posY);
}

void	Character::setItem(BaseItem* item) {
  BaseItem* currentItem = _job;

  _job = item;

  if (currentItem != NULL && currentItem->getOwner() != NULL) {
	currentItem->setOwner(NULL);
  }

  if (item != NULL && item->getOwner() != this) {
	item->setOwner(this);
  }
}

void	Character::use(BaseItem* item) {
  if (_job != NULL && _job->isComplete() == false) {
	WorldMap::getInstance()->buildAbort((BaseItem*)_job);
	_job = NULL;
  }
  build(item);
}

void  Character::updateNeeds() {
  if (_sleep > 0) {
	_sleep--;
	// If current item is not under construction: abort
	if (_sleep == 0 && _job != NULL && _job->isComplete()) {
	  _job->setOwner(NULL);
	  _job = NULL;
	}
	return;
  }

  if (_food > 0) {
	_food -= 2;
  }

  if (_oxygen > 0) {
	_oxygen = 0;
  }

  if (_energy > 0) {
	_energy -= 1;
  }

  //if (_hapiness > 0)_hapiness = 0;
  //if (_health > 0)_health = 0;
}

void  Character::update() {

  // Character as already a job or is sleeping
  if (_sleep > 0) {
	return;
  }

  if (_job != NULL && _job->isSleepingItem()) {
	return;
  }

  // Energy
  if (_energy < 20) {
	cout << Debug() << "Charactere: need sleep: " << _energy << endl;

	// Sleep in bed
	{
	  BaseItem* item = WorldMap::getInstance()->find(BaseItem::QUARTER_BED, true);
	  if (item != NULL) {
		use(item);
		return;
	  }
	}

	// Sleep in chair
	{
	  BaseItem* item = WorldMap::getInstance()->find(BaseItem::QUARTER_CHAIR, true);
	  if (item != NULL) {
		use(item);
		return;
	  }
	}

  }

  // Sleep on the ground
  if (_energy == 0) {
	_sleep = 20;
	_energy = 80;
	return;
  }

  // Need food
  if (_food < 20) {
	cout << Debug() << "Charactere: need food" << endl;

	BaseItem* item = WorldMap::getInstance()->find(BaseItem::BAR_PUB, false);
	if (item != NULL) {
	  use(item);
	  return;
	}
  }

}

void		Character::go(int toX, int toY) {
  cout << Debug() << "Charactere: go(" << toX << ", " << toY << ")" << endl;

  if (_astarsearch != NULL) {
	_astarsearch->FreeSolutionNodes();
	std::cout << Debug() << "free 1" << std::endl;
	_astarsearch->EnsureMemoryFreed();
	delete _astarsearch;
	_astarsearch = NULL;
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
	   if (_job != NULL) {
		 WorldMap::getInstance()->buildAbort((BaseItem*)_job);
		 _job = NULL;
	   }
	   std::cout << Debug() << "free 2" << std::endl;
	   _astarsearch->EnsureMemoryFreed();
	   delete _astarsearch;
	   _astarsearch = NULL;
	 }

	 SearchCount ++;
   }
}

void		Character::move() {
  // Character is sleeping
  if (_sleep != 0) {
	return;
  }

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

	  // Use
	  if (item->isComplete()) {
		switch (item->type) {

		  // Bar
		case BaseItem::BAR_PUB:
		  {
			_food = 100;
			BaseItem* goal = WorldMap::getInstance()->getRandomPosInRoom(item->room);
			if (goal != NULL) {
			  go(goal->getX(), goal->getY());
			}
			_job = NULL;
		  }
		  break;

		  // Bed
		case BaseItem::QUARTER_BED:
		  item->setOwner(this);
		  _hapiness += 1;
		  _sleep = 20;
		  _energy = 100;
		  if (_health > 40) {
			_health += 2;
		  }
		  break;

		  // Chair
		case BaseItem::QUARTER_CHAIR:
		  item->setOwner(this);
		  _hapiness -= 1;
		  _sleep = 20;
		  _energy = 100;
		  if (_health > 40) {
			_health += 1;
		  }
		  break;

		}
	  }

	  // Build
	  else {
		switch (ResourceManager::getInstance().build(item)) {
		case ResourceManager::NO_MATTER:
		  std::cout << Debug() << "Character: not enough matter" << std::endl;
		  WorldMap::getInstance()->buildAbort(item);
		  _job = NULL;
		  break;
		case ResourceManager::BUILD_COMPLETE:
		  std::cout << Debug() << "Character: build complete" << std::endl;
		  WorldMap::getInstance()->buildComplete(item);
		  _job = NULL;
		  go(_posX + 1, _posY);
		case ResourceManager::BUILD_PROGRESS:
		  std::cout << Debug() << "Character: build progress" << std::endl;
		}
	  }
	}
  }

}

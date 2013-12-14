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
  _item = NULL;
  _build = NULL;
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
  _build = item;
  _build->setOwner(this);
  int posX = item->getX();
  int posY = item->getY();
  go(posX, posY);
}

void	Character::setItem(BaseItem* item) {
  BaseItem* currentItem = _item;

  _item = item;

  if (currentItem != NULL && currentItem->getOwner() != NULL) {
	currentItem->setOwner(NULL);
  }

  if (item != NULL && item->getOwner() != this) {
	item->setOwner(this);
  }
}

void	Character::use(BaseItem* item) {
  if (_item != NULL && _item->isComplete() == false) {
	WorldMap::getInstance()->buildAbort((BaseItem*)_item);
	_item = NULL;
  }
  int posX = item->getX();
  int posY = item->getY();
  _item = item;
  go(posX, posY);
}

void  Character::updateNeeds() {

  // Character is sleeping
  if (_sleep > 0) {
	_sleep--;

	// Set hapiness
	if (_item && _item->type == BaseItem::QUARTER_BED)
	  _hapiness += 0.1;
	else if (_item && _item->type == BaseItem::QUARTER_CHAIR)
	  _hapiness -= 0.1;
	else
	  _hapiness -= 0.25;

	// If current item is not under construction: abort
	if (_sleep == 0 && _item != NULL && _item->isComplete()) {
	  _item->setOwner(NULL);
	  _item = NULL;
	}
	return;
  }

  if (_food == 0) {
	_hapiness -= 0.1;
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

  // Character is sleeping
  if (_sleep > 0) {
	return;
  }

  // // Chatacter moving to a position
  // if (_astarsearch != NULL) {
  // }

  // Chatacter moving to bedroom
  if (_item != NULL && _item->isSleepingItem()) {
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

  // If character have a job -> do not interrupt
  if (_build != NULL) {
	return;
  }

  // Need food
  if (_food < 20) {

	// If character already go to needed item
	if (_astarsearch != NULL) {
	  BaseItem* item = WorldMap::getInstance()->getItem(_toX, _toY);
	  if (item != NULL && item->getType() == BaseItem::BAR_PUB) {
		return;
	  }
	}

	cout << Debug() << "Charactere: need food" << endl;
	BaseItem* item = WorldMap::getInstance()->find(BaseItem::BAR_PUB, false);
	if (item != NULL) {
	  std::cout << Debug() << "Go to pub !" << std::endl;
	  use(item);
	  return;
	}
  }

}

void		Character::go(int toX, int toY) {
  _toX = toX;
  _toY = toY;

  cout << Debug() << "Charactere: go(" << _posX << ", " << _posY << " to " << toX << ", " << toY << ")" << endl;

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
	   cout << Debug() << "Search found goal state: " << SearchSteps << endl;
	 }

	 // No path found
	 else if( SearchState == AStarSearch<MapSearchNode>::SEARCH_STATE_FAILED ) {
	   cout << Warning() << "Search terminated. Did not find goal state\n";
	   if (_item != NULL) {
		 WorldMap::getInstance()->buildAbort((BaseItem*)_item);
		 _item = NULL;
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
	std::cout << "move: sleeping -> move canceled" << std::endl;
	return;
  }

  // Move
  if (_astarsearch != NULL) {
	std::cout << Debug() << "Character: move" << std::endl;

	// Next node
	MapSearchNode *node = 0;
	if (_steps == 0) {
	  node = _astarsearch->GetSolutionStart();
	} else {
	  node = _astarsearch->GetSolutionNext();
	}

	// Goto node
	if (node != NULL) {
	  node->PrintNodeInfo();
	  _posX = node->x;
	  _posY = node->y;
	  _steps++;
	  std::cout << Debug() << "goto: " << _posX << " x " << _posY << ", step: " << _steps << std::endl;
	}

	// clear path
	else {
	  std::cout << Debug() << "Character: reached" << std::endl;
	  _astarsearch->FreeSolutionNodes();
	  std::cout << Debug() << "free 3" << std::endl;
	  _astarsearch->EnsureMemoryFreed();
	  delete _astarsearch;
	  _astarsearch = 0;

	  //action();
	}
  }
}

void		Character::actionUse() {
  // Character is sleeping
  if (_sleep != 0) {
	std::cout << "use: sleeping -> use canceled" << std::endl;
	return;
  }

  std::cout << Info() << "Character: actionUse" << std::endl;

  switch (_item->type) {

	// Bar
  case BaseItem::BAR_PUB:
	{
	  _food = 100;
	  BaseItem* goal = WorldMap::getInstance()->getRandomPosInRoom(_item->room);
	  if (goal != NULL) {
		go(goal->getX(), goal->getY());
	  }
	  _item = NULL;
	}
	break;

	// Bed
  case BaseItem::QUARTER_BED:
	_item->setOwner(this);
	_sleep = 20;
	_energy = 100;
	if (_health > 40) {
	  _health += 2;
	}
	break;

	// Chair
  case BaseItem::QUARTER_CHAIR:
	_item->setOwner(this);
	_sleep = 20;
	_energy = 100;
	if (_health > 40) {
	  _health += 1;
	}
	break;

  }
}

void		Character::actionBuild() {
  std::cout << Info() << "Character: actionBuild" << std::endl;

  switch (ResourceManager::getInstance().build(_build)) {

  case ResourceManager::NO_MATTER:
	std::cout << Debug() << "Character: not enough matter" << std::endl;
	WorldMap::getInstance()->buildAbort(_build);
	_build = NULL;
	break;

  case ResourceManager::BUILD_COMPLETE:
	std::cout << Debug() << "Character: build complete" << std::endl;
	WorldMap::getInstance()->buildComplete(_build);
	_build = NULL;
	go(_posX + 1, _posY);
	break;

  case ResourceManager::BUILD_PROGRESS:
	std::cout << Debug() << "Character: build progress" << std::endl;
	break;

  }
}

void		Character::action() {
  if (_posX != _toX || _posY != _toY) {
	return;
  }

  std::cout << Info() << "Character: action" << std::endl;

  // Build on progress
  if (_build != NULL) {
	  actionBuild();
  }

  // If item still exists
  else if (_item != NULL) {
	if (_item != WorldMap::getInstance()->getItem(_posX, _posY)) {
	  std::cout << Error() << "Character: action on NULL or invalide item" << std::endl;
	  return;
	}
	actionUse();
  }

}

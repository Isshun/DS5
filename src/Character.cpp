#include <iostream>
#include <SFML/Graphics.hpp>
#include "defines.h"
#include "Character.h"
#include "ResourceManager.h"
#include "stlastar.h"
#include "WorldMap.h"
#include "Log.h"
#include "PathManager.h"

#define LIMITE_FOOD_OK 30
#define LIMITE_FOOD_HUNGRY 15
#define LIMITE_FOOD_STARVE 0

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

Character::Character(int id, int x, int y) {
  Debug() << "Character #" << id;

  _id = id;
  _astarsearch = NULL;
  _item = NULL;
  _build = NULL;
  _posY = y;
  _posX = x;
  _sleep = 0;

  _jobName = jobs[rand() % 4].name;

  memset(_messages, -100, CHARACTER_MAX_MESSAGE * sizeof(int));

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

  Debug() << "Character done: " << _name;
}

Character::~Character() {
  if (_astarsearch != NULL) {
	_astarsearch->FreeSolutionNodes();
	Debug() << "free 1";
	_astarsearch->EnsureMemoryFreed();
	delete _astarsearch;
	_astarsearch = NULL;
  }
}

void	Character::use(AStarSearch<MapSearchNode>* path, BaseItem* item) {
  Info() << "Character #" << _id <<": use item type: " << item->type;

  // If character currently building item: abort
  if (_build != NULL && _build->isComplete() == false) {
	WorldMap::getInstance()->buildAbort(_build);
	_build = NULL;
  }

  // Go to new item
  int toX = item->getX();
  int toY = item->getY();
  _item = item;
  go(path, toX, toY);
}

void	Character::build(AStarSearch<MapSearchNode>* path, BaseItem* item) {
  Info() << "Character #" << _id << ": build item type: " << item->type;

  _build = item;
  _build->setOwner(this);
  int toX = item->getX();
  int toY = item->getY();
  go(path, toX, toY);
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

void  Character::addMessage(int msg, int count) {
  _messages[msg] = count;
}

void  Character::updateNeeds(int count) {

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

  // Sleep on the ground
  if (_energy == 0 && (_item == NULL || _item->isSleepingItem() == false)) {
	addMessage(MSG_SLEEP_ON_FLOOR, count);
	_hapiness -= 10;
	_sleep = 20;
	_energy = 80;
	return;
  }

  // Food
  _food -= 2;

  // Food: starve
  if (_food <= LIMITE_FOOD_STARVE) {
	addMessage(MSG_STARVE, count);
	_hapiness -= 0.5;
  }
  // Food: hungry
  else if (_food <= LIMITE_FOOD_HUNGRY) {
	addMessage(MSG_HUNGRY, count);
	_hapiness -= 0.2;
  }


  if (_oxygen > 0) {
	_oxygen = 0;
  }

  if (_oxygen == 0) {
	addMessage(MSG_NEED_OXYGEN, count);
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
	Debug() << "Charactere #" << _id << ": need sleep: " << _energy;

	// Sleep in bed
	{
	  BaseItem* item = WorldMap::getInstance()->find(BaseItem::QUARTER_BED, true);
	  if (item != NULL) {
		AStarSearch<MapSearchNode>* path = PathManager::getInstance()->getPath(this, item);
		use(path, item);
		return;
	  }
	}

	// Sleep in chair
	{
	  BaseItem* item = WorldMap::getInstance()->find(BaseItem::QUARTER_CHAIR, true);
	  if (item != NULL) {
		AStarSearch<MapSearchNode>* path = PathManager::getInstance()->getPath(this, item);
		use(path, item);
		return;
	  }
	}

  }

  // If character have a job -> do not interrupt
  if (_build != NULL) {
	return;
  }

  // Need food
  if (_food <= LIMITE_FOOD_OK) {

	// If character already go to needed item
	if (_astarsearch != NULL) {
	  BaseItem* item = WorldMap::getInstance()->getItem(_toX, _toY);
	  if (item != NULL && item->getType() == BaseItem::BAR_PUB) {
		return;
	  }
	}

	Debug() << "Charactere #" << _id << ": need food";
	BaseItem* item = WorldMap::getInstance()->find(BaseItem::BAR_PUB, false);
	if (item != NULL) {
	  Debug() << "Charactere #" << _id << ": Go to pub !";
	  AStarSearch<MapSearchNode>* path = PathManager::getInstance()->getPath(this, item);
	  use(path, item);
	  return;
	} else {
	  Debug() << "Charactere #" << _id << ": no pub :(";
	}
  }

}

void		Character::go(AStarSearch<MapSearchNode>* astarsearch, int toX, int toY) {
  _toX = toX;
  _toY = toY;

  Debug() << "Charactere #" << _id << ": go(" << _posX << ", " << _posY << " to " << toX << ", " << toY << ")";

  if (_astarsearch != NULL) {
	_astarsearch->FreeSolutionNodes();
	Debug() << "free 1";
	_astarsearch->EnsureMemoryFreed();
	delete _astarsearch;
	_astarsearch = NULL;
  }

  _astarsearch = astarsearch;
  _steps = 0;
}

void		Character::move() {

  // Character is sleeping
  if (_sleep != 0) {
	Debug() << "Character #" << _id << ": sleeping -> move canceled";
	return;
  }

  // Move
  if (_astarsearch != NULL) {
	Debug() << "Character #" << _id << ": move";

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
	  Debug() << "Charactere #" << _id << ": goto " << _posX << " x " << _posY << ", step: " << _steps;
	}

	// clear path
	else {
	  Debug() << "Character #" << _id << ": reached";
	  _astarsearch->FreeSolutionNodes();
	  Debug() << "free 3";
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
	Debug() << "use: sleeping -> use canceled";
	return;
  }

  Debug() << "Character #" << _id << ": actionUse";

  switch (_item->type) {

	// Bar
  case BaseItem::BAR_PUB:
	{
	  _food = 100;
	  BaseItem* goal = WorldMap::getInstance()->getRandomPosInRoom(_item->room);
	  if (goal != NULL) {
		AStarSearch<MapSearchNode>* path = PathManager::getInstance()->getPath(this, goal);
		go(path, goal->getX(), goal->getY());
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
  Debug() << "Character #" << _id << ": actionBuild";

  switch (ResourceManager::getInstance().build(_build)) {

  case ResourceManager::NO_MATTER:
	Debug() << "Character #" << _id << ": not enough matter";
	WorldMap::getInstance()->buildAbort(_build);
	_build = NULL;
	break;

  case ResourceManager::BUILD_COMPLETE:
	Debug() << "Character #" << _id << ": build complete";
	WorldMap::getInstance()->buildComplete(_build);
	_build = NULL;
	// go(_posX + 1, _posY);
	break;

  case ResourceManager::BUILD_PROGRESS:
	Debug() << "Character #" << _id << ": build progress";
	break;

  }
}

void		Character::action() {
  if (_posX != _toX || _posY != _toY) {
	return;
  }

  // Build on progress
  if (_build != NULL) {
	  actionBuild();
  }

  // If item still exists
  else if (_item != NULL) {
	BaseItem* item = WorldMap::getInstance()->getItem(_posX, _posY);
	if (_item != item) {

	  Error() << "Character #" << _id << ": action on NULL or invalide item: " << item->type;
	  return;
	}
	actionUse();
  }

}

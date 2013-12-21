#include <iostream>
#include <SFML/Graphics.hpp>
#include "defines.h"
#include "Character.h"
#include "ResourceManager.h"
#include "stlastar.h"
#include "WorldMap.h"
#include "Log.h"
#include "PathManager.h"
#include "CharacterManager.h"

#define LIMITE_FOOD_OK 30
#define LIMITE_FOOD_HUNGRY 15
#define LIMITE_FOOD_STARVE 0
#define MESSAGE_COUNT_INIT -100

#define BLOCKED_COUNT_BEFORE_MESSAGE 5

const char* firstname[] = {
  // male
  "Galen",
  "Lewis",
  "Benjamin",
  "Michael",
  "Jonathan",
  "Gaius",
  "Samuel",
  "Wesley",
  // female
  "Jadzia",
  "Janice",
  "Alice",
  "Kathryn",
  "Beverly",
  "Willow",
  "Tasha",
  "Samantha"
};

const char* shortFirstname[] = {
  // male
  "Matt",
  "Jack",
  "Adam",
  "Bill",
  "Tom",
  "Saul",
  "Lee",
  // female
  "Tory",
  "Vic",
  "Ezri",
  "Ellen",
  "Kara",
  "Emma",
  "Wade",
  "Amy"
};

const char* middlename[] = {
  "Crashdown",
  "Hardball",
  "Apollo",
  "Boomer",
  "Doc",
  "Starbuck",
  "Hotdog",
  "Jammer",
  "Trip",
  "Helo",
  "Dee",
  "Oz",
  "Klaus",
  "Mac",
  "Betty",
  "Six"
};

const char* shortLastname[] = {
  "Mudd",
  "Dax",
  "Nerys",
  "Laren",
  "Rand",
  "McCoy",
  "Adama",
  "Tyrol",
  "Reed",
  "Sisko",
  "Riker",
  "Wells",
  "Quinn",
  "Weir",
  "Rush",
  "Tyler"
};

const char* lastname[] = {
  "Zimmerman",
  "Anders",
  "Barclay",
  "Archer",
  "Thrace",
  "Summers",
  "Holmes",
  "Wildman",
  "Lawton",
  "Mallory",
  "Beckett",
  "Hammond",
  "O'Neill",
  "Sheppard",
  "Cooper",
  "Hartness"
};

Character::Character(int id, int x, int y) {
  Debug() << "Character #" << id;

  _id = id;
  _gender = rand() % 2 ? Character::GENDER_MALE : Character::GENDER_FEMALE;
  _astarsearch = NULL;
  _item = NULL;
  _build = NULL;
  _posY = _toX = y;
  _posX = _toY = x;
  _sleep = 0;
  _selected = false;
  _blocked = 0;
  _frameIndex = rand() % 20;

  memset(_messages, MESSAGE_COUNT_INIT, CHARACTER_MAX_MESSAGE * sizeof(int));

  // Needs
  _food = CHARACTER_INIT_FOOD + rand() % 20 - 10;
  _oxygen = CHARACTER_INIT_OXYGEN + rand() % 20 - 10;
  _hapiness = CHARACTER_INIT_HAPINESS + rand() % 20 - 10;
  _health = CHARACTER_INIT_HEALTH + rand() % 20 - 10;
  _energy = CHARACTER_INIT_ENERGY + rand() % 20 - 10;

  int offset = (_gender == Character::GENDER_FEMALE ? 4 : 0);
  if (rand() % 2) {
    snprintf(_name, 24, "%s (%s) %s",
			 shortFirstname[rand() % 8 + offset],
			 middlename[rand() % 16],
			 shortLastname[rand() % 16]);
  } else {
    snprintf(_name, 24, "%s %s",
			 firstname[rand() % 8 + offset],
			 lastname[rand() % 16]);
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

void	Character::setProfession(int professionId) {
  const Profession* professions = CharacterManager::getInstance()->getProfessions();

	for (int i = 0; professions[i].id != Character::PROFESSION_NONE; i++) {
		if (professions[i].id == professionId) {
			Debug() << "setProfession: " << professions[i].name;
			_profession = professions[i];
		}
	}
}

void	Character::use(AStarSearch<MapSearchNode>* path, BaseItem* item) {
  Info() << "Character #" << _id <<": use item type: " << item->getType();

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
  Info() << "Character #" << _id << ": build item type: " << item->getType();

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

void  Character::removeMessage(int msg) {
  _messages[msg] = MESSAGE_COUNT_INIT;
}

void  Character::updateNeeds(int count) {

  // Character is sleeping
  if (_sleep > 0) {
	_sleep--;

	// Set hapiness
	if (_item && _item->isType(BaseItem::QUARTER_BED)) {
	  _hapiness += 0.1;
	  removeMessage(MSG_SLEEP_ON_FLOOR);
	  removeMessage(MSG_SLEEP_ON_CHAIR);
	} else if (_item && _item->isType(BaseItem::QUARTER_CHAIR)) {
	  _hapiness -= 0.1;
	  addMessage(MSG_SLEEP_ON_CHAIR, count);
	  removeMessage(MSG_SLEEP_ON_FLOOR);
	} else {
	  addMessage(MSG_SLEEP_ON_FLOOR, count);
	  _hapiness -= 0.25;
	}

	// If current item is not under construction: abort
	if (_sleep == 0 && _item != NULL && _item->isComplete()) {
	  _item->setOwner(NULL);
	  _item = NULL;
	}
	return;
  }

  // Food
  _food -= 2;

  // Food: starve
  if (_food <= LIMITE_FOOD_STARVE) {
	addMessage(MSG_STARVE, count);
	removeMessage(MSG_HUNGRY);
	_hapiness -= 0.5;
	_energy -= 1;
  }
  // Food: hungry
  else if (_food <= LIMITE_FOOD_HUNGRY) {
	addMessage(MSG_HUNGRY, count);
	_hapiness -= 0.2;
  } else {
	removeMessage(MSG_STARVE);
	removeMessage(MSG_HUNGRY);
  }


  if (_oxygen > 0) {
	_oxygen = 0;
  }

  if (_oxygen == 0) {
	addMessage(MSG_NEED_OXYGEN, count);
	_oxygen = 0;
  } else {
	removeMessage(MSG_NEED_OXYGEN);
  }

  // Energy
  _energy -= 1;

  //if (_hapiness > 0)_hapiness = 0;
  //if (_health > 0)_health = 0;
}

void	Character::sendEvent(int event) {
  switch (event) {
  case MSG_BLOCKED:
	if (++_blocked >= BLOCKED_COUNT_BEFORE_MESSAGE) {
	  addMessage(MSG_BLOCKED, -1);
	}
  }
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
		if (path != NULL) {
		  use(path, item);
		  return;
		} else {
		  sendEvent(MSG_BLOCKED);
		}
	  }
	}

	// Sleep in chair
	{
	  BaseItem* item = WorldMap::getInstance()->find(BaseItem::QUARTER_CHAIR, true);
	  if (item != NULL) {
		AStarSearch<MapSearchNode>* path = PathManager::getInstance()->getPath(this, item);
		if (path != NULL) {
		  use(path, item);
		  return;
		} else {
		  sendEvent(MSG_BLOCKED);
		}
	  }
	}

	// Sleep on the ground
	if (_energy == 0) {
	  _sleep = 20;
	  _energy = 80;
	  return;
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
	  if (path != NULL) {
		use(path, item);
		return;
	  } else {
		sendEvent(MSG_BLOCKED);
	  }
	} else {
	  Debug() << "Charactere #" << _id << ": no pub :(";
	}
  }

}

void		Character::go(AStarSearch<MapSearchNode>* astarsearch, int toX, int toY) {
  if (astarsearch == NULL) {
	sendEvent(MSG_BLOCKED);
	return;
  }

  _blocked = 0;

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

  switch (_item->getType()) {

	// Bar
  case BaseItem::BAR_PUB:
	{
	  _food = 100;
	  BaseItem* goal = WorldMap::getInstance()->getRandomPosInRoom(_item->getRoomId());
	  if (goal != NULL) {
		AStarSearch<MapSearchNode>* path = PathManager::getInstance()->getPath(this, goal);
		if (path != NULL) {
		  go(path, goal->getX(), goal->getY());
		} else {
		  sendEvent(MSG_BLOCKED);
		}
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

	  Error() << "Character #" << _id << ": action on NULL or invalide item: " << item->getType();
	  return;
	}
	actionUse();
  }

}

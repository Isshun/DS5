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
  _direction = DIRECTION_NONE;
  _goal = GOAL_NONE;
  _resolvePath = false;
  _offset = 0;
  _node = NULL;
  _job = NULL;

  memset(_messages, MESSAGE_COUNT_INIT, CHARACTER_MAX_MESSAGE * sizeof(int));

  // Needs
   _food = CHARACTER_INIT_FOOD + rand() % 40 - 20;
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

void	Character::create() {

}

void	Character::load(const char* filePath) {
}

void	Character::save(const char* filePath) {

}

void	Character::onPathSearch(Path* path, BaseItem* item) {
  _resolvePath = true;
}

void	Character::onPathComplete(Path* path, BaseItem* item) {
  switch (_goal) {
  case GOAL_MOVE: {
	_goal = GOAL_MOVE;
	// MapSearchNode* node = path->GetSolutionEnd();
	// path->GetSolutionStart();
	go(path, item->getX(), item->getY());
	break;
  }
  case GOAL_USE:
	_goal = GOAL_USE;
	use(path, item);
	break;
  case GOAL_BUILD:
	_goal = GOAL_BUILD;
	build(path, item);
	break;
  }
}

void	Character::onPathFailed(BaseItem* item) {
  switch (_goal) {
  case GOAL_MOVE:
	_goal = GOAL_NONE;
	Warning() << "Move failed (no path)";
	break;
  case GOAL_USE:
	_goal = GOAL_NONE;
	Warning() << "Use failed (no path)";
	break;
  case GOAL_BUILD:
	_goal = GOAL_NONE;
	Warning() << "Build failed (no path)";
	WorldMap::getInstance()->buildAbort(item);
	JobManager::getInstance()->abort(_job);
	_job = NULL;
	break;
  }
  sendEvent(MSG_BLOCKED);
}

void	Character::setDirection(int direction) {
  if (_direction != direction) {
	_direction = direction;
	_offset = 0;
  }
}

void	Character::setJob(Job* job) {
  if (_job != NULL) {
	JobManager::getInstance()->abort(_job);
  }
  _job = job;
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
	  _goal = GOAL_NONE;
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


  // Oxygen
  WorldArea* area = WorldMap::getInstance()->getArea(_posX, _posY);
  if (area != NULL) {
	if (area->getOxygen() > _oxygen) {
	  _oxygen = min(area->getOxygen(), _oxygen + 5);
	} else {
	  _oxygen = max(area->getOxygen(), _oxygen - 5);
	}
  } else {
	_oxygen = max(0, _oxygen - 5);
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

  // Character is busy
  if (_goal != GOAL_NONE) {
	return;
  }

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
		PathManager::getInstance()->getPathAsync(this, item);
		_goal = GOAL_USE;
		return;
	  }
	}

	// Sleep in chair
	{
	  BaseItem* item = WorldMap::getInstance()->find(BaseItem::QUARTER_CHAIR, true);
	  if (item != NULL) {
		PathManager::getInstance()->getPathAsync(this, item);
		_goal = GOAL_USE;
		return;
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
	PathManager::getInstance()->getPathAsync(this, _build);
	_goal = GOAL_BUILD;
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

	  PathManager::getInstance()->getPathAsync(this, item);
	  _goal = GOAL_USE;

	  // if (path != NULL) {
	  // 	use(path, item);
	  // 	return;
	  // } else {
	  // 	sendEvent(MSG_BLOCKED);
	  // }
	} else {
	  Debug() << "Charactere #" << _id << ": no pub :(";
	}
  }

}

void	Character::go(int toX, int toY) {
  PathManager::getInstance()->getPathAsync(this, toX, toY);
  _goal = GOAL_MOVE;
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
  _direction = DIRECTION_NONE;

  // Character is sleeping
  if (_sleep != 0) {
	Debug() << "Character #" << _id << ": sleeping -> move canceled";
	return;
  }

  // Goto node
  if (_node != NULL) {
	_node->PrintNodeInfo();

	// Set direction
	if (_node->x > _posX && _node->y > _posY) setDirection(DIRECTION_BOTTOM_RIGHT);
	else if (_node->x < _posX && _node->y > _posY) setDirection(DIRECTION_BOTTOM_LEFT);
	else if (_node->x > _posX && _node->y < _posY) setDirection(DIRECTION_TOP_RIGHT);
	else if (_node->x < _posX && _node->y < _posY) setDirection(DIRECTION_TOP_LEFT);
	else if (_node->x > _posX) setDirection(DIRECTION_RIGHT);
	else if (_node->x < _posX) setDirection(DIRECTION_LEFT);
	else if (_node->y > _posY) setDirection(DIRECTION_BOTTOM);
	else if (_node->y < _posY) setDirection(DIRECTION_TOP);

	_posX = _node->x;
	_posY = _node->y;
	_steps++;
	Debug() << "Charactere #" << _id << ": goto " << _posX << " x " << _posY << ", step: " << _steps;
  }

  // Next node
  if (_astarsearch != NULL) {
	Debug() << "Character #" << _id << ": move";

	if (_steps == 0) {
	  _node = _astarsearch->GetSolutionStart();
	} else {
	  _node = _astarsearch->GetSolutionNext();
	}

	// clear path
	if (_node == NULL) {
	  Debug() << "Character #" << _id << ": reached";
	  _astarsearch->FreeSolutionNodes();
	  Debug() << "free 3";
	  _astarsearch->EnsureMemoryFreed();
	  delete _astarsearch;
	  _astarsearch = NULL;
	}
  }
}

// TODO: make objects stats table instead switch
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
	  _goal = GOAL_NONE;
	  _food = 100;
	  BaseItem* item = WorldMap::getInstance()->getRandomPosInRoom(_item->getRoomId());
	  if (item != NULL) {
		PathManager::getInstance()->getPathAsync(this, item);
		_goal = GOAL_MOVE;
	  }
	  _item = NULL;
	}
	break;

	// Bed
  case BaseItem::QUARTER_BED:
	_goal = GOAL_NONE;
	_item->setOwner(this);
	_sleep = 20;
	_energy = 100;
	if (_health > 40) {
	  _health += 2;
	}
	break;

	// Chair
  case BaseItem::QUARTER_CHAIR:
	_goal = GOAL_NONE;
	_item->setOwner(this);
	_sleep = 20;
	_energy = 100;
	if (_health > 40) {
	  _health += 1;
	}
	break;

  default:
	_goal = GOAL_NONE;
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
	_goal = GOAL_NONE;
	break;

  case ResourceManager::BUILD_COMPLETE:
	Debug() << "Character #" << _id << ": build complete";
	WorldMap::getInstance()->buildComplete(_build);
	JobManager::getInstance()->complete(_job);
	_build = NULL;
	_goal = GOAL_NONE;
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

  switch (_goal) {

  case GOAL_MOVE: {
	_goal = GOAL_NONE;
	break;
  }

  case GOAL_USE: {
	BaseItem* item = WorldMap::getInstance()->getItem(_posX, _posY);
	if (_item == NULL) {
	  Error() << "Character #" << _id << ": actionUse on NULL item";
	  return;
	} if (_item != item) {
	  Error() << "Character #" << _id << ": actionUse on invalide item";
	  return;
	}
	actionUse();
	break;
  }

  case GOAL_BUILD: {
	WorldArea* area = WorldMap::getInstance()->getArea(_posX, _posY);
	BaseItem* item = WorldMap::getInstance()->getItem(_posX, _posY);
	if (_build == NULL || (_build != area && _build != item)) {
	  if (_build == NULL) {
		Error() << "Character #" << _id << ": actionBuild on NULL item";
	  } else if (_build != area) {
		Error() << "Character #" << _id << ": actionBuild on invalide area";
	  } else if (_build != item) {
		Error() << "Character #" << _id << ": actionBuild on invalide item";
	  }
	}
	actionBuild();
	break;
  }

  }
}

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
  _path = NULL;
  _posY = _toX = y;
  _posX = _toY = x;
  _selected = false;
  _blocked = 0;
  _frameIndex = rand() % 20;
  _direction = DIRECTION_NONE;
  _resolvePath = false;
  _offset = 0;
  _node = NULL;
  _job = NULL;
  _needs = new CharacterNeeds();

  memset(_messages, MESSAGE_COUNT_INIT, CHARACTER_MAX_MESSAGE * sizeof(int));

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
  if (_path != NULL) {
	_path->FreeSolutionNodes();
	Debug() << "free 1";
	_path->EnsureMemoryFreed();
	delete _path;
	_path = NULL;
  }
}

void	Character::create() {

}

void	Character::load(const char* filePath) {
}

void	Character::save(const char* filePath) {

}

void	Character::onPathSearch(Path* path, Job* job) {
  _resolvePath = true;
}

// TODO
void	Character::onPathComplete(Path* path, Job* job) {
  if (path == NULL) {
	sendEvent(CharacterNeeds::MSG_BLOCKED);
	return;
  }

  _blocked = 0;

  _toX = job->getX();
  _toY = job->getY();

  Debug() << "Charactere #" << _id << ": go(" << _posX << ", " << _posY << " to " << _toX << ", " << _toY << ")";

  if (_path != NULL) {
  	_path->FreeSolutionNodes();
  	Debug() << "free 1";
  	_path->EnsureMemoryFreed();
  	delete _path;
  	_path = NULL;
  }

  _path = path;
  _steps = 0;
}

void	Character::onPathFailed(Job* job) {
  switch (job->getAction()) {
  case JobManager::ACTION_MOVE:
	Warning() << "Move failed (no path)";
	break;
  case JobManager::ACTION_USE:
	Warning() << "Use failed (no path)";
	break;
  case JobManager::ACTION_BUILD:
	Warning() << "Build failed (no path)";
	break;
  }
  sendEvent(CharacterNeeds::MSG_BLOCKED);

  // Give up job
  // WorldMap::getInstance()->buildAbort(job->getItem());
  JobManager::getInstance()->abort(job);
  _job = NULL;
}

void	Character::setDirection(int direction) {
  if (_direction != direction) {
	_direction = direction;
	_offset = 0;
  }
}

void	Character::setJob(Job* job) {
  Debug() << "set new job";

  if (_job != NULL) {
	JobManager::getInstance()->abort(_job);
  }
  _job = job;
  _toX = job->getX();
  _toY = job->getY();
  if (_posX != job->getX() || _posY != job->getY()) {
	PathManager::getInstance()->getPathAsync(this, job);
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

// void		Character::go(AStarSearch<MapSearchNode>* astarsearch, Job* job) {
//   if (astarsearch == NULL) {
// 	sendEvent(MSG_BLOCKED);
// 	return;
//   }

//   _blocked = 0;

//   _toX = job->getX();
//   _toY = job->getY();

//   Debug() << "Charactere #" << _id << ": go(" << _posX << ", " << _posY << " to " << toX << ", " << toY << ")";

//   if (_astarsearch != NULL) {
//   	_astarsearch->FreeSolutionNodes();
//   	Debug() << "free 1";
//   	_astarsearch->EnsureMemoryFreed();
//   	delete _astarsearch;
//   	_astarsearch = NULL;
//   }

//   _astarsearch = astarsearch;
//   _steps = 0;
// }

// void	Character::use(AStarSearch<MapSearchNode>* path, Job* job) {
//   Info() << "Character #" << _id <<": use item type: " << item->getType();

//   // If character currently building item: abort
//   if (_job != NULL && _job->getItem() != NULL && _job->getItem()->isComplete() == false) {
// 	WorldMap::getInstance()->buildAbort(_job->getItem());
// 	_job = NULL;
//   }

//   // Go to new item
//   int toX = item->getX();
//   int toY = item->getY();
//   _item = item;
//   go(path, toX, toY);
// }

// void	Character::build(AStarSearch<MapSearchNode>* path, Job* job) {
//   Info() << "Character #" << _id << ": build item type: " << item->getType();

//   _build = item;
//   _build->setOwner(this);
//   int toX = item->getX();
//   int toY = item->getY();
//   go(path, toX, toY);
// }

// void	Character::setItem(BaseItem* item) {
//   BaseItem* currentItem = _item;

//   _item = item;

//   if (currentItem != NULL && currentItem->getOwner() != NULL) {
// 	currentItem->setOwner(NULL);
//   }

//   if (item != NULL && item->getOwner() != this) {
// 	item->setOwner(this);
//   }
// }

void  Character::addMessage(int msg, int count) {
  _messages[msg] = count;
}

void  Character::removeMessage(int msg) {
  _messages[msg] = MESSAGE_COUNT_INIT;
}

void  Character::updateNeeds(int count) {
  _needs->update();
}

void	Character::sendEvent(int event) {
  switch (event) {
  case CharacterNeeds::MSG_BLOCKED:
	if (++_blocked >= BLOCKED_COUNT_BEFORE_MESSAGE) {
	  addMessage(CharacterNeeds::MSG_BLOCKED, -1);
	}
  }
}

void  Character::update() {

  // Character is busy
  if (_job != NULL) {
	return;
  }

  // Character is sleeping
  if (_needs->isSleeping()) {
	return;
  }

  // // Chatacter moving to a position
  // if (_astarsearch != NULL) {
  // }

  // Chatacter moving to bedroom
  if (_job != NULL && _job->getItem() != NULL && _job->getItem()->isSleepingItem()) {
	return;
  }

  // TODO
  // Energy
  if (_needs->getEnergy() < 20) {
	Debug() << "Charactere #" << _id << ": need sleep: " << _needs->getEnergy();

	// Need sleep
	JobManager::getInstance()->need(this, BaseItem::QUARTER_BED);

	// // Sleep in chair
	// {
	//   BaseItem* item = WorldMap::getInstance()->find(BaseItem::QUARTER_CHAIR, true);
	//   if (item != NULL) {
	// 	PathManager::getInstance()->getPathAsync(this, item);
	// 	_goal = GOAL_USE;
	// 	return;
	//   }
	// }

  }

  // If character have a job -> do not interrupt
  if (_job != NULL) {
	PathManager::getInstance()->getPathAsync(this, _job);
	return;
  }

  // // Need food
  // if (_food <= LIMITE_FOOD_OK) {

  // 	// If character already go to needed item
  // 	if (_path != NULL) {
  // 	  BaseItem* item = WorldMap::getInstance()->getItem(_toX, _toY);
  // 	  if (item != NULL && item->getType() == BaseItem::BAR_PUB) {
  // 		return;
  // 	  }
  // 	}

  // 	Debug() << "Charactere #" << _id << ": need food";
  // 	BaseItem* item = WorldMap::getInstance()->find(BaseItem::BAR_PUB, false);
  // 	if (item != NULL) {
  // 	  Debug() << "Charactere #" << _id << ": Go to pub !";

  // 	  PathManager::getInstance()->getPathAsync(this, _job);
	
  // 	  // if (path != NULL) {
  // 	  // 	use(path, item);
  // 	  // 	return;
  // 	  // } else {
  // 	  // 	sendEvent(MSG_BLOCKED);
  // 	  // }
  // 	} else {
  // 	  Debug() << "Charactere #" << _id << ": no pub :(";
  // 	}
  // }

}

void		Character::move() {
  _direction = DIRECTION_NONE;

  // Character is sleeping
  if (_needs->isSleeping()) {
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
  if (_path != NULL) {
	Debug() << "Character #" << _id << ": move";

	if (_steps == 0) {
	  _node = _path->GetSolutionStart();
	} else {
	  _node = _path->GetSolutionNext();
	}

	// clear path
	if (_node == NULL) {
	  Debug() << "Character #" << _id << ": reached";
	  _path->FreeSolutionNodes();
	  Debug() << "free 3";
	  _path->EnsureMemoryFreed();
	  delete _path;
	  _path = NULL;
	}
  }
}

// TODO: make objects stats table instead switch
void		Character::actionUse() {
  // Wrong call
  if (_job == NULL || _job->getItem() == NULL) {
	Error() << "Character: actionUse on NULL job or NULL job's item";
	return;
  }

  // Item is no longer exists
  if (_job->getItem() != WorldMap::getInstance()->getItem(_job->getX(), _job->getY())) {
	Warning() << "Character #" << _id << ": actionUse on invalide item";
	return;
  }

  // Character is sleeping
  if (_needs->isSleeping()) {
	Debug() << "use: sleeping -> use canceled";
	return;
  }

  Debug() << "Character #" << _id << ": actionUse";

  BaseItem* item = _job->getItem();

  switch (item->getType()) {

	// Bar
  case BaseItem::BAR_PUB:
	_needs->eat();
	  // BaseItem* item = WorldMap::getInstance()->getRandomPosInRoom(item->getRoomId());
	  // if (item != NULL) {
	  // 	PathManager::getInstance()->getPathAsync(this, item);
	  // 	_goal = GOAL_MOVE;
	  // }
	break;

	// Bed
  case BaseItem::QUARTER_BED:
	_needs->sleep(BaseItem::QUARTER_BED);
	item->setOwner(this);
	break;

	// Chair
  case BaseItem::QUARTER_CHAIR:
	_needs->sleep(BaseItem::QUARTER_CHAIR);
	item->setOwner(this);
	break;
  }

  _job = NULL;
}

void		Character::actionBuild() {
  // Wrong call
  if (_job == NULL || _job->getItem() == NULL) {
	Error() << "Character: actionBuild on NULL job or NULL job's item";
	return;
  }

  // Item is no longer exists
  BaseItem* item = _job->getItem();
  WorldArea* currentArea = WorldMap::getInstance()->getArea(_job->getX(), _job->getY());
  BaseItem* currentItem = WorldMap::getInstance()->getItem(_job->getX(), _job->getY());
  if (item != currentArea && item != currentItem) {
	if (item != currentArea) {
	  Warning() << "Character #" << _id << ": actionBuild on invalide area";
	} else if (item != currentItem) {
	  Warning() << "Character #" << _id << ": actionBuild on invalide item";
	}
	return;
  }


  Debug() << "Character #" << _id << ": actionBuild";

  // Build
  switch (ResourceManager::getInstance().build(item)) {

  case ResourceManager::NO_MATTER:
	Debug() << "Character #" << _id << ": not enough matter";
	JobManager::getInstance()->abort(_job);
	_job = NULL;
	break;

  case ResourceManager::BUILD_COMPLETE:
	Debug() << "Character #" << _id << ": build complete";
	JobManager::getInstance()->complete(_job);
	_job = NULL;
	break;

  case ResourceManager::BUILD_PROGRESS:
	Debug() << "Character #" << _id << ": build progress";
	break;
  }
}

void		Character::action() {
  if (_job == NULL || _posX != _toX || _posY != _toY) {
	return;
  }

  switch (_job->getAction()) {

  case JobManager::ACTION_MOVE: {
	JobManager::getInstance()->complete(_job);
	_job = NULL;
	break;
  }

  case JobManager::ACTION_USE: {
	actionUse();
	break;
  }

  case JobManager::ACTION_BUILD: {
	actionBuild();
	break;
  }

  }
}

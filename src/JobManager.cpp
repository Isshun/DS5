#include <iostream>
#include <cstdlib>
#include <stdio.h>
#include <sstream>
#include <string.h>
#include <SFML/Graphics.hpp>
#include "defines.h"
#include "JobManager.h"
#include "Character.h"

JobManager* JobManager::_self = new JobManager();

JobManager::JobManager() {
  Debug() << "JobManager";

  _jobs = new std::list<Job*>();
  _count = 0;
  _id = 0;
  _start = 0;
  _countFree = 0;

  Debug() << "JobManager done";
}

JobManager::~JobManager() {
  Job* c;
  while ((c = _jobs->front()) != NULL) {
	delete c;
  }

  delete _jobs;
}

void	JobManager::create() {

}

void	JobManager::load(const char* filePath) {

}

void	JobManager::save(const char* filePath) {

}

Job*	JobManager::build(BaseItem* item) {
  if (item == NULL) {
	Error() << "JobManager: build on NULL item";
	return NULL;
  }

  Job* job = new Job(++_id, item->getX(), item->getY());
  job->setAction(ACTION_BUILD);
  job->setItemType(item->getType());
  job->setItem(item);

  addJob(job);

  return job;
}

Job*	JobManager::gather(WorldArea* area) {
  if (area == NULL) {
	Error() << "JobManager: gather on NULL area";
	return NULL;
  }

  // return if job already exist for this item
  std::list<Job*>::iterator it;
  for (it = _jobs->begin(); it != _jobs->end(); ++it) {
	if ((*it)->getItem() == area) {
	  return NULL;
	}
  }

  Job* job = new Job(++_id, area->getX(), area->getY());
  job->setAction(ACTION_GATHER);
  job->setItemType(area->getType());
  job->setItem(area);

  addJob(job);

  return job;
}

void	JobManager::removeJob(BaseItem* item) {
  std::list<Job*> toRemove = std::list<Job*>();

  std::list<Job*>::iterator it;
  for (it = _jobs->begin(); it != _jobs->end(); ++it) {
	if ((*it)->getItem() == item) {
	  toRemove.push_back(*it);
	}
  }

  for (it = toRemove.begin(); it != toRemove.end(); ++it) {
	_jobs->remove(*it);
  }
}

Job*	JobManager::build(int type, int x, int y) {
  BaseItem* item = NULL;

  // Structure
  if (BaseItem::isStructure(type)) {
	// if (WorldMap::getInstance()->getArea(x, y) == NULL) {
	  item = WorldMap::getInstance()->putItem(type, x, y);
	// } else {
	//   Error() << "JobManager: add build on non NULL area";
	//   return NULL;
	// }
  }

  // Item
  else if (BaseItem::isItem(type)) {
	if (WorldMap::getInstance()->getItem(x, y) != NULL) {
	  Error() << "JobManager: add build on non NULL item";
	  return NULL;
	} else if (WorldMap::getInstance()->getArea(x, y) == NULL
			   || WorldMap::getInstance()->getArea(x, y)->isType(BaseItem::STRUCTURE_FLOOR) == false) {
	  Error() << "JobManager: add build on non invalid area (NULL or not STRUCTURE_FLOOR)";
	  return NULL;
	} else {
	  item = WorldMap::getInstance()->putItem(type, x, y);
	}
  }

  Job* job = new Job(++_id, x, y);
  job->setAction(ACTION_BUILD);
  job->setItemType(type);
  job->setItem(item);

  addJob(job);

  return job;
}

// TODO: one pass + check profession
Job*	JobManager::getJob(Character* character) {
  if (_countFree == 0) {
	return NULL;
  }

  Info() << "bestJob: start";

  Job* bestJob = NULL;
  int bestDistance = -1;

  {
	std::list<Job*>::iterator it;
	int x = character->getX();
	int y = character->getY();
	for (it = _jobs->begin(); it != _jobs->end(); ++it) {
	  if ((*it)->getCharacter() == NULL && (*it)->getAction() != JobManager::ACTION_GATHER) {
		int distance = abs(x - (*it)->getX()) + abs(y - (*it)->getY());
		if (distance < bestDistance || bestDistance == -1) {
		  bestJob = *it;
		  bestDistance = distance;
		}
	  }
	}
  }

  if (bestJob == NULL) {
	std::list<Job*>::iterator it;
	int x = character->getX();
	int y = character->getY();
	for (it = _jobs->begin(); it != _jobs->end(); ++it) {
	  if ((*it)->getCharacter() == NULL) {
		int distance = abs(x - (*it)->getX()) + abs(y - (*it)->getY());
		if (distance < bestDistance || bestDistance == -1) {
		  bestJob = *it;
		  bestDistance = distance;
		}
	  }
	}
  }

  if (bestJob != NULL) {
	Info() << "bestjob: " << bestDistance << " (" << bestJob->getX() << ", " << bestJob->getY() << ")";
	_countFree--;
  } else {
	Info() << "bestjob: NULL";
  }

  return bestJob;
}

// TODO: ugly
Job*	JobManager::getJob() {
  if (_count == 0) {
	return NULL;
  }

  int i = 0;
  std::list<Job*>::iterator it = _jobs->begin();
  while (i++ < _start % _count) {
	it++;
  }

  for (i = 0; i < _count; i++) {
	if (it == _jobs->end()) {
	  it = _jobs->begin();
	}

	if ((*it)->getCharacter() == NULL) {
	  return *it;
	}

	it++;
  }

  return NULL;
}

void	JobManager::abort(Job* job) {
  Info() << "Job abort: " << job->getId();
  _start++;
  _countFree++;
  job->setCharacter(NULL);
}

void	JobManager::complete(Job* job) {
  Info() << "Job complete: " << job->getId();

  _jobs->remove(job);
  _count--;
  _start--;
}

void	JobManager::need(Character* character, int itemType) {
  Info() << "JobManager: Character '" << character->getName() << "' need item #" << itemType;

  BaseItem* item = WorldMap::getInstance()->find(itemType, true);
  if (item != NULL) {

	Job* job = new Job(++_id, item->getX(), item->getY());
	job->setAction(ACTION_USE);
	job->setCharacterRequire(character);
	job->setItemType(item->getType());
	job->setItem(item);

	addJob(job);
	// PathManager::getInstance()->getPathAsync(character, item);
	return;
  }
}

void	JobManager::addJob(Job* job) {
  _jobs->push_back(job);
  _count++;
  _countFree++;
}

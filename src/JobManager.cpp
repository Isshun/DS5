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

  _jobs->push_back(job);
  _count++;

  return job;
}

Job*	JobManager::build(int type, int posX, int posY) {
  Job* job = new Job(++_id, posX, posY);
  job->setAction(ACTION_BUILD);
  job->setItemType(type);

  _jobs->push_back(job);
  _count++;

  return job;
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
  }

  return NULL;
}

void	JobManager::abort(Job* job) {
  Info() << "Job abort: " << job->getId();
  _start++;
  job->setCharacter(NULL);
}

void	JobManager::complete(Job* job) {
  Info() << "Job complete: " << job->getId();

  _jobs->remove(job);
  _count--;
  _start--;
}
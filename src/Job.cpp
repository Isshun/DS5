#include <iostream>
#include <SFML/Graphics.hpp>
#include "defines.h"
#include "Job.h"
#include "Log.h"
#include "JobManager.h"

Job::Job(int id, int x, int y) {
  Debug() << "Job #" << id;

  _id = id;
  _posY = y;
  _posX = x;
  _item = NULL;
  _itemType = BaseItem::NONE;
  _action = JobManager::ACTION_NONE;
  _character = NULL;

  Debug() << "Job #" << id << " done";
}

Job::~Job() {
}

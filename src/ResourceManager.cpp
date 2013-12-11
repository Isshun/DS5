#include <iostream>

#include "defines.h"
#include "ResourceManager.h"

ResourceManager ResourceManager::_self = ResourceManager();

ResourceManager::ResourceManager() {
  _matter = 500;
  _power = 10;
  _o2Use = 0;
  _o2Supply = 0;
}

ResourceManager::~ResourceManager() {
}

ResourceManager&	ResourceManager::getInstance() {
  return _self;
}

int ResourceManager::build(BaseItem* item) {
  if (_matter == 0) {
	return NO_MATTER;
  }

  _matter--;
  item->progress++;

  // BUILD_COMPLETE
  if (item->progress == item->matter) {

	// Remove power use
	item->powerSupply = _power >= item->power ? item->power : _power;
	_power -= item->power;

	// remove O2 use
	if (item->type == BaseItem::STRUCTURE_FLOOR) {
	  _o2Use++;
	}

	if (item->type == BaseItem::ENVIRONMENT_O2_RECYCLER) {
	  _o2Supply += 100;
	}

	if (item->type >= BaseItem::ARBORETUM_TREE_1 && item->type <= BaseItem::ARBORETUM_TREE_9) {
	  _o2Supply += 10;
	}

   	return BUILD_COMPLETE;
  }

  // BUILD_PROGRESS
  else {
   	return BUILD_PROGRESS;
  }
}

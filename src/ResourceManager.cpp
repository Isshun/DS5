#include <iostream>

#include "defines.h"
#include "ResourceManager.h"

ResourceManager ResourceManager::_self = ResourceManager();

ResourceManager::ResourceManager() {
  _matter = 500;
  _power = 10;
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

  if (item->progress == item->matter) {
	item->powerSupply = _power >= item->power ? item->power : _power;
	_power -= item->power;
   	return BUILD_COMPLETE;
  }

  if (item->progress == item->matter) {
   	return BUILD_PROGRESS;
  }
}

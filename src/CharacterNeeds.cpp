#include "defines.h"
#include "CharacterNeeds.h"
#include "BaseItem.h"

#define LIMITE_FOOD_OK 30
#define LIMITE_FOOD_HUNGRY 15
#define LIMITE_FOOD_STARVE 0

CharacterNeeds::CharacterNeeds() {
  _sleepItem = BaseItem::NONE;
  _sleeping = 0;
  _food = CHARACTER_INIT_FOOD + rand() % 40 - 20;
  _oxygen = CHARACTER_INIT_OXYGEN + rand() % 20 - 10;
  _happiness = CHARACTER_INIT_HAPPINESS + rand() % 20 - 10;
  _health = CHARACTER_INIT_HEALTH + rand() % 20 - 10;
  _energy = CHARACTER_INIT_ENERGY + rand() % 20 - 10;
  _relation = 0;
  _security = 0;
  _injuries = 0;
  _sickness = 0;
  _satiety = 0;
}

void	CharacterNeeds::update() {

	// // Set hapiness
	// if (_item && _item->isType(BaseItem::QUARTER_BED)) {
	//   removeMessage(MSG_SLEEP_ON_FLOOR);
	//   removeMessage(MSG_SLEEP_ON_CHAIR);
	// } else if (_item && _item->isType(BaseItem::QUARTER_CHAIR)) {
	//   _hapiness -= 0.1;
	//   addMessage(MSG_SLEEP_ON_CHAIR, count);
	//   removeMessage(MSG_SLEEP_ON_FLOOR);
	// } else {
	//   addMessage(MSG_SLEEP_ON_FLOOR, count);
	//   _hapiness -= 0.25;
	// }

  // 	// If current item is not under construction: abort
  // 	if (_sleep == 0 && _item != NULL && _item->isComplete()) {
  // 	  _item->setOwner(NULL);
  // 	  _item = NULL;
  // 	  _job = NULL;
  // 	}
  // 	return;
  // }

  // Food
  _food -= 2;

  // Food: starve
  if (_food <= LIMITE_FOOD_STARVE) {
	// addMessage(MSG_STARVE, count);
	// removeMessage(MSG_HUNGRY);
	_happiness = max(_happiness - 0.5f, 0.0f);
	_energy = max(_energy - 1.0f, 0.0f);
	_health = max(_health - 0.1f, 0.0f);
  }

  // Food: hungry
  else if (_food <= LIMITE_FOOD_HUNGRY) {
	// addMessage(MSG_HUNGRY, count);
	_happiness = max(_happiness - 0.2f, 0.0f);
  } else {
	// removeMessage(MSG_STARVE);
	// removeMessage(MSG_HUNGRY);
  }


  // // Oxygen
  // WorldArea* area = WorldMap::getInstance()->getArea(_posX, _posY);
  // if (area != NULL) {
  // 	if (area->getOxygen() > _oxygen) {
  // 	  _oxygen = min(area->getOxygen(), _oxygen + 5);
  // 	} else {
  // 	  _oxygen = max(area->getOxygen(), _oxygen - 5);
  // 	}
  // } else {
  // 	_oxygen = max(0, _oxygen - 5);
  // }

  // if (_oxygen == 0) {
  // 	addMessage(MSG_NEED_OXYGEN, count);
  // 	_oxygen = 0;
  // } else {
  // 	removeMessage(MSG_NEED_OXYGEN);
  // }

  // Character is sleeping
  if (_sleeping > 0) {
	updateSleeping();
  } else {
	updateAwakening();
  }
}

void	CharacterNeeds::updateAwakening() {
  // Energy
  _energy -= 1;
}

void	CharacterNeeds::updateSleeping() {
  _sleeping--;

  // Strong heal if character in sickbay
  switch (_sleepItem) {
  case BaseItem::QUARTER_BED:
	_happiness += 0.1;
	break;
  case BaseItem::QUARTER_CHAIR:
	_happiness -= 0.1;
	break;
  case BaseItem::SICKBAY_BIOBED:
	if (_health > 20) {
	  _health = max(_health + 2, 100.0f);
	}
	break;
  case BaseItem::SICKBAY_EMERGENCY_SHELTERS:
	_health = max(_health + 4, 100.0f);
	break;
  default:
	_happiness -= 0.1;
	break;
  }

  // Minor health gain
  if (_health > 40) {
	_health = max(_health + 1, 100.0f);
  }

}

void	CharacterNeeds::eat() {
	_food = 100;
}

void	CharacterNeeds::drink() {
	_satiety = 100;
}

void	CharacterNeeds::sleep(int itemType) {
  _sleepItem = itemType;

  switch (itemType) {
  case BaseItem::QUARTER_BED:
	_sleeping = 20;
	_energy = 100;
	break;
  case BaseItem::SICKBAY_BIOBED:
	_sleeping = 20;
	_energy = 100;
	if (_health > 20) {
	  _health += 4;
	}
	break;
  case BaseItem::QUARTER_CHAIR:
	_sleeping = 20;
	_energy = 90;
	break;
  case BaseItem::SICKBAY_EMERGENCY_SHELTERS:
	_sleeping = 20;
	_energy = 100;
	_health += 8;
	break;
  case BaseItem::SCHOOL_DESK:
	_sleeping = 20;
	_energy = 100;
	break;
  default:
	_sleeping = 20;
	_energy = 80;
	break;
  }
}

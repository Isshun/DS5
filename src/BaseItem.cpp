/*
 * BaseItem.cpp
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#include "BaseItem.h"
#include "Character.h"
#include "UserInterfaceMenu.h"

// type													name, solid	width	height	matter	power	zone
ItemInfo	itemsInfo[] = {
  {BaseItem::STRUCTURE_ROOM,							"ROOM", true,	1, 1, 1, 0, 0},
  {BaseItem::STRUCTURE_HULL,							"HULL", true,	1, 1, 1, 0, 0},
  {BaseItem::STRUCTURE_WALL,							"WALL", true,	1, 1, 1, 0, 0},
  {BaseItem::STRUCTURE_FLOOR,							"FLOOR", false,	1, 1, 1, 0, 0},
  {BaseItem::STRUCTURE_DOOR,							"DOOR", false,	1, 1, 1, 0, 0},
  {BaseItem::STRUCTURE_WINDOW,							"WINDOW", true,	1, 1, 1, 0, 0},
  {BaseItem::TRANSPORTATION_TRANSPORTER_SYSTEMS,		"SYSTEMS", false,	1, 1, 10, 10, UserInterfaceMenu::CODE_ZONE_OPERATION},
  {BaseItem::QUARTER_BED,								"BED", false,	2, 2, 4, 0, UserInterfaceMenu::CODE_ZONE_QUARTER},
  {BaseItem::QUARTER_CHAIR,								"CHAIR", false,	1, 1, 2, 0, UserInterfaceMenu::CODE_ZONE_QUARTER},
  {BaseItem::HOLODECK_GRID,								"GRID", false,	1, 1, 6, 6, UserInterfaceMenu::CODE_ZONE_HOLODECK},
  {BaseItem::BAR_PUB,									"PUB", false,	1, 1, 5, 0, UserInterfaceMenu::CODE_ZONE_BAR},
  {BaseItem::ENGINE_CONTROL_CENTER,						"CENTER", false,	3, 2, 10, 5, UserInterfaceMenu::CODE_ZONE_ENGINE},
  {BaseItem::ENGINE_REACTION_CHAMBER,					"CHAMBER", false,	2, 3, 50, -200, UserInterfaceMenu::CODE_ZONE_ENGINE},
  {BaseItem::SICKBAY_BIOBED,							"BIOBED", false,	1, 2, 10, 10, UserInterfaceMenu::CODE_ZONE_SICKBAY},
  {BaseItem::ARBORETUM_TREE_1,							"1", false,	1, 2, 2, 0, 0},
  {BaseItem::ARBORETUM_TREE_2,							"2", false,	1, 2, 2, 0, 0},
  {BaseItem::ARBORETUM_TREE_3,							"3", false,	1, 2, 2, 0, 0},
  {BaseItem::ARBORETUM_TREE_4,							"4", false,	1, 2, 2, 0, 0},
  {BaseItem::ARBORETUM_TREE_5,							"5", false,	1, 1, 1, 0, 0},
  {BaseItem::ARBORETUM_TREE_6,							"6", false,	1, 1, 1, 0, 0},
  {BaseItem::ARBORETUM_TREE_7,							"7", false,	1, 1, 1, 0, 0},
  {BaseItem::ARBORETUM_TREE_8,							"8", false,	1, 1, 1, 0, 0},
  {BaseItem::ARBORETUM_TREE_9,							"9", false,	1, 1, 1, 0, 0},
  {BaseItem::ENVIRONMENT_O2_RECYCLER,					"RECYCLER", false,	1, 2, 10, 10, UserInterfaceMenu::CODE_ZONE_OPERATION},
  {BaseItem::NONE,										"NONE", false,	0, 0, 0, 0, 0},
};

BaseItem::BaseItem(int t, int id) {
  // Init
  isSolid = false;
  _type = t;
  progress = 0;
  _zoneId = 0;
  _zoneIdRequired = 0;
  _roomId = 0;
  _owner = NULL;
  _id = id;
  _name = NULL;

  // Default values
  _width = 1;
  _height = 1;
  matter = 1;
  power = 0;
  powerSupply = 0;
  isSolid = false;

  for (int i = 0; itemsInfo[i].type != BaseItem::NONE; i++) {
	if (itemsInfo[i].type == t) {
	  _name = itemsInfo[i].name;
	  _width = itemsInfo[i].width;
	  _height = itemsInfo[i].height;
	  _zoneId = itemsInfo[i].zone;
	  _zoneIdRequired = itemsInfo[i].zone;
	  matter = itemsInfo[i].matter;
	  power = itemsInfo[i].power;
	  powerSupply = 0;
	  isSolid = itemsInfo[i].solid;
	}
  }
}

BaseItem::~BaseItem() {
	// TODO Auto-generated destructor stub
}

ItemInfo BaseItem::getItemInfo(int type) {
  int i = 0;

  for (; itemsInfo[i].type != BaseItem::NONE; i++) {
	if (itemsInfo[i].type == type) {
	  return itemsInfo[i];
	}
  }

  return itemsInfo[i];
}

void	BaseItem::setOwner(Character* character) {
  Character* currentOwner = _owner;

  _owner = character;

  if (currentOwner != NULL && currentOwner->getItem() != NULL) {
	currentOwner->setItem(NULL);
  }
  if (character != NULL && character->getItem() != this) {
	character->setItem(this);
  }
}

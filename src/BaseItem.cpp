/*
 * BaseItem.cpp
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#include "BaseItem.h"

ItemInfo	itemsInfo[] = {
  {BaseItem::STRUCTURE_ROOM,							true,	1, 1, 1, 0},
  {BaseItem::STRUCTURE_HULL,							true,	1, 1, 1, 0},
  {BaseItem::STRUCTURE_WALL,							true,	1, 1, 1, 0},
  {BaseItem::STRUCTURE_FLOOR,							false,	1, 1, 1, 0},
  {BaseItem::STRUCTURE_DOOR,							false,	1, 1, 1, 0},
  {BaseItem::STRUCTURE_WINDOW,							true,	1, 1, 1, 0},
  {BaseItem::TRANSPORTATION_TRANSPORTER_SYSTEMS,		false,	1, 1, 10, 10},
  {BaseItem::QUARTER_BED,								false,	2, 2, 4, 0},
  {BaseItem::QUARTER_CHAIR,								false,	1, 1, 2, 0},
  {BaseItem::HOLODECK_GRID,								false,	1, 1, 6, 6},
  {BaseItem::ENGINE_CONTROL_CENTER,						false,	3, 2, 10, 5},
  {BaseItem::ENGINE_REACTION_CHAMBER,					false,	2, 3, 50, -200},
  {BaseItem::ARBORETUM_TREE_1,							false,	1, 2, 2, 0},
  {BaseItem::ARBORETUM_TREE_2,							false,	1, 2, 2, 0},
  {BaseItem::ARBORETUM_TREE_3,							false,	1, 2, 2, 0},
  {BaseItem::ARBORETUM_TREE_4,							false,	1, 2, 2, 0},
  {BaseItem::ARBORETUM_TREE_5,							false,	1, 1, 1, 0},
  {BaseItem::ARBORETUM_TREE_6,							false,	1, 1, 1, 0},
  {BaseItem::ARBORETUM_TREE_7,							false,	1, 1, 1, 0},
  {BaseItem::ARBORETUM_TREE_8,							false,	1, 1, 1, 0},
  {BaseItem::ARBORETUM_TREE_9,							false,	1, 1, 1, 0},
  {BaseItem::ENVIRONMENT_O2_RECYCLER,					false,	1, 2, 10, 10},
  {BaseItem::NONE,										false,	0, 0, 0, 0},
};

BaseItem::BaseItem(int t) {
  // Init
  isSolid = false;
  type = t;
  progress = 0;
  builder = NULL;

  // Default values
  _width = 1;
  _height = 1;
  matter = 10;
  power = 0;
  powerSupply = 0;
  isSolid = false;

  for (int i = 0; itemsInfo[i].type != BaseItem::NONE; i++) {
	if (itemsInfo[i].type == t) {
	  _width = itemsInfo[i].width;
	  _height = itemsInfo[i].height;
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

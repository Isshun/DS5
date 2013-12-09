/*
 * BaseItem.cpp
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#include "BaseItem.h"

ItemInfo	itemsInfo[] = {
  {BaseItem::STRUCTURE_HULL,							true, 1, 1},
  {BaseItem::STRUCTURE_WALL,							true, 1, 1},
  {BaseItem::STRUCTURE_FLOOR,							false, 1, 1},
  {BaseItem::TRANSPORTATION_TRANSPORTER_SYSTEMS,		false, 1, 1},
  {BaseItem::QUARTER_BED,								false, 2, 2},
  {BaseItem::QUARTER_CHAIR,								false, 1, 1},
  {BaseItem::HOLODECK_GRID,								false, 1, 1},
  {BaseItem::ENGINE_CONTROL_CENTER,						false, 3, 2},
  {BaseItem::NONE,										false, 0, 0},
};

BaseItem::BaseItem(int t) {
	isSolid = false;
	type = t;

	for (int i = 0; itemsInfo[i].type != BaseItem::NONE; i++) {
	  if (itemsInfo[i].type == t) {
		_width = itemsInfo[i].width;
		_height = itemsInfo[i].height;
		isSolid = itemsInfo[i].solid;
	  }
	}
}

BaseItem::~BaseItem() {
	// TODO Auto-generated destructor stub
}

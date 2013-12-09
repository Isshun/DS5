/*
 * BaseItem.h
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#ifndef BASEITEM_H_
#define BASEITEM_H_

#include <SFML/System.hpp>
#include <SFML/Window.hpp>
#include <SFML/Graphics.hpp>

struct {
  int			type;
  bool			solid;
  int			width;
  int			height;
  int			matter;
  int			power;
} typedef		ItemInfo;

class BaseItem {
 public:
  BaseItem(int type);
  ~BaseItem();

  enum {
	NONE,
	STRUCTURE_WALL,
	STRUCTURE_HULL,
	STRUCTURE_FLOOR,
	STRUCTURE_WINDOW,
	SICKBAY_BIOBED,
	SICKBAY_LAB,
	SICKBAY_EMERGENCY_SHELTERS,
	ENGINE_CONTROL_CENTER,
	ENGINE_REACTION_CHAMBER,
	HOLODECK_GRID,
	ARBORETUM_TREE_1,
	ARBORETUM_TREE_2,
	ARBORETUM_TREE_3,
	ARBORETUM_TREE_4,
	ARBORETUM_TREE_5,
	ARBORETUM_TREE_6,
	ARBORETUM_TREE_7,
	ARBORETUM_TREE_8,
	ARBORETUM_TREE_9,
	GYMNASIUM_STUFF_1,
	GYMNASIUM_STUFF_2,
	GYMNASIUM_STUFF_3,
	GYMNASIUM_STUFF_4,
	GYMNASIUM_STUFF_5,
	SCHOOL_DESK,
	BAR_PUB,
	AMPHITHEATER_STAGE,
	QUARTER_BED,
	QUARTER_DESK,
	QUARTER_CHAIR,
	QUARTER_WARDROBE,
	QUARTER_CHEST,
	QUARTER_BEDSIDE_TABLE,
	ENVIRONMENT_O2_RECYCLER,
	ENVIRONMENT_TEMPERATURE_REGULATION,
	TRANSPORTATION_SHUTTLECRAFT,
	TRANSPORTATION_CARGO,
	TRANSPORTATION_CONTAINER,
	TRANSPORTATION_TRANSPORTER_SYSTEMS,
	TACTICAL_PHOTON_TORPEDO,
	TACTICAL_PHASER,
	TACTICAL_SHIELD_GRID,
	TACTICAL_CLOAKING_DEVICE,
	SCIENCE_HYDROPONICS
  };

  int			type;
  bool			isSolid;
  int			progress;
  void*			builder;

  int			getWidth() { return _width; }
  int			getHeight() { return _height; }
  int			getX() { return _x; }
  int			getY() { return _y; }

  void			setPosition(int x, int y) { _x = x; _y = y; }

  static ItemInfo getItemInfo(int type);

  bool			isComplete() { return progress == matter; }
  bool			isSupply() { return power == powerSupply; }

  int			matter;
  int			power;
  int			powerSupply;

 private:
  int			_width;
  int			_height;
  int			_x;
  int			_y;
};

#endif /* BASEITEM_H_ */

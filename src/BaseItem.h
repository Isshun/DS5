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

class Character;

struct {
  int			type;
  bool			solid;
  int			width;
  int			height;
  int			matter;
  int			power;
  int			zone;
} typedef		ItemInfo;

class BaseItem {
 public:
  BaseItem(int type, int id);
  ~BaseItem();

  enum {
	NONE,
    STRUCTURE_ITEM_START,
    STRUCTURE_ROOM,
	STRUCTURE_WALL,
	STRUCTURE_HULL,
	STRUCTURE_FLOOR,
	STRUCTURE_WINDOW,
	STRUCTURE_DOOR,
    STRUCTURE_ITEM_STOP,
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

  // Sets
  void				setPosition(int x, int y) { _x = x; _y = y; }
  void				setOwner(Character* character);
  void				setRoomId(int roomId) { _roomId = roomId; }
  void				setZoneId(int zoneId) { _zoneId = zoneId; }

  // Gets
  Character*		getOwner() { return _owner; }
  static ItemInfo 	getItemInfo(int type);
  int				getWidth() { return _width; }
  int				getHeight() { return _height; }
  int				getX() { return _x; }
  int				getY() { return _y; }
  int				getType() { return _type; }
  int				getZoneId() { return _zoneId; }
  int				getZoneIdRequired() { return _zoneIdRequired; }
  int				getRoomId() { return _roomId; }
  int				getId() { return _id; }

  // Bools
  bool				isComplete() { return progress == matter; }
  bool				isSupply() { return power == powerSupply; }
  bool				isFree() { return _owner == NULL; }
  bool				isType(int type) { return _type == type; }
  bool				isZoneMatch() { return _zoneId == _zoneIdRequired; }
  bool				isSleepingItem() { return _type == QUARTER_BED || _type == QUARTER_CHAIR; }
  bool				isStructure() { return _type > STRUCTURE_ITEM_START && _type < STRUCTURE_ITEM_STOP; }

  bool				isSolid;
  int				progress;
  int				matter;
  int				power;
  int				powerSupply;

 private:
  Character*	_owner;
  int			_width;
  int			_height;
  int			_roomId;
  int			_zoneId;
  int			_zoneIdRequired;
  int			_type;
  int			_x;
  int			_y;
  int			_id;
};

#endif /* BASEITEM_H_ */

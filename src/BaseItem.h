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
  const char*	name;
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
    STRUCTURE_START,
    STRUCTURE_ROOM,
	STRUCTURE_WALL,
	STRUCTURE_HULL,
	STRUCTURE_FLOOR,
	STRUCTURE_WINDOW,
	STRUCTURE_DOOR,
    STRUCTURE_STOP,
    ITEM_START,
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
	SCIENCE_HYDROPONICS,
	RES_1,
    ITEM_STOP,
  };

  // Actions
  int				gatherMatter(int maxValue);
  void				addMatter(int value) { _matterSupply += matter; }

  // Sets
  void				setPosition(int x, int y) { _x = x; _y = y; }
  void				setOwner(Character* character);
  void				setRoomId(int roomId) { _roomId = roomId; }
  void				setZoneId(int zoneId) { _zoneId = zoneId; }

  // Gets
  int				getMatterSupply() { return _matterSupply; }
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
  const char*		getName() { return _name; }
  static const char* getItemName(int type) {
	switch(type) {
	case NONE: return "NONE";
	case STRUCTURE_ROOM: return "room";
	case STRUCTURE_WALL: return "wall";
	case STRUCTURE_HULL: return "hull";
	case STRUCTURE_FLOOR: return "floor";
	case STRUCTURE_WINDOW: return "window";
	case STRUCTURE_DOOR: return "door";
	case SICKBAY_BIOBED: return "biobed";
	case SICKBAY_LAB: return "lab";
	case SICKBAY_EMERGENCY_SHELTERS: return "emergency shelters";
	case ENGINE_CONTROL_CENTER: return "control center";
	case ENGINE_REACTION_CHAMBER: return "reaction chamber";
	case HOLODECK_GRID: return "grid";
	case ARBORETUM_TREE_1: return "tree 1";
	case ARBORETUM_TREE_2: return "tree 2";
	case ARBORETUM_TREE_3: return "tree 3";
	case ARBORETUM_TREE_4: return "tree 4";
	case ARBORETUM_TREE_5: return "tree 5";
	case ARBORETUM_TREE_6: return "tree 6";
	case ARBORETUM_TREE_7: return "tree 7";
	case ARBORETUM_TREE_8: return "tree 8";
	case ARBORETUM_TREE_9: return "tree 9";
	case GYMNASIUM_STUFF_1: return "stuff 1";
	case GYMNASIUM_STUFF_2: return "stuff 2";
	case GYMNASIUM_STUFF_3: return "stuff 3";
	case GYMNASIUM_STUFF_4: return "stuff 4";
	case GYMNASIUM_STUFF_5: return "stuff 5";
	case SCHOOL_DESK: return "desk";
	case BAR_PUB: return "pub";
	case AMPHITHEATER_STAGE: return "stage";
	case QUARTER_BED: return "bed";
	case QUARTER_DESK: return "desk";
	case QUARTER_CHAIR: return "chair";
	case QUARTER_WARDROBE: return "wardrobe";
	case QUARTER_CHEST: return "chest";
	case QUARTER_BEDSIDE_TABLE: return "bedside table";
	case ENVIRONMENT_O2_RECYCLER: return "o2 recycler";
	case ENVIRONMENT_TEMPERATURE_REGULATION: return "temperature regulation";
	case TRANSPORTATION_SHUTTLECRAFT: return "shuttlecraft";
	case TRANSPORTATION_CARGO: return "cargo";
	case TRANSPORTATION_CONTAINER: return "container";
	case TRANSPORTATION_TRANSPORTER_SYSTEMS: return "transporter systems";
	case TACTICAL_PHOTON_TORPEDO: return "photon torpedo";
	case TACTICAL_PHASER: return "phaser";
	case TACTICAL_SHIELD_GRID: return "shield grid";
	case TACTICAL_CLOAKING_DEVICE: return "cloaking device";
	case SCIENCE_HYDROPONICS: return "hydroponics";
	case RES_1: return "res 1";
	default: return "unknow_item";
	}
  }


  // Bools
  bool				isComplete() { return _matterSupply == matter; }
  bool				isSupply() { return power == powerSupply; }
  bool				isFree() { return _owner == NULL; }
  bool				isType(int type) { return _type == type; }
  bool				isZoneMatch() { return _zoneId == _zoneIdRequired; }
  bool				isSleepingItem() { return _type == QUARTER_BED || _type == QUARTER_CHAIR; }
  bool				isStructure() { return _type > STRUCTURE_START && _type < STRUCTURE_STOP; }
  bool				isRessource() { return _type == RES_1; }
  bool				isWalkable() { return _type != STRUCTURE_WALL; }

  bool				isSolid;
  int				matter;
  int				_matterSupply;
  int				power;
  int				powerSupply;

  static bool		isStructure(int type) { return type > STRUCTURE_START && type < STRUCTURE_STOP; }
  static bool		isItem(int type) { return type > ITEM_START && type < ITEM_STOP; }

 private:
  Character*	_owner;
  const char*	_name;
  int			_width;
  int			_height;
  int			_roomId;
  int			_zoneId;
  int			_zoneIdRequired;

 protected:
  int			_type;
  int			_x;
  int			_y;
  int			_id;
};

#endif /* BASEITEM_H_ */

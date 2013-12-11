/*
 * UserInterfaceMenu.h
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#ifndef USERINTERFACEMENU_H_
#define USERINTERFACEMENU_H_

#include <SFML/System.hpp>
#include <SFML/Window.hpp>
#include <SFML/Graphics.hpp>
#include "defines.h"
#include "Cursor.h"
#include "WorldMap.h"

struct {
  int			code;
  const char*	text;
  const char*	shortcut;
  int			key;
  int			data;
} typedef		Entry;

class UserInterfaceMenu {
 public:

  enum { ENTRY_NONE, ENTRY_MAIN, ENTRY_BUILD, ENTRY_ZONE };

  enum {
	CODE_ZONE_NONE,
    CODE_ZONE_ENGINE,
    CODE_ZONE_SICKBAY,
    CODE_ZONE_QUARTER,
    CODE_ZONE_BAR,
    CODE_ZONE_HOLODECK,
    CODE_ZONE_OPERATION,
  };

  enum {
    CODE_NONE,
    CODE_MAIN,
    CODE_BUILD,
    CODE_ZONE,
    CODE_ERASE,
    CODE_CREW,
    CODE_BUILD_ITEM,
    CODE_BUILD_STRUCTURE,
    CODE_BUILD_SICKBAY,
    CODE_BUILD_ENGINE,
    CODE_BUILD_HOLODECK,
    CODE_BUILD_ARBORETUM,
    CODE_BUILD_GYMNASIUM,
    CODE_BUILD_SCHOOL,
    CODE_BUILD_BAR,
    CODE_BUILD_AMPHITHEATER,
    CODE_BUILD_QUARTER,
    CODE_BUILD_ENVIRONMENT,
    CODE_BUILD_TRANSPORTATION,
    CODE_BUILD_TACTICAL,
    CODE_BUILD_SCIENCE
  };

  UserInterfaceMenu(sf::RenderWindow* app, WorldMap* worldmap, Cursor* cursor);
  ~UserInterfaceMenu();
  void  openMenu(Entry entry);
  void  openBack();
  void	setBuildMenu(int code);
  void	setBuildItem(int code, const char* text, int type);
  int	getBuildItemType() { return _buildItemType; }
  int	getCode() { return _code; }
  int   getParentCode() { return _parent_code; }
  void	mousePressed(sf::Mouse::Button button, int x, int y);
  void	refreshMenu();
  bool  checkKeyboard(int code);
  void	drawModeBuild();

 private:
  sf::RenderWindow*		_app;
  WorldMap*				_worldmap;
  Cursor*				_cursor;
  int					_code;
  Entry*				_entries;
  int					_parent_code;
  int					_buildItemType;
  const char*			_buildItemText;
};

#endif /* USERINTERFACEMENU_H_ */

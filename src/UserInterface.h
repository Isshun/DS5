/*
 * UserInterface.h
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#ifndef USERINTERFACE_H_
#define USERINTERFACE_H_

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


class UserInterface {
 public:
  UserInterface(sf::RenderWindow* app, WorldMap* worldMap);
  ~UserInterface();

  enum { ENTRY_NONE, ENTRY_MAIN, ENTRY_BUILD, ENTRY_ZONE };

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
	CODE_BUILD_SCIENCE,
	CODE_ZONE_ENGINE,
	CODE_ZONE_SICKBAY,
	CODE_ZONE_QUARTER,
	CODE_ZONE_BAR,
  };



  void	draw();
  void	drawModeBuild();
  void	setBuildItem(int code, const char* text, int type);
  void	setBuildMenu(int code);
  bool	checkKeyboard(sf::Event	event, int frame, int lastInput, WorldMap* worldMap);
  Cursor*	getCursor() { return _cursor; }
  int	getBuildItemType() { return _buildItemType; }
  int	getCode() { return _code; }
  void	mouseMoved(int x, int y);
  void	mousePress(int x, int y);
  void	mouseRelease(int x, int y);

 private:
  void	drawCursor(int startX, int startY, int toX, int toY);

  sf::RenderWindow* _app;
  Cursor*	_cursor;
  Entry*	_entries;
  WorldMap* _worldMap;
  int		_code;
  int		_parent_code;
  bool		_keyLeftPressed;
  int		_keyPressPosX;
  int		_keyPressPosY;
  int		_keyMovePosX;
  int		_keyMovePosY;
  int			_buildItemType;
  const char*	_buildItemText;
};

#endif /* USERINTERFACE_H_ */

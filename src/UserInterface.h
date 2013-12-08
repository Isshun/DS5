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
} typedef		Entry;


class UserInterface {
 public:
  UserInterface();
  ~UserInterface();

  enum { ENTRY_NONE, ENTRY_MAIN, ENTRY_BUILD, ENTRY_ZONE };

  enum { MODE_NONE, MODE_MAIN, MODE_BUILD, MODE_ZONE };

  enum {
	CODE_NONE,
	CODE_MAIN,
	CODE_BUILD,
	CODE_ZONE,
	CODE_ERASE,
	CODE_CREW,
	CODE_BUILD_FLOOR,
	CODE_BUILD_WALL,
	CODE_ZONE_ENGINE,
	CODE_ZONE_SICKBAY,
	CODE_ZONE_QUARTER,
	CODE_ZONE_BAR,
  };

  void	draw(sf::RenderWindow* app);
  void	drawModeBuild(sf::RenderWindow* app);
  void	setBuildItem(int code, const char* text);
  bool	checkKeyboard(sf::Event	event, int frame, int lastInput, WorldMap* worldMap);
  Cursor*	getCursor() { return _cursor; }

 private:
  Cursor*	_cursor;
  Entry*	_entries;
  int		_mode;
  int		_code;

  int			_buildItemType;
  const char*	_buildItemText;
};

#endif /* USERINTERFACE_H_ */

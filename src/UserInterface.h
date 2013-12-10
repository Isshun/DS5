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
#include "UserInterfaceMenu.h"

#define MOVE_VIEW_OFFSET        40

class UserInterface {
 public:
  UserInterface(sf::RenderWindow* app, WorldMap* worldMap);
  ~UserInterface();

  void	refresh();
  void	refreshMenu();
  void	refreshCursor();
  void	refreshResources();
  void  openMenu(Entry entry);
  void  openMenuBack();
  bool	checkKeyboard(sf::Event	event, int frame, int lastInput, WorldMap* worldMap);
  Cursor*	getCursor() { return _cursor; }
  void	mouseMoved(int x, int y);
  void	mousePress(sf::Mouse::Button button, int x, int y);
  void	mouseRelease(sf::Mouse::Button button, int x, int y);
  void	mouseWheel(int delta, int x, int y);
  int	getViewPosX() { return _viewPosX; }
  int	getViewPosY() { return _viewPosY; }
  sf::Transform  getViewTransform(sf::Transform transform);

 private:
  void	drawCursor(int startX, int startY, int toX, int toY);

  sf::RenderWindow* _app;
  Cursor*		_cursor;
  WorldMap*		_worldMap;
  bool			_keyLeftPressed;
  bool			_keyRightPressed;
  sf::Vector2i	_mouseRightPress;
  int			_keyPressPosX;
  int			_keyPressPosY;
  int			_keyMovePosX;
  int			_keyMovePosY;
  int			_viewPosX;
  int           _viewPosY;
  float			_zoom;
  UserInterfaceMenu*       _menu;
};

#endif /* USERINTERFACE_H_ */

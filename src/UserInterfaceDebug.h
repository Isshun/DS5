/*
 * UserInterfaceDebug.h
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#ifndef USERINTERFACE_DEBUG_H_
#define USERINTERFACE_DEBUG_H_

#include <SFML/System.hpp>
#include <SFML/Window.hpp>
#include <SFML/Graphics.hpp>
#include "defines.h"
#include "Cursor.h"

class UserInterfaceDebug {
 public:
  UserInterfaceDebug(sf::RenderWindow* app);
  ~UserInterfaceDebug();

  void	refresh(int frame, int x, int y);
  void  addDebug(const char* key, std::string value);

 private:
  sf::RenderWindow*     _app;
  sf::Font				_font;
  int					_index;
};

#endif

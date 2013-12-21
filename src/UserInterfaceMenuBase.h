/*
 * UserInterfaceMenu.h
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#ifndef USERINTERFACEMENUBASE_H_
#define USERINTERFACEMENUBASE_H_

#include <SFML/System.hpp>
#include <SFML/Window.hpp>
#include <SFML/Graphics.hpp>
#include "defines.h"

class UserInterfaceMenuBase {
 public:

  UserInterfaceMenuBase(sf::RenderWindow* app);
  ~UserInterfaceMenuBase();
  void	drawTile(int index);

 private:
  sf::RenderWindow* _app;
  sf::Font			_font;
};

#endif /* USERINTERFACERESOURCE_H_ */

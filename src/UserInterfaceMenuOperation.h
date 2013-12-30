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

class UserInterfaceMenuOperation {
 public:

  UserInterfaceMenuOperation(sf::RenderWindow* app);
  ~UserInterfaceMenuOperation();
  void	drawTile(int index);

 private:
  sf::RenderWindow* _app;
  sf::Font			_font;
  sf::Sprite		_background;
  sf::Texture		_backgroundTexture;
};

#endif /* USERINTERFACERESOURCE_H_ */

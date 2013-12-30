/*
 * UserInterfaceMenu.h
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#ifndef USERINTERFACERESOURCE_H_
#define USERINTERFACERESOURCE_H_

#include <SFML/System.hpp>
#include <SFML/Window.hpp>
#include <SFML/Graphics.hpp>
#include "defines.h"

class UserInterfaceResource {
 public:

  UserInterfaceResource(sf::RenderWindow* app);
  ~UserInterfaceResource();
  void refreshResources(int frame, long interval);
  void	drawTile(int index);

 private:
  sf::RenderWindow* _app;
  sf::Font			_font;
  sf::Sprite		_background;
  sf::Texture		_backgroundTexture;
};

#endif /* USERINTERFACERESOURCE_H_ */

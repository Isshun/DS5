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
#include "UserInterfaceBase.h"

class UserInterfaceResource : public UserInterfaceBase {
 public:

  UserInterfaceResource(sf::RenderWindow* app, int tileIndex);
  ~UserInterfaceResource();
  void refreshResources(int frame, long interval);
  void	draw(int frame);
  void	drawTile();
  void	drawPanel(int frame);
  bool	checkKey(sf::Keyboard::Key key);
};

#endif /* USERINTERFACERESOURCE_H_ */

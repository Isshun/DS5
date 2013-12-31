/*
 * UserInterfaceMenu.h
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#ifndef USERINTERFACESECURITY_H_
#define USERINTERFACESECURITY_H_

#include <SFML/System.hpp>
#include <SFML/Window.hpp>
#include <SFML/Graphics.hpp>
#include "defines.h"
#include "UserInterfaceBase.h"

class UserInterfaceSecurity : public UserInterfaceBase {
 public:

  UserInterfaceSecurity(sf::RenderWindow* app, int tileIndex);
  ~UserInterfaceSecurity();
  void refreshSecuritys(int frame, long interval);
  void	draw(int frame);
  void	drawTile();
  void	drawPanel(int frame);
  bool	checkKey(sf::Keyboard::Key key);
};

#endif /* USERINTERFACESECURITY_H_ */

/*
 * UserInterfaceMenu.h
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#ifndef USERINTERFACESCIENCE_H_
#define USERINTERFACESCIENCE_H_

#include <SFML/System.hpp>
#include <SFML/Window.hpp>
#include <SFML/Graphics.hpp>
#include "defines.h"
#include "UserInterfaceBase.h"

class UserInterfaceScience : public UserInterfaceBase {
 public:

  UserInterfaceScience(sf::RenderWindow* app, int tileIndex);
  ~UserInterfaceScience();
  void refreshSciences(int frame, long interval);
  void	draw(int frame);
  void	drawTile();
  void	drawPanel(int frame);
  bool	checkKey(sf::Keyboard::Key key);
};

#endif /* USERINTERFACESCIENCE_H_ */

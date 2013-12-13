/*
 * UserInterfaceMenuCharacter.h
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#ifndef USERINTERFACEMENUCHARACTER_H_
#define USERINTERFACEMENUCHARACTER_H_

#include <SFML/System.hpp>
#include <SFML/Window.hpp>
#include <SFML/Graphics.hpp>
#include "defines.h"

class UserInterfaceMenuCharacter {
 public:
  UserInterfaceMenuCharacter(sf::RenderWindow* app);
  ~UserInterfaceMenuCharacter();

  void	refresh();

 private:
  sf::RenderWindow* _app;
};

#endif

/*
 * UserInterfaceMenuCharacter.h
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#ifndef USERINTERFACE_CREW_H_
#define USERINTERFACE_CREW_H_

#include <SFML/System.hpp>
#include <SFML/Window.hpp>
#include <SFML/Graphics.hpp>
#include "defines.h"
#include "CharacterManager.h"
#include "Character.h"

class UserInterfaceCrew {
 public:
  UserInterfaceCrew(sf::RenderWindow* app);
  ~UserInterfaceCrew();

  void	refresh(int frame);
  void  addCharacter(int index, Character* character);
  void	drawTile(int index);

 private:
  sf::RenderWindow*     _app;
  sf::Font				_font;
  CharacterManager*     _characterManager;
};

#endif

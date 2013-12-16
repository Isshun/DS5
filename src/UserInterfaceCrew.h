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
  UserInterfaceCrew(sf::RenderWindow* app, CharacterManager* characterManager);
  ~UserInterfaceCrew();

  void	refresh(int frame);
  void  addCharacter(int index, Character* character);

 private:
  sf::RenderWindow*     _app;
  CharacterManager*     _characterManager;
};

#endif

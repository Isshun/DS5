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
#include "UserInterfaceBase.h"

class UserInterfaceCrew : public UserInterfaceBase {
 public:
  UserInterfaceCrew(sf::RenderWindow* app, int tileIndex);
  ~UserInterfaceCrew();

  void	draw(int frame);
  void	drawTile();
  void	drawPanel(int frame);
  bool	checkKey(sf::Keyboard::Key key);
  void  addCharacter(int frame, Character* character);

 private:
  CharacterManager*     _characterManager;
};

#endif

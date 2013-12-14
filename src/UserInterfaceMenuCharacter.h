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
#include "Character.h"

class UserInterfaceMenuCharacter {
 public:
  UserInterfaceMenuCharacter(sf::RenderWindow* app);
  ~UserInterfaceMenuCharacter();

  void	refresh(int frame);
  void  setCharacter(Character* character) { _character = character; }
  Character*  getCharacter() { return _character; }
  void  addGauge(int posX, int posY, int width, int height, int value, const char* text);
  void  addMessage(int posX, int posY, int width, int height, int value);

 private:
  sf::RenderWindow*     _app;
  Character*            _character;
};

#endif

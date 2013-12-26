/*
 * UserInterfaceMenuInfo.h
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#ifndef USERINTERFACEMENUINFO_H_
#define USERINTERFACEMENUINFO_H_

#include <SFML/System.hpp>
#include <SFML/Window.hpp>
#include <SFML/Graphics.hpp>
#include "defines.h"
#include "WorldMap.h"
#include "BaseItem.h"

class UserInterfaceMenuInfo {
 public:
  UserInterfaceMenuInfo(sf::RenderWindow* app);
  ~UserInterfaceMenuInfo();

  void	refresh(int frame);
  void  setArea(WorldArea* area) { _area = area; }
  WorldArea*  getArea() { return _area; }

 private:
  sf::RenderWindow*     _app;
  sf::Font				_font;
  sf::Texture			_backgroundTexture;
  WorldArea*			_area;
};

#endif

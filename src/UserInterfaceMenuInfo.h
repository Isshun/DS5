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

  void	init();
  void	refresh(int frame);
  void  setArea(WorldArea* area) { _area = area; }
  void  setItem(BaseItem* item) { _item = item; }
  WorldArea*	getArea() { return _area; }
  BaseItem*		getItem() { return _item; }
  void	addLine(sf::RenderStates render, const char* str);
  void	addLine(sf::RenderStates render, const char* label, const char* value);
  void	addLine(sf::RenderStates render, const char* label, int value);

 private:
  sf::RenderWindow*     _app;
  sf::Font				_font;
  sf::Sprite			_background;
  sf::Texture			_backgroundTexture;
  WorldArea*			_area;
  BaseItem*				_item;
  int					_line;
};

#endif

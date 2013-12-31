/*
 * UserInterfaceMenu.h
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#ifndef USERINTERFACEMENUBASE_H_
#define USERINTERFACEMENUBASE_H_

#include <SFML/System.hpp>
#include <SFML/Window.hpp>
#include <SFML/Graphics.hpp>
#include "defines.h"

class UserInterfaceMenuOperation {
 public:

  UserInterfaceMenuOperation(sf::RenderWindow* app);
  ~UserInterfaceMenuOperation();
  void	draw(int index);
  void	drawTile(int index);
  void	drawJobs();
  void	toogleJobs() { _isJobsOpen = !_isJobsOpen; }
  void	toogleTile() { _isTileOpen = !_isTileOpen; }

 private:
  sf::RenderWindow* _app;
  sf::Font			_font;
  sf::Sprite		_bgPanel;
  sf::Sprite		_bgTile;
  sf::Texture		_texturePanel;
  sf::Texture		_textureTile;
  bool				_isTileOpen;
  bool				_isJobsOpen;
};

#endif /* USERINTERFACERESOURCE_H_ */

#ifndef USERINTERFACEBASE_H_
#define USERINTERFACEBASE_H_

#include <SFML/System.hpp>
#include <SFML/Window.hpp>
#include <SFML/Graphics.hpp>
#include "defines.h"

class UserInterfaceBase {
 public:

  UserInterfaceBase(sf::RenderWindow* app, int tileIndex);
  ~UserInterfaceBase();
  virtual void	draw(int frame) = 0;
  void	openTile() { _isTileActive = true; }
  void	closeTile() { _isTileActive = false; }
  void	toogleTile() { _isTileActive = !_isTileActive; }
  void	toogle() { _isOpen = !_isOpen; }
  void	open() { _isOpen = true; }
  void	close() { _isOpen = false; }
  bool	checkKey(sf::Keyboard::Key key);
  bool	onMouseMove(int x, int y);
  bool	mousePress(sf::Mouse::Button button, int x, int y);
  bool	mouseRelease(sf::Mouse::Button button, int x, int y);
  bool	isOpen() { return _isOpen; }
  bool	isTileActive() { return _isTileActive; }

 protected:
  void	drawTile(sf::Color color);
  void	drawPanel();

  sf::RenderWindow* _app;
  sf::Texture		_texturePanel;
  sf::Texture		_textureTile;
  sf::Sprite		_bgPanel;
  sf::Sprite		_bgTile;
  int				_posX;
  int				_posY;
  int				_posTileX;
  int				_posTileY;
  bool			    _isTileActive;
  bool				_isOpen;

 private:
  int				_tileIndex;
};

#endif /* USERINTERFACEBASE_H_ */

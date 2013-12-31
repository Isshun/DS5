#ifndef USERINTERFACEENGENEERING_H_
#define USERINTERFACEENGENEERING_H_

#include <SFML/System.hpp>
#include <SFML/Window.hpp>
#include <SFML/Graphics.hpp>
#include "defines.h"

class UserInterfaceEngineering {
 public:

  UserInterfaceEngineering(sf::RenderWindow* app);
  ~UserInterfaceEngineering();
  void	draw(int index);
  void	drawTile(int index);
  void	drawPanel();
  void	openTile();
  void	closeTile();
  void	toogleTile();
  void	toogle();
  void	open();
  void	close();
  bool	checkKey(sf::Keyboard::Key key);

 private:
  void	drawIcon(int index, int type);

  sf::RenderWindow* _app;
  sf::Font			_font;
  sf::Sprite		_bgPanel;
  sf::Sprite		_bgTile;
  sf::Texture		_texturePanel;
  sf::Texture		_textureTile;
  bool				_tileActive;
  bool				_isOpen;
  int				_panelMode;
  int				_posX;
  int				_posY;

  enum {
	MODE_NONE,
	MODE_STRUCTURE,
	MODE_ITEM
  };

};

#endif /* USERINTERFACEENGENEERING_H_ */

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
  void	drawTile(int index);
  void	openTile();
  void	closeTile();
  void	toogleTile();
  void	open();
  void	close();

 private:
  sf::RenderWindow* _app;
  sf::Font			_font;
  bool				_tileOpened;
};

#endif /* USERINTERFACEENGENEERING_H_ */

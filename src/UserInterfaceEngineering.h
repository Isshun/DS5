#ifndef USERINTERFACEENGENEERING_H_
#define USERINTERFACEENGENEERING_H_

#include <SFML/System.hpp>
#include <SFML/Window.hpp>
#include <SFML/Graphics.hpp>
#include "defines.h"
#include "UserInterfaceBase.h"
#include "UserInteraction.h"

class UserInterfaceEngineering : public UserInterfaceBase {
 public:

  UserInterfaceEngineering(sf::RenderWindow* app, int tileIndex, UserInteraction* interaction);
  ~UserInterfaceEngineering();
  void	draw(int frame);
  bool	checkKey(sf::Keyboard::Key key);
  bool	onMouseMove(int x, int y);
  bool	mousePress(sf::Mouse::Button button, int x, int y);
  bool	mouseRelease(sf::Mouse::Button button, int x, int y);

 private:
  void	drawIcon(int index, int type);
  void	drawTile();
  void	drawPanel();

  int				_panelMode;
  int				_panelModeHover;
  int				_itemHover;

  enum {
	MODE_NONE,
	MODE_STRUCTURE,
	MODE_ITEM
  };

 private:
  UserInteraction* _interaction;

};

#endif /* USERINTERFACEENGENEERING_H_ */

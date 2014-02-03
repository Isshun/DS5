#ifndef HOME_SCREEN_H
#define HOME_SCREEN_H

#include <SFML/System.hpp>
#include <SFML/Window.hpp>
#include <SFML/Graphics.hpp>

#include "Game.h"
#include "defines.h"
#include "Options.h"
#include "Settings.h"
#include "UIViewGroup.h"
#include "UIFrame.h"
#include "UILabel.h"

class HomeScreen {

 public:
  HomeScreen(sf::RenderWindow *sapp) {
	app = sapp;
  }
  void	run();

 private:
  void	actionNew();
  void	actionSave();
  void	actionLoad();
  void	actionQuit();
  void	actionResume();

  Game* game;
  int	anim;
  sf::RenderWindow *app;
};

#endif

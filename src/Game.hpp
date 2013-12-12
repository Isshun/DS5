#ifndef _C_GAME_
#define _C_GAME_

#include <SFML/Window.hpp>
#include <utime.h>

#include "Character.h"
#include "defines.h"
#include "WorldMap.h"
#include "SpriteManager.h"
#include "Cursor.h"
#include "UserInterface.h"
#include "Viewport.h"
#include "CharacterManager.h"

struct s_link {
  const char	*name;
  int		x;
  int		y;
  const char	*jump_map;
  int		jump_x;
  int		jump_y;
};

class	Game {
public:
  Game(sf::RenderWindow* app);
  ~Game();

  WorldMap	*_worldMap;

  void	loop();
  void	update();
  void	refresh();
  void	draw_surface();

  bool	is_run() { return _run; }

  void	gere_key();
  void	gere_quit();

  bool	run;

  bool	get_physique(int x, int y);

  void	checkJump();
  void	jump(s_link link);

private:
  sf::RenderWindow* _app;
  sf::Event	event;
  sf::Sprite* _background;

  sf::Texture*	_backgroundTexture;

  SpriteManager*		_spriteManager;
  UserInterface*		_ui;

  bool	_force_refresh;
  bool                  _run;
  CharacterManager*		_characterManager;
  Viewport*             _viewport;

  unsigned int _frame;
  unsigned int _lastInput;
};

#endif

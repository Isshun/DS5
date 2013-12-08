#ifndef _C_GAME_
#define _C_GAME_

#include <SFML/Window.hpp>
#include <utime.h>

#include "Character.hpp"
#include "Layer.hpp"
#include "tosng.h"
#include "Scene.hpp"
#include "WorldMap.h"
#include "SpriteManager.h"
#include "Cursor.h"

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
  Game();
  ~Game();

  WorldMap	*_worldMap;

  void	loop();
  void	update();
  void	refresh();
  void	draw_surface();

  bool	is_paused() { return this->pause; }
  bool	is_run() { return !this->pause; }

  void	gere_key();
  void	gere_quit();

  bool	run;

  bool	get_physique(int x, int y);

  void	checkJump();
  void	jump(s_link link);


public:
  Scene		*scene;
  Cursor	*_cursor;
  //temp
  //sf::Sprite	Sprite2;

private:
  sf::Event	event;

  SpriteManager*		_spriteManager;
  Character*			character;

  bool	_force_refresh;
  // Music		*music;
  bool		up_to_date;
  //   SDL_Surface	*window;
  bool		pause;

  unsigned int _frame;
  unsigned int _lastInput;
};

#endif
